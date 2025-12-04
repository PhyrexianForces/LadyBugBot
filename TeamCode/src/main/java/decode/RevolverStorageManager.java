package decode;

import java.util.ArrayList;

public abstract class RevolverStorageManager {

    private static ArtifactState[] chamberStates = new ArtifactState[] {ArtifactState.PURPLE, ArtifactState.PURPLE, ArtifactState.PURPLE};

    public enum ArtifactState {
        PURPLE,
        GREEN,
        NONE
    }

    /**
     * Reset the state of the storage for preload
     * Should ONLY be called in init of the Autonomous
     */
    public static void resetFull() {
        chamberStates = new ArtifactState[] {ArtifactState.PURPLE, ArtifactState.PURPLE, ArtifactState.PURPLE};
    }

    /**
     * Reset the state of the storage to empty
     * Should ONLY be called in init of the Teleop
     */
    public static void reset() {
        chamberStates = new ArtifactState[] {ArtifactState.NONE, ArtifactState.NONE, ArtifactState.NONE};
    }

    public static void setStateOfChamber(int chamber, ArtifactState state) {
        if (chamber < 0 || chamber > 2) {
            throw new IllegalArgumentException("Chamber must be between 0 and 2, inclusive");
        }
        
        chamberStates[chamber] = state;
    }

    public static ArtifactState getStateOfChamber(int chamber) {
        return chamberStates[chamber];
    }

    public static ArrayList<Integer> getChambersWithState(ArtifactState state) {
        ArrayList<Integer> chambers = new ArrayList<>();

        for (int chamber = 0; chamber <= 2; chamber++) {
            if (getStateOfChamber(chamber) == state) {
                chambers.add(chamber);
            }
        }

        return chambers;
    }
}
