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
import codebase.actions.RotateRevolverAction;
import codebase.actions.SimultaneousAction;
import codebase.actions.TripleIntakeAction;
import codebase.actions.TripleLaunchAction;
import codebase.gamepad.Gamepad;
import codebase.geometry.MovementVector;
import codebase.hardware.Motor;
import codebase.manipulators.RevolverManipulator;
import codebase.movement.mecanum.MecanumDriver;
import codebase.sensors.ColorSensor;
import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

public class BaseCompetitionTeleop extends OpMode {

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

    private Telemetry.Item revolverStateDisplay;

    private ColorSensor storageColorSensor;

    private RevolverManipulator revolverManipulator;

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
        launchServo.setPosition(Constants.LAUNCH_SERVO_STORAGE_POSITION);

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"));

        gamepad = new Gamepad(gamepad1);
        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);
        actionThread = new SimultaneousAction();

        storageColorSensor = new ColorSensor(hardwareMap.get(RevColorSensorV3.class, "colorSensor"));

        RevolverStorageManager.setMotif(LimelightManager.Motif.PPG);

        revolverManipulator = new RevolverManipulator(revolverMotor);
        revolverManipulator.init();

        gamepad.rightTrigger.onPress(() -> {
            actionThread.add(new TripleLaunchAction(revolverManipulator, launchServo, launchMotor1, launchMotor2), true, true);
        });

        gamepad.leftTrigger.onPress(() -> {
            intakeAction.set(new TripleIntakeAction(intakeMotor, storageColorSensor, revolverManipulator));
            actionThread.add(intakeAction.get(), true, true);
        });

        revolverStateDisplay = telemetry.addData("Storage States", "");

        gamepad.bButton.onRelease(() -> {
            intakeMotor.setPower(0);

            for (Action action : actionThread.getActions()) {
                if (action instanceof TripleIntakeAction) {
                    intakeMotor.setPower(-Constants.INTAKE_POWER);
                }
            }
        });
    }

    @Override
    public void loop() {
        driver.setRelativePower(new MovementVector(gamepad.leftJoystick.getY(), gamepad.leftJoystick.getX(), gamepad.rightJoystick.getX() * 0.5));
        gamepad.loop();
        actionThread.loop();
        revolverManipulator.loop();

        if (gamepad.bButton.isPressed()) {
            intakeMotor.setPower(Constants.INTAKE_POWER);
        }

        revolverStateDisplay.setValue(RevolverStorageManager.getStateOfChamber(0) + ", " + RevolverStorageManager.getStateOfChamber(1) + ", " + RevolverStorageManager.getStateOfChamber(2));
    }
}
