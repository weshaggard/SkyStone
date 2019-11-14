package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.TimerTask;

import teamcode.common.Debug;
import teamcode.common.MetaTTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;
import teamcode.common.Vector2;

@TeleOp(name =  "Meta Tele Op")
public class MetaTTTeleOp extends TTOpMode {
    private static final double WRIST_COOLDOWN_SECONDS = 0.5;
    private MetaTTArm arm;
    private TTDriveSystem driveSystem;
    private static final double CLAW_COOLDOWN_SECONDS = 0.5;
    private boolean canUseClaw;
    private boolean canUseWrist;
    private static final double STRAIGHT_SPEED_MODIFIER = 0.75;
    private static final double TURN_SPEED_MODIFIER = 0.6;


    @Override
    protected void onInitialize() {
        arm = new MetaTTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap);
        canUseClaw = true;
        canUseWrist = true;
        arm.getArmlift().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.getArmlift().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    protected void onStart() {
        new IntakeInput().start();
        while(opModeIsActive()){
            driveUpdate();
        }
    }

    private void driveUpdate() {
        double vertical = gamepad1.right_stick_y * STRAIGHT_SPEED_MODIFIER;
        double horizontal = gamepad1.right_stick_x;
        double turn = -gamepad1.left_stick_x * TURN_SPEED_MODIFIER;
        if(gamepad1.right_bumper){
            vertical = vertical / STRAIGHT_SPEED_MODIFIER;
            Vector2 velocity = new Vector2(horizontal, vertical);
            driveSystem.continuous(velocity, turn);
        } else {
            Vector2 velocity = new Vector2(horizontal, vertical);
            driveSystem.continuous(velocity, turn);
        }
    }

    private class IntakeInput extends Thread{

        @Override
        public void run(){
            while(opModeIsActive()){
                armUpdate();
            }
        }

        public void armUpdate(){
            Debug debug = new Debug();
            debug.log(arm.getLiftPosition());
            if(gamepad1.right_trigger > 0) {
                arm.suck(gamepad1.right_trigger);
            }else if(gamepad1.left_trigger > 0){
                arm.spit(gamepad1.left_trigger);
            }else if(gamepad1.a && canUseClaw){
                arm.adjustClawPos();
                clawCooldown();
             }else if(gamepad1.b && canUseWrist) {
                arm.rotate();
                rotateCooldown();
            } else if(gamepad1.dpad_down) {
                arm.useArm(-1.0);
            } else if(gamepad1.dpad_up) {
                arm.useArm(1);
            } else if(gamepad1.dpad_left) {
                arm.useArm(-0.5);
            } else if(gamepad1.dpad_right){
                arm.useArm(0.5);
            }else{
                arm.spit(0);
                arm.useArm(0);
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
