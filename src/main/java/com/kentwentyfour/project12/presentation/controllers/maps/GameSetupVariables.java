package com.kentwentyfour.project12.presentation.controllers.maps;

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
}