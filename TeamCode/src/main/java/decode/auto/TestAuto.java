package decode.auto;

import static decode.auto.AutoConfiguration.SpikeMark.HIGH;
import static decode.auto.AutoConfiguration.SpikeMark.LOW;
import static decode.auto.AutoConfiguration.SpikeMark.MIDDLE;

import com.qualcomm.hardware.limelightvision.LLFieldMap;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImpl;

import java.util.List;

import codebase.Constants;
import codebase.actions.Action;
import codebase.actions.CustomAction;
import codebase.actions.EmptyAction;
import codebase.actions.LaunchAction;
import codebase.actions.MoveToAction;
import codebase.actions.RotateRevolverAction;
import codebase.actions.SequentialAction;
import codebase.actions.SimultaneousAction;
import codebase.actions.SleepAction;
import codebase.actions.TripleIntakeAction;
import codebase.actions.TripleLaunchAction;
import codebase.geometry.FieldPosition;
import codebase.geometry.MovementVector;
import codebase.hardware.Motor;
import codebase.hardware.PinpointModule;
import codebase.manipulators.RevolverManipulator;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.Localizer;
import codebase.pathing.PinpointLocalizer;
import codebase.sensors.ColorSensor;
import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

@Autonomous(name="Test Auto")
public class TestAuto extends OpMode {

    private final AutoConfiguration config = AutoConfiguration.CURRENT_CONFIG;

    private SequentialAction actionThread;

    private ServoImpl launchServo;
    private Motor launchMotor1;
    private Motor launchMotor2;

    private Motor intakeMotor;

    private MecanumDriver driver;

    private PinpointLocalizer localizer;

    private ColorSensor storageColorSensor;

    private LimelightManager limelightManager;

    private RevolverManipulator revolverManipulator;

    private MoveToAction moveToAction;  // Store reference for telemetry

    @Override
    public void init() {
        driver = new MecanumDriver(
                new Motor(hardwareMap.get(DcMotorEx.class, "fl"), Constants.DRIVE_MOTOR_CONFIG),
                new Motor(hardwareMap.get(DcMotorEx.class, "fr"), Constants.DRIVE_MOTOR_CONFIG),
                new Motor(hardwareMap.get(DcMotorEx.class, "bl"), Constants.DRIVE_MOTOR_CONFIG),
                new Motor(hardwareMap.get(DcMotorEx.class, "br"), Constants.DRIVE_MOTOR_CONFIG),
                Constants.MECANUM_COEFFICIENT_MATRIX,
                Constants.MAX_WHEEL_VELOCITY
        );

        Motor revolverMotor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION, 1, false);
//        revolverManipulator = new RevolverManipulator(revolverMotor);

        launchMotor1 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor1"));
        launchMotor2 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor2"));
        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");
        launchServo.setPosition(Constants.LAUNCH_SERVO_STORAGE_POSITION);

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"));

        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.FORWARD, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.FORWARD, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        localizer.init();

        do { localizer.loop(); } while (!localizer.isDoneInitializing());

        localizer.setCurrentFieldPosition(new FieldPosition(-72 + (14 + 4 * Math.sqrt(2)), 72 - (14 + 4 * Math.sqrt(2)), Math.toRadians(214)));

        storageColorSensor = new ColorSensor(hardwareMap.get(RevColorSensorV3.class, "colorSensor"));

        limelightManager = new LimelightManager(hardwareMap.get(Limelight3A.class, "limelight"));

        limelightManager.getLimelight().start();

        moveToAction = new MoveToAction(driver, localizer, new FieldPosition(0, 0, 0), 1, 1, 2, Math.toRadians(5));

        actionThread = new SequentialAction(moveToAction);

        actionThread.init();

        RevolverStorageManager.resetFull();
    }

    @Override
    public void loop() {
        actionThread.loop();
        localizer.loop();
//        revolverManipulator.loop();

        // Display error telemetry
        telemetry.addData("Error X", moveToAction.getErrorX());
        telemetry.addData("Error Y", moveToAction.getErrorY());
        telemetry.addData("Error Direction (deg)", Math.toDegrees(moveToAction.getErrorDirection()));
        telemetry.update();
    }
}