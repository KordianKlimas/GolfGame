package com.kentwentyfour.project12.bots.improvedbot.resources;

import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;

import java.util.*;

/**
 * SecondAlgorithm class implements a pathfinding algorithm using a variation of A*.
 * It attempts to find a path from a starting waypoint to a goal waypoint,
 * considering obstacles and varying step sizes to optimize the pathfinding process.
 */
public class SecondAlgorithm {
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private MapManager mapManager;
    private final double[] stepSizes = {0.5, 0.25, 0.125, 0.1};
    private final double penaltyFactor = 1.0;
    private double EPSILON = 1e-6; // Small epsilon

    /**
     * Constructs a SecondAlgorithm instance and initializes the map manager.
     */
    public SecondAlgorithm() {
        this.mapManager = referenceStore.getMapManager();
    }

    /**
     * Generates waypoints from a starting point to a goal using a pathfinding algorithm
     * that adjusts step sizes and epsilon values dynamically.
     *
     * @param startX The starting X coordinate.
     * @param startY The starting Y coordinate.
     * @param holeX The goal X coordinate.
     * @param holeY The goal Y coordinate.
     * @return A list of waypoints representing the path from start to goal,
     *         or an empty list if no path is found.
     */
    public List<Waypoint> generateWaypoints(double startX, double startY, double holeX, double holeY) {
        Waypoint start = new Waypoint(startX, startY);
        Waypoint goal = new Waypoint(holeX, holeY);

        List<Waypoint> path = new ArrayList<>();
        boolean pathFound = false;

        for (double stepSize : stepSizes) {
            // Reset allPoints and graph for each step size
            Set<Waypoint> allPoints = new HashSet<>();
            Map<Waypoint, List<Waypoint>> graph = new HashMap<>();

            // Generate points on the grid with the current step size
            generateAllPoints(allPoints, stepSize);

            // Connect each point to its neighbors
            connectNeighbors(graph, allPoints, stepSize);

            path = aStar(start, goal, graph);
            if (!path.isEmpty()) {
                pathFound = true;
                break;
            }

            // For step size 0.1, try again with EPSILON set to 0.1 if not already set
            if (stepSize == 0.1 && !pathFound) {
                EPSILON = 0.1;

                // Reset allPoints and graph for each step size
                allPoints.clear();
                graph.clear();

                // Generate points on the grid with the current step size
                generateAllPoints(allPoints, stepSize);

                // Connect each point to its neighbors
                connectNeighbors(graph, allPoints, stepSize);

                path = aStar(start, goal, graph);
                if (!path.isEmpty()) {
                    pathFound = true;
                    break;
                }
            }
        }

        return pathFound ? path : Collections.emptyList();
    }

    /**
     * Generates all possible waypoints on the grid with a given step size.
     *
     * @param allPoints The set to store all generated waypoints.
     * @param stepSize The step size for generating waypoints.
     */
    private void generateAllPoints(Set<Waypoint> allPoints, double stepSize) {
        for (double x = -5; x <= 5 + EPSILON; x += stepSize) {
            for (double y = -5; y <= 5 + EPSILON; y += stepSize) {
                Waypoint point = new Waypoint(roundToPrecision(x), roundToPrecision(y));
                if (isValidWaypoint(point.x, point.y)) {
                    allPoints.add(point);
                }
            }
        }
    }

    /**
     * Connects each waypoint to its valid neighboring waypoints within the given step size.
     *
     * @param graph The graph to store the connections between waypoints.
     * @param allPoints The set of all waypoints to be connected.
     * @param stepSize The step size for connecting neighboring waypoints.
     */
    private void connectNeighbors(Map<Waypoint, List<Waypoint>> graph, Set<Waypoint> allPoints, double stepSize) {
        for (Waypoint point : allPoints) {
            List<Waypoint> neighbors = new ArrayList<>();
            for (double dx = -stepSize; dx <= stepSize + EPSILON; dx += stepSize) {
                for (double dy = -stepSize; dy <= stepSize + EPSILON; dy += stepSize) {
                    if (dx == 0 && dy == 0) continue;
                    double nextX = roundToPrecision(point.x + dx);
                    double nextY = roundToPrecision(point.y + dy);
                    Waypoint neighbor = new Waypoint(nextX, nextY);

                    if (allPoints.contains(neighbor) && lineOfSight(point.x, point.y, neighbor.x, neighbor.y)) {
                        neighbors.add(neighbor);
                    }
                }
            }
            graph.put(point, neighbors);
        }
    }

    /**
     * Performs the A* algorithm to find the shortest path from start to goal in the given graph.
     *
     * @param start The starting waypoint.
     * @param goal The goal waypoint.
     * @param graph The graph representing connections between waypoints.
     * @return A list of waypoints representing the shortest path from start to goal,
     *         or an empty list if no path is found.
     */
    private List<Waypoint> aStar(Waypoint start, Waypoint goal, Map<Waypoint, List<Waypoint>> graph) {
        PriorityQueue<Waypoint> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fScore));
        Map<Waypoint, Double> gScore = new HashMap<>();
        Map<Waypoint, Waypoint> cameFrom = new HashMap<>();
        Map<Waypoint, Integer> nodeCount = new HashMap<>();

        start.gScore = 0;
        start.fScore = heuristic(start, goal, 0);
        gScore.put(start, 0.0);
        nodeCount.put(start, 0);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Waypoint current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            boolean neighborConnected = false;

            for (Waypoint neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                double tentativeGScore = gScore.get(current) + distance(current, neighbor);
                int tentativeNodeCount = nodeCount.get(current) + 1;
                double tentativeFScore = tentativeGScore + heuristic(neighbor, goal, tentativeNodeCount);

                if (tentativeFScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = tentativeFScore;
                    nodeCount.put(neighbor, tentativeNodeCount);
                    openSet.add(neighbor);
                    neighborConnected = true;
                }
            }

            if (!neighborConnected) {
                // No more neighbors can be connected from the current waypoint
            }
        }

        return Collections.emptyList(); // No path found
    }

    /**
     * Reconstructs the path from the goal back to the start using the cameFrom map.
     *
     * @param cameFrom The map containing the parent waypoints for each waypoint in the path.
     * @param current The current waypoint to start reconstructing the path.
     * @return The reconstructed list of waypoints from start to goal.
     */
    private List<Waypoint> reconstructPath(Map<Waypoint, Waypoint> cameFrom, Waypoint current) {
        List<Waypoint> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return simplifyPath(path);
    }

    /**
     * Simplifies the path by removing unnecessary waypoints.
     *
     * @param path The path to be simplified.
     * @return The simplified path.
     */
    private List<Waypoint> simplifyPath(List<Waypoint> path) {
        if (path.size() < 2) return path;

        List<Waypoint> simplifiedPath = new ArrayList<>();
        simplifiedPath.add(path.get(0));

        Waypoint prev = path.get(0);
        for (int i = 1; i < path.size() - 1; i++) {
            Waypoint next = path.get(i + 1);
            if (!lineOfSight(prev.x, prev.y, next.x, next.y)) {
                simplifiedPath.add(path.get(i));
                prev = path.get(i);
            }
        }

        simplifiedPath.add(path.get(path.size() - 1));
        return simplifiedPath;
    }

    /**
     * Checks if there is a clear line of sight between two points on the map.
     *
     * @param startX The X coordinate of the starting point.
     * @param startY The Y coordinate of the starting point.
     * @param endX The X coordinate of the ending point.
     * @param endY The Y coordinate of the ending point.
     * @return True if there is a clear line of sight; false otherwise.
     */
    private boolean lineOfSight(double startX, double startY, double endX, double endY) {
        int steps = (int) Math.max(50, 10 * Math.hypot(endX - startX, endY - startY));
        double buffer = 0.05;

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double x = startX + t * (endX - startX);
            double y = startY + t * (endY - startY);
            if (!isValidWaypointWithBuffer(x, y, buffer)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a waypoint at the given coordinates is valid (not in water).
     *
     * @param x The X coordinate of the waypoint.
     * @param y The Y coordinate of the waypoint.
     * @return True if the waypoint is valid; false otherwise.
     */
    private boolean isValidWaypoint(double x, double y) {
        MatrixMapArea area = mapManager.accessObject(x, y);
        return area != null && !(area instanceof Water);
    }

    /**
     * Checks if a waypoint at the given coordinates is valid with a buffer zone.
     *
     * @param x The X coordinate of the waypoint.
     * @param y The Y coordinate of the waypoint.
     * @param buffer The buffer distance to avoid obstacles.
     * @return True if the waypoint is valid with the buffer; false otherwise.
     */
    private boolean isValidWaypointWithBuffer(double x, double y, double buffer) {
        for (double dx = -buffer; dx <= buffer; dx += buffer / 2) {
            for (double dy = -buffer; dy <= buffer; dy += buffer / 2) {
                if (!isValidWaypoint(x + dx, y + dy)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Calculates the heuristic value (Manhattan distance plus node penalty) between two waypoints.
     *
     * @param a The first waypoint.
     * @param b The second waypoint.
     * @param nodesInPath The number of nodes visited in the current path.
     * @return The heuristic value between the two waypoints.
     */
    private double heuristic(Waypoint a, Waypoint b, int nodesInPath) {
        double dx = Math.abs(a.x - b.x);
        double dy = Math.abs(a.y - b.y);
        return dx + dy + (nodesInPath * penaltyFactor);
    }

    /**
     * Calculates the Euclidean distance between two waypoints.
     *
     * @param a The first waypoint.
     * @param b The second waypoint.
     * @return The Euclidean distance between the two waypoints.
     */
    private double distance(Waypoint a, Waypoint b) {
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    /**
     * Rounds a value to the specified precision.
     *
     * @param value The value to be rounded.
     * @return The rounded value.
     */
    private double roundToPrecision(double value) {
        return Math.round(value / EPSILON) * EPSILON;
    }
}
