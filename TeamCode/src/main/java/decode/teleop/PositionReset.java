package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import codebase.hardware.Motor;
import codebase.pathing.PinpointLocalizer;

@TeleOp(name="Position Reset")
public class PositionReset extends OpMode {

    private Motor revolverMotor;

    @Override
    public void init() {
        PinpointLocalizer.resetLastPosition();
    }

    @Override
    public void loop() {}
}
