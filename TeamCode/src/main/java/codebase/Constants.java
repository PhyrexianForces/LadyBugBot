package codebase;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

public class Constants {
    public static final PIDCoefficients MOVEMENT_PID_COEFFICIENTS = new PIDCoefficients(0, 0, 0);
    public static final PIDCoefficients DIRECTION_PID_COEFFICIENTS = new PIDCoefficients(0, 0, 0);

    public static class MotorConstants {
        public static double GOBILDA_5202_TICKS_PER_ROTATION = 384.5;
    }
}
