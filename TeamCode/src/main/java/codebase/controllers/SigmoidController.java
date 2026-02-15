package codebase.controllers;

import java.util.function.Supplier;

public class SigmoidController implements Controller {
    private final double power;
    private final double steepness;
    private final Supplier<Double> errorSupplier;

    public SigmoidController(double power, double steepness, Supplier<Double> errorSupplier) {
        this.power = power;
        this.steepness = steepness;
        this.errorSupplier = errorSupplier;
    }

    public SigmoidController(double power, double steepness, Supplier<Double> currentPosition, Supplier<Double> targetPosition) {
        this(power, steepness, () -> targetPosition.get() - currentPosition.get());
    }

    @Override
    public double getPower() {
        return power * 2 * ((1 / (1 + Math.pow(Math.E, -errorSupplier.get() * steepness))) - 0.5);
    }

    @Override
    public double getError() {
        return errorSupplier.get();
    }
}
