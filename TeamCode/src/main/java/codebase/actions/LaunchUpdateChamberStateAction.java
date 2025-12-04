package codebase.actions;

import decode.RevolverStorageManager;

public class LaunchUpdateChamberStateAction implements Action {


    private boolean done = false;

    public LaunchUpdateChamberStateAction() {}

    @Override
    public void init() {
        int currentChamber = RotateRevolverAction.getClosestChamberWithArtifact(RotateRevolverAction.RevolverMode.OUTPUT);
        RevolverStorageManager.setStateOfChamber(currentChamber, RevolverStorageManager.ArtifactState.NONE);
        done = true;
    }

    @Override
    public boolean isComplete() {
        return done;
    }

    @Override
    public void loop() {}
}
