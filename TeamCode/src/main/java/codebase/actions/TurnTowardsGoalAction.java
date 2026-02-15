package codebase.actions;

import codebase.Constants;
import codebase.geometry.FieldPosition;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.Localizer;
import codebase.vision.LimelightManager;
import decode.auto.AutoConfiguration;

public class TurnTowardsGoalAction extends MoveToAction {

    private final LimelightManager limelightManager;
    private final AutoConfiguration.AllianceColor allianceColor;
    private final Localizer localizer;

    public TurnTowardsGoalAction(MecanumDriver driver, Localizer localizer, LimelightManager limelightManager, AutoConfiguration.AllianceColor allianceColor) {
        super(driver, localizer, new FieldPosition(0, 0, 0), 1, 1, 2, Math.toRadians(2));

        this.limelightManager = limelightManager;
        this.allianceColor = allianceColor;
        this.localizer = localizer;
    }

    private static FieldPosition getTargetPositionForLaunch(Localizer localizer) {
        FieldPosition goalPosition = new FieldPosition(Constants.ShooterConstants.GOAL_POSITION_RED.x, Constants.ShooterConstants.GOAL_POSITION_RED.y * (AutoConfiguration.CURRENT_CONFIG.alliance == AutoConfiguration.AllianceColor.BLUE ? -1 : 1), 0);
        double directionToGoal = Math.atan2(goalPosition.y - localizer.getCurrentPosition().y, goalPosition.x - localizer.getCurrentPosition().x);

        return new FieldPosition(localizer.getCurrentPosition().x, localizer.getCurrentPosition().y, directionToGoal);
    }

    @Override
    public void init() {
        this.setDestination(getTargetPositionForLaunch(localizer));
    }

    @Override
    public void loop() {
        super.loop();

        if (limelightManager.canSeeGoalAprilTag(allianceColor)) {
            this.setDestination(new FieldPosition(localizer.getCurrentPosition().x, localizer.getCurrentPosition().y, localizer.getCurrentPosition().direction + limelightManager.getGoalAprilTag(allianceColor).horizontalOffsetRadians));
        }
    }
}
