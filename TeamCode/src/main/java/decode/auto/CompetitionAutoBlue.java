package decode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImpl;

import codebase.Constants;
import codebase.actions.CustomAction;
import codebase.actions.LaunchAction;
import codebase.actions.RotateRevolverAction;
import codebase.actions.SequentialAction;
import codebase.actions.SleepAction;
import codebase.actions.TripleLaunchAction;
import codebase.geometry.MovementVector;
import codebase.hardware.Motor;
import codebase.movement.mecanum.MecanumDriver;
import decode.RevolverStorageManager;

@Autonomous(name="Competition Auto Blue")
public class CompetitionAutoBlue extends OpMode {

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

        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);

        RotateRevolverAction.setRevolverMotor(revolverMotor);
        LaunchAction.setLaunchActionMotors(launchServo, launchMotor1, launchMotor2);

        RevolverStorageManager.resetFull();

        actionThread = new SequentialAction(
                new CustomAction(() -> {
                    driver.setRelativePower(new MovementVector(-0.5, 0, 0));
                }),
                new SleepAction(1050),
                new CustomAction(() -> {
                    driver.stop();
                }),
                new TripleLaunchAction(),
                new CustomAction(() -> {
                    driver.setRelativePower(new MovementVector(0, -0.5, 0));
                }),
                new SleepAction(600),
                new CustomAction(() -> {
                    driver.stop();
                })
        );

        actionThread.init();
    }

    @Override
    public void loop() {
        actionThread.loop();
    }
}
