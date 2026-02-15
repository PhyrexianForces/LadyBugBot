package codebase;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import codebase.geometry.FieldPosition;
import codebase.hardware.MotorConfig;
import codebase.movement.mecanum.MecanumCoefficientMatrix;
import codebase.movement.mecanum.MecanumCoefficientSet;

public class Constants {
    public static final double MOVEMENT_VELOCITY = 30;
    public static final double MOVEMENT_STEEPNESS = 0.8;
    public static final double ROTATION_VELOCITY = 17.5;
    public static final double ROTATION_STEEPNESS = 0.6;
    public static final double INTAKE_VELOCITY = 100;

    public static final double WHEEL_DIAMETER_INCHES = 4.0;

    public static class RevolverConstants {
        public static final double POWER = 1.0;
        public static final double STEEPNESS = 1.0;
        public static final double MAX_ERROR = Math.toRadians(5);
        public static final double SECONDS_PER_RADIAN = 0.365;
        public static final double OUTPUT_POSITION_ZERO = 0.66;
    }

    public static class ShooterConstants {
        public static final double POWER = 1.0;
        public static final double STEEPNESS = 1.0;
        public static final double MAX_ERROR = Math.toRadians(4);
        public static final FieldPosition GOAL_POSITION_RED = new FieldPosition(-68, 59, 0);
        public static final PIDFCoefficients PIDF_COEFFICIENTS = new PIDFCoefficients(0, 0, 0, 15.4330);
    }

    /**
     * The value which, if the [0,1] green value of the artifact read by the color sensor is above, will have the artifact considered to be green
     */
    public static final double ARTIFACT_GREEN_THRESHOLD = 0.65;
    public static final double ARTIFACT_GREEN_THRESHOLD_RED = 0.37;

    public static double ROTATION_RADIUS_IN = 9.9851;
    public static double PINPOINT_X_OFFSET = -101;
    public static double PINPOINT_Y_OFFSET = -169;

    public static double LIMELIGHT_LENS_HEIGHT = 0;

    public static final MecanumCoefficientMatrix MECANUM_COEFFICIENT_MATRIX = new MecanumCoefficientMatrix(new MecanumCoefficientSet(-1, -1, -1, 1), ROTATION_RADIUS_IN);
    public static final double MAX_WHEEL_VELOCITY = 100;

    public static class MotorConstants {
        public static double GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION = ((((1+(46.0/17.0))) * (1+(46.0/11.0))) * 28);
        public static double GOBILDA_1620RPM_5203_2402_0003_TICKS_PER_ROTATION = ((1+(46.0/17.0)) * 28);
        public static double GOBILDA_6000RPM_5203_2402_0001_TICKS_PER_ROTATION = 28;
    }

    public static final double LAUNCH_SERVO_LAUNCH_POSITION = 0.25;

    public static final double LAUNCH_SERVO_STORAGE_POSITION = 0.6;

    public static final double LAUNCH_ANGLE = Math.toRadians(60);
    public static final double GOAL_HEIGHT_IN = 38.97636;

    public static final double FLYWHEEL_DIAMETER_IN = 4;
    public static final double MAX_FLYWHEEL_VELOCITY_ERROR_IN_SEC = 30;

    public static final double INTAKE_WHEEL_DIAMETER_IN = 2;

    public static final MotorConfig DRIVE_MOTOR_CONFIG = new MotorConfig(
            MotorConstants.GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION,
            WHEEL_DIAMETER_INCHES
    );

    public static final MotorConfig FLYWHEEL_MOTOR_CONFIG = new MotorConfig(
            MotorConstants.GOBILDA_6000RPM_5203_2402_0001_TICKS_PER_ROTATION,
            FLYWHEEL_DIAMETER_IN
    );

    public static final MotorConfig INTAKE_MOTOR_CONFIG = new MotorConfig(
            MotorConstants.GOBILDA_1620RPM_5203_2402_0003_TICKS_PER_ROTATION,
            INTAKE_WHEEL_DIAMETER_IN
    );

    public static final double LAUNCH_VELOCITY_OFFSET = 80;
}
