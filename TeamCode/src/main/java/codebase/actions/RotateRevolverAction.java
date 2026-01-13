package codebase.actions;

import java.util.function.Supplier;

import codebase.manipulators.RevolverManipulator;
import static codebase.manipulators.RevolverManipulator.RevolverMode;

public class RotateRevolverAction extends RunOnceAction {
    private final Supplier<Integer> chamberNumberSupplier;
    private final Supplier<RevolverMode> revolverModeSupplier;
    private final RevolverManipulator manipulator;

    /**
     * @param chamberNumber the chamber number to rotate to (0-2)
     * @param revolverMode either input or output, due to offset for outputting
     */
    public RotateRevolverAction(int chamberNumber, RevolverMode revolverMode, RevolverManipulator manipulator) {
        this.chamberNumberSupplier = () -> chamberNumber;
        this.revolverModeSupplier = () -> revolverMode;
        this.manipulator = manipulator;
    }

    /**
     * @param chamberNumber the supplier of the chamber number to rotate to (0-2)
     * @param revolverMode either input or output, due to offset for outputting
     */
    public RotateRevolverAction(Supplier<Integer> chamberNumber, Supplier<RevolverMode> revolverMode, RevolverManipulator manipulator) {
        this.chamberNumberSupplier = chamberNumber;
        this.revolverModeSupplier = revolverMode;
        this.manipulator = manipulator;
    }

    @Override
    public void init() {}

    @Override
    public boolean isComplete() {
        return manipulator.isAtTarget();
    }

    @Override
    public void run() {
        manipulator.setChamber(chamberNumberSupplier.get(), revolverModeSupplier.get());
    }
}
