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
        RevolverStorageManager.setStateOfChamber(currentChamber, (colorSensor.getColor().green >= Constants.ARTIFACT_GREEN_THRESHOLD) ? RevolverStorageManager.ArtifactState.GREEN : RevolverStorageManager.ArtifactState.PURPLE);
    }
}
