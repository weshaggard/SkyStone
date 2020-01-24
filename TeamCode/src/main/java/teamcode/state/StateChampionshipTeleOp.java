package teamcode.state;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Timer;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;

@TeleOp(name = "State Championship Tele Op")
public class StateChampionshipTeleOp extends AbstractOpMode {

    private static final double WINCH_MOTOR_POWER = 1.0;
    private MoonshotArmSystem arm;
    private DriveSystem drive;

    private double NORMAL_MODIFIER_ROTATIONAL = 0.5;
    private double SPRINT_MODIFIER_ROTATIONAL = 0.75;
    private double NORMAL_MODIFIER_LINEAR = 0.5;
    private double SPRINT_MODIFIER_LINEAR = 1.0;
    private static final double TURN_SPEED_CORRECTION_MODIFIER = 0;
    private boolean canSnapUp;
    Timer snapUpCooldown;
    Timer snapDownCooldown;
    private boolean canSnapDown;

    Thread driveUpdate;
    Thread armUpdate;

    private boolean flipDriveControls = false;
    private boolean driverOne;

    @Override
    protected void onInitialize() {
        drive = new DriveSystem(hardwareMap);
        canSnapUp = true;
        snapUpCooldown = new Timer();
        snapDownCooldown = new Timer();
        canSnapDown = true;
        driverOne = true;
    }

    @Override
    protected void onStart() {
        arm = new MoonshotArmSystem(hardwareMap);
        driveUpdate = new Thread() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    driveUpdate();
                }
            }
        };
        armUpdate = new Thread() {
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
        } else if (gamepad1.right_stick_button) {
            arm.extend();
        } else if (gamepad1.dpad_up) {
            while (gamepad1.dpad_up) {
                arm.liftContinuously(WINCH_MOTOR_POWER);
            }
            arm.liftContinuously(0);
            //arm.snapBack(true);

        } else if (gamepad1.dpad_down) {
            while (gamepad1.dpad_down) {
                arm.fastDroop();
            }
            arm.liftContinuously(0);

        } else if (gamepad1.x) {
            arm.score();
        } else if (gamepad1.a) {
            arm.reset();
        } else if (gamepad1.dpad_left) {
            // free button
        } else if (gamepad1.left_bumper) {
            while(gamepad1.left_bumper) {
                arm.liftContinuously(0, true);
            }
            arm.liftContinuously(0);
        } else if (gamepad1.y) {
            arm.dumpStone();
        }
        if (gamepad1.right_stick_button && !rightStickDown) {
            rightStickDown = true;
            // free button
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


        if (gamepad2.a) {
            driverOne = !driverOne;
        } else if (gamepad2.x) {
            arm.adjustFoundation();
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


    private final double SPRINT_MODIFIER_LINEAR_DRIVER_TWO = 0.8;
    private final double SPRINT_MODIFIER_ROTATIONAL_DRIVER_TWO = 0.75;
    private final double NORMAL_MODIFIER_LINEAR_DRIVER_TWO = 0.25;
    private final double NORMAL_MODIFIER_ROTATIONAL_DRIVER_TWO = 0.3;

    private void driveUpdate() {
        double turnSpeed;
        Vector2D velocity;

        if (driverOne) {
            turnSpeed = -gamepad1.left_stick_x;
            velocity = new Vector2D(-gamepad1.right_stick_x, gamepad1.right_stick_y);
            if (gamepad1.left_trigger > 0.3) {
                velocity = velocity.multiply(SPRINT_MODIFIER_LINEAR);
                turnSpeed *= SPRINT_MODIFIER_ROTATIONAL;
            } else {
                velocity = velocity.multiply(NORMAL_MODIFIER_LINEAR);
                turnSpeed *= NORMAL_MODIFIER_ROTATIONAL;
            }
            if (flipDriveControls) {
                velocity = velocity.multiply(-1);
                turnSpeed *= -1;
            }

            turnSpeed += TURN_SPEED_CORRECTION_MODIFIER * velocity.magnitude();
            drive.continuous(velocity, turnSpeed);
        } else {
            turnSpeed = -gamepad2.left_stick_x;
            velocity = new Vector2D(-gamepad2.right_stick_x, gamepad2.right_stick_y);
            if (gamepad2.left_trigger > 0.3) {
                velocity = velocity.multiply(SPRINT_MODIFIER_LINEAR_DRIVER_TWO);
                turnSpeed *= SPRINT_MODIFIER_ROTATIONAL_DRIVER_TWO;
            } else {
                velocity = velocity.multiply(NORMAL_MODIFIER_LINEAR_DRIVER_TWO);
                turnSpeed *= NORMAL_MODIFIER_ROTATIONAL_DRIVER_TWO;
            }


            turnSpeed += TURN_SPEED_CORRECTION_MODIFIER * velocity.magnitude();
            drive.continuous(velocity, turnSpeed);
        }
    }

    @Override
    protected void onStop() {
        Debug.log("Crashes are bad");
        snapUpCooldown.cancel();
        snapDownCooldown.cancel();

    }


    /*
    State Tele-Op Control Scheme

    A: Home
    X: Score
    B:

     */

}
