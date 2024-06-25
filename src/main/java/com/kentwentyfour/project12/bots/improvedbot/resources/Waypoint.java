package com.kentwentyfour.project12.bots.improvedbot.resources;

import java.util.Objects;
/**
 * The Waypoint class represents a point in a pathfinding algorithm.
 * It holds coordinates, scores for pathfinding, and a reference to the previous waypoint in the path.
 */
public class Waypoint {
    public double x, y;
    public Waypoint cameFrom;
    public double gScore;
    public double hScore;
    public double fScore;
    /**
     * Constructs a Waypoint with the specified coordinates.
     * Initializes the gScore to positive infinity and the hScore and fScore to zero.
     *
     * @param x the X-coordinate of the waypoint.
     * @param y the Y-coordinate of the waypoint.
     */

    public Waypoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.gScore = Double.POSITIVE_INFINITY;
        this.hScore = 0;
        this.fScore = Double.POSITIVE_INFINITY;
    }
    /**
     * Indicates whether some other object is "equal to" this one.
     * Two Waypoint objects are considered equal if they have the same coordinates.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Waypoint that = (Waypoint) obj;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    /**
     * Returns a string representation of the Waypoint.
     * The string representation consists of the coordinates formatted to one decimal place.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return String.format("(%.1f, %.1f)", x, y);
    }
}