package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import java.util.List;

@TeleOp(name="Flywheel Tuning")
public class FlywheelPIDFTeleop extends OpMode {

    public DcMotorEx launchMotor1; // Has encoder
    public DcMotorEx launchMotor2; // No encoder, opposite direction

    public double highVelocity = 1500;
    public double lowVelocity = 900;

    double curTargetVelocity = highVelocity;

    double F = 0;
    double P = 0;

    double[] stepSizes = {10.0, 1.0, 0.1, 0.01, 0.001};

    int stepIndex = 1; // Start at 1.0 step size

    boolean motorRunning = false;

    // Button state tracking for debouncing
    boolean prevDpadUp = false;
    boolean prevDpadDown = false;
    boolean prevDpadLeft = false;
    boolean prevDpadRight = false;
    boolean prevA = false;
    boolean prevLeftBumper = false;
    boolean prevRightBumper = false;
    boolean prevB = false;

    @Override
    public void init() {
        // Enable bulk reads for better performance
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // Initialize launch motor 1 (with encoder)
        launchMotor1 = hardwareMap.get(DcMotorEx.class, "launchMotor1");
        launchMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launchMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launchMotor1.setDirection(DcMotorSimple.Direction.REVERSE); // Opposite direction

        // Initialize launch motor 2 (no encoder, opposite direction)
        launchMotor2 = hardwareMap.get(DcMotorEx.class, "launchMotor2");
        launchMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launchMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        telemetry.addLine("Flywheel Tuning Initialized");
        telemetry.addLine("Controls:");
        telemetry.addLine("  DPad Up/Down: Adjust P");
        telemetry.addLine("  DPad Left/Right: Adjust F");
        telemetry.addLine("  A: Toggle High/Low Velocity");
        telemetry.addLine("  Bumpers: Change Step Size");
        telemetry.addLine("  B: Stop/Start Motor");
        telemetry.update();
    }

    @Override
    public void loop() {
        // --- Handle P adjustment ---
        if (gamepad1.dpad_up && !prevDpadUp) {
            P += stepSizes[stepIndex];
        }
        if (gamepad1.dpad_down && !prevDpadDown) {
            P -= stepSizes[stepIndex];
            if (P < 0) P = 0;
        }

        // --- Handle F adjustment ---
        if (gamepad1.dpad_right && !prevDpadRight) {
            F += stepSizes[stepIndex];
        }
        if (gamepad1.dpad_left && !prevDpadLeft) {
            F -= stepSizes[stepIndex];
            if (F < 0) F = 0;
        }

        // --- Toggle velocity target ---
        if (gamepad1.a && !prevA) {
            if (curTargetVelocity == highVelocity) {
                curTargetVelocity = lowVelocity;
            } else {
                curTargetVelocity = highVelocity;
            }
        }

        // --- Change step size ---
        if (gamepad1.right_bumper && !prevRightBumper) {
            stepIndex++;
            if (stepIndex >= stepSizes.length) {
                stepIndex = stepSizes.length - 1;
            }
        }
        if (gamepad1.left_bumper && !prevLeftBumper) {
            stepIndex--;
            if (stepIndex < 0) {
                stepIndex = 0;
            }
        }

        // --- Apply PIDF coefficients to motor 1 ---
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        launchMotor1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        launchMotor2.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        // --- Toggle motor on/off with B button ---
        if (gamepad1.b && !prevB) {
            motorRunning = !motorRunning;
        }

        // --- Set motor velocities/powers ---
        if (motorRunning) {
            // Motor 1: Use PIDF velocity control
            launchMotor1.setVelocity(curTargetVelocity);
            // Motor 2: Mirror power (direction already reversed)
            launchMotor2.setVelocity(curTargetVelocity);
        } else {
            launchMotor1.setVelocity(0);
            launchMotor2.setPower(0);
        }

        // 15.4330 F
        //

        // --- Get current velocity and calculate error ---
        double currentVelocity = launchMotor1.getVelocity();
        double velocityError = curTargetVelocity - currentVelocity;

        // --- Update previous button states ---
        prevDpadUp = gamepad1.dpad_up;
        prevDpadDown = gamepad1.dpad_down;
        prevDpadLeft = gamepad1.dpad_left;
        prevDpadRight = gamepad1.dpad_right;
        prevA = gamepad1.a;
        prevLeftBumper = gamepad1.left_bumper;
        prevRightBumper = gamepad1.right_bumper;
        prevB = gamepad1.b;

        // --- Telemetry ---
        telemetry.addLine("=== PIDF COEFFICIENTS ===");
        telemetry.addData("P", "%.4f", P);
        telemetry.addData("F", "%.4f", F);
        telemetry.addLine();
        telemetry.addLine("=== VELOCITY ===");
        telemetry.addData("Target Velocity", "%.1f ticks/sec (%s)",
                curTargetVelocity,
                curTargetVelocity == highVelocity ? "HIGH" : "LOW");
        telemetry.addData("Current Velocity (Motor1)", "%.1f ticks/sec", currentVelocity);
        telemetry.addData("Error", "%.1f ticks/sec", velocityError);
        telemetry.addData("Error %", "%.2f%%", (velocityError / curTargetVelocity) * 100);
        telemetry.addLine();
        telemetry.addLine("=== MOTOR STATUS ===");
        telemetry.addData("Motors Running", motorRunning ? "YES" : "NO");
        telemetry.addData("Motor1 Power", "%.2f", launchMotor1.getPower());
        telemetry.addData("Motor2 Power", "%.2f", launchMotor2.getPower());
        telemetry.addLine();
        telemetry.addLine("=== SETTINGS ===");
        telemetry.addData("Step Size", "%.4f (index %d)", stepSizes[stepIndex], stepIndex);
        telemetry.addLine();
        telemetry.addLine("=== CONTROLS ===");
        telemetry.addLine("DPad ↑↓: P | DPad ←→: F");
        telemetry.addLine("A: Toggle Velocity | B: Start/Stop");
        telemetry.addLine("Bumpers: Step Size");
        telemetry.update();
    }
}