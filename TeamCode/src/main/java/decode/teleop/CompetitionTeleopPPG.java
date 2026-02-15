package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

@TeleOp(name="P P G")
public class CompetitionTeleopPPG extends BaseCompetitionTeleop {

    @Override
    public void init() {
        RevolverStorageManager.setMotif(LimelightManager.Motif.PPG);
        super.init();
    }
}
