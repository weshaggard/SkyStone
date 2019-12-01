package teamcode.league2;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.common.Vector2D;

@TeleOp(name = "Tele Op")
public class TeleOpLeague2 extends AbstractOpMode {

    private static final double MAX_LIFT_HEIGHT_INCHES = 14;
    private static final double LIFT_SCORE_STEP_INCHES = 5.25;
    private static final double LIFT_MANUAL_STEP_INCHES = 1;
    private static final double ARM_HOME_CLEARANCE_HEIGHT_INCHES = 12;
    private static final double FIRST_SCORE_HEIGHT_INCHES = 4.5;

    private static final double TURN_SPEED_MODIFIER = 0.45;
    private static final double VERTICAL_SPEED_MODIFIER = 0.5;
    private static final double LATERAL_SPEED_MODIFIER = 0.3;

    private static final long CLOSE_CLAW_DELAY = 1000;
    private static final long OPEN_CLAW_DELAY = 2500;

    private int scoreLevel;

    private DriveSystemLeague2 driveSystem;
    private ArmSystemLeague2 arm;
    private Timer timer2;

    private StoneBoxState stoneBoxState;
    private WristState armState;
    private ClawState clawState;
    private IntakeState intakeState;

    @Override
    protected void onInitialize() {
        driveSystem = new DriveSystemLeague2(hardwareMap);
        arm = new ArmSystemLeague2(this);
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

    protected void onStop() {
    }

    private void setStartState() {
        // reset the current skystone score level height
        scoreLevel = 0;

        // Open the claw
        openClaw(false);

        // Retract arm
        //retractArm(false);

        // Turn off the intake
        intakeOff();

        // The stone box should be empty
        stoneBoxState = StoneBoxState.Empty;
    }

    private enum StoneBoxState {
        Empty,
        Full
    }

    private enum WristState {
        Retracted,
        Extended
    }

    private enum ClawState {
        Open,
        Close
    }

    private enum IntakeState {
        Ingress,
        Egress,
        Off
    }

    private void intakeOff() {
        arm.intake(0.0);
        intakeState = IntakeState.Off;
    }

    private void intake() {
        arm.intake(0.5);
        intakeState = IntakeState.Ingress;
    }

    private void outtake(double power) {
        arm.intake(-power);
        intakeState = IntakeState.Egress;
    }

    private void closeClaw(boolean wait) {
        arm.setClawPosition(false);
        if (wait) {
            Utils.sleep(CLOSE_CLAW_DELAY);
        }
        clawState = ClawState.Close;
    }

    private void openClaw(boolean wait) {
        arm.setClawPosition(true);
        if (wait) {
            Utils.sleep(OPEN_CLAW_DELAY);
        }
        clawState = ClawState.Open;
    }

    private void extendWrist() {
        arm.extendWristIncrementally();
        armState = WristState.Extended;
    }

    private void retractArm(boolean wait) {
        arm.setWristPosition(false);
        if (wait) {
            Utils.sleep(500);
        }
        armState = WristState.Retracted;
    }

    private class DriveControl extends Thread {
        @Override
        public void run() {
            while (opModeIsActive()) {
                double vertical = gamepad1.right_stick_y;
                double lateral = gamepad1.right_stick_x;
                double turn = gamepad1.left_stick_x;
                if (!gamepad1.right_bumper) {
                    vertical *= VERTICAL_SPEED_MODIFIER;
                    lateral *= LATERAL_SPEED_MODIFIER;
                    turn *= TURN_SPEED_MODIFIER;
                }
                if (clawState == ClawState.Close) {
                    vertical *= -1;
                    lateral *= -1;
                }
                Vector2D velocity = new Vector2D(lateral, vertical);
                driveSystem.continuous(velocity, turn);
            }
        }
    }

    private class ScoreControl extends Thread {
        @Override
        public void run() {
            boolean yDown = false;
            while (opModeIsActive()) {
                if (gamepad1.dpad_right) {
                    // extend manually
                    extendWrist();
                } else if (gamepad1.dpad_left) {
                    // retract manually
                    retractArm(true);
                } else if (gamepad1.dpad_up) {
                    // move lift up
                    double inches = Math.min(arm.getLiftHeight() + LIFT_MANUAL_STEP_INCHES, MAX_LIFT_HEIGHT_INCHES);
                    Debug.log("Lift up to: " + inches);
                    arm.lift(inches, 1.0);
                } else if (gamepad1.dpad_down) {
                    // move lift down
                    double inches = Math.max(arm.getLiftHeight() - LIFT_MANUAL_STEP_INCHES, 0);
                    Debug.log("Lift down to: " + inches);
                    arm.lift(inches, 1.0);
                } else if (gamepad1.b) {
                    // first height
                    double inches = 2.5;
                    scoreLevel = 1;
                    Debug.log("Reset height: " + inches);
                    arm.lift(inches, 1.0);
                }
                if (gamepad1.y) {
                    if (!yDown) {
                        scorePosition();
                    }
                    yDown = true;
                } else {
                    yDown = false;
                }
                if (gamepad1.x) {
                    toggleClaw();
                } else if (gamepad1.a) {
                    homePosition();
                } else if (stoneBoxState == StoneBoxState.Full && scoreLevel == 0 && clawState == ClawState.Open) {
                    closeClaw(false);
                }
            }
        }

        private void scorePosition() {
            if (armState == WristState.Retracted) {
                arm.lift(ARM_HOME_CLEARANCE_HEIGHT_INCHES, 1.0);
                extendWrist();
            }

            if (scoreLevel <= 3) {
                double newHeight = FIRST_SCORE_HEIGHT_INCHES + (scoreLevel * LIFT_SCORE_STEP_INCHES);
                newHeight = Math.min(newHeight, MAX_LIFT_HEIGHT_INCHES);
                scoreLevel++;
                Debug.log("Lift to level " + scoreLevel + ": " + newHeight + " inches");
                liftArm(newHeight, 1.0);
            }
        }

        private void homePosition() {
            if (armState == WristState.Extended) {
                double height = arm.getLiftHeight();

                if (height < ARM_HOME_CLEARANCE_HEIGHT_INCHES) {
                    //liftArm(ARM_HOME_CLEARANCE_HEIGHT_INCHES, 1.0);
                    //Utils.sleep(500);
                    arm.lift(ARM_HOME_CLEARANCE_HEIGHT_INCHES, 1.0);
                }

                retractArm(false);
            }

            openClaw(false);
            Utils.sleep(500);
            liftArm(0, 1.0);
            scoreLevel = 0;
        }

        private void toggleClaw() {
            if (clawState == ClawState.Open) {
                closeClaw(true);
            } else {
                openClaw(true);
            }
        }

        private void liftArm(final double inches, final double power) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Debug.log("Lift to: " + inches);
                    arm.lift(inches, power);
                }
            };
            timer2.schedule(task, 0);
        }
    }

    private class IntakeControl extends Thread {
        @Override
        public void run() {
            while (opModeIsActive()) {
                if (gamepad1.left_bumper || stoneBoxState == StoneBoxState.Full) {
                    // Turn off the intake when left bumper is pressed
                    // or stone box is full
                    intakeOff();
                } else if (gamepad1.right_trigger > 0) {
                    // Turn on the intake
                    intake();
                } else if (gamepad1.left_trigger > 0) {
                    // Turn on the outtake
                    outtake(gamepad1.left_trigger);
                }
            }
        }
    }

    private class StoneBoxControl extends Thread {
        @Override
        public void run() {
            while (opModeIsActive()) {
                if (arm.intakeIsFull()) {
                    stoneBoxState = StoneBoxState.Full;
                } else {
                    stoneBoxState = StoneBoxState.Empty;
                }
            }
        }
    }
}
