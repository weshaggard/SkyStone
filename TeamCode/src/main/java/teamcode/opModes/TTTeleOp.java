package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.robotComponents.TTArmSystem;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.common.Vector2D;

import static fi.iki.elonen.NanoHTTPD.Method.HEAD;


@TeleOp(name = "TT TeleOp")
public class TTTeleOp extends AbstractOpMode {

    private static final double DRIVE_LINEAR_SPEED_MULTIPLIER = 0.4;
    private static final double DRIVE_TURN_SPEED_MULTIPLIER = 0.6;
    private static final long BUTTON_COOLDOWN = 500;
    private static final double LIFT_RETRACT_HEIGHT = 10;
    private static final double LIFT_CLEARANCE_HEIGHT = 6.25;
    private static final double LIFT_SCORE_HEIGHT_1 = 5.5;
    private static final double LIFT_SCORE_HEIGHT_2 = 9.75;

    private TTDriveSystem driveSystem;
    private TTArmSystem arm;
    private Timer timer;

    private boolean allowLiftAssist;
    private double currentLiftHeight;

    private boolean canToggleArmAssist;
    private boolean canUseClaw;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new TTArmSystem(this);
        timer = getNewTimer();

        allowLiftAssist = true;
        currentLiftHeight = 0;

        canToggleArmAssist = true;
        canUseClaw = true;

        arm.setClawPosition(true);
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
        double horizontal = gamepad1.right_stick_x;
        double vertical = gamepad1.right_stick_y;
        double turn = gamepad1.left_stick_x;
        Vector2D velocity = new Vector2D(horizontal, vertical);
        if (!gamepad1.right_bumper) {
            velocity.multiply(DRIVE_LINEAR_SPEED_MULTIPLIER);
            turn *= DRIVE_TURN_SPEED_MULTIPLIER;
        }
        driveSystem.continuous(velocity, turn);
    }

    public void armUpdate() {
        if (gamepad1.right_stick_button && canToggleArmAssist) {
            allowLiftAssist = !allowLiftAssist;
            toggleArmAssistCooldown();
        }

        if (gamepad1.right_trigger > 0) {
            extendAssistSequence(); // extend motion
        }

        if (gamepad1.x && canUseClaw) {
            if (arm.wristIsExtended()) {
                arm.setClawPosition(true);
                retractAssistSequence();
            } else {
                arm.setClawPosition(!arm.clawIsOpen());
                clawCooldown();
            }
        }

        if (gamepad1.dpad_down) {
            double delta = -0.1; // inches
            arm.lift(delta, 1);
            currentLiftHeight += delta;
        } else if (gamepad1.dpad_up) {
            double delta = 0.1; // inches
            arm.lift(delta, 1);
            currentLiftHeight += delta;
        } else if (gamepad1.b) {
            double delta = LIFT_SCORE_HEIGHT_1 - currentLiftHeight;
            arm.lift(delta, 1);
            currentLiftHeight = LIFT_SCORE_HEIGHT_1;
        } else if (gamepad1.y) {
            double delta = LIFT_SCORE_HEIGHT_2 - currentLiftHeight;
            arm.lift(delta, 1);
            currentLiftHeight = LIFT_SCORE_HEIGHT_2;
        } else {
            arm.liftContinuously(0);
        }

        if (gamepad1.dpad_left) {
            arm.setWristPosition(false);
        } else if (gamepad1.dpad_right) {
            arm.setWristPosition(true);
        }

        if (gamepad1.left_trigger > 0) {
            // spit out
            arm.intake(-gamepad1.left_trigger);
        } else {
            arm.intake(0);
        }
    }

    private void extendAssistSequence() {
        arm.intake(1);
        while (!arm.intakeIsFull() && !gamepad1.left_bumper) ;
        if (gamepad1.left_bumper) {
            // left bumper aborts the sequence
            return;
        }
        arm.intake(0);
        arm.setClawPosition(false);
        Utils.sleep(1500);
        double delta = LIFT_CLEARANCE_HEIGHT - currentLiftHeight;
        Debug.log("extend up: " + delta);
        arm.lift(delta, 1);
        currentLiftHeight = LIFT_CLEARANCE_HEIGHT;
        arm.setWristPosition(true);
        Utils.sleep(500);
        Debug.log("extend down: " + currentLiftHeight);
        arm.lift(-currentLiftHeight, 1);
        currentLiftHeight = 0;
    }

    private void retractAssistSequence() {
        double delta = LIFT_CLEARANCE_HEIGHT - currentLiftHeight;
        Debug.log("retract up: " + delta);
        arm.lift(delta, 1.0);
        currentLiftHeight = LIFT_CLEARANCE_HEIGHT;
        arm.setWristPosition(false);
        Utils.sleep(500);
        Debug.log("retract down: " + currentLiftHeight);
        arm.lift(-currentLiftHeight + 0.55, 1);
        currentLiftHeight = 0;
    }

    private void toggleArmAssistCooldown() {
        canToggleArmAssist = false;
        TimerTask enable = new TimerTask() {
            @Override
            public void run() {
                canToggleArmAssist = true;
            }
        };
        timer.schedule(enable, BUTTON_COOLDOWN);
    }

    private void clawCooldown() {
        canUseClaw = false;
        TimerTask enable = new TimerTask() {
            @Override
            public void run() {
                canUseClaw = true;
            }
        };
        timer.schedule(enable, BUTTON_COOLDOWN);
    }

    @Override
    protected void onStop() {
    }
}
