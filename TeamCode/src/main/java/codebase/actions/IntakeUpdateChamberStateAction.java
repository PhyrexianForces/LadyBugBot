package codebase.actions;

import codebase.Constants;
import codebase.manipulators.RevolverManipulator;
import codebase.sensors.ColorSensor;
import decode.RevolverStorageManager;

public class IntakeUpdateChamberStateAction extends RunOnceAction {

    private final ColorSensor colorSensor;
    private final RevolverManipulator revolverManipulator;

    public IntakeUpdateChamberStateAction(ColorSensor colorSensor, RevolverManipulator revolverManipulator) {
        this.colorSensor = colorSensor;
        this.revolverManipulator = revolverManipulator;
    }

    @Override
    public void init() {}

    @Override
    public void run() {
        int currentChamber = revolverManipulator.getClosestChamberOfState(RevolverStorageManager.ArtifactState.NONE, RevolverManipulator.RevolverMode.INPUT);
        boolean isGreen = colorSensor.getColor().green >= Constants.ARTIFACT_GREEN_THRESHOLD
                && colorSensor.getColor().red <= Constants.ARTIFACT_GREEN_THRESHOLD_RED;
        RevolverStorageManager.ArtifactState state = isGreen
                ? RevolverStorageManager.ArtifactState.GREEN
                : RevolverStorageManager.ArtifactState.PURPLE;
        RevolverStorageManager.setStateOfChamber(currentChamber, state);
    }
}
