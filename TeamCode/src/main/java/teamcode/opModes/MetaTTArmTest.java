package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.robotComponents.MetaTTArm;
import teamcode.robotComponents.TTHardwareComponentNames;

@Autonomous(name = "Meta Arm Test 2")
public class MetaTTArmTest extends AbstractOpMode {

    private static final int TICKS = 2000;
    private static final long CLOSE_CLAW_DELAY = 1000;
    private static final long OPEN_CLAW_DELAY = 2500;
    private static final int ARM_STATE_WAIT = 0;
    private static final int ARM_STATE_SCORE = 1;
    private static final int ARM_STATE_RETRACT = 2;

    private Timer timer1;
    private Timer timer2;
    private MetaTTArm arm;
    private int armState = ARM_STATE_WAIT; // 0 is wait, 1 is score, 2 is retract

    @Override
    protected void onInitialize() {
        timer1 = getNewTimer();
        timer2 = getNewTimer();
        arm = new MetaTTArm(this);
    }

    @Override
    protected void onStart() {
        arm.setClawPosition(true);
        arm.intake(1.0);

        // intake task
        TimerTask intakeTask = new TimerTask() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    if (arm.intakeIsFull()) {
                        arm.intake(0.0);
                        armState = ARM_STATE_SCORE;
                    }
                }
            }
        };

        // arm task
        TimerTask armTask = new TimerTask() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    if (armState == ARM_STATE_SCORE) {
                        // score
                        closeClaw();
                        Debug.log("Going up");
                        arm.lift(TICKS, 1.0);
                        Debug.log("Swinging out");
                        arm.extendWristIncrementally();
                        Debug.log("Going down");
                        arm.lift(-TICKS, 1.0);
                        openClaw();
                        armState = ARM_STATE_RETRACT;
                    } else if (armState == ARM_STATE_RETRACT) {
                        int t = 1200;
                        // retract
                        Debug.log("Going up");
                        liftArm(t, 1.0);
                        Utils.sleep(200);
                        Debug.log("Swinging in");
                        arm.setWristPosition(false);
                        Debug.log("Going down");
                        Utils.sleep(500);
                        liftArm(-t, 0.7);
                        armState = ARM_STATE_WAIT;
                    }
                }
            }
        };

        // Schedule all tasks
        timer1.schedule(intakeTask, 0);
        timer2.schedule(armTask, 0);

        while (opModeIsActive()) ;
    }

    @Override
    protected void onStop() {
    }

    private void closeClaw() {
        arm.setClawPosition(false);
        sleep(CLOSE_CLAW_DELAY);
    }

    private void openClaw() {
        arm.setClawPosition(true);
        sleep(OPEN_CLAW_DELAY);
    }

    private void liftArm(final int ticks, final double power) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                arm.lift(ticks, power);
            }
        };
        timer2.schedule(task, 0);
    }
}
