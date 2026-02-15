package decode.teleop;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.Constants;
import codebase.sensors.ColorSensor;


@TeleOp(name="Color Sensor Test", group="Test")
public class ColorSensorTestTeleop extends OpMode {
    private Telemetry.Item colorDisplay;
    private ColorSensor colorSensor;

    @Override
    public void init() {
        colorSensor = new ColorSensor(hardwareMap.get(RevColorSensorV3.class, "colorSensor"));
        colorDisplay = telemetry.addData("distance", 0 + " in");
    }

    @Override
    public void loop() {
        colorDisplay.setValue(colorSensor.getDistance() + " in, with color: " + colorSensor.getColor() + ", green? " + (colorSensor.getColor().red <= Constants.ARTIFACT_GREEN_THRESHOLD_RED));
    }
}