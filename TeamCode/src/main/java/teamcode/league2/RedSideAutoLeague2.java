package teamcode.league2;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.BoundingBox2D;
import teamcode.common.Debug;
import teamcode.common.Interval;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.common.Vector3D;

@Autonomous(name = "Red Side Auto")
public class RedSideAutoLeague2 extends AbstractOpMode {
    private static final Interval MID_RED = new Interval(-200, -50);
    private static final Interval RIGHT_RED = new Interval(50, 200);

    private ArmSystemLeague2 arm;
    private DriveSystemLeague2 driveSystem;
    private VisionLeague2 vision;
    private Timer timer;
    private SkyStoneConfiguration config;

    @Override
    protected void onInitialize() {
        arm = new ArmSystemLeague2(this);
        driveSystem = new DriveSystemLeague2(hardwareMap);
        vision = new VisionLeague2(hardwareMap, VisionLeague2.CameraType.PHONE);
        timer = getNewTimer();
    }

    @Override
    protected void onStart() {
//        moveToScanningPosition();
//        Vector3D skystonePos = vision.getSkystonePosition();
//        config = determineConfigRedSide(skystonePos);
//        Debug.log(config);

//        suckSkystone(config.getSecondStone());
//        moveToFoundation(config.getSecondStone());
//        scoreStoneInFoundation();
        arm.setClawPosition(false);
        arm.setClawPosition(true);
        arm.grabFoundation(true);
        arm.grabFoundation(false);
        while (opModeIsActive()) ;
    }

    private SkyStoneConfiguration determineConfigRedSide(Vector3D skystonePos) {
        if (skystonePos != null) {
            double horizontalDistanceFromRobot = -skystonePos.getY();
            if (MID_RED.contains(horizontalDistanceFromRobot)) {
                return SkyStoneConfiguration.TWO_FIVE;
            } else if (RIGHT_RED.contains(horizontalDistanceFromRobot)) {
                return SkyStoneConfiguration.THREE_SIX;
            }
        }
        return SkyStoneConfiguration.ONE_FOUR;
    }


    private void moveToScanningPosition() {
        arm.grabFoundation(true);
        driveSystem.vertical(4.9, 0.4);
        driveSystem.lateral(16, 0.4);
    }

    private void suckSkystone(int skystoneNum) {
        driveSystem.turn(-90, 0.6);
        arm.intake(1.0);
        driveSystem.lateral(37.1 - skystoneNum * 8, 0.6);
        driveSystem.vertical(-16, 0.6);
        driveSystem.vertical(-8, 0.2);
        arm.intake(0);
        arm.setClawPosition(false);
        sleep(1000);
        driveSystem.vertical(24, 0.6);
        driveSystem.turn(-90, 0.6);
    }


    private void moveToFoundation(int skystoneNum) {
        arm.grabFoundation(false);
        driveSystem.vertical(135 - skystoneNum * 8, 0.6);
        driveSystem.turn(-90, 0.6);
        driveSystem.vertical(20, 0.6);
        arm.grabFoundation(false);
        driveSystem.turn(180, 0.4);
        scoreStoneInFoundation();
        driveSystem.vertical(12, 0.6);
        //radius is arbetrary, need to fix
    }

    private void scoreStoneInFoundation() {
        TimerTask wristTask = new TimerTask() {
            @Override
            public void run() {
                score();
            }
        };
        timer.schedule(wristTask, 0);
    }

    private void score() {
        arm.intake(0);

        TimerTask wristTask = new TimerTask() {
            @Override
            public void run() {
                arm.setWristPosition(true);
            }
        };
        timer.schedule(wristTask, 100);
        arm.setLiftHeight(12, 1);
        arm.setLiftHeight(-4, -1);
        arm.setClawPosition(true);
    }

    private void scanRedSide() {
        sleep(500);
        Vector3D skystonePos = vision.getSkystonePosition();
        SkyStoneConfiguration config = determineConfigRedSide(skystonePos);
        Debug.log(config);
        while (opModeIsActive()) ;
    }


    @Override
    public void onStop() {

    }


}
