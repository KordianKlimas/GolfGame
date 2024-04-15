package com.example.gui.PhysicsEnginePackage;

public class GolfBall  {
     private Vector4d stateVector = new Vector4d(0,0,0,0);
     private double mass;

    GolfBall(double position_x,double position_y,double position_z,double mass){
        stateVector.setPositionX(position_x);
        stateVector.setPositionY(position_y);
        this.mass = mass;
    }
    //setters
    public void setStateVector(Vector4d stateVector){
        this.stateVector = stateVector;
    }
    //getters
    public Vector4d getStateVector(){
        return stateVector;
    }
    public double getMass(){
        return mass;
    }
}
