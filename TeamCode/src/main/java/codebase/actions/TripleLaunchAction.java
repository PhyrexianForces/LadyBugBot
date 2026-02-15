package codebase.actions;

import com.qualcomm.robotcore.hardware.Servo;

import codebase.manipulators.FlywheelManipulator;
import codebase.manipulators.RevolverManipulator;
import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

public class TripleLaunchAction implements Action {

    private final RevolverManipulator revolverManipulator;
    private final Servo launchServo;
    private final FlywheelManipulator flywheelManipulator;

    private int launchIndex = 0;
    private Action currentAction;
    private boolean complete = false;

    public TripleLaunchAction(RevolverManipulator revolverManipulator, Servo launchServo, FlywheelManipulator flywheelManipulator) {
        this.revolverManipulator = revolverManipulator;
        this.launchServo = launchServo;
        this.flywheelManipulator = flywheelManipulator;
    }

    @Override
    public void init() {
        currentAction = buildNextLaunchAction();
        if (currentAction == null) {
            flywheelManipulator.stopFlywheel();
            complete = true;
        } else {
            currentAction.init();
        }
    }

    @Override
    public void loop() {
        if (complete || currentAction == null) {
            return;
        }

        currentAction.loop();

        if (currentAction.isComplete()) {
            launchIndex++;
            currentAction = buildNextLaunchAction();

            if (currentAction == null) {
                flywheelManipulator.stopFlywheel();
                complete = true;
            } else {
                currentAction.init();
            }
        }
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    private Action buildNextLaunchAction() {
        if (launchIndex >= 3) {
            return null;
        }

        LimelightManager.Motif motif = RevolverStorageManager.getMotif();

        if (motif == LimelightManager.Motif.NOT_FOUND) {
            if (RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.GREEN).isEmpty()
                    && RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.PURPLE).isEmpty()) {
                return null;
            }

            return new SequentialAction(
                    new RotateRevolverAction(
                            () -> revolverManipulator.getClosestChamberWithArtifact(RevolverManipulator.RevolverMode.OUTPUT),
                            () -> RevolverManipulator.RevolverMode.OUTPUT,
                            revolverManipulator
                    ),
                    new LaunchAction(revolverManipulator, launchServo, flywheelManipulator)
            );
        }

        RevolverStorageManager.ArtifactState[] motifStates = motif.toArtifactStates();
        RevolverStorageManager.ArtifactState needed = motifStates[launchIndex];

        if (RevolverStorageManager.getChambersWithState(needed).isEmpty()) {
            return null;
        }

        return new SequentialAction(
                new RotateRevolverAction(
                        () -> revolverManipulator.getClosestChamberOfState(needed, RevolverManipulator.RevolverMode.OUTPUT),
                        () -> RevolverManipulator.RevolverMode.OUTPUT,
                        revolverManipulator
                ),
                new LaunchAction(revolverManipulator, launchServo, flywheelManipulator)
        );
    }
}
