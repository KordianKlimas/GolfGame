package com.kentwentyfour.project12.Bots.Algorithms;

import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;

import java.util.*;

public class AStarAlgorithm {
    private PhysicsEngine physicsEngine;
    private MapManager mapManager;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();

    public AStarAlgorithm() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.mapManager = referenceStore.getMapManager();
    }

    /**
     * Finds the path from start to end coordinates.
     *
     * @param mapManager  Map manager for coordinate transformations.
     * @param startCoordX Start X in coordinates.
     * @param startCoordY Start Y in coordinates.
     * @param endCoordX   End X in coordinates.
     * @param endCoordY   End Y in coordinates.
     * @param range       Search range.
     * @return Path as a list of nodes with coordinate values.
     */
    public List<Node> findPath(MapManager mapManager, double startCoordX, double startCoordY, double endCoordX, double endCoordY, int range) {
        int[] startMatrix = mapManager.coordinatesToMatrix(startCoordX, startCoordY);
        int[] endMatrix = mapManager.coordinatesToMatrix(endCoordX, endCoordY);

        int startX = startMatrix[0];
        int startY = startMatrix[1];
        int endX = endMatrix[0];
        int endY = endMatrix[1];

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.totalCost));
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(startX, startY, startCoordX, startCoordY);
        Node endNode = new Node(endX, endY, endCoordX, endCoordY);
        startNode.currentCost = 0;
        startNode.expectedCost = heuristic(startCoordX, startCoordY, endCoordX, endCoordY);
        startNode.totalCost = startNode.currentCost + startNode.expectedCost;
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            if (currentNode.equals(endNode)) {
                return reconstructPath(currentNode);
            }
            closedList.add(currentNode);

            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int newX = currentNode.matrixX + dx;
                    int newY = currentNode.matrixY + dy;

                    if (!isValidPosition(newX, newY, mapManager) || closedList.contains(new Node(newX, newY))) {
                        continue;
                    }

                    double[] newCoords = mapManager.matrixToCoordinates(newX, newY);
                    double tentativeCurrentCost = currentNode.currentCost + Math.sqrt(dx * dx + dy * dy);
                    Node neighbor = new Node(newX, newY, newCoords[0], newCoords[1]);

                    if (!openList.contains(neighbor) || tentativeCurrentCost < neighbor.currentCost) {
                        neighbor.parent = currentNode;
                        neighbor.currentCost = tentativeCurrentCost;
                        neighbor.expectedCost = heuristic(newCoords[0], newCoords[1], endCoordX, endCoordY);
                        neighbor.totalCost = neighbor.currentCost + neighbor.expectedCost;
                        if (!openList.contains(neighbor)) {
                            openList.add(neighbor);
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Reconstructs the path from the end node to the start node, converting matrix coordinates to actual coordinates.
     *
     * @param currentNode The end node.
     * @return The path from start to end with nodes having coordinate values.
     */
    private List<Node> reconstructPath(Node currentNode) {
        List<Node> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Checks if the position is valid.
     *
     * @param x          X position in matrix units.
     * @param y          Y position in matrix units.
     * @param mapManager The map manager.
     * @return True if the position is valid, false otherwise.
     */
    private boolean isValidPosition(int x, int y, MapManager mapManager) {
        if (x < 0 || y < 0 || x >= mapManager.WIDTH || y >= mapManager.HEIGHT) {
            return false;
        }

        double[] coords = mapManager.matrixToCoordinates(x, y);
        MatrixMapArea mapArea = mapManager.accessObject(coords[0], coords[1]);
        if (mapArea == null || mapArea instanceof Water) {
            return false;
        }

        // Check surrounding area within 5 blocks to ensure that inaccuracy of the shot doesn't lead to invalid shot
        //needs to be modified further
        int range = 5;
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int newX = x + dx;
                int newY = y + dy;
                if (newX < 0 || newY < 0 || newX >= mapManager.WIDTH || newY >= mapManager.HEIGHT) {
                    continue;
                }

                double[] surroundingCoords = mapManager.matrixToCoordinates(newX, newY);
                MatrixMapArea surroundingMapArea = mapManager.accessObject(surroundingCoords[0], surroundingCoords[1]);
                if (surroundingMapArea instanceof Water) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Calculates the heuristic (Euclidean distance) between two coordinate points.
     *
     * @param x1 First point x-coordinate.
     * @param y1 First point y-coordinate.
     * @param x2 Second point x-coordinate.
     * @param y2 Second point y-coordinate.
     * @return The heuristic value.
     */
    private double heuristic(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}