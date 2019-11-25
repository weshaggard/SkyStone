package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.obsolete.MetaTTArm;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.common.Vector2D;


@TeleOp(name = "Meta Tele Op")
public class TTTeleOp extends AbstractOpMode {

    private static final double WRIST_COOLDOWN_SECONDS = 0.5;
    private static final double CLAW_COOLDOWN_SECONDS = 0.5;
    private static final double STRAIGHT_SPEED_MODIFIER = 0.75;
    private static final double TURN_SPEED_MODIFIER = 0.6;
    private static final int LOW_LEVEL_WINCH = 0;
    private static final int MID_LEVEL_WINCH = 1546;
    private static final int MAX_LEVEL_WINCH = 3230;
    private TTDriveSystem driveSystem;
    private MetaTTArm arm;
    private Timer timer;

    private boolean canUseClaw;
    private boolean canUseWrist;
    private int presetIndex;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new MetaTTArm(this);
        timer = getNewTimer();

        canUseClaw = true;
        canUseWrist = true;
    }

    @Override
    protected void onStart() {
        new ArmListener().start();
        while (opModeIsActive()) {
            driveUpdate();
        }
    }

    private void driveUpdate() {
        double vertical = gamepad1.right_stick_y * STRAIGHT_SPEED_MODIFIER;
        double horizontal = gamepad1.right_stick_x;
        double turn = -gamepad1.left_stick_x * TURN_SPEED_MODIFIER;
        if (gamepad1.right_bumper) {
            vertical = vertical / STRAIGHT_SPEED_MODIFIER;
            Vector2D velocity = new Vector2D(horizontal, vertical);
            driveSystem.continuous(velocity, turn);
        } else {
            Vector2D velocity = new Vector2D(horizontal, vertical);
            driveSystem.continuous(velocity, turn);
        }
    }

    private class ArmListener extends Thread {

        @Override
        public void run() {
            while (opModeIsActive()) {
                armUpdate();
            }
        }

        public void armUpdate() {

            if (gamepad1.right_trigger > 0) {
                arm.intake(gamepad1.right_trigger);
            } else if (gamepad1.left_trigger > 0) {
                arm.intake(gamepad1.left_trigger);
            } else if (gamepad1.x && canUseClaw) {
                arm.setClawPosition(!arm.clawIsOpen());
                clawCooldown();
            }

            if (gamepad1.dpad_down) {
                arm.liftContinuously(-0.5);
            } else if (gamepad1.dpad_up) {
                arm.liftContinuously(0.5);
            } else if (gamepad1.dpad_left) {
                arm.liftContinuously(-0.25);
            } else if (gamepad1.dpad_right) {
                arm.liftContinuously(0.25);
            } else if (gamepad1.y && canUseWrist) {
                arm.lift(MAX_LEVEL_WINCH, 1);
            } else if (gamepad1.b && canUseWrist) {
                arm.lift(MID_LEVEL_WINCH, 1);
            } else if (gamepad1.a && canUseWrist) {
                arm.lift(LOW_LEVEL_WINCH, 1);
            }
            telemetry.addData("Lift Pos Ticks:", arm.getLiftHeight());
            telemetry.update();
            arm.liftContinuously(0);
            arm.intake(0);
        }
    }

    private void wristCooldown() {
        canUseWrist = false;
        TimerTask enableWrist = new TimerTask() {
            @Override
            public void run() {
                canUseWrist = true;
            }
        };
        timer.schedule(enableWrist, (long) (WRIST_COOLDOWN_SECONDS * 1000));
    }

    private void clawCooldown() {
        canUseClaw = false;
        TimerTask enableClaw = new TimerTask() {
            @Override
            public void run() {
                canUseClaw = true;
            }
        };
        timer.schedule(enableClaw, (long) (CLAW_COOLDOWN_SECONDS * 1000));
    }

    @Override
    protected void onStop() {
    }
}
