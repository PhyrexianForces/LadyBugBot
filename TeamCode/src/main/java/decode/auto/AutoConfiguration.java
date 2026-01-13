package decode.auto;

import static decode.auto.AutoConfiguration.SpikeMark.*;

import java.util.Arrays;
import java.util.List;

public class AutoConfiguration {
    public static final AutoConfiguration CURRENT_CONFIG = new AutoConfiguration(AllianceColor.RED, StartPosition.GOAL, Arrays.asList(HIGH, MIDDLE, LOW));

    public final AllianceColor alliance;
    public final StartPosition startPosition;
    public final List<SpikeMark> spikeMarks;

    public AutoConfiguration(AllianceColor alliance, StartPosition startPosition, List<SpikeMark> spikeMarks) {
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
