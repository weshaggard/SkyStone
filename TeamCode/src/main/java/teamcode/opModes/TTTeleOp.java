package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Utils;
import teamcode.robotComponents.TTArmSystem;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.common.Vector2D;


@TeleOp(name = "TT TeleOp")
public class TTTeleOp extends AbstractOpMode {

    private static final double LINEAR_SPEED_MODIFIER = 0.75;
    private static final double TURN_SPEED_MODIFIER = 0.6;
    private static final long CLAW_COOLDOWN = 500;
    private static final double LIFT_SCORE_HEIGHT = 6.25;

    private TTDriveSystem driveSystem;
    private TTArmSystem arm;
    private Timer timer;

    private boolean canUseClaw;
    private boolean canUseWrist;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new TTArmSystem(this);
        timer = getNewTimer();

        canUseClaw = true;
        canUseWrist = true;
    }

    @Override
    protected void onStart() {
        // make the arm run parallel to the drive system
        TimerTask arm = new TimerTask() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    armUpdate();
                }
            }
        };
        getNewTimer().schedule(arm, 0);

        while (opModeIsActive()) {
            driveUpdate();
        }
    }

    private void driveUpdate() {
        double vertical = gamepad1.right_stick_y * LINEAR_SPEED_MODIFIER;
        double horizontal = gamepad1.right_stick_x;
        double turn = -gamepad1.left_stick_x * TURN_SPEED_MODIFIER;
        if (gamepad1.right_bumper) {
            vertical /= LINEAR_SPEED_MODIFIER;
            horizontal /= LINEAR_SPEED_MODIFIER;
        }
        Vector2D velocity = new Vector2D(horizontal, vertical);
        driveSystem.continuous(velocity, turn);
    }

    public void armUpdate() {
        if (gamepad1.right_trigger > 0) {
            extendArmSequence(); // extend motion
        }

        if (gamepad1.left_trigger > 0) {
            // spit out
            arm.intake(-gamepad1.left_trigger);
        } else {
            arm.intake(0);
        }

        if (gamepad1.x && canUseClaw) {
            arm.setClawPosition(!arm.clawIsOpen());
            clawCooldown();
        }

        if (gamepad1.dpad_down) {
            // arm.liftContinuously(-0.5);
        } else if (gamepad1.dpad_up) {
            // arm.liftContinuously(0.5);
        } else if (gamepad1.dpad_left) {
            // arm.liftContinuously(-0.25);
        } else if (gamepad1.dpad_right) {
            //arm.liftContinuously(0.25);
        } else if (gamepad1.y && canUseWrist) {
            //arm.lift(LIFT_SCORE_HEIGHT, 1);
        } else if (gamepad1.b && canUseWrist) {
            //arm.lift(LIFT_SCORE_HEIGHT, 1);
        } else if (gamepad1.a && canUseWrist) {
            //arm.lift(LIFT_SCORE_HEIGHT, 1);
        } else {
            //arm.liftContinuously(0);
        }
    }

    private void extendArmSequence() {
        arm.intake(1);
        while (!arm.intakeIsFull()) ;
        arm.intake(0);
        arm.setClawPosition(false);
        Utils.sleep(1500);
        arm.lift(LIFT_SCORE_HEIGHT, 1);
        Utils.sleep(1500);
        arm.setWristPosition(true);
        arm.lift(-LIFT_SCORE_HEIGHT, 1);
        arm.setClawPosition(true);
        Utils.sleep(500);
        arm.lift(LIFT_SCORE_HEIGHT, 1);
        arm.setWristPosition(false);
        arm.lift(-LIFT_SCORE_HEIGHT, 1);
    }

    private void clawCooldown() {
        canUseClaw = false;
        TimerTask enableClaw = new TimerTask() {
            @Override
            public void run() {
                canUseClaw = true;
            }
        };
        timer.schedule(enableClaw, CLAW_COOLDOWN);
    }

    @Override
    protected void onStop() {
    }
}
