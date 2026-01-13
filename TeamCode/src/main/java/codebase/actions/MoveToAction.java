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

    private final FieldPosition destination;

    /**
     * The speed to move horizontally/vertically or some combination of the two in inches/sec
     */
    private final double movementSpeed;

    /**
     * The max rotational speed of the robot in radians/sec
     */
    private final double rotationalSpeed;

    private final double maxDistanceError;
    private final double maxRotationalError;

    private final Controller xController;
    private final Controller yController;
    private final Controller directionController;

    public MoveToAction(MecanumDriver driver, Localizer localizer, FieldPosition destination, double movementSpeed, double rotationalSpeed, double maxDistanceError, double maxRotationalError) {
        this.driver = driver;
        this.localizer = localizer;
        this.destination = destination;
        this.movementSpeed = movementSpeed;
        this.rotationalSpeed = rotationalSpeed;
        this.maxDistanceError = maxDistanceError;
        this.maxRotationalError = maxRotationalError;

        this.xController = new SigmoidController(Constants.MOVEMENT_POWER, Constants.MOVEMENT_STEEPNESS, () -> localizer.getCurrentPosition().x, () -> destination.x);
        this.yController = new SigmoidController(Constants.MOVEMENT_POWER, Constants.MOVEMENT_STEEPNESS, () -> localizer.getCurrentPosition().y, () -> destination.y);
        this.directionController = new SigmoidController(
                Constants.ROTATION_POWER,
                Constants.ROTATION_STEEPNESS,
                () -> Angles.angleDifference(localizer.getCurrentPosition().direction, destination.direction)
        );
    }

    @Override
    public void init() {}

    @Override
    public void loop() {
        double powerX = xController.getPower() * movementSpeed;
        double powerY = yController.getPower() * movementSpeed;
        double powerRotational = directionController.getPower() * rotationalSpeed;

        MovementVector vector = new MovementVector(
                powerX,
                powerY,
                powerRotational
        );

        driver.setAbsolutePower(localizer.getCurrentPosition(), vector);
    }

    @Override
    public boolean isComplete() {
        double distanceError = Math.sqrt(Math.pow(localizer.getCurrentPosition().x - destination.x, 2) + Math.pow(localizer.getCurrentPosition().y - destination.y, 2));
        double rotationalError = Angles.angleDifference(localizer.getCurrentPosition().direction, destination.direction);

        if ((distanceError <= maxDistanceError) && (rotationalError <= maxRotationalError)) {
            driver.stop();
            return true;
        }

        return false;
    }
}
