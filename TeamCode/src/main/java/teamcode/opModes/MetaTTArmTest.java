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

    private Timer timer;
    private MetaTTArm arm;
    private int armState = 0; // 0 is wait, 1 is score, 2 is retract

    @Override
    protected void onInitialize() {
        timer = getNewTimer();
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
                while (opModeIsActive())
                {
                    if (arm.intakeIsFull()) {
                        arm.intake(0.0);
                        armState = 1;
                    }
                }
            }
        };

        // arm task
        TimerTask armTask = new TimerTask() {
            @Override
            public void run() {
                while (opModeIsActive())
                {
                    if (armState == 1) {
                        // score
                        closeClaw();
                        Debug.log("Going up");
                        arm.lift(TICKS, 1.0);
                        Debug.log("Swinging out");
                        arm.extendWristIncrementally();
                        Debug.log("Going down");
                        arm.lift(-TICKS, 1.0);
                        openClaw();
                        armState = 2;
                    }
                    else if(armState == 2){
                        long t = 1200;
                        // retract
                        Debug.log("Going up");
                        liftArm(t, 1.0);
                        Utils.sleep(200);
                        Debug.log("Swinging in");
                        arm.setWristPosition(false);
                        Debug.log("Going down");
                        Utils.sleep(500);
                        liftArm(-t, 0.7);
                        armState = 0;
                    }
                }
            }
        };

        // Schedule all tasks
        timer.schedule(intakeTask, 0);
        getNewTimer().schedule(armTask, 0);

        while (opModeIsActive());
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

    private void liftArm(final double ticks, final double power) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                arm.lift(ticks, power);
            }
        };
        getNewTimer().schedule(task, 0);
    }
}
