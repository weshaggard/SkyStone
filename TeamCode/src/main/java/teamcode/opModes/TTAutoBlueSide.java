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

    private final double VERTICAL_SPEED = 0.7;
    private final double LATERAL_SPEED = 0.7;
    private final double TURN_SPEED = 0.5;

    @Override
    protected void onInitialize() {
        arm = new TTArmSystem(this);
        driveSystem = new TTDriveSystem(hardwareMap);
        vision = new TTVision(hardwareMap, TTVision.CameraType.PHONE);
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
            scoreFoundation();
            driveSystem.vertical(50, VERTICAL_SPEED);
            moveToStone(3);
            moveToSkystone();
            suckStone();
            

        }else if(config.equals(SkyStoneConfiguration.TWO_FIVE)){
            moveToStone(5);
            moveToSkystone();
            suckStone();
            scoreFoundation();
            driveSystem.vertical(50, VERTICAL_SPEED);
        }else{
            moveToStone(4);
            moveToSkystone();
            suckStone();
            scoreFoundation();
            driveSystem.vertical(50, VERTICAL_SPEED);
        }
    }

    private void scoreFoundation() {
        Timer armTimer = getNewTimer();

        foundationGrabber(true);
        TimerTask armScoring = new TimerTask() {
            @Override
            public void run() {
                arm.moveToScoringPos();
            }
        };
        armTimer.schedule(armScoring, 0);
        driveSystem.frontArc(false, TURN_SPEED, -90);
        foundationGrabber(false);
    }

    private void moveToStone(int stoneNum) {
        driveSystem.turn(-90, TURN_SPEED);
        driveSystem.vertical(-12, VERTICAL_SPEED);
        //driveSystem.adjustGrabberPos(false);
        driveSystem.vertical(-15, VERTICAL_SPEED);
        driveSystem.lateral(3 - (48 - 8 * stoneNum), LATERAL_SPEED);
        driveSystem.vertical(11, VERTICAL_SPEED);
        driveSystem.turn(90, TURN_SPEED);
    }

    private void moveToScanningPos() {
        foundationGrabber(true);
        driveSystem.lateral(12, LATERAL_SPEED);
    }

    private void suckStone() {
        Timer armListener = getNewTimer();
        while(!arm.intakeIsFull());
        //stall the program until it has intaken the block
        driveSystem.turn(30, TURN_SPEED);

        TimerTask blockProcessing = new TimerTask(){
            @Override
            public void run(){
                arm.intake(0);
                arm.setClawPosition(false);
            }
        };
        armListener.schedule(blockProcessing, 0);
        driveSystem.vertical(11, VERTICAL_SPEED);
        driveSystem.turn(-90, TURN_SPEED);
        moveToFoundation(6);
    }

    private void moveToFoundation(int stoneNum) {
        driveSystem.vertical(-72 - (48 - stoneNum * 8), VERTICAL_SPEED);
        driveSystem.turn(90, TURN_SPEED);
        driveSystem.vertical(24, VERTICAL_SPEED);
    }

    private void moveToSkystone(){
        //driveSystem.foundationGrabbers(1);
        driveSystem.vertical(-15, VERTICAL_SPEED);
        arm.intake(1);
        driveSystem.lateral(5, LATERAL_SPEED);
        driveSystem.vertical(-9,VERTICAL_SPEED);
        driveSystem.turn(-30, TURN_SPEED);
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
