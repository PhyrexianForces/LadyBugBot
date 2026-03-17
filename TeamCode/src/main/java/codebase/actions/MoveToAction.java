package codebase.actions;

import static codebase.Constants.DIRECTION_PID_COEFFICIENTS;
import static codebase.Constants.MOVEMENT_PID_COEFFICIENTS;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

import codebase.controllers.PIDController;
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

    private final PIDController xPID;
    private final PIDController yPID;
    private final PIDController directionPID;

    public MoveToAction(MecanumDriver driver, Localizer localizer, FieldPosition destination, double movementSpeed, double rotationalSpeed, double maxDistanceError, double maxRotationalError) {
        this.driver = driver;
        this.localizer = localizer;
        this.destination = destination;
        this.movementSpeed = movementSpeed;
        this.rotationalSpeed = rotationalSpeed;
        this.maxDistanceError = maxDistanceError;
        this.maxRotationalError = maxRotationalError;

        this.xPID = new PIDController(MOVEMENT_PID_COEFFICIENTS, () -> localizer.getCurrentPosition().x, () -> destination.x);
        this.yPID = new PIDController(MOVEMENT_PID_COEFFICIENTS, () -> localizer.getCurrentPosition().y, () -> destination.y);
        this.directionPID = new PIDController(
                DIRECTION_PID_COEFFICIENTS,
                () -> Angles.angleDifference(localizer.getCurrentPosition().direction, destination.direction)
        );
    }

    @Override
    public void init() {}

    @Override
    public void loop() {
        double powerX = xPID.getPower();
        double powerY = yPID.getPower();
        double powerRotational = directionPID.getPower();

        MovementVector vector = new MovementVector(
                movementSpeed * powerX,
                movementSpeed * powerY,
                rotationalSpeed * powerRotational);

        this.driver.setAbsolutePower(localizer.getCurrentPosition(), vector);
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

    public void setPIDCoefficients(PIDCoefficients movementCoefficients, PIDCoefficients rotationCoefficients) {
        this.xPID.setCoefficients(movementCoefficients);
        this.yPID.setCoefficients(movementCoefficients);
        this.directionPID.setCoefficients(rotationCoefficients);
    }
}
