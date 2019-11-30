package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.common.Vector2D;
import teamcode.robotComponents.TTArm2;
import teamcode.robotComponents.TTDriveSystem;

@TeleOp(name = "TT TeleOp 2")
public class TTTeleOp2 extends AbstractOpMode {

    private static final double TURN_SPEED_MODIFIER = 0.3;
    private static final double STRAIGHT_SPEED_MODIFIER = 0.5;
    private static final int SCORING_TICKS = 2000;
    private static final int LIFT_STEP_TICKS = 100;

    public static final long CLOSE_CLAW_DELAY = 1000;
    public static final long OPEN_CLAW_DELAY = 2500;

    private TTDriveSystem driveSystem;
    private TTArm2 arm;
    private Timer timer2;


    private StoneBoxState stoneBoxState;
    private ArmState armState;
    private ClawState clawState;
    private IntakeState intakeState;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new TTArm2(this);
        timer2 = getNewTimer();
    }

    @Override
    protected void onStart() {
        setStartState();

        new StoneBoxControl().start();
        new IntakeControl().start();
        new ScoreControl().start();
        new DriveControl().start();

        while (opModeIsActive()) ;
    }

    @Override
    protected void onStop() {
    }

    private void setStartState() {
        // close the claw
        arm.setClawPosition(true);
        clawState = ClawState.OPEN;
        armState = ArmState.RETRACTED;

        // turn off the intake
        arm.intake(0.0);
        intakeState = IntakeState.OFF;
        stoneBoxState = StoneBoxState.EMPTY;
    }

    private enum StoneBoxState {
        EMPTY,
        FULL
    }

    private enum ArmState {
        RETRACTED,
        EXTENDED
    }

    private enum ClawState {
        OPEN,
        CLOSE
    }

    private enum IntakeState {
        ON_FORWARD,
        ON_REVERSE,
        OFF
    }

    private class DriveControl extends Thread {
        @Override
        public void run() {
            while (opModeIsActive()) {
                double vertical = gamepad1.right_stick_y * STRAIGHT_SPEED_MODIFIER;
                double horizontal = gamepad1.right_stick_x;
                double turn = -gamepad1.left_stick_x * TURN_SPEED_MODIFIER;
                if (gamepad1.right_bumper) {
                    vertical = 1.0;
                    Vector2D velocity = new Vector2D(horizontal, vertical);
                    driveSystem.continuous(velocity, turn);
                } else {
                    Vector2D velocity = new Vector2D(horizontal, vertical);
                    driveSystem.continuous(velocity, turn);
                }
            }
        }
    }

    private class ScoreControl extends Thread {
        @Override
        public void run() {
            while (opModeIsActive()) {
                if (gamepad1.dpad_right) {
                    // extend manually
                    extendArm();
                } else if (gamepad1.dpad_left) {
                    // retract manually
                    retractArm();
                } else if (gamepad1.dpad_up) {
                    // move lift up
                    int ticks = Math.min(arm.getLiftHeight() + LIFT_STEP_TICKS, SCORING_TICKS);
                    Debug.log("Lift up to: " + ticks);
                    arm.lift(ticks, 1.0);
                } else if (gamepad1.dpad_down) {
                    // move lift down
                    int ticks = Math.max(arm.getLiftHeight() - LIFT_STEP_TICKS, 0);
                    Debug.log("Lift down to: " + ticks);
                    arm.lift(ticks, 1.0);
                } else if (gamepad1.b) {
                    // first height
                    int ticks = 300;
                    Debug.log("Lift to 1st: " + ticks);
                    arm.lift(ticks, 1.0);
                } else if (gamepad1.y) {
                    // second height
                    int ticks = 1300;
                    Debug.log("Lift to 2nd: " + ticks);
                    arm.lift(ticks, 1.0);
                } else if (gamepad1.x) {
                    toggleClaw();
                } else if (armState == ArmState.RETRACTED && stoneBoxState == StoneBoxState.FULL) {
                    // extend to score
                    scorePosition();
                } else if (armState == ArmState.EXTENDED && stoneBoxState == StoneBoxState.EMPTY) {
                    // retract to home
                    homePosition();
                }
            }
        }

        private void extendArm() {
            arm.setWristPosition(true);
            Utils.sleep(500);
            armState = ArmState.EXTENDED;
        }

        private void retractArm() {
            arm.setWristPosition(false);
            Utils.sleep(500);
            armState = ArmState.RETRACTED;
        }

        private void scorePosition() {
            closeClaw();
            int ticks = arm.getLiftHeight() + SCORING_TICKS;
            Debug.log("Score Lift to: " + ticks);
            arm.lift(ticks, 1.0);
            arm.extendWristIncrementally();
            ticks = arm.getLiftHeight() - SCORING_TICKS;
            Debug.log("Score Lift to: " + ticks);
            arm.lift(ticks, 1.0);
            armState = ArmState.EXTENDED;
        }

        private void homePosition() {
            int c = 1200;
            int t = arm.getLiftHeight() + c;
            liftArm(t, 1.0);
            Utils.sleep(500);
            arm.setWristPosition(false);
            Utils.sleep(500);
            t = arm.getLiftHeight() - c;
            liftArm(t, 0.7);
            openClaw();
            armState = ArmState.RETRACTED;
        }

        private void toggleClaw() {
            if (clawState == ClawState.OPEN) {
                closeClaw();
            } else {
                openClaw();
            }
        }

        private void closeClaw() {
            arm.setClawPosition(false);
            Utils.sleep(CLOSE_CLAW_DELAY);
            clawState = ClawState.CLOSE;
        }

        private void openClaw() {
            arm.setClawPosition(true);
            Utils.sleep(OPEN_CLAW_DELAY);
            clawState = ClawState.OPEN;
        }

        private void liftArm(final int ticks, final double power) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Debug.log("Lift to: " + ticks);
                    arm.lift(ticks, power);
                }
            };
            timer2.schedule(task, 0);
        }
    }

    private class IntakeControl extends Thread {
        @Override
        public void run() {
            while (opModeIsActive()) {
                if (gamepad1.right_trigger > 0) {
                    // Turn on the intake
                    arm.intake(1.0);
                    intakeState = IntakeState.ON_FORWARD;
                } else if (gamepad1.left_trigger > 0) {
                    // Turn on the outtake
                    arm.intake(gamepad1.left_trigger);
                    intakeState = IntakeState.ON_REVERSE;
                } else if (gamepad1.left_bumper || stoneBoxState == StoneBoxState.FULL) {
                    // Turn off the intake when left bumper is pressed
                    // or stone box is full
                    arm.intake(0.0);
                    intakeState = IntakeState.OFF;
                }
            }
        }
    }

    private class StoneBoxControl extends Thread {
        @Override
        public void run() {
            while (opModeIsActive()) {
                if (arm.intakeIsFull()) {
                    stoneBoxState = StoneBoxState.FULL;
                } else {
                    stoneBoxState = StoneBoxState.EMPTY;
                }
            }
        }
    }
}