package com.kentwentyfour.project12.bots.improvedbot.resources;

import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;

import java.util.*;

public class SecondAlgorithm {
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private MapManager mapManager;
    private final double[] stepSizes = {0.5, 0.25, 0.125, 0.1};
    private final double penaltyFactor = 1.0;
    private double EPSILON = 1e-6; // Small epsilon

    public SecondAlgorithm() {
        this.mapManager = referenceStore.getMapManager();
    }

    public List<Waypoint> generateWaypoints(double startX, double startY, double holeX, double holeY) {
        Waypoint start = new Waypoint(startX, startY);
        Waypoint goal = new Waypoint(holeX, holeY);

        List<Waypoint> path = new ArrayList<>();
        boolean pathFound = false;

        for (double stepSize : stepSizes) {
//            System.out.println("Trying step size: " + stepSize);

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
            } else {
                System.out.println("No path found with step size: " + stepSize + ". Reducing step size.");
            }

            // For step size 0.1, try again with EPSILON set to 0.1 if not already set
            if (stepSize == 0.1 && !pathFound) {
                EPSILON = 0.1;
//                System.out.println("Trying step size: " + stepSize + " with increased EPSILON");

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
                } else {
//                    System.out.println("No path found with step size: " + stepSize + " and increased EPSILON. Reducing step size.");
                }
            }
        }

        return pathFound ? path : Collections.emptyList();
    }

    private void generateAllPoints(Set<Waypoint> allPoints, double stepSize) {
        for (double x = -5; x <= 5 + EPSILON; x += stepSize) {
            for (double y = -5; y <= 5 + EPSILON; y += stepSize) {
                Waypoint point = new Waypoint(roundToPrecision(x), roundToPrecision(y));
                if (isValidWaypoint(point.x, point.y)) {
                    allPoints.add(point);
//                    System.out.println("Added waypoint: (" + point.x + ", " + point.y + ")");
                } else {
//                    System.out.println("Invalid waypoint: (" + point.x + ", " + point.y + ")");
                }
            }
        }
    }

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
//                        System.out.println("Connected neighbor: (" + neighbor.x + ", " + neighbor.y + ") to point: (" + point.x + ", " + point.y + ")");
                    }
                }
            }
            graph.put(point, neighbors);
        }
    }

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
//        System.out.println("Starting A* algorithm from (" + start.x + ", " + start.y + ")");

        while (!openSet.isEmpty()) {
            Waypoint current = openSet.poll();
//            System.out.println("Evaluating node: (" + current.x + ", " + current.y + ")");

            if (current.equals(goal)) {
//                System.out.println("Goal reached: (" + goal.x + ", " + goal.y + ")");
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
//                    System.out.println("Adding neighbor: (" + neighbor.x + ", " + neighbor.y + ") with fScore: " + tentativeFScore);
                    neighborConnected = true;
                }
            }

            if (!neighborConnected) {
//                System.out.println("No more neighbors can be connected from: (" + current.x + ", " + current.y + ")");
            }
        }

//        System.out.println("No path found from (" + start.x + ", " + start.y + ") to (" + goal.x + ", " + goal.y + ")");
        return Collections.emptyList();
    }

    private List<Waypoint> reconstructPath(Map<Waypoint, Waypoint> cameFrom, Waypoint current) {
        List<Waypoint> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return simplifyPath(path);
    }

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

    private boolean lineOfSight(double startX, double startY, double endX, double endY) {
        int steps = (int) Math.max(50, 10 * Math.hypot(endX - startX, endY - startY)); // Increase the steps for finer checks
        double buffer = 0.05; // Buffer distance to avoid getting too close to obstacles

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double x = startX + t * (endX - startX);
            double y = startY + t * (endY - startY);
            if (!isValidWaypointWithBuffer(x, y, buffer)) {
//                System.out.println("Line of sight blocked at: (" + x + ", " + y + ")");
                return false;
            }
        }
        return true;
    }

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


    private boolean isValidWaypoint(double x, double y) {
        MatrixMapArea area = mapManager.accessObject(x, y);
        return area != null && !(area instanceof Water);
    }

    private double heuristic(Waypoint a, Waypoint b, int nodesInPath) {
        double dx = Math.abs(a.x - b.x);
        double dy = Math.abs(a.y - b.y);
        return dx + dy + (nodesInPath * penaltyFactor); // Manhattan distance plus node penalty
    }

    private double distance(Waypoint a, Waypoint b) {
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    private double roundToPrecision(double value) {
        return Math.round(value / EPSILON) * EPSILON;
    }
}
