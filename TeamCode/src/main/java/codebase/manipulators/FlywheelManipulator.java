package codebase.manipulators;

import static codebase.Constants.ShooterConstants.GOAL_POSITION_RED;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import codebase.Constants;
import codebase.controllers.SigmoidController;
import codebase.hardware.Motor;
import codebase.pathing.Localizer;
import codebase.vision.LimelightManager;
import decode.auto.AutoConfiguration;

public class FlywheelManipulator {
    private final Motor launchMotor1;
    private final Motor launchMotor2;

    private final Localizer localizer;
    private final AutoConfiguration.AllianceColor allianceColor;

    private double targetVelocity_in_per_sec = 0;

    public FlywheelManipulator(Motor launchMotor1, Motor launchMotor2, Localizer localizer, AutoConfiguration.AllianceColor allianceColor) {
        this.launchMotor1 = launchMotor1;
        this.launchMotor2 = launchMotor2;
        this.localizer = localizer;
        this.allianceColor = allianceColor;
    }

    public void init() {
        launchMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launchMotor1.getMotor().setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, Constants.ShooterConstants.PIDF_COEFFICIENTS);

        launchMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launchMotor2.getMotor().setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, Constants.ShooterConstants.PIDF_COEFFICIENTS);
        launchMotor2.getMotor().setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void runFlywheel() {
        double allianceCoefficient = (allianceColor == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);
        double distanceToGoalInches = Math.sqrt(
                                        Math.pow(GOAL_POSITION_RED.x - localizer.getCurrentPosition().x, 2)
                                        + Math.pow((GOAL_POSITION_RED.y * allianceCoefficient) - localizer.getCurrentPosition().y, 2));

        distanceToGoalInches += 5;

        targetVelocity_in_per_sec = 645;//6.9 * distanceToGoalInches + 220;//2.5 * Math.sqrt(((32.2 * 12)*Math.pow(distanceToGoalInches, 2)) / (2 * Math.pow(Math.cos(Constants.LAUNCH_ANGLE), 2) * (distanceToGoalInches * Math.tan(Constants.LAUNCH_ANGLE) - Constants.GOAL_HEIGHT_IN))) + Constants.LAUNCH_VELOCITY_OFFSET;

        launchMotor1.setVelocity(targetVelocity_in_per_sec);
        launchMotor2.setVelocity(targetVelocity_in_per_sec);
    }

    public boolean isAtTargetVelocity() {
        return Math.abs(Math.abs(launchMotor1.getVelocity()) - targetVelocity_in_per_sec) < Constants.MAX_FLYWHEEL_VELOCITY_ERROR_IN_SEC;
    }

    public void stopFlywheel() {
        targetVelocity_in_per_sec = 0;

        launchMotor1.setVelocity(targetVelocity_in_per_sec);
        launchMotor2.setVelocity(targetVelocity_in_per_sec);
    }

    public double getError() {
        return targetVelocity_in_per_sec - (-launchMotor1.getVelocity());
    }

    public double getTargetVelocity_in_per_sec() {
        return targetVelocity_in_per_sec;
    }
}
