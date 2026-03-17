package codebase.sensors;

import com.qualcomm.robotcore.hardware.DcMotor;

public class MotorEncoder implements Encoder {
    private final DcMotor encoder;
    private final double ticksPerRotation;
    /**
     * Diameter of the wheel measured in inches.
     */
    private final double wheelDiameter;

    public MotorEncoder(DcMotor encoder, double ticksPerRotation, double wheelDiameter) {
        this.encoder = encoder;
        this.ticksPerRotation = ticksPerRotation;
        this.wheelDiameter = wheelDiameter;
        this.encoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public double getTicks() {
        return this.encoder.getCurrentPosition();
    }

    /**
     * Gets the encoder position in relation to the diameter of the wheel and the ticks per rotation of the encoder.
     * @return The position of the encoder in inches.
     */
    public double getPosition() {
        return getTicks() / (ticksPerRotation / (wheelDiameter * Math.PI));
    }

    public void reset() {
        this.encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.encoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}