package com.kentwentyfour.project12.Bots.Algorithms;

import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;
import com.kentwentyfour.project12.gameobjects.movableobjects.MovableObjects;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;

import java.util.*;

public class AStarAlgorithm {
    private PhysicsEngine physicsEngine;
    private MapManager mapManager;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private List<MovableObjects> obstacles;

    public AStarAlgorithm() {
        this.physicsEngine = referenceStore.getPhysicsEngine();
        this.mapManager = referenceStore.getMapManager();
        this.obstacles = referenceStore.getObstacles(); // Retrieve the obstacles from the reference store
    }

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

    private List<Node> reconstructPath(Node currentNode) {
        List<Node> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private boolean isValidPosition(int x, int y, MapManager mapManager) {
        int range = 5;
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int newX = x + dx;
                int newY = y + dy;
                if (newX < 0 || newY < 0 || newX >= mapManager.WIDTH || newY >= mapManager.HEIGHT) {
                    continue;
                }

                double[] coords = mapManager.matrixToCoordinates(newX, newY);
                MatrixMapArea mapArea = mapManager.accessObject(coords[0], coords[1]);
                if (mapArea == null || mapArea instanceof Water) {
                    return false;
                }

                for (MovableObjects obstacle : obstacles) {
                    double distance = Math.sqrt(Math.pow(obstacle.getX() - coords[0], 2) + Math.pow(obstacle.getY() - coords[1], 2));
                    if (distance < obstacle.getDistanceFromOrigin()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private double heuristic(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public void printObstacleList() {
        System.out.println("List of Obstacles:");
        for (MovableObjects obstacle : obstacles) {
            System.out.println("Obstacle at (" + obstacle.getX() + ", " + obstacle.getY() + ") with radius " + obstacle.getDistanceFromOrigin());
        }
    }
}
