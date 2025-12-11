package codebase;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

import codebase.movement.mecanum.MecanumCoefficientMatrix;
import codebase.movement.mecanum.MecanumCoefficientSet;

public class Constants {
    public static final PIDCoefficients MOVEMENT_PID_COEFFICIENTS = new PIDCoefficients(0, 0, 0);
    public static final PIDCoefficients DIRECTION_PID_COEFFICIENTS = new PIDCoefficients(0, 0, 0);

    public static double ROTATION_RADIUS_IN = 9.9851;
    public static double MAX_WHEEL_VELOCITY_INCHES_PER_SECOND = 1000;

    public static double PINPOINT_X_OFFSET = 0;
    public static double PINPOINT_Y_OFFSET = 0;

    public static double LIMELIGHT_LENS_HEIGHT;

    public static final MecanumCoefficientMatrix MECANUM_COEFFICIENT_MATRIX = new MecanumCoefficientMatrix(new MecanumCoefficientSet(-1, 1, -1, -1), ROTATION_RADIUS_IN);

    public static class MotorConstants {
        public static double GOBILDA_5203_2402_0019_TICKS_PER_ROTATION = 537.7;
    }
}
