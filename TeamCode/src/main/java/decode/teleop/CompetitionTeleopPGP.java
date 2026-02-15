package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import codebase.vision.LimelightManager;
import decode.RevolverStorageManager;

@TeleOp(name="P G P")
public class CompetitionTeleopPGP extends BaseCompetitionTeleop {

    @Override
    public void init() {
        RevolverStorageManager.setMotif(LimelightManager.Motif.PGP);
        super.init();
    }
}
