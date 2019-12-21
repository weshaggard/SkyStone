package teamcode.league3;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;

import com.qualcomm.robotcore.eventloop.opmode.*;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp")
public class TeleOp extends AbstractOpMode {
    private Thread driveSystemExcecution;
    private Thread armSystemExcecution;

    private DriveSystem driveSystem;
    private ArmSystem arm;
    private int presetNum;
    private final double SPRINTING_SPEED = 0.85;
    private final double NORMAL_SPEED = 0.4;
    private final double ARM_SYSTEM_POWER = 0.7;

    @Override
    protected void onInitialize() {
        //driveSystem = new DriveSystem(hardwareMap);
        arm = new ArmSystem(hardwareMap);
        presetNum = 1;
        driveSystemExcecution = new Thread(){
          public void run(){
              while(opModeIsActive()){
                  driveUpdate();
              }
          }
        };
        armSystemExcecution = new Thread(){
          @Override
          public void run(){
              while(opModeIsActive()){
                  try {
                      armUpdate();
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
          }

        };
    }

    private void armUpdate() throws InterruptedException {
        if(gamepad1.a){
            arm.goToHome(0.6);
            presetNum = 1;
        }else if(gamepad1.b){
            arm.lift(presetNum, ARM_SYSTEM_POWER);
            presetNum++;
        }else if(gamepad1.left_trigger > 0.3){
            arm.intake(gamepad1.left_trigger);
            while(!arm.intakeIsFull());
            arm.moveToScoreFromIntookStone(presetNum, ARM_SYSTEM_POWER);
            presetNum++;
        }else if(gamepad1.y){
            arm.intake(0);
        }else if(gamepad1.right_trigger > 0.3){
            arm.intake(-gamepad1.right_trigger);
        }else if(gamepad1.dpad_up || gamepad2.dpad_up){
            arm.adjustLiftHeight(1, ARM_SYSTEM_POWER);
        }else if(gamepad1.dpad_down || gamepad2.dpad_down){
            arm.adjustLiftHeight(-1, ARM_SYSTEM_POWER);
        }else if(gamepad1.dpad_left || gamepad2.dpad_right){
            arm.adjustFoundationGrabbers();
        }else if(gamepad1.x){
            arm.adjustClawPosition();
        }
    }



    private void driveUpdate() {
        if(gamepad1.left_bumper){
            Vector2D velocity = new Vector2D(gamepad1.right_stick_x * SPRINTING_SPEED, gamepad1.left_stick_y * SPRINTING_SPEED);
            driveSystem.continuous(velocity, gamepad1.left_stick_y);
        }else {
            Vector2D velocity = new Vector2D(gamepad1.right_stick_x * NORMAL_SPEED, gamepad1.left_stick_y * NORMAL_SPEED);
            driveSystem.continuous(velocity, gamepad1.left_stick_y);
        }
    }

    @Override
    protected void onStart() {
        driveSystemExcecution.start();
        armSystemExcecution.start();
    }

    @Override
    protected void onStop() {

    }
}
