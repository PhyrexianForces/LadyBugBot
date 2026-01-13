package codebase;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

import codebase.geometry.FieldPosition;
import codebase.movement.mecanum.MecanumCoefficientMatrix;
import codebase.movement.mecanum.MecanumCoefficientSet;

public class Constants {
//    public static final PIDCoefficients MOVEMENT_PID_COEFFICIENTS = new PIDCoefficients(0.03, 0, 0.005);
    public static final double MOVEMENT_POWER = 0.25;
    public static final double MOVEMENT_STEEPNESS = 1.8;
    public static final double ROTATION_POWER = 0.2;
    public static final double ROTATION_STEEPNESS = 0.3;
//    public static final PIDCoefficients DIRECTION_PID_COEFFICIENTS = new PIDCoefficients(0.03, 0, 0);

//    public static final PIDCoefficients REVOLVER_PID_COEFFICIENTS = new PIDCoefficients(0.38, 0, 0.05);

    public static final double INTAKE_POWER = 0.4;

    public static class RevolverConstants {
        public static final double POWER = 1.0;
        public static final double STEEPNESS = 1.0;
        public static final double MAX_ERROR = Math.toRadians(5);
    }

    public static class ShooterConstants {
        public static final double POWER = 1.0;
        public static final double STEEPNESS = 1.0;
        public static final double MAX_ERROR = Math.toRadians(4);
        public static final FieldPosition GOAL_POSITION_RED = new FieldPosition(-64, 59, 0);
    }

    /**
     * The value which, if the [0,1] green value of the artifact read by the color sensor is above, will have the artifact considered to be green
     */
    public static final double ARTIFACT_GREEN_THRESHOLD = 0.76;

    public static double ROTATION_RADIUS_IN = 9.9851;
    public static double PINPOINT_X_OFFSET = -101;
    public static double PINPOINT_Y_OFFSET = -169;

    public static double LIMELIGHT_LENS_HEIGHT = 0;

    public static final MecanumCoefficientMatrix MECANUM_COEFFICIENT_MATRIX = new MecanumCoefficientMatrix(new MecanumCoefficientSet(-1, 1, -1, -1), ROTATION_RADIUS_IN);

    public static class MotorConstants {
        public static double GOBILDA_5203_2402_0019_TICKS_PER_ROTATION = 537.7;
    }

    public static final double LAUNCH_SERVO_LAUNCH_POSITION = 0.1;

    public static final double LAUNCH_SERVO_STORAGE_POSITION = 0.5;
}
