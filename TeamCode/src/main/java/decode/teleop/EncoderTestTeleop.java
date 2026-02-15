package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.Constants;
import codebase.controllers.PIDController;
import codebase.gamepad.Gamepad;
import codebase.geometry.Angles;
import codebase.hardware.Motor;

@TeleOp(name="Encoder Test Teleop")
public class EncoderTestTeleop extends OpMode {

    private Motor motor;

    private Telemetry.Item encoderDisplay;
    private Telemetry.Item PIDPowerDisplay;

    private PIDController revolverPID;

    private Motor revolverMotor;

    private Gamepad gamepad;

    @Override
    public void init() {
        motor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION, 1, false);

        encoderDisplay = telemetry.addData("encoder position", 0);
        PIDPowerDisplay = telemetry.addData("PID power", 0);

        revolverMotor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION, 1, false);
        gamepad = new Gamepad(gamepad1);

        revolverMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        revolverPID = new PIDController(new PIDCoefficients(1, 0, 0), () -> Angles.angleDifference(motor.getMotorEncoder().getPosition(), 2.09439510239));

        telemetry.setMsTransmissionInterval(10);

        System.out.println("Start of new code");
    }

    @Override
    public void loop() {
        encoderDisplay.setValue(motor.getMotorEncoder().getPosition() + ", " + Angles.angleDifference(motor.getMotorEncoder().getPosition(), 2.09439510239));

        double pidPower = revolverPID.getPower();
        PIDPowerDisplay.setValue(pidPower);

//        System.out.println("pid power: " + pidPower + ", angle dif: " + Angles.angleDifference(motor.getMotorEncoder().getPosition(), 2.09439510239) + ", servo pos: " + motor.getMotorEncoder().getPosition());

        if (gamepad.rightTrigger.getValue() >= 0.1) {
            revolverMotor.setPower(pidPower * gamepad.rightTrigger.getValue());
            revolverMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } else {
            revolverMotor.setPower(0);
            revolverMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }
}
