package teamcode.league3;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;

@TeleOp(name= "TeleOp")
public class LeagueThreeTeleOp extends AbstractOpMode {

    private static final double WINCH_MOTOR_POWER = 0.5;
    private static final double RIGHT_INTAKE_MOTOR_POWER = 0.3;
    private static final double LEFT_INTAKE_MOTOR_POWER = 0.7;
    private MoonshotArmSystem arm;
    private Thread armUpdate;
    private DriveSystem driveSystem;
    private Thread driveUpdate;
    private int presetNum;

    private double TURN_SPEED_MODIFIER = 0.3;
    private double TURN_SPRINT_SPEED_MODIFIER = 0.8;
    private double LINEAR_SPRINT_SPEED_MODIFIER = 1.0;
    private double NORMAL_SPEED_MODIFIER = 0.4;


    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(this.hardwareMap);
        driveSystem = new DriveSystem(hardwareMap, new GPS(hardwareMap, new Vector2D(36, 72), Math.toRadians(90)), new Vector2D(36, 72), Math.toRadians(90));
        //presetNum = 1;

    }

    private void driveUpdate() {
            Vector2D velocity;
            if (gamepad1.left_trigger > 0.3) {
                velocity = new Vector2D(gamepad1.right_stick_x * LINEAR_SPRINT_SPEED_MODIFIER, gamepad1.right_stick_y * LINEAR_SPRINT_SPEED_MODIFIER);

            } else {
                velocity = new Vector2D(gamepad1.right_stick_x * NORMAL_SPEED_MODIFIER, gamepad1.right_stick_y * NORMAL_SPEED_MODIFIER);
            }
            driveSystem.continuous(velocity, gamepad1.left_stick_x * TURN_SPEED_MODIFIER);
    }

        private void armUpdate () {
            if (gamepad1.a) {
            } else if (gamepad1.x) {
                arm.score(presetNum, WINCH_MOTOR_POWER);
                presetNum++;
            } else if (gamepad1.y) {

            } else if (gamepad1.right_trigger > 0.3) {
                arm.intake(1);
            }

        }

        @Override
        protected void onStart () {
            armUpdate = new Thread(){
                public void run(){
                    while(opModeIsActive()){
                        armUpdate();
                    }
                }
            };
            driveUpdate = new Thread() {
                public void run() {
                    while (opModeIsActive()) {
                        driveUpdate();
                    }
                }
            };
            driveUpdate.start();
            armUpdate.start();
            while(opModeIsActive());
        }

        @Override
        protected void onStop () {
        }


        //Patrick's Driver 2 Control scheme
    /*
    lstick translational rstick rotational
    movement speed todo, not 100%
    translational sprint and rotational sprint indepent, press down on each of the sticks
    rt modular intake
    lt modular outtake

    lb (jams)/rb(hotkeys)
    a lb rb back arm come down and eject block
    b
    y
    x lb tranfer case goes down, move front arm up, move slide out and in, eject intake

    dpad (activate with rb/lb)
    left lb same as right rb adjust arm
    right: lb linear slide rb adjust arm
    up: lb transfer case adjustment rb adjust arm
    down: lb transfer case adjustment rb adjust arm
     */

}
