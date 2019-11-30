package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import teamcode.common.AbstractOpMode;
import teamcode.common.BoundingBox2D;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.common.Vector3D;
import teamcode.robotComponents.TTArmSystem;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.robotComponents.TTVision;

@Autonomous(name = "TTAutoRedSide")
public class TTAutoRedSide extends AbstractOpMode {

    private static final BoundingBox2D MIDDLE_STONE_BOUNDS = new BoundingBox2D(-10, 0, 110, 0);
    private static final BoundingBox2D RIGHT_STONE_BOUNDS = new BoundingBox2D(120, 0, 500, 0);

    private TTArmSystem arm;
    private TTDriveSystem driveSystem;
    private TTVision vision;
    private SkyStoneConfiguration config;

    @Override
    protected void onInitialize() {
        arm = new TTArmSystem(this);
        driveSystem = new TTDriveSystem(hardwareMap);
        vision = new TTVision(hardwareMap, TTVision.CameraType.PHONE);
    }

    @Override
    protected void onStart() {
        moveToScanningPosition();
        Vector3D skystonePos = vision.getSkystonePosition();
        config = determineSkystoneConfig(skystonePos);
        Debug.log(config);

        suckSkystone(config.getSecondStone());
        moveToFoundation(config.getSecondStone());
        scoreStoneInFoundation();
    }

    private SkyStoneConfiguration determineSkystoneConfig(Vector3D skystonePosition) {
        if (skystonePosition != null) {
            double horizontalDistanceFromRobot = skystonePosition.getY();
            Vector2D visionPos = new Vector2D(horizontalDistanceFromRobot, 0);
            if (MIDDLE_STONE_BOUNDS.contains(visionPos)) {
                return SkyStoneConfiguration.TWO_FIVE;
            } else if (RIGHT_STONE_BOUNDS.contains(visionPos)) {
                return SkyStoneConfiguration.ONE_FOUR;
            }
        }
        return SkyStoneConfiguration.ONE_FOUR;
    }

    private void moveToScanningPosition() {
        arm.setFoundationGrabberPosition(true);
        driveSystem.lateral(12, 0.6);
    }

    private void suckSkystone(int skystoneNum) {
        driveSystem.turn(-90, 0.6);
        arm.intake(1.0);
        driveSystem.lateral(42 - skystoneNum * 8, 0.6);
        driveSystem.vertical(-20, 0.6);
        driveSystem.vertical(-8, 0.2);
        arm.intake(0);
        driveSystem.vertical(24, 0.6);
        arm.setClawPosition(true);
        driveSystem.turn(-90, 0.6);
    }

    private void moveToFoundation(int skystoneNum) {
        arm.setFoundationGrabberPosition(false);
        driveSystem.vertical(123 - skystoneNum * 8, 0.6);
        driveSystem.turn(-90, 0.6);
        driveSystem.vertical(20, 0.6);
        arm.setFoundationGrabberPosition(true);
        driveSystem.frontArc(true, 0.6, -90);
    }

    private void scoreStoneInFoundation() {
        Timer wrist = getNewTimer();

        TimerTask wristTask = new TimerTask() {
            @Override
            public void run() {
                arm.setWristPosition(true);
            }
        };

        wrist.schedule(wristTask, 100);
    }

    @Override
    protected void onStop() {
    }

}
