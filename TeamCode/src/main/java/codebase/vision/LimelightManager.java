package codebase.vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import codebase.Constants;
import codebase.geometry.FieldPosition;
import decode.RevolverStorageManager;
import decode.auto.AutoConfiguration;

public class LimelightManager {
    private final Limelight3A limelight;

    int currentPipelineIndex = 0;

    public LimelightManager(Limelight3A limelight) {
        this.limelight = limelight;
    }

    public Motif getMotif() {
        System.out.println("Getting april tags");
        List<Integer> aprilTags = getVisibleAprilTagIds();

        System.out.println("got april tags__");

        if (aprilTags.contains(21)) {
            return Motif.GPP;
        }
        if (aprilTags.contains(22)) {
            return Motif.PGP;
        }
        if (aprilTags.contains(23)) {
            return Motif.PPG;
        }
        return Motif.NOT_FOUND;
    }

    public static class AprilTagResult {
        public int id;
        public double horizontalOffsetRadians;
        public double verticalOffsetRadians;
        public double imagePercentage;
        public Pose3D targetPositionRelative;

        public AprilTagResult(int id, double horizontalOffsetRadians, double verticalOffsetRadians, double imagePercentage, Pose3D targetPositionRelative) {
            this.id = id;
            this.horizontalOffsetRadians = horizontalOffsetRadians;
            this.verticalOffsetRadians = verticalOffsetRadians;
            this.imagePercentage = imagePercentage;
            this.targetPositionRelative = targetPositionRelative;
        }
    }

    public boolean canSeeGoalAprilTag(AutoConfiguration.AllianceColor allianceColor) {
        int targetAprilTagId = (allianceColor == AutoConfiguration.AllianceColor.BLUE ? 20 : 24);

        return getVisibleAprilTagIds().contains(targetAprilTagId);
    }

    public AprilTagResult getGoalAprilTag(AutoConfiguration.AllianceColor allianceColor) {
        int targetAprilTagId = (allianceColor == AutoConfiguration.AllianceColor.BLUE ? 20 : 24);

        switchToPipeline(3);

        List<AprilTagResult> aprilTags = getVisibleAprilTags();

        for (AprilTagResult aprilTag : aprilTags) {
            if (aprilTag.id == targetAprilTagId) {
                return aprilTag;
            }
        }

        return null;
    }

    public FieldPosition getNearestVisibleArtifactPosition(FieldPosition robotPosition) {
        switchToPipeline(2);

        LLResult result = limelight.getLatestResult();

        if (result == null || !result.isValid()) {
            return null;
        }

        double TxDegrees = result.getTx();
        double TyDegrees = result.getTy();

        double distanceToArtifact = Math.abs(Constants.LIMELIGHT_LENS_HEIGHT / (Math.tan(TyDegrees)));
        double absoluteAngleToArtifact = robotPosition.direction + TxDegrees;

        return new FieldPosition(
                robotPosition.x + distanceToArtifact * Math.cos(absoluteAngleToArtifact),
                robotPosition.y + distanceToArtifact * Math.sin(absoluteAngleToArtifact),
                absoluteAngleToArtifact
        );
    }

    public List<Integer> getVisibleAprilTagIds() {
        return getVisibleAprilTags()
                .stream()
                .map(tag -> tag.id)
                .collect(Collectors.toList());
    }

    public List<AprilTagResult> getVisibleAprilTags() {
        System.out.println("switching pipelines to 3");
        switchToPipeline(3);
        System.out.println("switched pipelines");

        LLResult result = limelight.getLatestResult();

        System.out.println("got result");

        if (result != null && result.isValid()) {
            System.out.println("getting fiducialresults");
            return result.getFiducialResults()
                    .stream()
                    .map(f -> new AprilTagResult(
                            f.getFiducialId(),
                            f.getTargetXDegrees(),
                            f.getTargetYDegrees(),
                            f.getTargetArea(),
                            f.getTargetPoseRobotSpace()
                    ))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>(0);
    }

    private void switchToPipeline(int pipelineIndex) {

        if (pipelineIndex == currentPipelineIndex) {
            return;
        }

        limelight.pipelineSwitch(pipelineIndex);
        double lastTimestamp = limelight.getLatestResult().getTimestamp();

        double startTime = System.currentTimeMillis();

        while (limelight.getLatestResult().getTimestamp() == lastTimestamp) {
            System.out.println(limelight.getLatestResult().getTimestamp());
            if (System.currentTimeMillis() - startTime > 1000) {
                return;
            }
            // wait for new frame so that new pipeline has initialized
        }

        currentPipelineIndex = pipelineIndex;
    }

    public Limelight3A getLimelight() {
        return limelight;
    }

    public enum Motif {
        GPP,
        PGP,
        PPG,
        NOT_FOUND;

        public RevolverStorageManager.ArtifactState[] toArtifactStates() {
            switch (this) {
                case GPP: return new RevolverStorageManager.ArtifactState[] {RevolverStorageManager.ArtifactState.GREEN, RevolverStorageManager.ArtifactState.PURPLE, RevolverStorageManager.ArtifactState.PURPLE};
                case PGP: return new RevolverStorageManager.ArtifactState[] {RevolverStorageManager.ArtifactState.PURPLE, RevolverStorageManager.ArtifactState.GREEN, RevolverStorageManager.ArtifactState.PURPLE};
                case PPG: return new RevolverStorageManager.ArtifactState[] {RevolverStorageManager.ArtifactState.PURPLE, RevolverStorageManager.ArtifactState.PURPLE, RevolverStorageManager.ArtifactState.GREEN};
                default: return null;
            }
        }
    }
}
