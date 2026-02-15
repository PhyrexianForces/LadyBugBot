package codebase.manipulators;

import static codebase.Constants.RevolverConstants.MAX_ERROR;
import static codebase.Constants.RevolverConstants.POWER;
import static codebase.Constants.RevolverConstants.STEEPNESS;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

import codebase.Constants;
import codebase.controllers.SigmoidController;
import codebase.geometry.Angles;
import codebase.hardware.Motor;
import decode.RevolverStorageManager;

public class RevolverManipulator {
//    private final Motor revolverMotor;
//    private final SigmoidController controller;

    private final Servo revolverServo;
    private final Motor intakeMotor;
    private int chamberNumber = 0;
    private RevolverMode mode = RevolverMode.INPUT;

    private double startMs;
    private double distanceToGo;

    private boolean wasMoving = false;

    public RevolverManipulator(Servo revolverServo, Motor intakeMotor) {
        this.revolverServo = revolverServo;
        this.intakeMotor = intakeMotor;
//        this.controller = new SigmoidController(POWER, STEEPNESS, () -> Angles.angleDifference(revolverMotor.getMotorEncoder().getPosition(), getRotationForChamber(chamberNumber, mode)));
    }

    public void setChamber(int chamberNumber, RevolverMode mode) {
        this.distanceToGo = Math.abs(Angles.angleDifference(getRotationForChamber(this.chamberNumber, this.mode), getRotationForChamber(chamberNumber, mode)));
        this.chamberNumber = chamberNumber;
        this.mode = mode;
        this.startMs = System.currentTimeMillis();
        this.wasMoving = true;
    }

//    public void init() {
//        revolverMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//    }

    public void loop() {
//        System.out.println("Moving to: " + ((getRotationForChamber(chamberNumber, mode) / (Math.PI * 9)) + Constants.RevolverConstants.SERVO_OFFSET_FOR_OUTPUT));
        revolverServo.setPosition(getRotationForChamber(chamberNumber, mode));
//        revolverServo.setPosition((getRotationForChamber(chamberNumber, mode) / (Math.PI * 9)) + Constants.RevolverConstants.SERVO_OFFSET_FOR_OUTPUT);
//        revolverMotor.setPower(controller.getPower());
        if (!isAtTarget() && intakeMotor.getPower() == 0) {
            intakeMotor.setVelocity(-20);
        }
        if (isAtTarget() && wasMoving && mode == RevolverMode.OUTPUT) {
            wasMoving = false;
            intakeMotor.setVelocity(0);
        }
    }

    public int getClosestChamberOfState(RevolverStorageManager.ArtifactState state, RevolverMode revolverMode) {
        return getClosestChamberFrom(RevolverStorageManager.getChambersWithState(state), revolverMode);
    }

    public int getClosestChamberWithArtifact(RevolverMode revolverMode) {
        ArrayList<Integer> chambersWithArtifacts = new ArrayList<>();
        chambersWithArtifacts.addAll(RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.GREEN));
        chambersWithArtifacts.addAll(RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.PURPLE));

        return getClosestChamberFrom(chambersWithArtifacts, revolverMode);
    }

    public int getClosestChamberFrom(ArrayList<Integer> chambers, RevolverMode revolverMode) {
        int closest = 0;
        double closestDistance = Double.MAX_VALUE;

        for (int chamber : chambers) {
            double distance = Math.abs(Angles.angleDifference(getRotationForChamber(chamber, revolverMode), getRotationForChamber(chamberNumber, revolverMode)));

            if (distance < closestDistance) {
                closestDistance = distance;
                closest = chamber;
            }
        }

        return closest;
    }

//    /**
//     * Get the target rotation in radians for the target chamber
//     * @param chamberNumber the chamber to rotate to (0-2)
//     * @param revolverMode either input or output, due to offset for outputting
//     * @return the rotation, in radians,
//     */
//    private static double getRotationForChamber(int chamberNumber, RevolverMode revolverMode) {
//        return (chamberNumber / 3.0) * (Math.PI * 2) + (revolverMode == RevolverMode.OUTPUT ? Math.PI : 0);
//    }

    private static double getRotationForChamber(int chamberNumber, RevolverMode mode) {
        double outputZero = Constants.RevolverConstants.OUTPUT_POSITION_ZERO;
        if (mode == RevolverMode.INPUT) {
            double chamberOffset = -(chamberNumber == 0 ? 0 : (chamberNumber == 1 ? 0.08 : 0.155));
            return outputZero - 0.11 + chamberOffset - 0.02;
        } else {
            double chamberOffset = -(chamberNumber == 0 ? 0 : (chamberNumber == 1 ? 0.07 : 0.145));
            return outputZero + chamberOffset - 0.02;
        }
    }

    public boolean isAtTarget() {
        double secondsMoving = (System.currentTimeMillis() - startMs) / 1000.0;

        return secondsMoving >= distanceToGo * Constants.RevolverConstants.SECONDS_PER_RADIAN * 10 * Math.PI;
    }

    public enum RevolverMode {
        OUTPUT,
        INPUT
    }
}
