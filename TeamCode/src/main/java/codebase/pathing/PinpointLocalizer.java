package codebase.pathing;

import com.qualcomm.hardware.lynx.LynxNackException;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import codebase.geometry.FieldPosition;
import codebase.geometry.MovementVector;
import codebase.hardware.PinpointModule;

public class PinpointLocalizer implements Localizer {

    private final PinpointModule pinpointModule;

    /**
     *
     * The center of rotation of the robot is the point it rotates around when spinning.
     * It can be found by finding the intersection of the two lines made by diagonal wheel pairs.
     *
     * @param pinpointModule The PinPoint device
     * @param xPodOffsetFromCenter The distance in the x-direction (right is positive) of the forward-backward pod from the robot's center of rotation
     * @param xDirection The direction the y-pod (which measures delta in the y direction) is oriented
     * @param yPodOffsetFromCenter The distance in the y-direction (forward is positive) of the right-left pod from the robot's center of rotation
     * @param yDirection The direction the x-pod (which measures delta in the x direction) is oriented
     * @param encoderResolution The number of ticks per mm of the encoders attached to the PinPoint
     */
    public PinpointLocalizer(PinpointModule pinpointModule, double xPodOffsetFromCenter, PinpointModule.EncoderDirection xDirection, double yPodOffsetFromCenter, PinpointModule.EncoderDirection yDirection, double encoderResolution) {
        this.pinpointModule = pinpointModule;
        this.pinpointModule.setEncoderResolution(encoderResolution);
        this.pinpointModule.setOffsets(-xPodOffsetFromCenter * 25.4,yPodOffsetFromCenter * 25.4);
        this.pinpointModule.setEncoderDirections(xDirection, yDirection);
    }

    /**
     *
     * The center of rotation of the robot is the point it rotates around when spinning.
     * It can be found by finding the intersection of the two lines made by diagonal wheel pairs.
     *
     * @param pinpointModule The PinPoint device
     * @param xPodOffsetFromCenter The distance in the x-direction (right is positive) of the forward-backward pod from the robot's center of rotation
     * @param xDirection The direction the y-pod (which measures delta in the y direction) is oriented
     * @param yPodOffsetFromCenter The distance in the y-direction (forward is positive) of the right-left pod from the robot's center of rotation
     * @param yDirection The direction the x-pod (which measures delta in the x direction) is oriented
     * @param pods The type of pods you are using
     *
     *             THIS IS NOT DONE // REVISIT X AND Y AS WE SWITCHED TO FTC FIELD COORDINATE SYSTEM
     */
    public PinpointLocalizer(PinpointModule pinpointModule, double xPodOffsetFromCenter, PinpointModule.EncoderDirection xDirection, double yPodOffsetFromCenter, PinpointModule.EncoderDirection yDirection, PinpointModule.GoBildaOdometryPods pods) {
        this.pinpointModule = pinpointModule;
        this.pinpointModule.setEncoderResolution(pods);
        this.pinpointModule.setOffsets(-xPodOffsetFromCenter * 25.4,yPodOffsetFromCenter * 25.4);
        this.pinpointModule.setEncoderDirections(xDirection,yDirection);
    }

    public void setCurrentFieldPosition(FieldPosition position) {
        pinpointModule.setPosition(new Pose2D(DistanceUnit.INCH, position.x, position.y, AngleUnit.RADIANS, position.direction));
    }

    @Override
    public FieldPosition getCurrentPosition() {
        Pose2D pinpointPosition = pinpointModule.getPosition();
        return new FieldPosition(pinpointPosition.getX(DistanceUnit.INCH), pinpointPosition.getY(DistanceUnit.INCH), pinpointPosition.getHeading(AngleUnit.RADIANS));
    }

    @Override
    public void init(FieldPosition initialPosition) {
        this.pinpointModule.resetPosAndIMU();
        this.setCurrentFieldPosition(initialPosition);
    }

    public MovementVector getVelocity() {
        Pose2D pinpointVelocity = pinpointModule.getVelocity();
        return new MovementVector(pinpointVelocity.getX(DistanceUnit.INCH), pinpointVelocity.getY(DistanceUnit.INCH), pinpointVelocity.getHeading(AngleUnit.RADIANS));
    }

    public String status() {
        return pinpointModule.getDeviceStatus().toString();
    }

    public double getFrequency() {
        return pinpointModule.getFrequency();
    }

    @Override
    public void loop() {
        try {
            pinpointModule.update();
        } catch (LynxNackException e) {
        }
    }

    public boolean isDoneInitializing() {
        return pinpointModule.getDeviceStatus() == PinpointModule.DeviceStatus.READY;
    }
}