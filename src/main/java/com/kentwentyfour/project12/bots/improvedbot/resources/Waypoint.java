package com.kentwentyfour.project12.bots.improvedbot.resources;

import java.util.Objects;

public class Waypoint {
    public double x, y;
    public Waypoint cameFrom;
    public double gScore;
    public double hScore;
    public double fScore;

    public Waypoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.gScore = Double.POSITIVE_INFINITY;
        this.hScore = 0;
        this.fScore = Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Waypoint that = (Waypoint) obj;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%.1f, %.1f)", x, y);
    }
}