package teamcode.league3;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;

@TeleOp(name = "TeleOp")
public class LeagueThreeTeleOp extends AbstractOpMode {

    private static final double WINCH_MOTOR_POWER = 0.5;
    private MoonshotArmSystem arm;
    private Thread armControllerOne;
    private Thread armUpdateControllerTwo;
    private DriveSystem driveSystem;
    private Thread driveUpdateOne;
    private Thread driveUpdateTwo;
    private int presetNum;


    private double FIRST_DRIVER_NORMAL_MODIFIER_ROTATIONAL = 0.3;
    private double FIRST_DRIVER_SPRINT_MODIFIER_ROTATIONAL = 0.6;
    private double FIRST_DRIVER_NORMAL_MODIFIER_LINEAR = 0.5;
    private double FIRST_DRIVER_SPRINT_MODIFIER_LINEAR = 1.0;

    private double SECOND_DRIVER_NORMAL_MODIFIER_LINEAR = 0.75;
    private double SECOND_DRIVER_NORMAL_MODIFIER_ROTATIONAL = 0.65;
    private double SECOND_DRIVER_SPRINT_MODIFIER_LINEAR = 1;
    private double SECOND_DRIVER_SPRINT_MODIFIER_ROTATIONAL = 1;

    private boolean isControllerOneDelivery;

    private boolean isZeroControllerOne;
    private boolean isZeroControllerTwo;


    private boolean CanUseDPADUpControllerOne;
    private boolean CanUseDPADDownControllerOne;
    private Timer DPADUpTimerControllerOne;
    private Timer DPADDownTimerControllerOne;

    private boolean CanUseDPADUpControllerTwo;
    private boolean CanUseDPADDownControllerTwo;
    private Timer DPADUpTimerControllerTwo;
    private Timer DPADDownTimerControllerTwo;


    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(this.hardwareMap);
        driveSystem = new DriveSystem(hardwareMap);
        isZeroControllerOne = true;
        isZeroControllerTwo = true;
        isControllerOneDelivery = true;
        Debug.log("Player 1 in control");
        CanUseDPADUpControllerOne = true;
        CanUseDPADDownControllerOne = true;
        DPADUpTimerControllerOne = new Timer();
        DPADDownTimerControllerOne = new Timer();

        CanUseDPADUpControllerOne = true;
        CanUseDPADDownControllerOne = true;
        DPADUpTimerControllerOne = new Timer();
        DPADDownTimerControllerTwo = new Timer();
    }


    private void driveUpdateControllerOne() {
        if (gamepad2.left_stick_x == 0 && gamepad2.left_stick_y == 0 && gamepad2.right_stick_x == 0 && gamepad2.right_stick_y == 0) {
            Vector2D velocity;
            double turnSpeed = -gamepad1.left_stick_x;
            if (gamepad1.left_trigger > 0.3) {
                velocity = new Vector2D(gamepad1.right_stick_x * FIRST_DRIVER_SPRINT_MODIFIER_LINEAR, gamepad1.right_stick_y * FIRST_DRIVER_SPRINT_MODIFIER_LINEAR);
                turnSpeed *= FIRST_DRIVER_SPRINT_MODIFIER_ROTATIONAL;

            } else {
                velocity = new Vector2D(gamepad1.right_stick_x * FIRST_DRIVER_NORMAL_MODIFIER_LINEAR, gamepad1.right_stick_y * FIRST_DRIVER_NORMAL_MODIFIER_LINEAR);
                turnSpeed *= FIRST_DRIVER_NORMAL_MODIFIER_ROTATIONAL;
            }
            driveSystem.continuous(velocity, turnSpeed);
        }
    }

    private void driveUpdateControllerTwo() {
        if (gamepad1.left_stick_x == 0 && gamepad1.left_stick_y == 0 && gamepad1.right_stick_x == 0 && gamepad1.right_stick_y == 0) {
            Vector2D velocity;
            double turnSpeed = -gamepad2.right_stick_x;

            if (!gamepad2.right_stick_button) {
                velocity = new Vector2D(gamepad2.left_stick_x * SECOND_DRIVER_NORMAL_MODIFIER_LINEAR, gamepad2.left_stick_y * SECOND_DRIVER_NORMAL_MODIFIER_LINEAR);
            } else {
                velocity = new Vector2D(gamepad2.left_stick_x * SECOND_DRIVER_SPRINT_MODIFIER_LINEAR, gamepad2.left_stick_y * SECOND_DRIVER_SPRINT_MODIFIER_LINEAR);
            }
            if (!gamepad2.left_stick_button) {
                turnSpeed *= SECOND_DRIVER_NORMAL_MODIFIER_ROTATIONAL;

            } else {
                turnSpeed *= SECOND_DRIVER_SPRINT_MODIFIER_ROTATIONAL;
            }
            driveSystem.continuous(velocity, turnSpeed);
        }
    }

    private void armControllerOne() {
        if (isControllerOneDelivery) {
            //Arm update for delivery/Score
            if (gamepad1.a) {
                // Toggle the control flag to give the other person control.
                isControllerOneDelivery = false;
                Debug.log("Player 1 in control");
            } else if (gamepad1.x) {
                arm.score(WINCH_MOTOR_POWER);
            } else if (gamepad1.right_bumper) {
                if (isZeroControllerOne) {
                    arm.primeToScore(0, WINCH_MOTOR_POWER);
                    isZeroControllerOne = false;
                } else {
                    arm.primeToScore(1, WINCH_MOTOR_POWER);
                }
            } else if (gamepad1.left_bumper) {
                arm.primeToScore(-1, WINCH_MOTOR_POWER);
            } else if (gamepad1.dpad_up && CanUseDPADUpControllerOne) {
                arm.lift(1, WINCH_MOTOR_POWER);
                CanUseDPADUpControllerOne = false;
                TimerTask upCooldown = new TimerTask() {
                    public void run() {
                        CanUseDPADUpControllerOne = true;
                    }
                };
                DPADUpTimerControllerOne.schedule(upCooldown, 200);
            } else if (gamepad1.dpad_down && CanUseDPADDownControllerOne) {
                arm.lift(1, WINCH_MOTOR_POWER);
                CanUseDPADDownControllerOne = false;
                TimerTask downCooldown = new TimerTask() {
                    public void run() {
                        CanUseDPADDownControllerOne = true;
                    }
                };
                DPADDownTimerControllerOne.schedule(downCooldown, 200);
            }
        }
    }

    private void armUpdateControllerTwo() {
        if (!isControllerOneDelivery) {
            //Arm update for delivery/Score
            if (gamepad2.a) {
                // Toggle the control flag to give the other person control.
                isControllerOneDelivery = true;
                Debug.log("Player 1 in control");
            } else if (gamepad2.right_trigger > 0.3) {
                arm.intake(1);
            } else if (gamepad2.left_trigger > 0.3) {
                arm.suck(-1);
            } else if (gamepad2.b) {
                arm.attemptToAdjust();
            }
        }
    }

    @Override
    protected void onStart() {

        armControllerOne = new Thread() {
            public void run() {
                while (opModeIsActive()) {
                    armControllerOne();
                    //Debug.log("Thread Active ARM 1");
                }

                //Debug.log("Thread Stop ARM 1");
            }
        };
        armUpdateControllerTwo = new Thread() {
            @Override
            public void run() {
                while (opModeIsActive()) {
                    armUpdateControllerTwo();
                    //Debug.log("Thread Active ARM 2");
                }

                //Debug.log("Thread Stop ARM 2");
            }
        };
        driveUpdateOne = new Thread() {
            public void run() {
                while (opModeIsActive()) {
                    driveUpdateControllerOne();
                    //Debug.log("Thread Active DRIVE 1");
                }

                //Debug.log("Thread Stop DRIVE 1");
            }
        };
        driveUpdateTwo = new Thread() {
            public void run() {
                while (opModeIsActive()) {
                    driveUpdateControllerTwo();
                    //Debug.log("Thread Active DRIVE 2");
                }

                //Debug.log("Thread Stop DRIVE 2");
            }
        };
        driveUpdateOne.start();
        driveUpdateTwo.start();
        armControllerOne.start();
        armUpdateControllerTwo.start();

        while (opModeIsActive()) {
            //Debug.log("Thread Active MAIN");
        }

        //Debug.log("Thread Stop MAIN");
    }

    @Override
    protected void onStop() {
        driveUpdateOne.interrupt();
        driveUpdateTwo.interrupt();
        armControllerOne.interrupt();
        armUpdateControllerTwo.interrupt();
        DPADDownTimerControllerOne.cancel();
        DPADUpTimerControllerOne.cancel();
        DPADDownTimerControllerTwo.cancel();
        // DPADUpTimerControllerTwo.cancel();
    }


    //Patrick's Driver 2 Control scheme
    /*
    lstick translational rstick rotational
    movement speed todo, not 100%
    translational sprint and rotational sprint indepent, press down on each of the sticks
    rt modular intake
    lt modular outtake

    For intake and outtake when triggers are not pressed, transfer case is up and the front grabber arm is in the closed position. Only when a trigger is pressed will the transfer case go down and the front grabber arm open out.
    If intake OR outtake is pressed, it overrides color sensor


    Need: Button that unlocks front grabber arm and just push out the stone. then reset.
    Forget right button hotkeys. Right button becomes override of color sensor. If I press it, it initiates the sequence that the color sensor would

    lb (jams)/rb(hotkeys)
    a lb rb back arm come down and eject block

    y
    x lb tranfer case goes down, move front arm up, move slide out and in, eject intake

    dpad (activate with rb/lb)
    left lb same as right rb adjust arm
    right: lb linear slide rb adjust arm
    up: lb transfer case adjustment rb adjust arm
    down: lb transfer case adjustment rb adjust arm
     */


    /*
    Christian's Main Driver control
    x score
    dpad up/down manual adjustment
    dpad right out
    dpad left in
     */

}
