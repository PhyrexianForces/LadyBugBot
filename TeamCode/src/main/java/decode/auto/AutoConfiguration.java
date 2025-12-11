package decode.auto;

import static decode.auto.AutoConfiguration.SpikeMark.*;

public class AutoConfiguration {
    public static final AutoConfiguration TESTING_CONFIG_RED = new AutoConfiguration(AllianceColor.RED, StartPosition.GOAL, new SpikeMark[] {HIGH, MIDDLE, LOW});

    public final AllianceColor alliance;
    public final StartPosition startPosition;
    public final SpikeMark[] spikeMarks;

    public AutoConfiguration(AllianceColor alliance, StartPosition startPosition, SpikeMark[] spikeMarks) {
        this.alliance = alliance;
        this.startPosition = startPosition;
        this.spikeMarks = spikeMarks;
    }

    public enum StartPosition {
        GOAL,
        FAR
    }

    public enum AllianceColor {
        BLUE,
        RED
    }

    public enum SpikeMark {
        HIGH,
        MIDDLE,
        LOW
    }
}
