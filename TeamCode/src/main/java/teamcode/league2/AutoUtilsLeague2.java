package teamcode.league2;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.common.Vector2D;

public class AutoUtilsLeague2 {

    public static final double ARM_CLEARANCE_HEIGHT = 12;
    public static final double SCORE_HEIGHT = 6;

    public static void armScorePosition(ArmSystemLeague2 arm) {
        arm.setLiftHeight(ARM_CLEARANCE_HEIGHT, 0.5);
        arm.setWristPosition(true);
        Utils.sleep(1000);
        arm.setLiftHeight(SCORE_HEIGHT, 0.5);
    }

    public static void armRetractSequence(ArmSystemLeague2 arm) {
        arm.setLiftHeight(ARM_CLEARANCE_HEIGHT, 0.5);
        arm.setWristPosition(false);
        Utils.sleep(1000);
        arm.setLiftHeight(0, 1);
    }

    public static void stopIntakeWhenFull(final ArmSystemLeague2 arm) {
        final AbstractOpMode opMode = AbstractOpMode.currentOpMode();
        final Timer timer = opMode.getNewTimer();
        TimerTask stopIntake = new TimerTask() {
            @Override
            public void run() {
                while (!arm.intakeIsFull()) ;
                arm.setClawPosition(false);
                arm.intake(0);
                opMode.cancelTimer(timer);
            }
        };
        timer.schedule(stopIntake, 0);
    }

}
