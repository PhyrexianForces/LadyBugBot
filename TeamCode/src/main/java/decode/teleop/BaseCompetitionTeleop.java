package decode.teleop;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImpl;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.concurrent.atomic.AtomicReference;

import codebase.Constants;
import codebase.actions.Action;
import codebase.actions.CustomAction;
import codebase.actions.MoveToAction;
import codebase.actions.SequentialAction;
import codebase.actions.SimultaneousAction;
import codebase.actions.TripleIntakeAction;
import codebase.actions.TripleLaunchAction;
import codebase.actions.TurnTowardsGoalAction;
import codebase.gamepad.Gamepad;
import codebase.geometry.FieldPosition;
import codebase.geometry.MovementVector;
import codebase.hardware.Motor;
import codebase.hardware.PinpointModule;
import codebase.manipulators.FlywheelManipulator;
import codebase.manipulators.RevolverManipulator;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.PinpointLocalizer;
import codebase.sensors.ColorSensor;
import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;
import decode.auto.AutoConfiguration;

public class BaseCompetitionTeleop extends OpMode {

    private Gamepad gamepad;
    private MecanumDriver driver;
    private SimultaneousAction actionThread;
    private ServoImpl launchServo;
    private Motor launchMotor1;
    private Motor launchMotor2;

    private Motor intakeMotor;

    private Telemetry.Item revolverStateDisplay;
    private Telemetry.Item positionDisplay;

    private ColorSensor storageColorSensor;

    private ServoImpl revolverServo;
    private RevolverManipulator revolverManipulator;

    private PinpointLocalizer localizer;

    AtomicReference<TripleIntakeAction> intakeAction = new AtomicReference<>();

    private FlywheelManipulator flywheelManipulator;

    private LimelightManager limelightManager;

    private Telemetry.Item telem;

    private final AutoConfiguration config = AutoConfiguration.CURRENT_CONFIG;

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

        revolverServo = hardwareMap.get(ServoImpl.class, "revolverServo");
        revolverServo.setPosition(Constants.RevolverConstants.OUTPUT_POSITION_ZERO);

        launchMotor1 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor1"), Constants.FLYWHEEL_MOTOR_CONFIG);
        launchMotor2 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor2"), Constants.FLYWHEEL_MOTOR_CONFIG);
        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");
        launchServo.setPosition(Constants.LAUNCH_SERVO_STORAGE_POSITION);

        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.FORWARD, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.FORWARD, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        localizer.setCurrentFieldPosition(PinpointLocalizer.getLastPosition());

        flywheelManipulator = new FlywheelManipulator(launchMotor1, launchMotor2, localizer, config.alliance);
        flywheelManipulator.init();

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"), Constants.INTAKE_MOTOR_CONFIG);

        gamepad = new Gamepad(gamepad1);
        actionThread = new SimultaneousAction();

        storageColorSensor = new ColorSensor(hardwareMap.get(RevColorSensorV3.class, "colorSensor"));

        revolverManipulator = new RevolverManipulator(revolverServo, intakeMotor);
//        revolverManipulator.init();

        limelightManager = new LimelightManager(hardwareMap.get(Limelight3A.class, "limelight"));

        limelightManager.getLimelight().start();

        gamepad.rightTrigger.onPress(() -> {
//            actionThread.add(new TurnTowardsGoalAction(driver, localizer, limelightManager, config.alliance), true, true);
            actionThread.add(new CustomAction(flywheelManipulator::runFlywheel), true, false);
            actionThread.add(
                new SequentialAction(
                        new TripleLaunchAction(revolverManipulator, launchServo, flywheelManipulator),
                        new CustomAction(flywheelManipulator::stopFlywheel)
                ), true, false
            );
            actionThread.removeActionsOfType(TripleIntakeAction.class);
        });

        gamepad.leftTrigger.onPress(() -> {
            intakeAction.set(new TripleIntakeAction(intakeMotor, storageColorSensor, revolverManipulator));
            actionThread.add(intakeAction.get(), true, true);
        });

        gamepad.leftJoystick.onMove((Float x, Float y) -> {
            actionThread.removeActionsOfType(TurnTowardsGoalAction.class);
        });

        gamepad.rightJoystick.onMove((Float x, Float y) -> {
           actionThread.removeActionsOfType(TurnTowardsGoalAction.class);
        });

        revolverStateDisplay = telemetry.addData("Storage States", "");
        positionDisplay = telemetry.addData("Pos: ", localizer.getCurrentPosition());

        gamepad.bButton.onRelease(() -> {
            intakeMotor.setPower(0);

            for (Action action : actionThread.getActions()) {
                if (action instanceof TripleIntakeAction) {
                    intakeMotor.setVelocity(-Constants.INTAKE_VELOCITY);
                }
            }
        });

        telem = telemetry.addData("target: ", "");
    }

    @Override
    public void loop() {
        driver.setRelativeVelocity(new MovementVector(gamepad.leftJoystick.getY() * 55, gamepad.leftJoystick.getX() * 55, gamepad.rightJoystick.getX() * 5));
        gamepad.loop();
        actionThread.loop();
        revolverManipulator.loop();
        localizer.loop();

        if (gamepad.bButton.isPressed()) {
            intakeMotor.setVelocity(Constants.INTAKE_VELOCITY);
        }

//        double allianceCoefficient = (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);
//        double distanceToGoalInches = Math.sqrt(
//                Math.pow(GOAL_POSITION_RED.x - localizer.getCurrentPosition().x, 2)
//                        + Math.pow((GOAL_POSITION_RED.y * allianceCoefficient) - localizer.getCurrentPosition().y, 2));

//        distanceToGoalInches += 5;

        revolverStateDisplay.setValue(RevolverStorageManager.getStateOfChamber(0) + ", " + RevolverStorageManager.getStateOfChamber(1) + ", " + RevolverStorageManager.getStateOfChamber(2));// + ", " + 6.9 * distanceToGoalInches + 220 + ", " + launchMotor1.getVelocity());
        positionDisplay.setValue("can see goal april tag: " + limelightManager.canSeeGoalAprilTag(config.alliance));
        telem.setValue("error: " + flywheelManipulator.getError() + ", current: " + launchMotor1.getVelocity());
    }
}
