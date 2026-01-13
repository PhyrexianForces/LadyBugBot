package codebase.actions;

import com.qualcomm.robotcore.hardware.Servo;

import codebase.Constants;
import codebase.hardware.Motor;
import codebase.manipulators.RevolverManipulator;

public class LaunchAction extends SequentialAction {
    public LaunchAction(RevolverManipulator revolverManipulator, Servo launchServo, Motor launchMotor1, Motor launchMotor2) {
        super(
                new SetServoRotationAction(launchServo, Constants.LAUNCH_SERVO_STORAGE_POSITION), // reset launcher servo position
                new SimultaneousAction(
                        new SetMotorPowerAction(launchMotor1, 1),
                        new SetMotorPowerAction(launchMotor2, -1)
                ), // start launch motors
                new SleepAction(250), // wait for motors to get up to speed
                new SetServoRotationAction(launchServo, Constants.LAUNCH_SERVO_LAUNCH_POSITION), // push artifact into launcher
                new SleepAction(1000), // wait for ball to launch
                new SimultaneousAction(
                        new SetMotorPowerAction(launchMotor1, 0),
                        new SetMotorPowerAction(launchMotor2, 0),
                        new SetServoRotationAction(launchServo, Constants.LAUNCH_SERVO_STORAGE_POSITION)
                ), // stop launcher motors and reset launch servo
                new SleepAction(300), // wait for launch servo to get back to normal position
                new LaunchUpdateChamberStateAction(revolverManipulator)
        );
    }
}
