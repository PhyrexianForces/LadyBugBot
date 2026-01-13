package codebase.actions;

import java.util.ArrayList;

import codebase.Constants;
import codebase.hardware.Motor;
import codebase.manipulators.RevolverManipulator;
import static codebase.manipulators.RevolverManipulator.RevolverMode;
import codebase.sensors.ColorSensor;
import decode.RevolverStorageManager;

public class TripleIntakeAction extends SequentialAction {

    public TripleIntakeAction(Motor intakeMotor, ColorSensor colorSensor, RevolverManipulator revolverManipulator) {
        super(getIntakeActionsForEmptySlots(intakeMotor, colorSensor, revolverManipulator));
    }

    private static class SingleIntakeAction extends SequentialAction {
        public SingleIntakeAction(ColorSensor colorSensor, RevolverManipulator revolverManipulator) {
            super(
                new RotateRevolverAction(
                        () -> revolverManipulator.getClosestChamberOfState(RevolverStorageManager.ArtifactState.NONE, RevolverMode.INPUT),
                        () -> RevolverMode.INPUT,
                        revolverManipulator
                ),
                new ColorSensorDistanceAction(colorSensor, 0.6, ColorSensorDistanceAction.DistanceMode.LESS_THAN_EQUAL_TO),
                new IntakeUpdateChamberStateAction(colorSensor, revolverManipulator)
            );
        }
    }

    private static SequentialAction getIntakeActionsForEmptySlots(Motor intakeMotor, ColorSensor colorSensor, RevolverManipulator revolverManipulator) {
        ArrayList<Action> result = new ArrayList<>();
        for (int i = 0; i < RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.NONE).size(); i++) {
            result.add(new SingleIntakeAction(colorSensor, revolverManipulator));
        }

        if (result.isEmpty()) {
            return new SequentialAction(new EmptyAction());
        }

        result.add(new SetMotorPowerAction(intakeMotor, 0));

        result.add(new RotateRevolverAction(
            revolverManipulator.getClosestChamberWithArtifact(RevolverMode.OUTPUT),
            RevolverMode.OUTPUT,
            revolverManipulator
        ));

        return new SequentialAction(
            new SetMotorPowerAction(intakeMotor, -Constants.INTAKE_POWER),
            result.toArray(new Action[0])
        );
    }
}
