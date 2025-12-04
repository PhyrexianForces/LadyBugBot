package codebase.actions;

import java.util.ArrayList;

import codebase.hardware.Motor;
import codebase.sensors.ColorSensor;
import decode.RevolverStorageManager;

public class TripleIntakeAction extends SequentialAction {

    public TripleIntakeAction(Motor intakeMotor, ColorSensor colorSensor) {
        super(getIntakeActionsForEmptySlots(intakeMotor, colorSensor));
    }

    private static class SingleIntakeAction extends SequentialAction {
        public SingleIntakeAction(ColorSensor colorSensor) {
            super(
                new RotateRevolverAction(
                        () -> RotateRevolverAction.getClosestChamberOfState(RevolverStorageManager.ArtifactState.NONE, RotateRevolverAction.RevolverMode.INPUT),
                        RotateRevolverAction.RevolverMode.INPUT
                ),
                new ColorSensorDistanceAction(colorSensor, 0.6, ColorSensorDistanceAction.DistanceMode.LESS_THAN_EQUAL_TO),
                new IntakeUpdateChamberStateAction(colorSensor)
            );
        }
    }

    private static SequentialAction getIntakeActionsForEmptySlots(Motor intakeMotor, ColorSensor colorSensor) {
        ArrayList<Action> result = new ArrayList<>();
        for (int i = 0; i < RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.NONE).size(); i++) {
            result.add(new SingleIntakeAction(colorSensor));
        }

        if (result.isEmpty()) {
            return new SequentialAction(new EmptyAction());
        }

        result.add(new RotateRevolverAction(RotateRevolverAction.getClosestChamberWithArtifact(RotateRevolverAction.RevolverMode.OUTPUT), RotateRevolverAction.RevolverMode.OUTPUT));
        result.add(new SetMotorPowerAction(intakeMotor, 0));

        return new SequentialAction(
            new SetMotorPowerAction(intakeMotor, 1),
            result.toArray(new Action[0])
        );
    }
}
