package codebase.actions;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import java.util.function.Supplier;

import codebase.controllers.PIDController;
import codebase.geometry.Angles;
import codebase.hardware.Motor;

public class DcMotorToPositionAction implements Action {
    private final Motor motor;

    /**
     * The target rotation of the motor in radians from starting point
     */
    private final Supplier<Double> targetRotation;

    private final double rotationalSpeed;

    private final double maxRotationalError;

    private final PIDController pid;

    /**
     * @param motor the motor to rotate
     * @param targetRotation the target rotation in radians
     * @param rotationalSpeed speed multiplier for the rotation
     * @param maxRotationalError max error, in radians, to be considered complete
     * @param pidCoefficients the coefficients for the rotational PID
     */
    public DcMotorToPositionAction(Motor motor, double targetRotation, double rotationalSpeed, double maxRotationalError, PIDCoefficients pidCoefficients) {
        this.motor = motor;
        this.targetRotation = () -> targetRotation;
        this.maxRotationalError = maxRotationalError;
        this.rotationalSpeed = rotationalSpeed;

        pid = new PIDController(pidCoefficients, () -> Angles.angleDifference(motor.getMotorEncoder().getPosition(), targetRotation));
    }

    /**
     * @param motor the motor to rotate
     * @param targetRotation the supplier of the target rotation in radians
     * @param rotationalSpeed speed multiplier for the rotation
     * @param maxRotationalError max error, in radians, to be considered complete
     * @param pidCoefficients the coefficients for the rotational PID
     */
    public DcMotorToPositionAction(Motor motor, Supplier<Double> targetRotation, double rotationalSpeed, double maxRotationalError, PIDCoefficients pidCoefficients) {
        this.motor = motor;
        this.targetRotation = targetRotation;
        this.maxRotationalError = maxRotationalError;
        this.rotationalSpeed = rotationalSpeed;

        pid = new PIDController(pidCoefficients, () -> Angles.angleDifference(motor.getMotorEncoder().getPosition(), targetRotation.get()));
    }

    @Override
    public void init() {
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        double power = pid.getPower();
        motor.setPower(power * rotationalSpeed);
    }

    @Override
    public boolean isComplete() {
        double rotationalError = Angles.angleDifference(motor.getMotorEncoder().getPosition(), targetRotation.get());

        if (Math.abs(rotationalError) <= maxRotationalError) {
            motor.setPower(0);
            return true;
        }

        return false;
    }

    public void setPIDCoefficients(PIDCoefficients pidCoefficients) {
        pid.setCoefficients(pidCoefficients);
    }

    public double getRotationalError() {
        return Angles.angleDifference(motor.getMotorEncoder().getPosition(), targetRotation.get());
    }
}
