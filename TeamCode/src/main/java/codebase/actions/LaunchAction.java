package codebase.actions;

import com.qualcomm.robotcore.hardware.Servo;

import codebase.Constants;
import codebase.hardware.Motor;
import codebase.manipulators.FlywheelManipulator;
import codebase.manipulators.RevolverManipulator;
import decode.RevolverStorageManager;

public class LaunchAction extends SequentialAction {
    public LaunchAction(RevolverManipulator revolverManipulator, Servo launchServo, FlywheelManipulator flywheelManipulator) {
        super(
                new SetServoRotationAction(launchServo, Constants.LAUNCH_SERVO_STORAGE_POSITION), // reset launcher servo position
                new WaitForFlywheelSpeedAction(flywheelManipulator),
                new SetServoRotationAction(launchServo, Constants.LAUNCH_SERVO_LAUNCH_POSITION), // push artifact into launcher
                new SleepAction(450), // wait for servo to go up
                new SetServoRotationAction(launchServo, Constants.LAUNCH_SERVO_STORAGE_POSITION),
                new SleepAction(550), // wait for launch servo to get back to normal position
                new LaunchUpdateChamberStateAction(revolverManipulator)
        );
    }
}
