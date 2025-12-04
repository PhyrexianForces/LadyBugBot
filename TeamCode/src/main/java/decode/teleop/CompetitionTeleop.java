package decode.teleop;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImpl;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.concurrent.atomic.AtomicReference;

import codebase.Constants;
import codebase.actions.Action;
import codebase.actions.LaunchAction;
import codebase.actions.RotateRevolverAction;
import codebase.actions.SimultaneousAction;
import codebase.actions.TripleIntakeAction;
import codebase.actions.TripleLaunchAction;
import codebase.gamepad.Gamepad;
import codebase.geometry.MovementVector;
import codebase.hardware.Motor;
import codebase.movement.mecanum.MecanumDriver;
import codebase.sensors.ColorSensor;
import decode.RevolverStorageManager;

@TeleOp(name="Competition Teleop")
public class CompetitionTeleop extends OpMode {

    private Gamepad gamepad;
    private MecanumDriver driver;
    private SimultaneousAction actionThread;

    private Motor fl;
    private Motor fr;
    private Motor bl;
    private Motor br;

    private Motor revolverMotor;

    private ServoImpl launchServo;
    private Motor launchMotor1;
    private Motor launchMotor2;

    private Motor intakeMotor;

    private Telemetry.Item runningActionDisplay;

    private RotateRevolverAction revolverAction;

    private ColorSensor storageColorSensor;

    AtomicReference<TripleIntakeAction> intakeAction = new AtomicReference<>();

    @Override
    public void init() {
        fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"));
        fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"));
        bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"));
        br = new Motor(hardwareMap.get(DcMotorEx.class, "br"));

        revolverMotor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_5203_2402_0019_TICKS_PER_ROTATION);

        launchMotor1 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor1"));
        launchMotor2 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor2"));
        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");
        launchServo.setPosition(1);

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"));

        gamepad = new Gamepad(gamepad1);
        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);
        actionThread = new SimultaneousAction();

        storageColorSensor = new ColorSensor(hardwareMap.get(RevColorSensorV3.class, "colorSensor"));

        RotateRevolverAction.setRevolverMotor(revolverMotor);
        LaunchAction.setLaunchActionMotors(launchServo, launchMotor1, launchMotor2);




        //  IMPORTANT READ THIS!!!!!!!!!!!!!!
//        RevolverStorageManager.reset(); // remove this once we have an Auto running first

//        gamepad.dpadLeft.onPress(() -> {
//            revolverAction = new RotateRevolverAction(0, getRevolverMode());
//            actionThread.add(revolverAction, true, true);
//        });
//
//        gamepad.dpadUp.onPress(() -> {
//            revolverAction = new RotateRevolverAction(1, getRevolverMode());
//            actionThread.add(revolverAction, true, true);
//        });
//
//        gamepad.dpadRight.onPress(() -> {
//            revolverAction = new RotateRevolverAction(2, getRevolverMode());
//            actionThread.add(revolverAction, true, true);
//        });

        RevolverStorageManager.reset();

        gamepad.rightTrigger.onPress(() -> {
            actionThread.add(new TripleLaunchAction(), true, true);
        });

        gamepad.leftTrigger.onPress(() -> {
            intakeAction.set(new TripleIntakeAction(intakeMotor, storageColorSensor));
            actionThread.add(intakeAction.get(), true, true);
        });

        runningActionDisplay = telemetry.addData("runningAction", "");

        gamepad.bButton.onRelease(() -> {
            intakeMotor.setPower(0);
        });
    }

    private RotateRevolverAction.RevolverMode getRevolverMode() {
        return (gamepad.leftBumper.isPressed() ? RotateRevolverAction.RevolverMode.INPUT : RotateRevolverAction.RevolverMode.OUTPUT);
    }

    @Override
    public void loop() {
        driver.setRelativePower(new MovementVector(gamepad.leftJoystick.getY(), gamepad.leftJoystick.getX(), gamepad.rightJoystick.getX()));
        gamepad.loop();
        actionThread.loop();

        if (gamepad.bButton.isPressed()) {
            Action toRemove = null;
            for (Action action : actionThread.getActions()) {
                if (action.getClass() == TripleIntakeAction.class) {
                    toRemove = action;
                }
            }

            actionThread.getActions().remove(toRemove);

            intakeMotor.setPower(-1);
        }

        runningActionDisplay.setValue(RevolverStorageManager.getStateOfChamber(0) + ", " + RevolverStorageManager.getStateOfChamber(1) + ", " + RevolverStorageManager.getStateOfChamber(2));
    }
}
