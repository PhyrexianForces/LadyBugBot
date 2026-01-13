package decode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import codebase.Constants;
import codebase.actions.MoveToAction;
import codebase.actions.SequentialAction;
import codebase.geometry.FieldPosition;
import codebase.hardware.Motor;
import codebase.hardware.PinpointModule;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.PinpointLocalizer;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import codebase.Constants;
import codebase.actions.MoveToAction;
import codebase.actions.SequentialAction;
import codebase.geometry.FieldPosition;
import codebase.hardware.Motor;
import codebase.hardware.PinpointModule;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.PinpointLocalizer;

@Autonomous(name="New Auto")
public class NewAuto extends OpMode {

    private SequentialAction actionThread;

    private Motor fl;
    private Motor fr;
    private Motor bl;
    private Motor br;

    private MecanumDriver driver;

    private PinpointLocalizer localizer;

    @Override
    public void init() {
        fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"));
        fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"));
        bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"));
        br = new Motor(hardwareMap.get(DcMotorEx.class, "br"));

        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);

        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.FORWARD, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.REVERSED, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        localizer.init();

        actionThread = new SequentialAction(
                new MoveToAction(driver, localizer, new FieldPosition(5, 0, 0), 1, 1, 0.1, Math.PI / 180)
        );
        actionThread.init();

    }

    @Override
    public void loop() {
        actionThread.loop();
        localizer.loop();
    }
}
