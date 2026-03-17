package codebase.geometry;

public final class Angles {
    public static double angleDifference(double fromAngle, double toAngle) {
        double diff = toAngle - fromAngle;

        // Normalize to [-2PI, 2PI]
        diff = diff % (Math.PI * 2);

        // Convert to [-PI, PI] range
        if (diff > Math.PI) {
            diff -= (Math.PI * 2);
        } else if (diff <= -Math.PI) {
            diff += (Math.PI * 2);
        }

        return diff;
    }

    private Angles() {}
}
