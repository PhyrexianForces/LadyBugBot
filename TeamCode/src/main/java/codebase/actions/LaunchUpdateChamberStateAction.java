package codebase.actions;

import codebase.hardware.Motor;
import codebase.manipulators.RevolverManipulator;
import decode.RevolverStorageManager;

public class LaunchUpdateChamberStateAction extends RunOnceAction {
    private final RevolverManipulator revolverManipulator;

    public LaunchUpdateChamberStateAction(RevolverManipulator revolverManipulator) {
        this.revolverManipulator = revolverManipulator;
    }

    @Override
    public void init() {}

    @Override
    public void run() {
        int currentChamber = revolverManipulator.getClosestChamberWithArtifact(RevolverManipulator.RevolverMode.OUTPUT);
        RevolverStorageManager.setStateOfChamber(currentChamber, RevolverStorageManager.ArtifactState.NONE);
    }
}
