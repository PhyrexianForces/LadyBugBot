package codebase.actions;

import com.qualcomm.robotcore.hardware.Servo;

import codebase.hardware.Motor;

public class LaunchAction extends SequentialAction {

    private static Servo launchServo;
    private static Motor launchMotor1;
    private static Motor launchMotor2;

    public LaunchAction() {
        super(
                new SetServoRotationAction(launchServo, 1), // reset launcher servo position
                new SimultaneousAction(
                        new SetMotorPowerAction(launchMotor1, 1),
                        new SetMotorPowerAction(launchMotor2, -1)
                ), // start launch motors
                new SleepAction(250), // wait for motors to get up to speed
                new SetServoRotationAction(launchServo, 0), // push artifact into launcher
                new SleepAction(1000), // wait for ball to launch
                new SimultaneousAction(
                        new SetMotorPowerAction(launchMotor1, 0),
                        new SetMotorPowerAction(launchMotor2, 0),
                        new SetServoRotationAction(launchServo, 1)
                ), // stop launcher motors and reset launch servo
                new SleepAction(300), // wait for launch servo to get back to normal position
                new LaunchUpdateChamberStateAction()
        );

        if (launchServo == null || launchMotor1 == null || launchMotor2 == null) {
            throw new IllegalStateException("Can not use a Launch Action before setup through LaunchAction.setLaunchActionMotors");
        }
    }

    public static void setLaunchActionMotors(Servo launchServo, Motor launchMotor1, Motor launchMotor2) {
        LaunchAction.launchServo = launchServo;
        LaunchAction.launchMotor1 = launchMotor1;
        LaunchAction.launchMotor2 = launchMotor2;
    }
}
