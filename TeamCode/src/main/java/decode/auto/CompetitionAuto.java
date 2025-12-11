package decode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImpl;

import codebase.Constants;
import codebase.actions.CustomAction;
import codebase.actions.LaunchAction;
import codebase.actions.MoveToAction;
import codebase.actions.RotateRevolverAction;
import codebase.actions.SequentialAction;
import codebase.actions.SleepAction;
import codebase.actions.TripleLaunchAction;
import codebase.geometry.FieldPosition;
import codebase.geometry.MovementVector;
import codebase.hardware.Motor;
import codebase.hardware.PinpointModule;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.Localizer;
import codebase.pathing.PinpointLocalizer;
import decode.RevolverStorageManager;

@Autonomous(name="Competition Auto Red")
public class CompetitionAuto extends OpMode {

    private AutoConfiguration config = AutoConfiguration.TESTING_CONFIG_RED;

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

    private Localizer localizer;

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

        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.REVERSED, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.REVERSED, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        localizer.init(getStartPosition(config));

        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);

        RotateRevolverAction.setRevolverMotor(revolverMotor);
        LaunchAction.setLaunchActionMotors(launchServo, launchMotor1, launchMotor2);

        RevolverStorageManager.resetFull();

        actionThread = new SequentialAction(
            new MoveToAction(new FieldPosition(0, 0, 0), 1, 1, 1, Math.PI * 4 / 180)
        );

        actionThread.init();
    }

    private FieldPosition getStartPosition(AutoConfiguration config) {
        FieldPosition startPosition;
        if (config.startPosition == AutoConfiguration.StartPosition.GOAL) {
            startPosition = new FieldPosition(-72 + (14 + 4 * Math.sqrt(2)), 72 - (14 + 4 * Math.sqrt(2)), -Math.PI / 4);
        } else {
            startPosition = new FieldPosition(72 - 7.5, 0, 0);
        }

        startPosition.y = startPosition.y * (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);

        return startPosition;
    }

    @Override
    public void loop() {
        actionThread.loop();
    }
}
