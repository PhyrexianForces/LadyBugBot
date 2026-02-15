package decode.teleop;

import androidx.core.view.TintableBackgroundView;

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
import codebase.manipulators.RevolverManipulator;
import codebase.movement.mecanum.MecanumDriver;
import codebase.sensors.ColorSensor;
import decode.RevolverStorageManager;

@TeleOp(name="Revolver Test Teleop")
public class RevolverTestTeleop extends OpMode {

    private Gamepad gamepad;
    private SimultaneousAction actionThread;

    private RevolverManipulator revolverManipulator;

    private Motor intakeMotor;


    @Override
    public void init() {

        gamepad = new Gamepad(gamepad1);

        actionThread = new SimultaneousAction();

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"), Constants.INTAKE_MOTOR_CONFIG);

        ServoImpl revolverServo = hardwareMap.get(ServoImpl.class, "revolverServo");
        revolverManipulator = new RevolverManipulator(revolverServo, intakeMotor);

        gamepad.dpadLeft.onPress(() -> {
            actionThread.add(new RotateRevolverAction(0, getRevolverMode(), revolverManipulator), true, true);
        });

        gamepad.dpadUp.onPress(() -> {
             actionThread.add(new RotateRevolverAction(1, getRevolverMode(), revolverManipulator), true, true);
        });

        gamepad.dpadRight.onPress(() -> {
            actionThread.add(new RotateRevolverAction(2, getRevolverMode(), revolverManipulator), true, true);
        });
    }

    private RevolverManipulator.RevolverMode getRevolverMode() {
        return (gamepad.leftBumper.isPressed() ? RevolverManipulator.RevolverMode.INPUT : RevolverManipulator.RevolverMode.OUTPUT);
    }

    @Override
    public void loop() {
        gamepad.loop();
        actionThread.loop();
        revolverManipulator.loop();
    }
}
