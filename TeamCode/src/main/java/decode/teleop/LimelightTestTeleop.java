package decode.teleop;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.vision.LimelightManager;

@TeleOp(name="LimelightTest")
public class LimelightTestTeleop extends OpMode {
    private LimelightManager limelightManager;
    private Telemetry.Item resultsDisplay;

    @Override
    public void init() {
        limelightManager = new LimelightManager(hardwareMap.get(Limelight3A.class, "limelight"));

        limelightManager.getLimelight().start();

        resultsDisplay = telemetry.addData("results", "");

        System.out.println("Limelight connected: " + limelightManager.getLimelight().isConnected());
    }

    @Override
    public void loop() {
        resultsDisplay.setValue(limelightManager.getVisibleAprilTagIds().size());
    }
}
