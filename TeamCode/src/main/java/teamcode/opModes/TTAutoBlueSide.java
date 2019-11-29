package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.BoundingBox2D;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.common.Vector3D;
import teamcode.robotComponents.TTArmSystem;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.robotComponents.TTVision;


@Autonomous(name = "Meta Blue Auto")
public class TTAutoBlueSide extends AbstractOpMode {
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
        //TODO need to get vision working, assuming the path is 1
        //also init arm
    }

    private SkyStoneConfiguration determineSkystoneConfig() {
        return SkyStoneConfiguration.ONE_FOUR;
    }

    @Override
    protected void onStart() {
        moveToScanningPos();
        Vector3D pos;
        boolean flag = true;
        while (flag) {
            Debug.log("Line 1");
            pos = vision.getSkystonePosition();
            if (pos == null) {
                continue;
            } else {
                flag = false;
            }
            Debug.log("Line 2");
            double horizontalDistanceFromRobot = pos.getY();
            Debug.log("Line 3");
            config = determineSkystoneConfig(horizontalDistanceFromRobot);
            Debug.log(config);
        }
        if(config.equals(SkyStoneConfiguration.THREE_SIX)){
            moveToStone(6);
            moveToSkystone();
            suckStone();
        }else if(config.equals(SkyStoneConfiguration.TWO_FIVE)){
            moveToStone(5);
            moveToSkystone();
            suckStone();
        }else{
            moveToStone(4);
            moveToSkystone();
            suckStone();
        }
    }

    private void moveToStone(int stoneNum) {
        driveSystem.vertical(-12, 0.7);
        //driveSystem.adjustGrabberPos(false);
        driveSystem.vertical(-15, 0.7);
        driveSystem.lateral(3 + (48 - 8 * stoneNum), 0.7);
        //probably a negative, have to test it tomorrow
        driveSystem.vertical(11, 0.7);
        driveSystem.turn(90, 0.5);
    }

    private void moveToScanningPos() {
        foundationGrabber(true);
        driveSystem.lateral(12, 0.6);
    }

    private void suckStone() {
        Timer armTimer = getNewTimer();
        while(!arm.intakeIsFull());
        //stall the program until it has intaken the block
        driveSystem.turn(30, 0.5);
        TimerTask armScoring = new TimerTask() {
            @Override
            public void run() {
                arm.moveToScoringPos();
            }
        };
        //TODO move this to after the scoring foundation
        armTimer.schedule(armScoring, 0);
        driveSystem.vertical(11, 0.7);
        driveSystem.turn(-90, 0.5);
    }

    private void moveToSkystone(){
        //driveSystem.foundationGrabbers(1);
        driveSystem.vertical(-15, 0.7);
        arm.intake(1);
        driveSystem.lateral(5, 0.7);
        driveSystem.vertical(-9,0.7);
        driveSystem.turn(-30, 0.5);

    }

    private void foundationGrabber(final boolean open){
        TimerTask activateGrabber = new TimerTask(){
            @Override
            public void run(){
                arm.grabFoundation(open);
            }
        };
        getNewTimer().schedule(activateGrabber, 0);
    }

    private SkyStoneConfiguration determineSkystoneConfig(double horizontalDistanceFromRobot) {
        Vector2D visionPos = new Vector2D(horizontalDistanceFromRobot, 0);
        if (MIDDLE_STONE_BOUNDS.contains(visionPos)) {
            return SkyStoneConfiguration.TWO_FIVE;
        } else if (RIGHT_STONE_BOUNDS.contains(visionPos)) {
            return SkyStoneConfiguration.ONE_FOUR;
        } else {
            return SkyStoneConfiguration.THREE_SIX;
        }
    }

    @Override
    protected void onStop() {

    }


}
