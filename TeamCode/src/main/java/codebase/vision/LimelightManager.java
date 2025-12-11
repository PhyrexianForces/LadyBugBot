package codebase.vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import codebase.Constants;
import codebase.geometry.FieldPosition;

public class LimelightManager {
    private final Limelight3A limelight;

    public LimelightManager(Limelight3A limelight) {
        this.limelight = limelight;
    }

    public Motif getMotif() {
        ArrayList<Integer> aprilTags = getVisibleAprilTags();

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

    public FieldPosition getNearestVisibleArtifactPosition(FieldPosition robotPosition) {
        limelight.pipelineSwitch(2);

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

    public ArrayList<Integer> getVisibleAprilTags() {
        limelight.pipelineSwitch(3);

        LLResult result = limelight.getLatestResult();

        ArrayList<Integer> aprilTags = new ArrayList<>();

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

            for (LLResultTypes.FiducialResult fiducial : fiducials) {
                aprilTags.add(fiducial.getFiducialId());
            }
        }

        return aprilTags;
    }

    public enum Motif {
        GPP,
        PGP,
        PPG,
        NOT_FOUND
    }
}
