package com.example.gui.PhysicsEnginePackage;
import java.util.ArrayList;
import java.util.List;
/**
 * 4-th dimensional state vector:
 * <ol>
 *     <li> x(t).</li>
 *     <li> y(t)}</li>
 *     <li> Vx(t)</li>
 *     <li> Vy(t)</li>
 * </ol>
 * @author Kordian
 */
public class Vector4d {
    private double x;
    private double y;
    private double Vx;
    private double Vy;
    /**
     * Constructs a 4d Vector  with the specified components.
     *
     * @param x  the x-coordinate of the position component
     * @param y  the y-coordinate of the position component
     * @param Vx the x-component of the velocity
     * @param Vy the y-component of the velocity
     */
    Vector4d(double x,double y, double Vx, double Vy){
        this.x = x;
        this.y = y;
        this.Vx = x;
        this.Vy = Vy;
    }
    //Setters
    /**
     * Sets the x-coordinate of the position component.
     *
     * @param x the new x-coordinate of the position
     */
    public void setPositionX(double x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the position component.
     *
     * @param y the new y-coordinate of the position
     */
    public void setPositionY(double y) {
        this.y = y;
    }

    /**
     * Sets the x-component of the velocity.
     *
     * @param Vx the new x-component of the velocity
     */
    public void setVelocityX(double Vx) {
        this.Vx = Vx;
    }

    /**
     * Sets the y-component of the velocity.
     *
     * @param Vy the new y-component of the velocity
     */
    public void setVelocityY(double Vy) {
        this.Vy = Vy;
    }
    // Getters

    /**
     * Returns the x-coordinate of the position component.
     *
     * @return the x-coordinate of the position
     */
    public double getPositionX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the position component.
     *
     * @return the y-coordinate of the position
     */
    public double getPositionY() {
        return y;
    }

    /**
     * Returns the x-component of the velocity.
     *
     * @return the x-component of the velocity
     */
    public double getVelocityX() {
        return Vx;
    }

    /**
     * Returns the y-component of the velocity.
     *
     * @return the y-component of the velocity
     */
    public double getVelocityY() {
        return Vy;
    }

    /**
     * Returns the vector as a list containing its components.
     *
     * @return a list containing the vector components in the order [x, y, Vx, Vy]
     */
    public List<Double> getVector() {
        List<Double> vector = new ArrayList<>();
        vector.add(x);
        vector.add(y);
        vector.add(Vx);
        vector.add(Vy);
        return vector;
    }

}
