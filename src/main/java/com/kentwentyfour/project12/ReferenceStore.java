package com.kentwentyfour.project12;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Grass;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.areatypes.Sand;
import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.gameobjects.movableobjects.Hole;
import com.kentwentyfour.project12.gameobjects.MapManager;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * (singleton pattern)
 *
 * **Still need to be implemented in most areas of app**
 *
 * Stores all often used references
 * - PhysicEngine
 * - MapManager
 * - Hole
 * - GolfBalls (as List)
 * - AreaTypeFactory ( to be implemented )
 */
public  class ReferenceStore {
    // Singleton instance
    private static ReferenceStore instance;

    //  Game Objects variables
    private ArrayList<GolfBall> listOfGolfBalls = new ArrayList<>();
    private Hole hole;
    private MapManager mapManager;
    private PhysicsEngine physicsEngine;
    private String courseProfileFormula;

    // Private constructor to prevent instantiation
    private ReferenceStore() {
    }

    // Public method to provide access to the singleton instance
    public static ReferenceStore getInstance() {
        if (instance == null) {
            synchronized (ReferenceStore.class) {
                if (instance == null) {
                    instance = new ReferenceStore();
                }
            }
        }
        return instance;
    }

    // Game Objects - AreaTypes
    /**
     * Sets the kinetic and static frictions for the specified AreaType object.
     * If the object is an instance of Grass or Sand, their respective friction
     * values are updated accordingly.
     *
     * @param className             The AreaType object for which frictions need to be set.
     * @param kinetic_friction The new kinetic friction value.
     * @param static_friction  The new static friction value.
     */
    public void setFrictionsAreaType(String className,double kinetic_friction,double static_friction){
        if (className.equals("Grass")) {
            Grass.kinetic_Friction= kinetic_friction;
            Grass.static_Friction= static_friction;
        }
        else if (className.equals("Sand")) {
            Sand.kinetic_Friction= kinetic_friction;
            Sand.static_Friction= static_friction;
        }
    }
    //  Game Objects
    /**
     * Sets reference for Hole
     * @param hole
     */
    public void setHoleReference(Hole hole){
        this.hole = hole;
    }

    /**
     *  Returns hole reference
     * @return Hole
     */
    public  Hole getHole(){
        return this.hole;
    }

    /**
     * Sets reference for GolfBall
     * @param golfball
     */
    public  void addGolfballReference(GolfBall golfball){
        this.listOfGolfBalls.add(golfball);
    }

    /**
     * Returns List of used Golfballs
     * @return List<GolfBall>
     */
    public  List<GolfBall> getGolfballList(){
        return this.listOfGolfBalls;
    }
    /**
     * Sets reference for MapManager
     * @param mapManager
     */
    public void setMapManagerReference(MapManager mapManager) {
        this.mapManager = mapManager;
    }
    /**
     * Returns reference for MapManager
     * @return Mapmanager
     */
    public MapManager getMapManager(){
        return this.mapManager;
    }
    /**
     * Sets reference for physicsEngine
     * @param physicsEngine
     */
    public void setPhysicsEngine(PhysicsEngine physicsEngine){
        this.physicsEngine = physicsEngine;
    }
    /**
     * Returns reference for PhysicsEngine
     * @return PhysicsEngine
     */
    public PhysicsEngine getPhysicsEngine() { return this.physicsEngine; }
    /**
     * Sets reference for courseProfileFormula
     * @param courseProfileFormula
     */
    public void setCourseProfileFormula(String courseProfileFormula) { this.courseProfileFormula = courseProfileFormula; }
    /**
     * Returns reference for formula
     * @return courseProfileFormula
     */
    public String getCourseProfileFormula() {
        return this.courseProfileFormula;
    }
    public double getFrictionCoefficient() {
        // Assuming we have a method to get the current surface type
        String surfaceType = getCourseProfileFormula();

        // Default friction coefficients for different surfaces
        switch (surfaceType) {
            case "Grass":
                return Grass.kinetic_Friction;
            case "Sand":
                return Sand.kinetic_Friction;
            default:
                return 0.1;
        }
    }
}
