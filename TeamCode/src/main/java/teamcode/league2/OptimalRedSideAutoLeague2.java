package teamcode.league2;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Interval;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Utils;
import teamcode.common.Vector3D;

@Autonomous(name = "Optimal Red Side Auto")
public class OptimalRedSideAutoLeague2 extends AbstractOpMode {

    private static final Interval MIDDLE_STONE_BOUNDS = new Interval(-200, -50);
    private static final Interval RIGHT_STONE_BOUNDS = new Interval(50, 200);

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
        pullfoundation();

        while (opModeIsActive()) ;
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
        sleep(2000);
        Vector3D skystonePos = vision.getSkystonePosition();
        if (skystonePos != null) {
            double horizontalPos = -skystonePos.getY();
            Debug.log(horizontalPos);
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
            drive.turn(30, 0.6);
            drive.vertical(-18, 0.4);
            arm.setClawPosition(false);
            sleep(500);
            drive.vertical(21, 0.4);
            drive.turn(-30, 0.6);
        } else {
            drive.turn(180, 0.6);
            drive.vertical((stone - 6) * Utils.SKYSTONE_LENGTH_INCHES + 27, 1);
            drive.lateral(-24, 0.6);
            arm.intake(0.4, 0.6);
            AutoUtilsLeague2.stopIntakeWhenFull(arm);
            drive.vertical(-10, 1);
            arm.setClawPosition(false);
            sleep(500);
            drive.lateral(18, 0.6);
        }
    }

    private void scoreFirstStoneAndGrabFoundation(SkyStoneConfiguration config) {
        int stone = config.getSecondStone();
        double distance = 60 + (6 - stone) * Utils.SKYSTONE_LENGTH_INCHES;
        if (stone == 6) {
            // account for special case when stone is at end
            distance -= 8;
        }
        drive.vertical(distance, 1);

        // get ready to pull foundation
        arm.toggleFoundationGrabbers(true);
        TimerTask scorePositionTask = new TimerTask() {
            @Override
            public void run() {
                AutoUtilsLeague2.armScorePosition(arm);
            }
        };
        timer.schedule(scorePositionTask, 0);
        drive.turn(-90, 0.4);
        drive.vertical(12, 0.5);
        arm.toggleFoundationGrabbers(false);
        sleep(1000);
        arm.setClawPosition(true);
        sleep(750);
        TimerTask retractArmTask = new TimerTask() {
            @Override
            public void run() {
                AutoUtilsLeague2.armRetractSequence(arm);
            }
        };
        // schedule so that we can proceed onto the next step without waiting for the arm
        timer.schedule(retractArmTask, 0);
    }

    private void pullfoundation() {
        // arcMotion();
//        arm.toggleFoundationGrabbers(false);
//        sleep(1000);
    }

    private void arcMotion() {
        DcMotor[] motors = drive.getMotors();
        DcMotor frontLeft = motors[0];
        DcMotor frontRight = motors[1];
        DcMotor backLeft = motors[2];
        DcMotor backRight = motors[3];

        int frontLeftTicks = frontLeft.getCurrentPosition();

        double frontLeftPow = 0;
        double frontRightPow = 0;
        double backLeftPow = 0;
        double backRightPow = 0;

        frontLeft.setTargetPosition(frontLeftTicks);
        frontLeft.setPower(frontLeftPow);
        frontRight.setPower(frontRightPow);
        backLeft.setPower(backLeftPow);
        backRight.setPower(backRightPow);
    }

    private void intakeSecondStone(SkyStoneConfiguration config) {
        int skystone = config.getFirstStone();
        drive.vertical(-(Utils.SKYSTONE_LENGTH_INCHES * (6 - skystone) + 24), 1);
        drive.lateral(-18, 1);
        arm.intake(0.4, 0.6);
        AutoUtilsLeague2.stopIntakeWhenFull(arm);
        drive.vertical(-10, 1);
        drive.lateral(18, 1);
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
