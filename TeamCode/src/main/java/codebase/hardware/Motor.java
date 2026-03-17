package codebase.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Motor {
    private final DcMotorEx motor;
    private final double ticksPerRotation;
    /**
     * Diameter of the wheel measured in inches.
     */
    private final double wheelDiameter;

    public Motor(DcMotorEx motor, double ticksPerRotation, double wheelDiameter) {
        this.motor = motor;
        this.ticksPerRotation = ticksPerRotation;
        this.wheelDiameter = wheelDiameter;
    }

    public Motor(DcMotorEx motor) {
        this(motor, 1, 1);
    }

    public void setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior behavior) {
        this.motor.setZeroPowerBehavior(behavior);
    }

    /**
     * Sets the velocity in relation to the diameter of the wheel and the ticks per rotation of the motor.
     * @param velocity The desired velocity measured in inches per second.
     */
    public void setVelocity(double velocity) {
        double ticksPerSecond = velocity * (ticksPerRotation / (wheelDiameter * Math.PI));

        motor.setVelocity(ticksPerSecond);
    }

    /**
     * Gets the velocity in relation to the diameter of the wheel and the ticks per rotation of the motor.
     * @return The desired velocity measured in inches per second.
     */
    public double getVelocity() {
        return motor.getVelocity() / (ticksPerRotation / (wheelDiameter * Math.PI));
    }

    public void setPower(double power) {
        motor.setPower(power);
    }

    public double getPower() {
        return motor.getPower();
    }
}