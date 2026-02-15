package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

@TeleOp(name="G P P")
public class CompetitionTeleopGPP extends BaseCompetitionTeleop {

    @Override
    public void init() {
        RevolverStorageManager.setMotif(LimelightManager.Motif.GPP);
        super.init();
    }
}
