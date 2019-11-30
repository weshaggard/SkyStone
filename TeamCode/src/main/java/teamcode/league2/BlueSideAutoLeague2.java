package teamcode.league2;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.BoundingBox2D;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.common.Vector3D;

import static teamcode.league2.AutoUtilsLeague2.*;

@Autonomous(name = "Blue Side Auto")
public class BlueSideAutoLeague2 extends AbstractOpMode {

    private static final BoundingBox2D LEFT_STONE_BOUNDS = new BoundingBox2D(120, 0, 500, 0);
    private static final BoundingBox2D MIDDLE_STONE_BOUNDS = new BoundingBox2D(-10, 0, 110, 0);

    private ArmSystemLeague2 arm;
    private DriveSystemLeague2 driveSystem;
    private VisionLeague2 vision;
    private SkyStoneConfiguration config;

    private Timer armTimer, blockProcessor, foundationTimer;

    @Override
    protected void onInitialize() {
        arm = new ArmSystemLeague2(this);
        driveSystem = new DriveSystemLeague2(hardwareMap);
        vision = new VisionLeague2(hardwareMap, VisionLeague2.CameraType.PHONE);
        armTimer = getNewTimer();
        blockProcessor = getNewTimer();
        foundationTimer = getNewTimer();
        armInit();

    }

    private void armInit() {
        arm.setClawPosition(true);
        arm.setWristPosition(false);
    }


    @Override
    protected void onStart() {
        moveToScanningPos();

        Vector3D skystonePos = vision.getSkystonePosition();
        config = determineSkystoneConfig(skystonePos);

        moveToStone(config.getSecondStone());
        suckStone(config.getSecondStone());
        repositionFoundation();
        driveSystem.vertical(50, VERTICAL_SPEED);
        moveToStone(config.getFirstStone());
        suckStone(config.getFirstStone());
        foundationAndPark();
    }

    private SkyStoneConfiguration determineSkystoneConfig(Vector3D skystonePosition) {
        double horizontalDistanceFromRobot = skystonePosition.getY();
        Vector2D visionPos = new Vector2D(horizontalDistanceFromRobot, 0);
        if (visionPos != null) {
            if (LEFT_STONE_BOUNDS.contains(visionPos)) {
                return SkyStoneConfiguration.THREE_SIX;
            } else if (MIDDLE_STONE_BOUNDS.contains(visionPos)) {
                return SkyStoneConfiguration.TWO_FIVE;
            }
        }
        return SkyStoneConfiguration.ONE_FOUR;
    }

    private void repositionFoundation() {
        driveSystem.turn(90, TURN_SPEED);
        driveSystem.vertical(24, VERTICAL_SPEED);
        foundationGrabber(true);
        reposistionArm();
        driveSystem.frontArc(false, TURN_SPEED, -90);
        foundationGrabber(false);
    }

    private void reposistionArm() {
        if (arm.intakeIsFull()) {
            TimerTask armScoring = new TimerTask() {
                @Override
                public void run() {
                    arm.moveToScoringPos();
                }
            };
            armTimer.schedule(armScoring, 0);
        }
    }

    private void moveToStone(int stoneNum) {
        driveSystem.turn(-90, TURN_SPEED);
        driveSystem.vertical(-12, VERTICAL_SPEED);
        //driveSystem.adjustGrabberPos(false);
        driveSystem.vertical(-15, VERTICAL_SPEED);
        driveSystem.lateral(-3 - (48 - 8 * stoneNum), LATERAL_SPEED);
        arm.intake(INTAKE_LEFT_SPEED, INTAKE_RIGHT_SPEED);
        driveSystem.vertical(-9, VERTICAL_SPEED);
        driveSystem.turn(30, TURN_SPEED);
        //driveSystem.turn(90, TURN_SPEED);
    }

    private void moveToScanningPos() {
        foundationGrabber(true);
        driveSystem.lateral(12, LATERAL_SPEED);
    }

    private void suckStone(int stoneNum) {

        while (!arm.intakeIsFull()) ;
        //stall the program until it has intaken the block

        TimerTask blockProcessing = new TimerTask() {
            @Override
            public void run() {
                arm.intake(0);
                arm.setClawPosition(false);
            }
        };
        blockProcessor.schedule(blockProcessing, 0);
        driveSystem.vertical(11, VERTICAL_SPEED);
        driveSystem.turn(-90, TURN_SPEED);
        driveSystem.vertical(-72 - (48 - stoneNum * 8), VERTICAL_SPEED);
    }


    private void foundationGrabber(final boolean open) {
        TimerTask activateGrabber = new TimerTask() {
            @Override
            public void run() {
                arm.grabFoundation(open);
            }
        };
        foundationTimer.schedule(activateGrabber, 0);
    }

    private void foundationAndPark() {
        reposistionArm();
        driveSystem.frontArc(true, TURN_SPEED, -90);
        driveSystem.lateral(24, LATERAL_SPEED);
        driveSystem.vertical(-18, VERTICAL_SPEED);
        driveSystem.lateral(24, LATERAL_SPEED);

    }

    @Override
    protected void onStop() {

    }

}
