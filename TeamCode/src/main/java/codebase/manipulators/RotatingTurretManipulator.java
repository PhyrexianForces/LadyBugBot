package codebase.manipulators;

import static codebase.Constants.ShooterConstants.GOAL_POSITION_RED;

import com.qualcomm.robotcore.hardware.DcMotor;

import codebase.Constants;
import codebase.controllers.SigmoidController;
import codebase.hardware.Motor;
import codebase.pathing.Localizer;
import codebase.vision.LimelightManager;
import decode.auto.AutoConfiguration;

public class RotatingTurretManipulator {
    private final Motor shooterRotatorMotor;
    private final Localizer localizer;
    private final LimelightManager limelightManager;
    private final SigmoidController controller;
    private final AutoConfiguration.AllianceColor allianceColor;

    private double targetRotation = 0;

    public RotatingTurretManipulator(Motor shooterRotaterMotor, Localizer localizer, LimelightManager limelightManager, AutoConfiguration.AllianceColor allianceColor) {
        this.shooterRotatorMotor = shooterRotaterMotor;
        this.localizer = localizer;
        this.limelightManager = limelightManager;
        this.allianceColor = allianceColor;

        this.controller = new SigmoidController(Constants.ShooterConstants.POWER, Constants.ShooterConstants.STEEPNESS, () -> getRotationErrorWithNoLoopAround(shooterRotaterMotor.getMotorEncoder().getPosition(), targetRotation));
    }

    public void init() {
        shooterRotatorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void loop() {
        double fieldGoalRotation = -Math.atan2(GOAL_POSITION_RED.x - localizer.getCurrentPosition().x, (GOAL_POSITION_RED.y * (allianceColor == AutoConfiguration.AllianceColor.BLUE ? -1 : 1)) - localizer.getCurrentPosition().y)
                + Math.PI / 2.0;

        targetRotation = fieldGoalRotation - localizer.getCurrentPosition().direction;

        shooterRotatorMotor.setPower(controller.getPower());
    }

    public boolean isShooterAligned() {
        return controller.getError() <= Constants.ShooterConstants.MAX_ERROR;
    }

    private double getRotationErrorWithNoLoopAround(double targetRotation, double currentRotation) {
        return Math.atan2(
                Math.sin(targetRotation - currentRotation),
                Math.cos(targetRotation - currentRotation)
        );
    }
}
