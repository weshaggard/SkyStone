package teamcode.league3;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;


@TeleOp(name="Tele-Op")
public class LeagueThreeTeleOpBasic extends AbstractOpMode {

    private static final double WINCH_MOTOR_POWER = 0.5;
    private MoonshotArmSystem arm;
    private DriveSystem drive;

    private double NORMAL_MODIFIER_ROTATIONAL = 0.3;
    private double SPRINT_MODIFIER_ROTATIONAL = 0.6;
    private double NORMAL_MODIFIER_LINEAR = 0.5;
    private double SPRINT_MODIFIER_LINEAR = 1.0;

    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);
        drive = new DriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        Thread driveUpdate = new Thread(){
           @Override
            public void run(){
               while(opModeIsActive()) {
                   driveUpdate();
               }
            }
        };
        Thread armUpdate = new Thread(){
            @Override
            public void run(){
                while(opModeIsActive()) {
                    armUpdate();
                }
            }
        };
        driveUpdate.start();
        armUpdate.start();
        while(opModeIsActive());
    }

    private void armUpdate() {
        if(gamepad1.right_trigger > 0.3){
            arm.intakeSequence();
        }else if(gamepad1.left_trigger > 0.3) {
            arm.suck(-1);
        }else if(gamepad1.dpad_right){
            //arm.primeToScore();
        }else if(gamepad1.dpad_up){
            arm.lift(WINCH_MOTOR_POWER);
        }else if(gamepad1.dpad_down){
            arm.lift(-WINCH_MOTOR_POWER);
        }else if(gamepad1.x){
            //arm.score(WINCH_MOTOR_POWER);
        }else if(gamepad1.a){
            //arm.reset();
        }
    }

    private void driveUpdate(){
            Vector2D velocity;
            double turnSpeed = -gamepad1.left_stick_x;
            if (gamepad1.left_trigger > 0.3) {
                velocity = new Vector2D(gamepad1.right_stick_x * SPRINT_MODIFIER_LINEAR, gamepad1.right_stick_y * SPRINT_MODIFIER_LINEAR);
                turnSpeed *= SPRINT_MODIFIER_ROTATIONAL;

            } else {
                velocity = new Vector2D(gamepad1.right_stick_x * NORMAL_MODIFIER_LINEAR, gamepad1.right_stick_y * NORMAL_MODIFIER_LINEAR);
                turnSpeed *= NORMAL_MODIFIER_ROTATIONAL;
            }
            drive.continuous(velocity, turnSpeed);
    }

    @Override
    protected void onStop() {

    }
}
