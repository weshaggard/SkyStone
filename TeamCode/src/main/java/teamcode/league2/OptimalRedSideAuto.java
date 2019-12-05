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

@Autonomous(name = "Optimal Red Side Auto")
public class OptimalRedSideAuto extends AbstractOpMode {

    private static final Interval MIDDLE_STONE_BOUNDS = new Interval(-200, -50);
    private static final Interval RIGHT_STONE_BOUNDS = new Interval(50, 200);

    private DriveSystemLeague2 drive;
    private ArmSystemLeague2 arm;
    private VisionLeague2 vision;
    private Timer timer;

    @Override
    protected void onInitialize() {
        drive = new DriveSystemLeague2(hardwareMap);
        arm = new ArmSystemLeague2(this);
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
        scoreFirstStone(config);
//        pullfoundation();
//        intakeSecondStone(config);
//        scoreSecondStone(config);
//        pushFoundation();

        // pause for testing purposes
        while (opModeIsActive()) ;
    }

    private void setStartState() {
        arm.setClawPosition(true);
        arm.setWristPosition(false);
        arm.resetLift();
    }

    private void toScanningPos() {
        // align with the space between the two stones to be scanned
        drive.vertical(4, 0.6);
        // move toward the stones
        drive.lateral(16, 0.6);
    }

    private SkyStoneConfiguration scan() {
        // allow some time for Vuforia to process image
        sleep(1000);
        Vector3D skystonePos = vision.getSkystonePosition();
        if (skystonePos != null) {
            double horizontalPos = -skystonePos.getY();
            if (MIDDLE_STONE_BOUNDS.contains(horizontalPos)) {
                return SkyStoneConfiguration.TWO_FIVE;
            } else if (RIGHT_STONE_BOUNDS.contains(horizontalPos)) {
                return SkyStoneConfiguration.THREE_SIX;
            }
        }
        return SkyStoneConfiguration.ONE_FOUR;
    }

    private void intakeFirstStone(SkyStoneConfiguration config) {
        int stone = config.getSecondStone();
        if (stone == 6) {
            drive.turn(180, 0.6);
            // travel toward the tape to set up for intake
            drive.vertical(24, 0.6);
            drive.lateral(-6, 0.6);
            arm.intake(0.1, 0.75);
            AutoUtilsLeague2.stopIntakeWhenFull(arm);
            // come in diagonally if stone is at the end
            drive.turn(60, 0.6);
            drive.vertical(-18, 0.4);
            arm.setClawPosition(false);
            drive.vertical(18, 0.4);
            drive.turn(-60, 0.6);
        } else {
            drive.turn(180, 0.6);
            drive.vertical((stone - 6) * Utils.SKYSTONE_LENGTH_INCHES + 27, 1);
            drive.lateral(-24, 0.6);
            arm.intake(0.4, 0.6);
            AutoUtilsLeague2.stopIntakeWhenFull(arm);
            drive.vertical(-10, 1);
            drive.lateral(18, 0.6);
        }
    }

    private void scoreFirstStone(SkyStoneConfiguration config) {
        int stone = config.getSecondStone();
        double distance = 68 + (6 - stone) * Utils.SKYSTONE_LENGTH_INCHES;
        if (stone == 6) {
            // account for special case when stone is at end
            distance -= 8;
        }
        drive.vertical(distance, 1);
        // get ready to pull foundation
        arm.grabFoundation(true);
        TimerTask scorePositionTask = new TimerTask(){
            @Override
            public void run(){
                AutoUtilsLeague2.armScorePosition(arm);
            }
        };
        timer.schedule(scorePositionTask,0);
        drive.turn(-90, 0.6);
        drive.vertical(8, 0.5);
        sleep(1000);
        AutoUtilsLeague2.armRetractSequence(arm);
    }

    private void pullfoundation() {
        arm.grabFoundation(false);
        sleep(1000);
        drive.vertical(-24, 1);
        drive.turn(90, 0.6);
        drive.vertical(-24, 1);
        arm.grabFoundation(false);
        sleep(1000);
    }

    private void intakeSecondStone(SkyStoneConfiguration config) {
        drive.vertical(-(3 * Utils.SKYSTONE_LENGTH_INCHES + 24), 1);
        drive.turn(45, 0.6);
        arm.intake(0.4, 0.6);
        AutoUtilsLeague2.stopIntakeWhenFull(arm);
        drive.vertical(-10, 1);
        drive.vertical(10, 1);
        drive.turn(-45, 0.6);
    }

    private void scoreSecondStone(SkyStoneConfiguration config) {
        int stone = config.getFirstStone();
        drive.vertical((6 - stone) * Utils.SKYSTONE_LENGTH_INCHES + 48, 1);
        AutoUtilsLeague2.armScorePosition(arm);
        AutoUtilsLeague2.armRetractSequence(arm);
    }

    private void pushFoundation() {
        arm.grabFoundation(false);
        sleep(1000);
        drive.vertical(48, 1);
    }

    private void park() {
        drive.vertical(-72, 1);
    }

    @Override
    protected void onStop() {
    }

}
