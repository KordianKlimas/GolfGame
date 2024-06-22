package com.kentwentyfour.project12.Bots.Algorithms;

import com.kentwentyfour.project12.ReferenceStore;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.Water;
import com.kentwentyfour.project12.gameobjects.movableobjects.MovableObjects;
import com.kentwentyfour.project12.gameobjects.movableobjects.Tree;
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

    public List<Node> findPath(MapManager mapManager, int startX, int startY, int endX, int endY) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.totalCost));
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(startX, startY);
        Node endNode = new Node(endX, endY);
        startNode.currentCost = 0;
        startNode.expectedCost = 0;
        startNode.totalCost = 0;
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            if (currentNode.equals(endNode)) {
                List<Node> path = reconstructPath(currentNode);
                //checkDirectionChanges(path); // Check direction changes
                printPath(path);
                return path;
            }
            closedList.add(currentNode);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    //  checks all nodes around current node
                    if (dx == 0 && dy == 0) continue; // same node
                    int newX = currentNode.matrixX + dx;  // lef/x node
                    int newY = currentNode.matrixY + dy;  // top/bottom node

                    if (!isValidPosition(newX, newY, mapManager) || closedList.contains(new Node(newX, newY))) {
                        continue;
                    }
                   //if(!isAdjacentToObstacle(currentNode)){
                   //    continue;
                   //}
                    double tentativeCurrentCost = currentNode.currentCost + Math.sqrt(dx * dx + dy * dy);
                    Node neighbor = new Node(newX, newY);

                    if (!openList.contains(neighbor) || tentativeCurrentCost < neighbor.currentCost) {
                        neighbor.parent = currentNode;
                        neighbor.currentCost = tentativeCurrentCost;
                        neighbor.expectedCost = heuristic(neighbor, endNode,endNode);
                        neighbor.totalCost = neighbor.currentCost + neighbor.expectedCost;
                        // Log direction change
                        if (currentNode.parent != null) {
                            int prevDX = currentNode.matrixX - currentNode.parent.matrixX;
                            int prevDY = currentNode.matrixY - currentNode.parent.matrixY;
                            if (prevDX != dx || prevDY != dy) {
                               //System.out.println("Direction change at (" + newX + ", " + newY + ")");
                               //double[] arr =  mapManager.matrixToCoordinates(dx,dy) ;
                               //mapManager.addMovableObjectToMap(new Tree(arr[0],arr[1],0.3));
                            }
                        }
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

    double heuristic(Node a, Node b,Node c) {

        return 1;//Math.sqrt(Math.pow(a.matrixX - b.matrixX, 2) + Math.pow(a.matrixY - b.matrixY, 2)); // Euclidean distance}
    }
    boolean isAdjacentToObstacle(Node node ){
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // same node
                int newX = node.matrixX + dx;  // lef/x node
                int newY = node.matrixY + dy;  // top/bottom node
                double[] coords = mapManager.matrixToCoordinates(newX,newY);
                MatrixMapArea mapArea = mapManager.accessObject(coords[0], coords[1]);
                if (mapArea == null || mapArea instanceof Water) {
                    return true;
                }
            }
        }
        return false;
    }

    public void printObstacleList() {
        System.out.println("List of Obstacles:");
        for (MovableObjects obstacle : obstacles) {
            System.out.println("Obstacle at (" + obstacle.getX() + ", " + obstacle.getY() + ") with radius " + obstacle.getDistanceFromOrigin());
        }
    }

    public void checkDirectionChanges(List<Node> path) {
        if (path.size() < 3) {
            System.out.println("Path is too short to have direction changes.");
            return;
        }

        Node previousNode = path.get(0);
        Node currentNode = path.get(1);

        for (int i = 2; i < path.size(); i++) {
            Node nextNode = path.get(i);

            // Calculate directions from previousNode to currentNode and currentNode to nextNode
            String direction1 = calculateDirection(previousNode, currentNode);
            String direction2 = calculateDirection(currentNode, nextNode);

            // Compare directions and log changes if they differ
            if (!direction1.equals(direction2)) {
                double[] prevCoords = mapManager.matrixToCoordinates(previousNode.matrixX, previousNode.matrixY);
                double[] curCoords = mapManager.matrixToCoordinates(currentNode.matrixX, currentNode.matrixY);
                double[] newCoords = mapManager.matrixToCoordinates(nextNode.matrixX, nextNode.matrixY);

                System.out.println("Move from (" + prevCoords[0] + ", " + prevCoords[1] + ") to (" + curCoords[0] + ", " + curCoords[1] + ") - Direction: " + direction1);
                System.out.println("Move from (" + curCoords[0] + ", " + curCoords[1] + ") to (" + newCoords[0] + ", " + newCoords[1] + ") - Direction: " + direction2);

                // Optionally, add an object to the map based on currentNode coordinates
                double[] currentNodeCoords = mapManager.matrixToCoordinates(currentNode.matrixX, currentNode.matrixY);
                mapManager.addMovableObjectToMap(new Tree(currentNodeCoords[0], currentNodeCoords[1], 0.05));
            }

            // Update nodes for the next iteration
            previousNode = currentNode;
            currentNode = nextNode;
        }
    }

    private String calculateDirection(Node fromNode, Node toNode) {
        int dx = toNode.matrixX - fromNode.matrixX;
        int dy = toNode.matrixY - fromNode.matrixY;

        if (dx == 0 && dy > 0) return "Up";
        else if (dx == 0 && dy < 0) return "Down";
        else if (dx > 0 && dy == 0) return "Right";
        else if (dx < 0 && dy == 0) return "Left";
        else if (dx > 0 && dy > 0) return "Up-Right";
        else if (dx > 0 && dy < 0) return "Down-Right";
        else if (dx < 0 && dy > 0) return "Up-Left";
        else if (dx < 0 && dy < 0) return "Down-Left";
        else return "Unknown";
    }


    private void printPath(List<Node> path) {
        System.out.println("Path found:");
        for (Node node : path) {
            double[] coordinates = mapManager.matrixToCoordinates(node.matrixX, node.matrixY);

           // System.out.println("Node at matrix position (" + node.matrixX + ", " + node.matrixY + ") - coordinates: (" + coordinates[0] + ", " + coordinates[1] + ")");
        }
        checkDirectionChanges(path);

    }


}

