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
        this.obstacles = referenceStore.getObstacles();
    }

    public List<Node> findPath(MapManager mapManager, int startX, int startY, int endX, int endY, int range) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.totalCost));
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(startX, startY);
        Node endNode = new Node(endX, endY);
        startNode.currentCost = 0;
        startNode.expectedCost = heuristic(startNode, endNode);
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

                    double tentativeCurrentCost = currentNode.currentCost + Math.sqrt(dx * dx + dy * dy);
                    Node neighbor = new Node(newX, newY);

                    if (!openList.contains(neighbor) || tentativeCurrentCost < neighbor.currentCost) {
                        neighbor.parent = currentNode;
                        neighbor.currentCost = tentativeCurrentCost;
                        neighbor.expectedCost = heuristic(neighbor, endNode);
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
        if (x < 0 || y < 0 || x >= mapManager.WIDTH || y >= mapManager.HEIGHT) {
            return false;
        }

        double[] coords = mapManager.matrixToCoordinates(x, y);
        MatrixMapArea mapArea = mapManager.accessObject(coords[0], coords[1]);
        if (mapArea == null || mapArea instanceof Water) {
            return false;
        }

        for (MovableObjects obstacle : obstacles) {
            int[] obstacleMatrixCoords = mapManager.coordinatesToMatrix(obstacle.getX(), obstacle.getY());
            double distance = Math.sqrt(Math.pow(obstacleMatrixCoords[0] - x, 2) + Math.pow(obstacleMatrixCoords[1] - y, 2));
            if (distance < obstacle.getDistanceFromOrigin()) {
                return false;
            }
        }

        return true;
    }

    double heuristic(Node a, Node b) {
        return Math.sqrt(Math.pow(a.matrixX - b.matrixX, 2) + Math.pow(a.matrixY - b.matrixY, 2)); // Euclidean distance}
    }
    public void printObstacleList() {
        System.out.println("List of Obstacles:");
        for (MovableObjects obstacle : obstacles) {
            System.out.println("Obstacle at (" + obstacle.getX() + ", " + obstacle.getY() + ") with radius " + obstacle.getDistanceFromOrigin());
        }
    }
}

