package decode.teleop;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.Constants;
import codebase.gamepad.Gamepad;
import codebase.hardware.Motor;

@TeleOp(name = "Motor Velocity Test", group = "Test")
public class MotorVelocityTestTeleop extends OpMode {

    private Gamepad gamepad;

    private Motor fl;
    private Motor fr;
    private Motor bl;
    private Motor br;

    private double flTargetVelocity = 0;
    private double frTargetVelocity = 0;
    private double blTargetVelocity = 0;
    private double brTargetVelocity = 0;

    private Telemetry.Item flDisplay;
    private Telemetry.Item frDisplay;
    private Telemetry.Item blDisplay;
    private Telemetry.Item brDisplay;
    private Telemetry.Item instructionsDisplay;

    @Override
    public void init() {
        fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"), Constants.DRIVE_MOTOR_CONFIG);
        fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"), Constants.DRIVE_MOTOR_CONFIG);
        bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"), Constants.DRIVE_MOTOR_CONFIG);
        br = new Motor(hardwareMap.get(DcMotorEx.class, "br"), Constants.DRIVE_MOTOR_CONFIG);

        gamepad = new Gamepad(gamepad1);

        // Y button - Front Left motor
        gamepad.yButton.onPress(() -> {
            flTargetVelocity += 10;
            fl.setVelocity(flTargetVelocity);
        });

        // B button - Front Right motor
        gamepad.bButton.onPress(() -> {
            frTargetVelocity += 10;
            fr.setVelocity(frTargetVelocity);
        });

        // X button - Back Left motor
        gamepad.xButton.onPress(() -> {
            blTargetVelocity += 10;
            bl.setVelocity(blTargetVelocity);
        });

        // A button - Back Right motor
        gamepad.aButton.onPress(() -> {
            brTargetVelocity += 10;
            br.setVelocity(brTargetVelocity);
        });

        // Left bumper - Stop all motors
        gamepad.leftBumper.onPress(() -> {
            flTargetVelocity = 0;
            frTargetVelocity = 0;
            blTargetVelocity = 0;
            brTargetVelocity = 0;
            fl.setPower(0);
            fr.setPower(0);
            bl.setPower(0);
            br.setPower(0);
        });

        // Set up telemetry displays
        instructionsDisplay = telemetry.addData("Controls", "Y=FL, B=FR, X=BL, A=BR | LB=Stop All");
        flDisplay = telemetry.addData("FL (Y)", "");
        frDisplay = telemetry.addData("FR (B)", "");
        blDisplay = telemetry.addData("BL (X)", "");
        brDisplay = telemetry.addData("BR (A)", "");
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void loop() {
        gamepad.loop();

        // Update telemetry with target and actual velocities
        flDisplay.setValue(String.format("Target: %.2f | Actual: %.2f", flTargetVelocity, fl.getVelocity()));
        frDisplay.setValue(String.format("Target: %.2f | Actual: %.2f", frTargetVelocity, fr.getVelocity()));
        blDisplay.setValue(String.format("Target: %.2f | Actual: %.2f", blTargetVelocity, bl.getVelocity()));
        brDisplay.setValue(String.format("Target: %.2f | Actual: %.2f", brTargetVelocity, br.getVelocity()));
    }
}