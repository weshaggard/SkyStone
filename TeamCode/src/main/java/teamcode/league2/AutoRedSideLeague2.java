package teamcode.league2;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Interval;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Utils;
import teamcode.common.Vector3D;

@Autonomous(name = "Red Side Auto")
public class AutoRedSideLeague2 extends AbstractOpMode {

    private static final Interval LEFT_STONE_BOUNDS = new Interval(-200, -50);
    private static final Interval MIDDLE_STONE_BOUNDS = new Interval(50, 200);

    private DriveSystemLeague2 drive;
    private ArmSystemLeague2 arm;
    private VisionLeague2 vision;
    private Timer timer;

    @Override
    protected void onInitialize() {
        drive = new DriveSystemLeague2(hardwareMap);
        arm = new ArmSystemLeague2(hardwareMap);
        vision = new VisionLeague2(hardwareMap);
        timer = new Timer();
    }

    @Override
    protected void onStart() {
        setStartState();
        toScanningPos();
        SkyStoneConfiguration config = scan();
        Debug.log(config);
        intakeFirstStone(config);
        scoreFirstStoneAndGrabFoundation(config);
        pullFoundation();
        intakeSecondStone(config);

        while (opModeIsActive()) ;
//        scoreSecondStone(config);
//        pushFoundation();
    }

    private void setStartState() {
        arm.setClawPosition(true);
        arm.setWristPosition(false);
        arm.toggleFoundationGrabbers(true);
    }

    private void toScanningPos() {
        // move toward the stones
        drive.lateral(16, 0.4);
    }

    private SkyStoneConfiguration scan() {
        // pause to process image
        sleep(500);
        Vector3D skystonePos = vision.getSkystonePosition();
        if (skystonePos != null) {
            double horizontalPos = -skystonePos.getY();
            if (LEFT_STONE_BOUNDS.contains(horizontalPos)) {
                return SkyStoneConfiguration.THREE_SIX;
            } else if (MIDDLE_STONE_BOUNDS.contains(horizontalPos)) {
                return SkyStoneConfiguration.TWO_FIVE;
            }
        }
        return SkyStoneConfiguration.ONE_FOUR;
    }

    private void intakeFirstStone(SkyStoneConfiguration config) {
        int stone = config.getSecondStone();
        if (stone == 6) {
            drive.turn(180, 0.4);
            // travel toward the tape to set up for intake
            drive.vertical(12, 0.6);
            drive.lateral(-8.5, 0.6);
            arm.intake(0.75, 0.1);
            AutoUtilsLeague2.stopIntakeWhenFull(arm);
            // come in diagonally if stone is at the end
            drive.turn(45, 0.4);
            drive.vertical(-18, 0.4);
            arm.setClawPosition(false);
            sleep(500);
            drive.vertical(18, 0.4);
            // turn extra to account for error
            drive.turn(-48, 0.6);
        } else {
            drive.turn(180, 0.6);
            drive.vertical((stone - 6) * Utils.SKYSTONE_LENGTH_INCHES + 11, 1);
            drive.lateral(-25, 0.4);
            arm.intake(0.8, 0.6);
            AutoUtilsLeague2.stopIntakeWhenFull(arm);
            drive.vertical(-10, 0.2);
            arm.setClawPosition(false);
            drive.lateral(17, 0.6);
        }
    }

    private void scoreFirstStoneAndGrabFoundation(SkyStoneConfiguration config) {
        int stone = config.getSecondStone();
        double distance = 65 + (6 - stone) * Utils.SKYSTONE_LENGTH_INCHES;
        if (stone == 6) {
            // account for special case when stone is at end
            distance -= 8;
        }

        // get ready to pull foundation
        arm.toggleFoundationGrabbers(true);
        TimerTask scorePositionTask = new TimerTask() {
            @Override
            public void run() {
                AutoUtilsLeague2.armScorePosition(arm);
            }
        };
        timer.schedule(scorePositionTask, 1500);

        drive.vertical(distance, 0.6);
        drive.turn(-90, 0.4);
        drive.vertical(10, 0.2);
        arm.toggleFoundationGrabbers(false);
        arm.setClawPosition(true);
        sleep(1250);
        TimerTask retractArmTask = new TimerTask() {
            @Override
            public void run() {
                AutoUtilsLeague2.armRetractSequence(arm);
            }
        };
        // schedule so that we can proceed onto the next step without waiting for the arm
        timer.schedule(retractArmTask, 0);
    }

    private void pullFoundation() {
        // arc
        drive.customMotion(4460, -0.64, -0.16, -0.64, -0.16);
        arm.toggleFoundationGrabbers(true);
        sleep(500);
        drive.lateral(-8, 0.4);
        drive.vertical(-24, 0.4);
        drive.lateral(-12, 0.4);
        drive.lateral(4, 0.2);
    }

    private void intakeSecondStone(SkyStoneConfiguration config) {
        int stone = config.getFirstStone();
        drive.vertical(-(26 + (3 - stone) * Utils.SKYSTONE_LENGTH_INCHES), 0.6);
        drive.lateral(-16, 0.4);
        arm.intake(0.8, 0.6);
        AutoUtilsLeague2.stopIntakeWhenFull(arm);
        drive.vertical(-10, 0.2);
        arm.setClawPosition(false);
        drive.lateral(17, 0.4);
    }

    private void scoreSecondStone(SkyStoneConfiguration config) {
        int stone = config.getFirstStone();
        drive.vertical((6 - stone) * Utils.SKYSTONE_LENGTH_INCHES + 24, 1);

        TimerTask scorePositionTask = new TimerTask() {
            @Override
            public void run() {
                AutoUtilsLeague2.armScorePosition(arm);
            }
        };
        timer.schedule(scorePositionTask, 0);
        arm.setClawPosition(true);
        arm.toggleFoundationGrabbers(false);
        sleep(1000);
        TimerTask retractArmTask = new TimerTask() {
            @Override
            public void run() {
                AutoUtilsLeague2.armRetractSequence(arm);
            }
        };
        // schedule so that we can proceed onto the next step without waiting for the arm
        timer.schedule(retractArmTask, 0);
        drive.vertical(24, 1);
    }

    private void pushFoundation() {
        arm.toggleFoundationGrabbers(true);
        drive.vertical(48, 1);
        arm.toggleFoundationGrabbers(true);
        sleep(500);
    }

    private void park() {
        drive.vertical(-72, 1);
    }

    @Override
    protected void onStop() {
    }

}
