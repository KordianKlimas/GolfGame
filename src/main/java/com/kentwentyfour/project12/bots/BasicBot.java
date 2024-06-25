package com.kentwentyfour.project12.bots;


import com.kentwentyfour.project12.gameobjects.movableobjects.GolfBall;
import com.kentwentyfour.project12.physicsengine.CoordinatesPath;
import com.kentwentyfour.project12.physicsengine.PhysicsEngine;
import com.kentwentyfour.project12.ReferenceStore;


public class BasicBot implements BotPlayer {
    private static final double thresholdDistance = 0.1;
    private ReferenceStore referenceStore = ReferenceStore.getInstance();
    private PhysicsEngine physicsEngine = referenceStore.getPhysicsEngine();
    private long computationTime;
    private int numberOfTurns = 1;
    private double bestPathVx = 0;
    private double bestPathVy = 0;
    private double dampeningFactor = 0.5; // Introducing a dampening factor

    public BasicBot() {}

    public CoordinatesPath calculatePath(GolfBall golfBall, double targetX, double targetY) {
        long startTime = System.nanoTime();
        CoordinatesPath path = null;

        double pointX = targetX; // X coordinate of the hole
        double pointY = targetY; // Y coordinate of the hole

        double ballX = golfBall.getX(); // X coordinate of the ball
        double ballY = golfBall.getY(); // Y coordinate of the ball

        double velocityScalar = 1.0;

        // Calculate the difference vector from ball to hole
        double diffX = pointX - ballX;
        double diffY = pointY - ballY;

        // Calculate the distance between the ball and the hole
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        // Normalize the difference vector to get the unit direction vector
        double directionX = diffX / distance;
        double directionY = diffY / distance;

        // Scale the direction vector to the maximum velocity
        double velocityX = directionX * velocityScalar;
        double velocityY = directionY * velocityScalar;

        // Check if it's not exceeding max speed 5m/s
        while (Math.abs(velocityX) > 5 || Math.abs(velocityY) > 5) {
            velocityScalar -= 0.01;
            velocityX = directionX * velocityScalar;
            velocityY = directionY * velocityScalar;
        }

        // Loop for finding the best path
        int maxIteration = 50;
        double bestDistance = Double.POSITIVE_INFINITY;

        while (true) {
            path = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY, 0.02, 1);

            // Returns path if hole scored
            if (path.getStoppingCondition().equals("ball_in_the_hole")) {
                break;
            } else if (path.getStoppingCondition().equals("obstacle_hit")) {
                // Handle obstacle hit condition
            }

            double[][] arr = path.getPath();
            double lastX = arr[0][arr[0].length - 2];
            double lastY = arr[1][arr[0].length - 2];
            double distanceNew = Math.sqrt(Math.pow(lastX - pointX, 2) + Math.pow(lastY - pointY, 2));

            if (Math.abs(distanceNew) < .01) {
                break;
            } else {
                if (bestDistance > Math.abs(distanceNew)) {
                    bestPathVx = velocityX;
                    bestPathVy = velocityY;
                }

                // Scales the vectors to make them lead the ball closer to the target
                double startTargetDistance = Math.sqrt(Math.pow(pointX - ballX, 2) + Math.pow(pointY - ballY, 2));
                double startBallDistance = Math.sqrt(Math.pow(lastX - ballX, 2) + Math.pow(lastY - ballY, 2));

                if (startBallDistance > startTargetDistance) {
                    System.out.println("ball after the target");
                    velocityScalar -= dampeningFactor * (startBallDistance - startTargetDistance) / startBallDistance;
                } else {
                    System.out.println("ball before the target");
                    velocityScalar += dampeningFactor * (startTargetDistance - startBallDistance) / startTargetDistance;
                }

                System.err.println("Scalar : " + velocityScalar);

                velocityX = directionX * velocityScalar;
                velocityY = directionY * velocityScalar;

                System.out.println("Velocity X scaled: " + velocityX);
                System.out.println("Velocity Y scaled: " + velocityY);

                while (Math.abs(velocityX) > 5 || Math.abs(velocityY) > 5) {
                    double scaleChange = 0.01;
                    velocityX -= velocityX * scaleChange;
                    velocityY -= velocityY * scaleChange;
                }

                System.out.println("Velocity X adjusted: " + velocityX);
                System.out.println("Velocity Y adjusted: " + velocityY);
            }

            maxIteration--;
            if (maxIteration < 0) {
                velocityX = bestPathVx;
                velocityY = bestPathVy;
                System.err.println("Using best path due to iteration limit " + velocityX + " " + velocityY);
                break;
            }
        }

        path = physicsEngine.calculateCoordinatePath(golfBall, velocityX, velocityY, 0.01, 1);

        long endTime = System.nanoTime();
        computationTime = endTime - startTime;

        double[][] arrOfCoordinates = path.getPath();
        System.err.println("Expected last coordinates: " + pointX + " " + pointY);
        System.err.println("Last coordinates: " + arrOfCoordinates[0][arrOfCoordinates[0].length - 1] + " " + arrOfCoordinates[1][arrOfCoordinates[1].length - 1]);

        return path;
    }

    @Override
    public long getComputationTime() {
        return computationTime;
    }

    @Override
    public String getName() {
        return "BasicBot";
    }

    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }
}