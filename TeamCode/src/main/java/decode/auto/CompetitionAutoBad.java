package decode.auto;

import static decode.auto.AutoConfiguration.SpikeMark.HIGH;
import static decode.auto.AutoConfiguration.SpikeMark.LOW;
import static decode.auto.AutoConfiguration.SpikeMark.MIDDLE;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImpl;

import codebase.Constants;
import codebase.actions.CustomAction;
import codebase.actions.EmptyAction;
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
import codebase.pathing.PinpointLocalizer;
import codebase.sensors.ColorSensor;
import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

@Autonomous(name="Competition Auto Bad")
public class CompetitionAutoBad extends OpMode {

    private AutoConfiguration config = AutoConfiguration.CURRENT_CONFIG;

    private SequentialAction actionThread;

    private Motor fl;
    private Motor fr;
    private Motor bl;
    private Motor br;

    private Motor revolverMotor;

    private ServoImpl launchServo;
    private Motor launchMotor1;
    private Motor launchMotor2;

    private MecanumDriver driver;

    private RevolverManipulator revolverManipulator;

    @Override
    public void init() {
        fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"));
        fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"));
        bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"));
        br = new Motor(hardwareMap.get(DcMotorEx.class, "br"));

        revolverMotor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION, 1, false);

        launchMotor1 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor1"));
        launchMotor2 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor2"));
        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");
        launchServo.setPosition(Constants.LAUNCH_SERVO_STORAGE_POSITION);

        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);

//        revolverManipulator = new RevolverManipulator(revolverMotor);
//        revolverManipulator.init();

        actionThread = new SequentialAction(
            new CustomAction(() -> {
                driver.setRelativePower(new MovementVector(-0.5, 0, 0));
            }),
            new SleepAction(400),
            new CustomAction(() -> {
                driver.setRelativePower(new MovementVector(-0.3, 0, 0));
            }),
            new SleepAction(450),
            new CustomAction(() -> {
                driver.stop();
            }),
//            new TripleLaunchAction(revolverManipulator, launchServo, launchMotor1, launchMotor2, ),
            new CustomAction(() -> {
                driver.setRelativePower(new MovementVector(-0.5, 0.9 * (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1), 0));
            }),
            new SleepAction(1000),
            new CustomAction(() -> {
                driver.stop();
            })
        );

        actionThread.init();

        RevolverStorageManager.resetFull();
    }

    @Override
    public void loop() {
        actionThread.loop();
        revolverManipulator.loop();
    }
}
