package teamcode.league3;

import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;

@TeleOp(name= "TeleOp")
public class LeagueThreeTeleOp extends AbstractOpMode {

    private static final double WINCH_MOTOR_POWER = 0.5;
    private static final double RIGHT_INTAKE_MOTOR_POWER = 0.3;
    private static final double LEFT_INTAKE_MOTOR_POWER = 0.7;
    //private MoonshotArmSystem arm;
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
        //arm = new MoonshotArmSystem(this.hardwareMap);
        driveSystem = new DriveSystem(this.hardwareMap);
        presetNum = 1;
//        armUpdate = new Thread(){
//            public void run(){
//                while(opModeIsActive()){
//                    armUpdate();
//                }
//            }
//        };
        driveUpdate = new Thread(){
            public void run(){
                while(opModeIsActive()){
                    driveUpdate();
                    Debug.log("Drive Update active");
                }
            }
        };

    }

    private synchronized void driveUpdate() {

    }

    private synchronized void armUpdate() {
//        if(gamepad1.a){
//            arm.goToHome(WINCH_MOTOR_POWER);
//        }else if(gamepad1.x){
//            arm.score(presetNum, WINCH_MOTOR_POWER);
//            presetNum++;
//        }else if(gamepad1.y){
//            arm.goToHome(WINCH_MOTOR_POWER);
//            presetNum = 1;
//        }else if(gamepad1.right_trigger > 0.3){
//            arm.intake(LEFT_INTAKE_MOTOR_POWER, RIGHT_INTAKE_MOTOR_POWER);
//        }

    }

    @Override
    protected void onStart() {
        //driveUpdate.start();
        //armUpdate.start();
        while(opModeIsActive()) {
            Vector2D velocity;
            if (gamepad1.left_trigger > 0.3) {
                velocity = new Vector2D(gamepad1.right_stick_x * LINEAR_SPRINT_SPEED_MODIFIER, gamepad1.right_stick_y * LINEAR_SPRINT_SPEED_MODIFIER);

            } else {
                velocity = new Vector2D(gamepad1.right_stick_x * NORMAL_SPEED_MODIFIER, gamepad1.right_stick_y * NORMAL_SPEED_MODIFIER);
            }
            driveSystem.continuous(velocity, gamepad1.left_stick_x * TURN_SPEED_MODIFIER);
        }
    }

    @Override
    protected void onStop() {
    }
}
