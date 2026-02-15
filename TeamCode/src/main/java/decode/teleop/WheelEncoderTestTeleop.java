package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Wheel Encoder Test Telep", group = "Test")
public class WheelEncoderTestTeleop extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void runOpMode() {
        // Initialize motors from hardware map
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        // Set motor modes to use encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addLine("Encoders Reset. Ready to start.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Read encoder positions in ticks
            int flTicks = frontLeft.getCurrentPosition();
            int frTicks = frontRight.getCurrentPosition();
            int blTicks = backLeft.getCurrentPosition();
            int brTicks = backRight.getCurrentPosition();

            // Display on telemetry
            telemetry.addLine("=== Encoder Positions (ticks) ===");
            telemetry.addData("Front Left (fl)", flTicks);
            telemetry.addData("Front Right (fr)", frTicks);
            telemetry.addData("Back Left (bl)", blTicks);
            telemetry.addData("Back Right (br)", brTicks);
            telemetry.addLine();
            telemetry.addData("Total", flTicks + frTicks + blTicks + brTicks);
            telemetry.update();
        }
    }
}