package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import codebase.Constants;

import codebase.hardware.Motor;

@TeleOp(name="Encoder Calibration Teleop")
public class EncoderCalibrationTeleop extends OpMode {

    private Motor revolverMotor;

    @Override
    public void init() {
        revolverMotor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_5203_2402_0019_TICKS_PER_ROTATION);
        revolverMotor.getMotorEncoder().reset();
    }

    @Override
    public void loop() {}
}
