package codebase.actions;

import codebase.sensors.ColorSensor;
import decode.RevolverStorageManager;

public class IntakeUpdateChamberStateAction implements Action {

    private static final double GREEN_THRESHOLD = 0.72;

    private final ColorSensor colorSensor;
    private boolean done = false;

    public IntakeUpdateChamberStateAction(ColorSensor colorSensor) {
        this.colorSensor = colorSensor;
    }

    @Override
    public void init() {
        int currentChamber = RotateRevolverAction.getClosestChamberOfState(RevolverStorageManager.ArtifactState.NONE, RotateRevolverAction.RevolverMode.INPUT);
        RevolverStorageManager.setStateOfChamber(currentChamber, (colorSensor.getColor().green >= GREEN_THRESHOLD) ? RevolverStorageManager.ArtifactState.GREEN : RevolverStorageManager.ArtifactState.PURPLE);
        done = true;
    }

    @Override
    public boolean isComplete() {
        return done;
    }

    @Override
    public void loop() {}
}
