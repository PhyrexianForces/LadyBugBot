package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ServoImpl;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.actions.SetServoRotationAction;
import codebase.actions.SimultaneousAction;
import codebase.gamepad.Gamepad;

@TeleOp(name="Servo Test Teleop")
public class ServoTestTeleop extends OpMode {
    private Gamepad gamepad;
    private SimultaneousAction actionThread;
    private ServoImpl servo;

    private double servoPosition = 1;

    private Telemetry.Item positionDisplay;

    private int selectedIndex = 0;

    @Override
    public void init() {
        servo = hardwareMap.get(ServoImpl.class, "revolverServo");

        gamepad = new Gamepad(gamepad1);
        actionThread = new SimultaneousAction();

        gamepad.rightBumper.onPress(() -> {
            servoPosition += 0.01;
            updateServoPosition();
        });

        gamepad.leftBumper.onPress(() -> {
            servoPosition -= 0.01;
            updateServoPosition();
        });

        gamepad.rightTrigger.onPress(() -> {
            servoPosition += 0.1;
            updateServoPosition();
        });

        gamepad.leftTrigger.onPress(() -> {
            servoPosition -= 0.1;
            updateServoPosition();
        });

        positionDisplay = telemetry.addData("Position", servoPosition);
    }

    private void updateServoPosition() {
        if (servoPosition > 1) {
            servoPosition = 1;
        }
        if (servoPosition < 0) {
            servoPosition = 0;
        }
        actionThread.add(
                new SetServoRotationAction(
                        servo,
                        servoPosition
                ),
                true,
                true
        );

        positionDisplay.setValue(servoPosition);
    }

    @Override
    public void loop() {
        gamepad.loop();
        actionThread.loop();
    }
}