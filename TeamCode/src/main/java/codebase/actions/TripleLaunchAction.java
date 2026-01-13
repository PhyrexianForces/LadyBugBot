package codebase.actions;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.Arrays;

import codebase.hardware.Motor;
import codebase.manipulators.RevolverManipulator;
import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

public class TripleLaunchAction extends SequentialAction {

    private LimelightManager.Motif motif;

    private final RevolverManipulator revolverManipulator;
    private final Servo launchServo;
    private final Motor launchMotor1;
    private final Motor launchMotor2;

    public TripleLaunchAction(RevolverManipulator revolverManipulator, Servo launchServo, Motor launchMotor1, Motor launchMotor2) {
        super(getLaunchActionsForAllArtifacts(revolverManipulator, launchServo, launchMotor1, launchMotor2));

        this.revolverManipulator = revolverManipulator;
        this.launchServo = launchServo;
        this.launchMotor1 = launchMotor1;
        this.launchMotor2 = launchMotor2;
    }

    @Override
    public void loop() {
        super.loop();

        if (RevolverStorageManager.getMotif() != LimelightManager.Motif.NOT_FOUND && motif == LimelightManager.Motif.NOT_FOUND) {
            this.motif = RevolverStorageManager.getMotif();
            this.currentActionNode = new ActionNode(getLaunchActionsForMotif(revolverManipulator, launchServo, launchMotor1, launchMotor2, motif));
            System.out.println("motif found, switching to launch it");
        }
    }

    private static class SingleLaunchAction extends SequentialAction {
        public SingleLaunchAction(RevolverManipulator revolverManipulator, Servo launchServo, Motor launchMotor1, Motor launchMotor2) {
            super(
                new RotateRevolverAction(
                        revolverManipulator.getClosestChamberWithArtifact(RevolverManipulator.RevolverMode.OUTPUT),
                        RevolverManipulator.RevolverMode.OUTPUT,
                        revolverManipulator
                ),
                new LaunchAction(revolverManipulator, launchServo, launchMotor1, launchMotor2)
            );
        }

        public SingleLaunchAction(RevolverManipulator revolverManipulator, Servo launchServo, Motor launchMotor1, Motor launchMotor2, RevolverStorageManager.ArtifactState artifactState) {
            super(
                    new RotateRevolverAction(
                            revolverManipulator.getClosestChamberOfState(artifactState, RevolverManipulator.RevolverMode.OUTPUT),
                            RevolverManipulator.RevolverMode.OUTPUT,
                            revolverManipulator
                    ),
                    new LaunchAction(revolverManipulator, launchServo, launchMotor1, launchMotor2)
            );
        }
    }

    private static SequentialAction getLaunchActionsForAllArtifacts(RevolverManipulator revolverManipulator, Servo launchServo, Motor launchMotor1, Motor launchMotor2) {
        ArrayList<Action> result = new ArrayList<>();

        for (int i = 0; i < (3 - RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.NONE).size()); i++) {
            result.add(new SingleLaunchAction(revolverManipulator, launchServo, launchMotor1, launchMotor2));
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

    private static Action[] getLaunchActionsForMotif(RevolverManipulator revolverManipulator, Servo launchServo, Motor launchMotor1, Motor launchMotor2, LimelightManager.Motif motif) {
        ArrayList<Action> result = new ArrayList<>();

        RevolverStorageManager.ArtifactState[] motifStates = motif.toArtifactStates();

        if (motifStates == null) {
            throw new RuntimeException("Can not launch for motif until motif is found!");
        }

        for (int i = 0; i < 3; i++) {
            System.out.println("looking for " + String.valueOf(motifStates[i]));
            if (RevolverStorageManager.getChambersWithState(motifStates[i]).isEmpty()) {
                System.out.println("did not find " + String.valueOf(motifStates[i]));
                break;
            }

            result.add(new SingleLaunchAction(revolverManipulator, launchServo, launchMotor1, launchMotor2, motifStates[i]));
        }

        if (result.isEmpty()) {
            System.out.println("results empty");
            return new Action[] {new EmptyAction()};
        }

        return result.toArray(new Action[0]);
    }
}
