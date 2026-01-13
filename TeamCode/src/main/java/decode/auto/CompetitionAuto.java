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

    @Override
    public void init() {
        driver = new MecanumDriver(
                new Motor(hardwareMap.get(DcMotorEx.class, "fl")),
                new Motor(hardwareMap.get(DcMotorEx.class, "fr")),
                new Motor(hardwareMap.get(DcMotorEx.class, "bl")),
                new Motor(hardwareMap.get(DcMotorEx.class, "br")),
                Constants.MECANUM_COEFFICIENT_MATRIX
        );

        Motor revolverMotor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_5203_2402_0019_TICKS_PER_ROTATION);
        revolverManipulator = new RevolverManipulator(revolverMotor);
        revolverManipulator.init();

        launchMotor1 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor1"));
        launchMotor2 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor2"));
        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");
        launchServo.setPosition(Constants.LAUNCH_SERVO_STORAGE_POSITION);

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"));

        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.FORWARD, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.FORWARD, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        localizer.init();

        do { localizer.loop(); } while (!localizer.isDoneInitializing());

        localizer.setCurrentFieldPosition(getStartPosition());

        storageColorSensor = new ColorSensor(hardwareMap.get(RevColorSensorV3.class, "colorSensor"));

        limelightManager = new LimelightManager(hardwareMap.get(Limelight3A.class, "limelight"));

        limelightManager.getLimelight().start();

        actionThread = new SequentialAction(
            readMotif(),
            moveToLaunchPosition(),
//            new TripleLaunchAction(revolverManipulator, launchServo, launchMotor1, launchMotor2),
            cycleConfiguredSpikeMarks(),
            moveOutOfLaunchZone()
        );

        actionThread.init();

        RevolverStorageManager.resetFull();
    }

    private FieldPosition getStartPosition() {
        FieldPosition startPosition;
        if (config.startPosition == AutoConfiguration.StartPosition.GOAL) {
            startPosition = new FieldPosition(-72 + (14 + 4 * Math.sqrt(2)), 72 - (14 + 4 * Math.sqrt(2)), Math.PI * (3.0)/(4.0));
        } else {
            startPosition = new FieldPosition(72 - 7.5, 0, Math.PI);
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
//               RevolverStorageManager.setMotif(limelightManager.getMotif());
//               if (RevolverStorageManager.getMotif() == LimelightManager.Motif.NOT_FOUND) {
//                   RevolverStorageManager.setMotif(LimelightManager.Motif.PGP);
//               }
            })
        );
    }

    private MoveToAction moveToLaunchPosition() {
        int allianceCoefficient = (config.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1);

        FieldPosition launchPosition = new FieldPosition(-30, 30 * allianceCoefficient, Math.PI * (3.0)/(4.0) * allianceCoefficient);

        return new MoveToAction(driver, localizer, launchPosition, 1, 1, 3, Math.toRadians(4));
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

        double spikeMarkX = (spikeMark == HIGH ? -12 : (spikeMark == MIDDLE ? 12 : 36));
        double spikeMarkAlignmentY = 29 * allianceCoefficient;
        double spikeMarkPickUpY = 46 * allianceCoefficient;
        double spikeMarkRotation = (Math.PI / 2) * allianceCoefficient;

        return new SequentialAction(
            new MoveToAction(driver, localizer, new FieldPosition(spikeMarkX, spikeMarkAlignmentY, spikeMarkRotation), 1, 1, 5, Math.toRadians(5)),
            new SimultaneousAction(
//                new TripleIntakeAction(intakeMotor, storageColorSensor, revolverManipulator),
                new MoveToAction(driver, localizer, new FieldPosition(spikeMarkX, spikeMarkPickUpY, spikeMarkRotation), 1, 1, 1.5, Math.toRadians(5))
            ),
            moveToLaunchPosition()//,
//            new TripleLaunchAction(revolverManipulator, launchServo, launchMotor1, launchMotor2)
        );
    }

    private MoveToAction moveOutOfLaunchZone() {
        FieldPosition outPosition = new FieldPosition(0, 43, 0);

        return new MoveToAction(driver, localizer, outPosition, 1, 1, 5, 8);
    }

    @Override
    public void loop() {
        actionThread.loop();
        localizer.loop();
        revolverManipulator.loop();
    }
}
