package codebase.sensors;

public interface Encoder {
    double getTicks();

    /**
     * Gets the current position of the encoder in inches.
     * @return position in inches
     */
    double getPosition();

    /**
     * Sets the encoder position to zero.
     */
    void reset();
}