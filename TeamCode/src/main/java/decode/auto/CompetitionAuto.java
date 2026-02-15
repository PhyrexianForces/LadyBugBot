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

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.Constants;
import codebase.actions.CustomAction;
import codebase.actions.EmptyAction;
import codebase.actions.MoveToAction;
import codebase.actions.SequentialAction;
import codebase.actions.SimultaneousAction;
import codebase.actions.SleepAction;
import codebase.actions.TripleIntakeAction;
import codebase.actions.TripleLaunchAction;
import codebase.geometry.FieldPosition;
import codebase.hardware.Motor;
import codebase.hardware.PinpointModule;
import codebase.manipulators.FlywheelManipulator;
import codebase.manipulators.RevolverManipulator;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.PinpointLocalizer;
import codebase.sensors.ColorSensor;
import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

@Autonomous(name="Competition Auto")
public class CompetitionAuto extends OpMode {

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

    private FlywheelManipulator flywheelManipulator;

    private Telemetry.Item telem;

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



        launchMotor1 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor1"), Constants.FLYWHEEL_MOTOR_CONFIG);
        launchMotor2 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor2"), Constants.FLYWHEEL_MOTOR_CONFIG);

        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");
        launchServo.setPosition(Constants.LAUNCH_SERVO_STORAGE_POSITION);

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"), Constants.INTAKE_MOTOR_CONFIG);

        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.FORWARD, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.FORWARD, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        localizer.init();

        do { localizer.loop(); } while (!localizer.isDoneInitializing());

        localizer.setCurrentFieldPosition(getStartPosition());

        storageColorSensor = new ColorSensor(hardwareMap.get(RevColorSensorV3.class, "colorSensor"));

        limelightManager = new LimelightManager(hardwareMap.get(Limelight3A.class, "limelight"));

        limelightManager.getLimelight().start();

        flywheelManipulator = new FlywheelManipulator(launchMotor1, launchMotor2, localizer, config.alliance);
        flywheelManipulator.init();

        ServoImpl revolverServo = hardwareMap.get(ServoImpl.class, "revolverServo");
        revolverManipulator = new RevolverManipulator(revolverServo, intakeMotor);

        RevolverStorageManager.resetFull();

        actionThread = new SequentialAction(
            readMotif(),
            new CustomAction(flywheelManipulator::runFlywheel),
            moveToLaunchPosition(),
            new TripleLaunchAction(revolverManipulator, launchServo, flywheelManipulator),
            cycleConfiguredSpikeMarks(),
            moveOutOfLaunchZone()
        );

        actionThread.init();

        telem = telemetry.addData("target: ", "");

    }

    private FieldPosition getStartPosition() {
        FieldPosition startPosition;
        if (config.startPosition == AutoConfiguration.StartPosition.GOAL) {
            startPosition = new FieldPosition(-72 + (14 + 4 * Math.sqrt(2)), 72 - (14 + 4 * Math.sqrt(2)), Math.toRadians(214));
        } else {
            startPosition = new FieldPosition(72 - 7.5, 15, Math.PI);
        }

        int allianceCoefficient = (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);

        startPosition.y *= allianceCoefficient;
        startPosition.direction *= allianceCoefficient;

        return startPosition;
    }

    private SequentialAction readMotif() {
        int allianceCoefficient = (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);

        FieldPosition motifReadPosition = new FieldPosition(-30, 30 * allianceCoefficient, Math.PI * (7.0)/(6.0) * allianceCoefficient);

        return new SequentialAction(
            new MoveToAction(driver, localizer, motifReadPosition, 1, 1, 3, Math.toRadians(4)),
            new CustomAction(() -> {
                double startTime = System.currentTimeMillis();
                while(limelightManager.getVisibleAprilTagIds().isEmpty() || RevolverStorageManager.getMotif() == LimelightManager.Motif.NOT_FOUND) {
                    RevolverStorageManager.setMotif(limelightManager.getMotif());
                    System.out.println("Found motif of: " + limelightManager.getMotif());
                    if (System.currentTimeMillis() - startTime >= 1000) {
                        RevolverStorageManager.setMotif(LimelightManager.Motif.PPG);
                        break;
                    }
                }
            })
        );
    }

    private MoveToAction moveToLaunchPosition() {
        int allianceCoefficient = (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);

        FieldPosition launchPosition;

        if (config.startPosition == AutoConfiguration.StartPosition.GOAL) {
            launchPosition = new FieldPosition(-10, 10 * allianceCoefficient, 2.2 * allianceCoefficient);
        } else {
            launchPosition = new FieldPosition(60, 12 * allianceCoefficient, 2.768 * allianceCoefficient);
        }

        return new MoveToAction(driver, localizer, launchPosition, 1, 1, 1, Math.toRadians(2));
    }

    private SequentialAction cycleConfiguredSpikeMarks() {
        return new SequentialAction(
            (config.spikeMarks.contains(HIGH) ? cycleSpikeMark(HIGH) : new EmptyAction()),
            (config.spikeMarks.contains(MIDDLE) ? cycleSpikeMark(MIDDLE) : new EmptyAction()),
            (config.spikeMarks.contains(LOW) ? cycleSpikeMark(LOW) : new EmptyAction())
        );
    }

    private SequentialAction cycleSpikeMark(AutoConfiguration.SpikeMark spikeMark) {
        int allianceCoefficient = (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);

        double spikeMarkX = (spikeMark == HIGH ? -7 : (spikeMark == MIDDLE ? 12 : 36));
        double spikeMarkAlignmentY = 27 * allianceCoefficient;
        double spikeMarkPickUpYFirst = 48 * allianceCoefficient;
        double spikeMarkPickUpYLast = 54 * allianceCoefficient;
        double spikeMarkRotation = (Math.PI / 2) * allianceCoefficient;

        return new SequentialAction(
            new MoveToAction(driver, localizer, new FieldPosition(spikeMarkX, spikeMarkAlignmentY, spikeMarkRotation), 1, 1, 1.5, Math.toRadians(3)),
            new SimultaneousAction(
                new TripleIntakeAction(intakeMotor, storageColorSensor, revolverManipulator),
                new MoveToAction(driver, localizer, new FieldPosition(spikeMarkX, spikeMarkPickUpYFirst, spikeMarkRotation), 0.3, 1, 1, Math.toRadians(3)),
                new SleepAction(500),
                new MoveToAction(driver, localizer, new FieldPosition(spikeMarkX, spikeMarkPickUpYLast, spikeMarkRotation), 0.3, 1, 1, Math.toRadians(3))
            ),
            new CustomAction(flywheelManipulator::runFlywheel),
            moveToLaunchPosition(),
            new TripleLaunchAction(revolverManipulator, launchServo, flywheelManipulator)
        );
    }

    private MoveToAction moveOutOfLaunchZone() {
        FieldPosition outPosition = new FieldPosition(-16, 45, Math.PI);
        outPosition.y *= (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);
        return new MoveToAction(driver, localizer, outPosition, 1, 1, 5, 8);
    }

    @Override
    public void loop() {
        actionThread.loop();
        localizer.loop();
        revolverManipulator.loop();

        telem.setValue("error: " + flywheelManipulator.getError() + ", current: " + launchMotor1.getVelocity());
    }
}
