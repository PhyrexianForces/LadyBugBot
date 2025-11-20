package codebase.actions;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

import codebase.geometry.Angles;
import codebase.hardware.Motor;
import decode.RevolverStorageManager;

public class RotateRevolverAction extends DcMotorToPositionAction {

    private static Motor revolverMotor;
    private static final double MAX_ROTATIONAL_ERROR = (Math.PI / 180) / 2;

    private static final PIDCoefficients PID_COEFFICIENTS = new PIDCoefficients(0.5, 0.1, 0.1);

    /**
     * @param chamberNumber the chamber number to rotate to (0-2)
     * @param revolverMode either input or output, due to offset for outputting
     */
    public RotateRevolverAction(int chamberNumber, RevolverMode revolverMode) {
        super(revolverMotor, RotateRevolverAction.getRotationForChamber(chamberNumber, revolverMode), 1, MAX_ROTATIONAL_ERROR, PID_COEFFICIENTS);
    }

    public static int getClosestChamberOfState(RevolverStorageManager.ArtifactState state, RevolverMode revolverMode) {
        int closest = 0;
        double closestDistance = Double.MAX_VALUE;

        for (int chamber : RevolverStorageManager.getChambersWithState(state)) {
            double distance = Math.abs(Angles.angleDifference(getRotationForChamber(chamber, revolverMode), revolverMotor.getMotorEncoder().getPosition()));

            if (distance < closestDistance) {
                closestDistance = distance;
                closest = chamber;
            }
        }

        return closest;
    }

    /**
     * Get the rotation in radians to rotate to the target chamber
     * @param chamberNumber the chamber to rotate to (0-2)
     * @param revolverMode either input or output, due to offset for outputting
     * @return the rotation, in radians,
     */
    private static double getRotationForChamber(int chamberNumber, RevolverMode revolverMode) {
        return (chamberNumber / 3.0) * (Math.PI * 2) + (revolverMode == RevolverMode.OUTPUT ? Math.PI : 0);
    }

    public static void setRevolverMotor(Motor revolverMotor) {
        RotateRevolverAction.revolverMotor = revolverMotor;
    }

    public enum RevolverMode {
        OUTPUT,
        INPUT
    }
}
