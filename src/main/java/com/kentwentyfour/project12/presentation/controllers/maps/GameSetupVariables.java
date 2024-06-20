package com.kentwentyfour.project12.presentation.controllers.maps;

import com.kentwentyfour.project12.gameobjects.matrixmapobjects.MatrixMapArea;
import com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles.ObstacleArea;
import com.kentwentyfour.project12.gameobjects.movableobjects.MovableObjects;
import com.kentwentyfour.project12.gameobjects.movableobjects.ReboundingObstacle;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates predefined map initial conditions
 */
public  class GameSetupVariables {
    private double startX;
    private double startY;
    private double targetX;
    private double targetY;
    private double ballRadius;
    private double targetRadius;
    private double staticFrictionSand;
    private double kineticFrictionSand;
    private double staticFrictionGrass;
    private double kineticFrictionGrass;
    private String formula;
    private List<ReboundingObstacle> reboundingObstacles = new ArrayList<>();
    private List<ObstacleArea> areaObstacles = new ArrayList<>();


    /**
     * Creates map object to store game/map settings
     * @param startX
     * @param startY
     * @param targetX
     * @param targetY
     * @param ballRadius
     * @param targetRadius
     * @param staticFrictionSand
     * @param kineticFrictionSand
     * @param staticFrictionGrass
     * @param kineticFrictionGrass
     * @param formula
     */
    public GameSetupVariables(double startX, double startY, double targetX, double targetY, double ballRadius, double targetRadius,
                              double staticFrictionSand, double kineticFrictionSand, double staticFrictionGrass,
                              double kineticFrictionGrass, String formula) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.ballRadius = ballRadius;
        this.targetRadius = targetRadius;
        this.staticFrictionSand = staticFrictionSand;
        this.kineticFrictionSand = kineticFrictionSand;
        this.staticFrictionGrass = staticFrictionGrass;
        this.kineticFrictionGrass = kineticFrictionGrass;
        this.formula = formula;
    }
    /**
     * Creates map object to store game/map settings
     * Constructor with areaObstacles only
     */

    public GameSetupVariables(double startX, double startY, double targetX, double targetY, double ballRadius, double targetRadius,
                              double staticFrictionSand, double kineticFrictionSand, double staticFrictionGrass,
                              double kineticFrictionGrass, String formula, List<ObstacleArea> areaObstacles) {
        this(startX, startY, targetX, targetY, ballRadius, targetRadius, staticFrictionSand, kineticFrictionSand,
                staticFrictionGrass, kineticFrictionGrass, formula);
        this.areaObstacles = areaObstacles;
    }
    /**
     * Creates map object to store game/map settings
     * Constructor with reboundingObstacles only
     */
    public GameSetupVariables(double startX, double startY, double targetX, double targetY, double ballRadius, double targetRadius,
                              double staticFrictionSand, double kineticFrictionSand, double staticFrictionGrass,
                              double kineticFrictionGrass, List<ReboundingObstacle> reboundingObstacles, String formula) {
        this(startX, startY, targetX, targetY, ballRadius, targetRadius, staticFrictionSand, kineticFrictionSand,
                staticFrictionGrass, kineticFrictionGrass, formula);
        this.reboundingObstacles = reboundingObstacles;
    }
    /**
     * Creates map object to store game/map settings
     * Constructor with both areaObstacles and reboundingObstacles
     */
    public GameSetupVariables(double startX, double startY, double targetX, double targetY, double ballRadius, double targetRadius,
                              double staticFrictionSand, double kineticFrictionSand, double staticFrictionGrass,
                              double kineticFrictionGrass, String formula, List<ObstacleArea> areaObstacles,
                              List<ReboundingObstacle> reboundingObstacles) {
        this(startX, startY, targetX, targetY, ballRadius, targetRadius, staticFrictionSand, kineticFrictionSand,
                staticFrictionGrass, kineticFrictionGrass, formula);
        this.areaObstacles = areaObstacles;
        this.reboundingObstacles = reboundingObstacles;
    }


    // Getters
    public double getStartX() {
        return startX;
    }
    public double getStartY() {
        return startY;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getBallRadius() {
        return ballRadius;
    }

    public double getTargetRadius() {
        return targetRadius;
    }

    public double getStaticFrictionSand() {
        return staticFrictionSand;
    }

    public double getKineticFrictionSand() {
        return kineticFrictionSand;
    }

    public double getStaticFrictionGrass() {
        return staticFrictionGrass;
    }

    public double getKineticFrictionGrass() {
        return kineticFrictionGrass;
    }

    public String getFormula() {
        return formula;
    }

    public List<ObstacleArea> getAreaObstacles() {
        return areaObstacles;
    }

    public List<ReboundingObstacle> getReboundingObstacles() {
        return reboundingObstacles;
    }
}