//package decode.teleop;
//
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.PIDCoefficients;
//
//import org.firstinspires.ftc.robotcore.external.Telemetry;
//
//import codebase.Constants;
//import codebase.actions.MoveToAction;
//import codebase.actions.SimultaneousAction;
//import codebase.gamepad.Gamepad;
//import codebase.geometry.FieldPosition;
//import codebase.hardware.Motor;
//import codebase.hardware.PinpointModule;
//import codebase.movement.mecanum.MecanumCoefficientMatrix;
//import codebase.movement.mecanum.MecanumCoefficientSet;
//import codebase.movement.mecanum.MecanumDriver;
//import codebase.pathing.Localizer;
//import codebase.pathing.PinpointLocalizer;
//
///**
// * A teleop to tune PID coefficients for robot movement (including rotation)
// * Instructions:
// *  Dpad left to turn on decreasing mode
// *  Dpad right to turn on increasing mode
// *  Dpad down to toggle movement on/off (for pushing around, turn it off)
// *  Dpad up to toggle movement/rotation mode
// *  1. Increase P value with X button first until there is very slight oscillation upon reaching the target (if there is large oscillation, P is too high)
// *  2. Increase D value with A button slowly to remove oscillation
// *  3. Add slight I value with Y button to prevent getting stuck (or motor power being too low)
// *  4. Record both the correct movement/rotation coefficients to save in Constants
// */
//@TeleOp(name="PID Tuner Teleop")
//public class MovementPIDTunerTeleop extends OpMode {
//    private Motor fl;
//    private Motor fr;
//    private Motor bl;
//    private Motor br;
//
//    private MecanumDriver driver;
//    private Gamepad gamepad;
//
//    private Localizer localizer;
//
//    private SimultaneousAction runningActions;
//
//    private PIDCoefficients currentPIDCoefficientsMovement;
//    private PIDCoefficients currentPIDCoefficientsRotation;
//
//    private Telemetry.Item pidTelemtry;
//    private Telemetry.Item modeTelemetry;
//    private Telemetry.Item negativeTelemetry;
//    private Telemetry.Item movingTelemetry;
//
//    private boolean shouldBeMoving = false;
//    private boolean rotationMode = false;
//    private boolean negativeMode = false;
//
//    private MoveToAction action;
//
//    @Override
//    public void init() {
//        this.fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"));
//        this.fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"));
//        this.bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"));
//        this.br = new Motor(hardwareMap.get(DcMotorEx.class, "br"));
//
//        gamepad = new Gamepad(gamepad1);
//
//        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);
//
//        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.FORWARD, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.REVERSED, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
//        localizer.init();
//
//        runningActions = new SimultaneousAction();
//
//        currentPIDCoefficientsMovement = new PIDCoefficients(0, 0, 0);
//        currentPIDCoefficientsRotation = new PIDCoefficients(0, 0, 0);
//
//        action = new MoveToAction(driver, localizer, new FieldPosition(12, 12, 0), 1, 1, 1, (Math.PI / 180) * 5);
//
//        action.setPIDCoefficients(currentPIDCoefficientsMovement, currentPIDCoefficientsRotation);
//
//        gamepad.bButton.onPress(() -> {
//            action = new MoveToAction(driver, localizer, new FieldPosition(localizer.getCurrentPosition().x + 10, localizer.getCurrentPosition().y + 10, 0), 1, 1, 3, (Math.PI / 180) * 5);
//            runningActions.add(action, true);
//        });
//
//        // tune P until slight oscillation - ENSURE this is not too high (you will know P is too high if the robot oscillates or jitters when reaching the destination)
//        gamepad.xButton.onPress(() -> {
//            PIDCoefficients currentCoefficients = (rotationMode ? currentPIDCoefficientsRotation : currentPIDCoefficientsMovement);
//            PIDCoefficients newCoefficients = new PIDCoefficients(currentCoefficients.p + 0.01 * (negativeMode ? -1 : 1), currentCoefficients.i, currentCoefficients.d);
//
//            if (rotationMode) {
//                currentPIDCoefficientsRotation = newCoefficients;
//            } else {
//                currentPIDCoefficientsMovement = newCoefficients;
//            }
//        });
//
//        // add slight I to eliminate steady-state error (prevents getting stuck, increases over time)
//        gamepad.yButton.onPress(() -> {
//            PIDCoefficients currentCoefficients = (rotationMode ? currentPIDCoefficientsRotation : currentPIDCoefficientsMovement);
//            PIDCoefficients newCoefficients = new PIDCoefficients(currentCoefficients.p, currentCoefficients.i + 0.001 * (negativeMode ? -1 : 1), currentCoefficients.d);
//
//            if (rotationMode) {
//                currentPIDCoefficientsRotation = newCoefficients;
//            } else {
//                currentPIDCoefficientsMovement = newCoefficients;
//            }
//        });
//
//        // add D slowly to lower oscillation (but check that high P value is not what is causing oscillation first)
//        gamepad.aButton.onPress(() -> {
//            PIDCoefficients currentCoefficients = (rotationMode ? currentPIDCoefficientsRotation : currentPIDCoefficientsMovement);
//            PIDCoefficients newCoefficients = new PIDCoefficients(currentCoefficients.p, currentCoefficients.i, currentCoefficients.d + 0.001 * (negativeMode ? -1 : 1));
//
//            if (rotationMode) {
//                currentPIDCoefficientsRotation = newCoefficients;
//            } else {
//                currentPIDCoefficientsMovement = newCoefficients;
//            }
//        });
//
//        gamepad.dpadDown.onPress(() -> {
//            shouldBeMoving = !shouldBeMoving;
//            driver.stop();
//        });
//
//        gamepad.dpadUp.onPress(() -> {
//            rotationMode = !rotationMode;
//        });
//
//        gamepad.dpadLeft.onPress(() -> {
//            negativeMode = true;
//        });
//
//        gamepad.dpadRight.onPress(() -> {
//            negativeMode = false;
//        });
//
//        modeTelemetry = telemetry.addData("mode:", rotationMode ? "Rotation" : "Movement");
//        pidTelemtry = telemetry.addData("pid:", (rotationMode ? currentPIDCoefficientsRotation : currentPIDCoefficientsMovement));
//        negativeTelemetry = telemetry.addData("inc/dec mode:", (negativeMode ? "decreasing" : "increasing"));
//        movingTelemetry = telemetry.addData("moving:", shouldBeMoving);
//    }
//
//    @Override
//    public void loop() {
//        gamepad.loop();
//
//        localizer.loop();
//
//        if (shouldBeMoving) {
//            runningActions.loop();
//        }
//
//        modeTelemetry.setValue(rotationMode ? "Rotation" : "Movement");
//        pidTelemtry.setValue((rotationMode ? currentPIDCoefficientsRotation : currentPIDCoefficientsMovement));
//        negativeTelemetry.setValue((negativeMode ? "decreasing" : "increasing"));
//        movingTelemetry.setValue(shouldBeMoving);
//
//        action.setPIDCoefficients(currentPIDCoefficientsMovement, currentPIDCoefficientsRotation);
//    }
//}