package codebase.manipulators;

import static codebase.Constants.RevolverConstants.MAX_ERROR;
import static codebase.Constants.RevolverConstants.POWER;
import static codebase.Constants.RevolverConstants.STEEPNESS;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.ArrayList;

import codebase.controllers.SigmoidController;
import codebase.geometry.Angles;
import codebase.hardware.Motor;
import decode.RevolverStorageManager;

public class RevolverManipulator {
    private final Motor revolverMotor;
    private final SigmoidController controller;
    private int chamberNumber = 0;
    private RevolverMode mode = RevolverMode.INPUT;

    public RevolverManipulator(Motor revolverMotor) {
        this.revolverMotor = revolverMotor;
        this.controller = new SigmoidController(POWER, STEEPNESS, () -> Angles.angleDifference(revolverMotor.getMotorEncoder().getPosition(), getRotationForChamber(chamberNumber, mode)));
    }

    public void setChamber(int chamberNumber, RevolverMode mode) {
        this.chamberNumber = chamberNumber;
        this.mode = mode;
    }

    public void init() {
        revolverMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void loop() {
        revolverMotor.setPower(controller.getPower());
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
            double distance = Math.abs(Angles.angleDifference(getRotationForChamber(chamber, revolverMode), revolverMotor.getMotorEncoder().getPosition()));

            if (distance < closestDistance) {
                closestDistance = distance;
                closest = chamber;
            }
        }

        return closest;
    }

    /**
     * Get the target rotation in radians for the target chamber
     * @param chamberNumber the chamber to rotate to (0-2)
     * @param revolverMode either input or output, due to offset for outputting
     * @return the rotation, in radians,
     */
    private static double getRotationForChamber(int chamberNumber, RevolverMode revolverMode) {
        return (chamberNumber / 3.0) * (Math.PI * 2) + (revolverMode == RevolverMode.OUTPUT ? Math.PI : 0);
    }

    public boolean isAtTarget() {
        return controller.getError() <= MAX_ERROR;
    }

    public enum RevolverMode {
        OUTPUT,
        INPUT
    }
}
