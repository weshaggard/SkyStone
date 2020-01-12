package teamcode.league3;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;


@TeleOp(name = "Tele-Op")
public class LeagueThreeTeleOpBasic extends AbstractOpMode {

    private static final double WINCH_MOTOR_POWER = 0.7;
    private MoonshotArmSystem arm;
    private DriveSystem drive;

    private double NORMAL_MODIFIER_ROTATIONAL = 0.5;
    private double SPRINT_MODIFIER_ROTATIONAL = 0.75;
    private double NORMAL_MODIFIER_LINEAR = 0.5;
    private double SPRINT_MODIFIER_LINEAR = 1.0;
    private static final double TURN_SPEED_CORRECTION_MODIFIER = 0;

    private boolean flipDriveControls = false;

    @Override
    protected void onInitialize() {
        drive = new DriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        arm = new MoonshotArmSystem(hardwareMap);
        Thread driveUpdate = new Thread() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    driveUpdate();
                }
            }
        };
        Thread armUpdate = new Thread() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    armUpdate();
                }
            }
        };
        Thread cancelUpdate = new Thread() {
            public void run() {
                while (opModeIsActive()) {
                    cancelUpdate();
                }
            }
        };
        arm.initCapstoneServo();
        driveUpdate.start();
        armUpdate.start();
        cancelUpdate.start();
        while (opModeIsActive()) ;
    }

    private boolean rightStickDown = false;
    private boolean bDown = false;
    private boolean rbDown = false;

    private void armUpdate() {
        if (gamepad1.right_trigger > 0.3) {
            arm.intakeSequence();
        } else if (gamepad1.left_trigger > 0.3) {
            arm.outtakeServoPos();
            arm.suck(-1);
        } else if (gamepad1.dpad_right) {
            arm.extend();
        } else if (gamepad1.dpad_up) {
            while (gamepad1.dpad_up) {
                arm.lift(WINCH_MOTOR_POWER, true);
            }
            arm.lift(0, true);
        } else if (gamepad1.dpad_down) {
            while (gamepad1.dpad_down) {
                arm.lift(-WINCH_MOTOR_POWER, true);
            }
            arm.lift(0, true);
        } else if (gamepad1.x) {
            arm.score();
        } else if (gamepad2.dpad_up) {
            while (gamepad2.dpad_up) {
                arm.lift(WINCH_MOTOR_POWER / 2.0, true);
            }
            arm.lift(0, true);
        } else if (gamepad2.dpad_down) {
            while (gamepad2.dpad_down) {
                arm.lift(-WINCH_MOTOR_POWER / 2.0, true);
            }
            arm.lift(0, true);
        }
        if (gamepad1.right_stick_button && !rightStickDown) {
            rightStickDown = true;
            arm.adjustFoundation();
        }
        if (!gamepad1.right_stick_button) {
            rightStickDown = false;
        }
        if (gamepad1.right_bumper && !rbDown) {
            flipDriveControls = !flipDriveControls;
            rbDown = true;
        }
        if (!gamepad1.right_bumper) {
            rbDown = false;
        }
        if (gamepad2.b && !bDown) {
            arm.capstoneScoring();
            bDown = true;
        }
        if (!gamepad2.b) {
            bDown = false;
        }
        if (gamepad1.y) {
            arm.lift(0, false);
            //that is dangerous, do NOT do this near the top
        } else if (gamepad2.a) {
            arm.attemptToAdjust();
        }

    }

    private void cancelUpdate() {
        if (gamepad1.b) {
            arm.cancelIntakeSequence();
        } else if (gamepad1.left_stick_button) {
            arm.cancelIntakeSequence();
            arm.primeToScore();
        }
    }


    private void driveUpdate() {
        double turnSpeed = -gamepad1.left_stick_x;
        Vector2D velocity = new Vector2D(-gamepad1.right_stick_x, gamepad1.right_stick_y);

        if (gamepad1.left_bumper) {
            velocity = velocity.multiply(SPRINT_MODIFIER_LINEAR);
            turnSpeed *= SPRINT_MODIFIER_ROTATIONAL;
        } else {
            velocity = velocity.multiply(NORMAL_MODIFIER_LINEAR);
            turnSpeed *= NORMAL_MODIFIER_ROTATIONAL;
        }
        if (flipDriveControls) {
            velocity = velocity.multiply(-1);
        }

        turnSpeed += TURN_SPEED_CORRECTION_MODIFIER * velocity.magnitude();
        drive.continuous(velocity, turnSpeed);
    }

    @Override
    protected void onStop() {

    }


    /*
    front of robot to be lift

     */
}
