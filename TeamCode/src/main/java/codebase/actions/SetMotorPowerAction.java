package codebase.actions;

import codebase.hardware.Motor;

public class SetMotorPowerAction extends RunOnceAction {

    private final Motor motor;
    private final double power;

    public SetMotorPowerAction(Motor motor, double power) {
        this.motor = motor;
        this.power = power;
    }

    @Override
    public void init() {}

    @Override
    public void run() {
        motor.setPower(power);
    }
}
