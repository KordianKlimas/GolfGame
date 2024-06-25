package com.kentwentyfour.project12.bots.improvedbot.resources;

import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;

import java.util.*;

/**
 * A* Algorithm implementation for generating waypoints on a map with obstacles.
 */
public class AStarAlgorithm {
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private MapManager mapManager;
    private final double[][] directions = {
            {0.1, 0}, {-0.1, 0}, {0, 0.1}, {0, -0.1},
            {0.1, 0.1}, {0.1, -0.1}, {-0.1, 0.1}, {-0.1, -0.1}
    };
    private final double epsilon = 0.2; // Minimum change requirement
    private final double buffer = 0.2;  // Buffer around water obstacles

    /**
     * Constructs an AStarAlgorithm instance.
     * Initializes the map manager from the reference store.
     */
    public AStarAlgorithm() {
        this.mapManager = referenceStore.getMapManager();
    }

    /**
     * Generates waypoints from a starting point to a goal using the A* algorithm.
     *
     * @param startX The starting X coordinate.
     * @param startY The starting Y coordinate.
     * @param holeX The goal X coordinate.
     * @param holeY The goal Y coordinate.
     * @return A list of waypoints representing the path from start to goal.
     */
    public List<Waypoint> generateWaypoints(double startX, double startY, double holeX, double holeY) {
        List<Waypoint> result = null;
        PriorityQueue<Waypoint> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fScore));
        Map<Waypoint, Waypoint> allNodes = new HashMap<>();

        Waypoint start = new Waypoint(startX, startY);
        Waypoint goal = new Waypoint(holeX, holeY);

        start.gScore = 0;
        start.hScore = heuristic(start, goal);
        start.fScore = start.hScore;
        openSet.add(start);
        allNodes.put(start, start);

        while (!openSet.isEmpty()) {
            Waypoint current = openSet.poll();
            if (lineOfSight(current.x, current.y, holeX, holeY)) {
                System.out.println("Direct line of sight from " + current + " to goal");
                result = reconstructPath(current, holeX, holeY);
                break;
            }

            for (double[] dir : directions) {
                double nextX = roundToTenth(current.x + dir[0]);
                double nextY = roundToTenth(current.y + dir[1]);
                Waypoint nextWaypoint = new Waypoint(nextX, nextY);

                if (!isValidWaypoint(nextX, nextY)) {
                    continue;
                }

                double tentativeGScore = current.gScore + 1;
                Waypoint nextNode = allNodes.getOrDefault(nextWaypoint, new Waypoint(nextX, nextY));

                if (tentativeGScore < nextNode.gScore) {
                    nextNode.cameFrom = current;
                    nextNode.gScore = tentativeGScore;
                    nextNode.hScore = heuristic(nextNode, goal);
                    nextNode.fScore = tentativeGScore + nextNode.hScore;

                    if (!openSet.contains(nextNode)) {
                        openSet.add(nextNode);
                    }
                    allNodes.put(nextWaypoint, nextNode);
                }
            }
        }
        if (result == null) {
            result = Collections.emptyList(); // Return an empty list if no path is found
        }

        return result;
    }

    /**
     * Reconstructs the path from the goal back to the start.
     *
     * @param current The current waypoint.
     * @param holeX The goal X coordinate.
     * @param holeY The goal Y coordinate.
     * @return The list of waypoints representing the reconstructed path.
     */
    private List<Waypoint> reconstructPath(Waypoint current, double holeX, double holeY) {
        List<Waypoint> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = current.cameFrom;
        }
        Collections.reverse(path);

        // Ensure the direct path to the goal is added
        if (lineOfSight(path.get(path.size() - 1).x, path.get(path.size() - 1).y, holeX, holeY)) {
            path.add(new Waypoint(holeX, holeY));
        } else {
            System.out.println("Warning: Unable to reach the goal from the last waypoint in path.");
        }

        // Simplify the path by removing unnecessary waypoints with a minimum change requirement
        List<Waypoint> simplifiedPath = new ArrayList<>();
        simplifiedPath.add(path.get(0)); // start point

        for (int i = 1; i < path.size() - 1; i++) {
            Waypoint prev = simplifiedPath.get(simplifiedPath.size() - 1);
            Waypoint next = path.get(i + 1);

            if (!lineOfSight(prev.x, prev.y, next.x, next.y) &&
                    Math.hypot(path.get(i).x - prev.x, path.get(i).y - prev.y) > epsilon) {
                simplifiedPath.add(path.get(i));
            }
        }

        simplifiedPath.add(new Waypoint(holeX, holeY)); // end point
        return simplifiedPath;
    }

    /**
     * Checks if there is a direct line of sight between two points.
     *
     * @param startX The starting X coordinate.
     * @param startY The starting Y coordinate.
     * @param endX The ending X coordinate.
     * @param endY The ending Y coordinate.
     * @return True if there is a direct line of sight; false otherwise.
     */
    private boolean lineOfSight(double startX, double startY, double endX, double endY) {
        int steps = (int) Math.max(200, 20 * Math.hypot(endX - startX, endY - startY)); // Increased step size
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double x = startX + t * (endX - startX);
            double y = startY + t * (endY - startY);
            if (!isValidWaypoint(x, y)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a waypoint is valid.
     *
     * @param x The X coordinate of the waypoint.
     * @param y The Y coordinate of the waypoint.
     * @return True if the waypoint is valid; false otherwise.
     */
    private boolean isValidWaypoint(double x, double y) {
        MatrixMapArea area = mapManager.accessObject(x, y);
        boolean isValid = area != null && !(area instanceof Water);

        // Apply buffer for water obstacles
        if (area instanceof Water) {
            double bufferDistance = Math.hypot(((Water) area).getCoordinateX1() - x, ((Water) area).getCoordinateY1() - y);
            if (bufferDistance < buffer) {
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Rounds a double value to the nearest tenth.
     *
     * @param value The value to round.
     * @return The rounded value.
     */
    private double roundToTenth(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    /**
     * Calculates the Euclidean heuristic distance between two waypoints.
     *
     * @param a The first waypoint.
     * @param b The second waypoint.
     * @return The Euclidean distance between waypoints a and b.
     */
    private double heuristic(Waypoint a, Waypoint b) {
        return Math.hypot(a.x - b.x, a.y - b.y);
    }
}
