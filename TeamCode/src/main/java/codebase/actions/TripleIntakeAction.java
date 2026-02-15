package codebase.actions;

import java.util.ArrayList;

import codebase.Constants;
import codebase.hardware.Motor;
import codebase.manipulators.RevolverManipulator;
import static codebase.manipulators.RevolverManipulator.RevolverMode;
import codebase.sensors.ColorSensor;
import decode.RevolverStorageManager;

public class TripleIntakeAction implements Action {

    private final Motor intakeMotor;
    private final ColorSensor colorSensor;
    private final RevolverManipulator revolverManipulator;

    private SequentialAction delegate;

    public TripleIntakeAction(Motor intakeMotor, ColorSensor colorSensor, RevolverManipulator revolverManipulator) {
        this.intakeMotor = intakeMotor;
        this.colorSensor = colorSensor;
        this.revolverManipulator = revolverManipulator;
    }

    @Override
    public void init() {
        Action[] actions = getIntakeActionsForEmptySlots(intakeMotor, colorSensor, revolverManipulator);
        delegate = new SequentialAction(actions);
        delegate.init();
    }

    @Override
    public void loop() {
        delegate.loop();
    }

    @Override
    public boolean isComplete() {
        return delegate.isComplete();
    }

    private static class SingleIntakeAction extends SequentialAction {
        public SingleIntakeAction(ColorSensor colorSensor, RevolverManipulator revolverManipulator) {
            super(
                new RotateRevolverAction(
                        () -> revolverManipulator.getClosestChamberOfState(RevolverStorageManager.ArtifactState.NONE, RevolverMode.INPUT),
                        () -> RevolverMode.INPUT,
                        revolverManipulator
                ),
                new ColorSensorDistanceAction(colorSensor, 1, ColorSensorDistanceAction.DistanceMode.LESS_THAN_EQUAL_TO),
                new IntakeUpdateChamberStateAction(colorSensor, revolverManipulator)
            );
        }
    }

    private static Action[] getIntakeActionsForEmptySlots(Motor intakeMotor, ColorSensor colorSensor, RevolverManipulator revolverManipulator) {
        ArrayList<Action> result = new ArrayList<>();

        result.add(new CustomAction(() -> intakeMotor.setVelocity(-Constants.INTAKE_VELOCITY)));

        for (int i = 0; i < RevolverStorageManager.getChambersWithState(RevolverStorageManager.ArtifactState.NONE).size(); i++) {
            result.add(new SingleIntakeAction(colorSensor, revolverManipulator));
        }

        if (result.isEmpty()) {
            return new Action[0];
        }

        result.add(new RotateRevolverAction(
            revolverManipulator.getClosestChamberWithArtifact(RevolverMode.OUTPUT),
            RevolverMode.OUTPUT,
            revolverManipulator
        ));

        result.add(new SetMotorPowerAction(intakeMotor, 0));

        return result.toArray(new Action[0]);
    }
}
