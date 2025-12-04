package codebase.actions;

import java.util.ArrayList;
import java.util.Arrays;

import decode.RevolverStorageManager;

public class TripleLaunchAction extends SequentialAction {

    public TripleLaunchAction() {
        super(getLaunchActionsForAllArtifacts());
    }

    private static class SingleLaunchAction extends SequentialAction {
        public SingleLaunchAction() {
            super(
                new RotateRevolverAction(
                        () -> RotateRevolverAction.getClosestChamberWithArtifact(RotateRevolverAction.RevolverMode.OUTPUT),
                        RotateRevolverAction.RevolverMode.OUTPUT
                ),
                new LaunchAction()
            );
        }
    }

    private static SequentialAction getLaunchActionsForAllArtifacts() {
        ArrayList<Action> result = new ArrayList<>();

        for (int i = 0; i < (3 - RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.NONE).size()); i++) {
            result.add(new SingleLaunchAction());
        }

        if (result.isEmpty()) {
            return new SequentialAction(new EmptyAction());
        }

        Action[] resultArray = result.toArray(new Action[0]);

        return new SequentialAction(
                resultArray[0],
                Arrays.copyOfRange(resultArray, 1, resultArray.length)
        );
    }
}
