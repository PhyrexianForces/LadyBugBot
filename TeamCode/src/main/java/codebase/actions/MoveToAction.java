package codebase.actions;

import codebase.Constants;
import codebase.controllers.Controller;
import codebase.controllers.SigmoidController;
import codebase.geometry.Angles;
import codebase.geometry.FieldPosition;
import codebase.geometry.MovementVector;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.Localizer;

public class MoveToAction implements Action {
    private final MecanumDriver driver;
    private final Localizer localizer;

    private FieldPosition destination;

    /**
     * The speed to move horizontally/vertically or some combination of the two in inches/sec
     */
    private final double movementSpeedMultiplier;

    /**
     * The max rotational speed of the robot in radians/sec
     */
    private final double rotationalSpeedMultiplier;

    private final double maxDistanceError;
    private final double maxRotationalError;

    private final Controller xController;
    private final Controller yController;
    private final Controller directionController;

    public MoveToAction(MecanumDriver driver, Localizer localizer, FieldPosition destination, double movementSpeedMultiplier, double rotationalSpeedMultiplier, double maxDistanceError, double maxRotationalError) {
        this.driver = driver;
        this.localizer = localizer;
        this.destination = destination;
        this.movementSpeedMultiplier = movementSpeedMultiplier;
        this.rotationalSpeedMultiplier = rotationalSpeedMultiplier;
        this.maxDistanceError = maxDistanceError;
        this.maxRotationalError = maxRotationalError;

        this.xController = new SigmoidController(Constants.MOVEMENT_VELOCITY, Constants.MOVEMENT_STEEPNESS, () -> localizer.getCurrentPosition().x, () -> destination.x);
        this.yController = new SigmoidController(Constants.MOVEMENT_VELOCITY, Constants.MOVEMENT_STEEPNESS, () -> localizer.getCurrentPosition().y, () -> destination.y);
        this.directionController = new SigmoidController(
                Constants.ROTATION_VELOCITY,
                Constants.ROTATION_STEEPNESS,
                () -> Angles.angleDifference(localizer.getCurrentPosition().direction, destination.direction)
        );
    }

    @Override
    public void init() {}

    private boolean complete = false;

    @Override
    public void loop() {
        double distanceError = Math.sqrt(Math.pow(localizer.getCurrentPosition().x - destination.x, 2) + Math.pow(localizer.getCurrentPosition().y - destination.y, 2));
        double rotationalError = Angles.angleDifference(localizer.getCurrentPosition().direction, destination.direction);

        if ((distanceError <= maxDistanceError) && (rotationalError <= maxRotationalError)) {
            driver.stop();
            complete = true;
            return;
        }

        double velocityX = xController.getPower() * movementSpeedMultiplier;
        double velocityY = yController.getPower() * movementSpeedMultiplier;
        double velocityRotational = -directionController.getPower() * rotationalSpeedMultiplier;

        MovementVector vector = new MovementVector(
                velocityX,
                velocityY,
                velocityRotational
        );

        driver.setAbsoluteVelocity(localizer.getCurrentPosition(), vector);
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    public void setDestination(FieldPosition destination) {
        this.destination = destination;
    }

    public double getErrorY() {
        return yController.getError();
    }

    public double getErrorX() {
        return xController.getError();
    }

    public double getErrorDirection() {
        return directionController.getError();
    }
}
