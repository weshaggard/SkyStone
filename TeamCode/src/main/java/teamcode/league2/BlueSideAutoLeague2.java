package teamcode.league2;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.BoundingBox2D;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.common.Vector3D;
import teamcode.opModes.league2.ArmSystemLeague2;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.robotComponents.TTVision;


@Autonomous(name = "Blue Side Auto")
public class BlueSideAutoLeague2 extends AbstractOpMode {
    private static final BoundingBox2D MIDDLE_STONE_BOUNDS = new BoundingBox2D(-10, 0, 110, 0);
    private static final BoundingBox2D RIGHT_STONE_BOUNDS = new BoundingBox2D(120, 0, 500, 0);

    private ArmSystemLeague2 arm;
    private DriveSystemLeague2 driveSystem;
    private VisionLeague2 vision;
    private SkyStoneConfiguration config;

    private final double VERTICAL_SPEED = 0.7;
    private final double LATERAL_SPEED = 0.7;
    private final double TURN_SPEED = 0.5;
    private final double INTAKE_LEFT_SPEED = 0.4;
    private final double INTAKE_RIGHT_SPEED = 0.6;

    @Override
    protected void onInitialize() {
        arm = new ArmSystemLeague2(this);
        driveSystem = new DriveSystemLeague2(hardwareMap);
        vision = new VisionLeague2(hardwareMap, VisionLeague2.CameraType.PHONE);
    }

    private void armInit() {
        arm.setClawPosition(true);
        arm.setWristPosition(false);
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
            suckStone(6);
            repositionFoundation();
            driveSystem.vertical(50, VERTICAL_SPEED);
            moveToStone(3);
            suckStone(3);

        }else if(config.equals(SkyStoneConfiguration.TWO_FIVE)){
            moveToStone(5);
            suckStone(5);
            repositionFoundation();
            driveSystem.vertical(50, VERTICAL_SPEED);
            moveToStone(2);
            suckStone(2);

        }else{
            moveToStone(4);
            suckStone(4);
            repositionFoundation();
            driveSystem.vertical(50, VERTICAL_SPEED);
            moveToStone(1);
            suckStone(1);
        }
        FoundationAndPark();
    }

    private void repositionFoundation() {
        driveSystem.turn(90, TURN_SPEED);
        driveSystem.vertical(24, VERTICAL_SPEED);
        foundationGrabber(true);
        reposistionArm();
        driveSystem.frontArc(false, TURN_SPEED, -90);
        foundationGrabber(false);
    }

    private void reposistionArm(){
        if(arm.intakeIsFull()) {
            Timer armTimer = getNewTimer();
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
        driveSystem.vertical(-9,VERTICAL_SPEED);
        driveSystem.turn(30, TURN_SPEED);
        //driveSystem.turn(90, TURN_SPEED);
    }

    private void moveToScanningPos() {
        foundationGrabber(true);
        driveSystem.lateral(12, LATERAL_SPEED);
    }

    private void suckStone(int stoneNum) {
        Timer armListener = getNewTimer();
        while(!arm.intakeIsFull());
        //stall the program until it has intaken the block

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
        driveSystem.vertical(-72 - (48 - stoneNum * 8), VERTICAL_SPEED);
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

    private void FoundationAndPark(){
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
