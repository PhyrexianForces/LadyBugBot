package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.Constants;
import codebase.gamepad.Gamepad;
import codebase.geometry.FieldPosition;
import codebase.geometry.MovementVector;
import codebase.hardware.Motor;
import codebase.hardware.PinpointModule;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.Localizer;
import codebase.pathing.PinpointLocalizer;

@TeleOp(name="Localizer Test Teleop")
public class LocalizerTestTeleop extends OpMode {
    private Motor fl;
    private Motor fr;
    private Motor bl;
    private Motor br;

    private MecanumDriver driver;
    private Gamepad gamepad;

    private Localizer localizer;

    private Telemetry.Item positionDisplay;

    @Override
    public void init() {
        this.fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"), 1200, 2.5);
        this.fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"), 1200, 2.5);
        this.bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"), 1200, 2.5);
        this.br = new Motor(hardwareMap.get(DcMotorEx.class, "br"), 1200, 2.5);

        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);

        gamepad = new Gamepad(gamepad1);

 // check forward vs reversed, and recheck signs of the vars
        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.FORWARD, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.FORWARD, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        localizer.init();

        positionDisplay = telemetry.addData("pos:", localizer.getCurrentPosition());
    }

    @Override
    public void loop() {
        positionDisplay.setValue(localizer.getCurrentPosition());

        localizer.loop();
        driver.setRelativePower(new MovementVector(gamepad.leftJoystick.getY(), gamepad.leftJoystick.getX(), gamepad.rightJoystick.getX()));

        gamepad.loop();
    }
}
