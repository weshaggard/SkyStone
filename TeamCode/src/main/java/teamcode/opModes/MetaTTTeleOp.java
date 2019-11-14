package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.TimerTask;

import teamcode.robotComponents.TTDriveSystem;
import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2;

@TeleOp(name =  "Meta Tele Op")
public class MetaTTTeleOp extends AbstractOpMode {
    private static final double WRIST_COOLDOWN_SECONDS = 0.5;
    private MetaTTArm arm;
    private TTDriveSystem driveSystem;
    private static final double CLAW_COOLDOWN_SECONDS = 0.5;
    private boolean canUseClaw;
    private boolean canUseWrist;


    @Override
    protected void onInitialize() {
        arm = new MetaTTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap);
        canUseClaw = true;
        canUseWrist = true;
    }

    @Override
    protected void onStart() {
        new IntakeInput().start();
        while(opModeIsActive()){
            driveUpdate();
        }
    }

    private void driveUpdate() {
        double vertical = gamepad1.right_stick_y;
        double horizontal = gamepad1.right_stick_x;
        double turn = -gamepad1.left_stick_x;
        Vector2 velocity = new Vector2(horizontal, vertical);
        driveSystem.continuous(velocity, turn);
    }

    private class IntakeInput extends Thread{

        @Override
        public void run(){
            while(opModeIsActive()){
                armUpdate();
            }
        }

        public void armUpdate(){
            if(gamepad1.right_trigger > 0) {
                arm.suck(gamepad1.right_trigger);
            }else if(gamepad1.left_trigger > 0){
                arm.spit(gamepad1.left_trigger);
            }else if(gamepad1.a && canUseClaw){
                arm.adjustClawPos();
                clawCooldown();
             }else if(gamepad1.b && canUseWrist){
                arm.rotate();
                rotateCooldown();
            }else{
                arm.spit(0);
            }
            telemetry.addData("can rotate: ", canUseWrist);
            telemetry.update();

        }


    }
    private void rotateCooldown() {
        canUseWrist = false;
        TimerTask enableRotation = new TimerTask() {
            @Override
            public void run() {
                canUseWrist = true;
            }
        };
        getNewTimer().schedule(enableRotation, (long)(WRIST_COOLDOWN_SECONDS * 1000));
    }
    private void clawCooldown() {
        canUseClaw = false;
        TimerTask enableClaw = new TimerTask() {
            @Override
            public void run() {
                canUseClaw = true;
            }
        };
        getNewTimer().schedule(enableClaw, (long) (CLAW_COOLDOWN_SECONDS * 1000));
    }

    @Override
    protected void onStop() {
    }
}
