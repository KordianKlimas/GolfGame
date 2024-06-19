package com.kentwentyfour.project12.Bots.Algorithms;

import java.util.Objects;

public class Node {
    public int matrixX;
    public int matrixY;
    public double coordX;
    public double coordY;
    double currentCost, expectedCost, totalCost;
    Node parent;

    // Constructor for matrix coordinates
    public Node(int matrixX, int matrixY) {
        this.matrixX = matrixX;
        this.matrixY = matrixY;
    }

    // Constructor for coordinate values
    public Node(int matrixX, int matrixY, double coordX, double coordY) {
        this.matrixX = matrixX;
        this.matrixY = matrixY;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return matrixX == node.matrixX && matrixY == node.matrixY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matrixX, matrixY);
    }

    @Override
    public String toString() {
        return "Node{" +
                "coordX=" + coordX +
                ", coordY=" + coordY +
                '}';
    }
}