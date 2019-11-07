package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.TimerTask;

import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;
import teamcode.common.Vector2;

@TeleOp(name =  "Meta Tele Op")
public class MetaTTTeleOp extends TTOpMode {
    private MetaTTArm arm;
    private TTDriveSystem driveSystem;
    private static final double CLAW_COOLDOWN_SECONDS = 0.5;
    private boolean canUseClaw;



    @Override
    protected void onInitialize() {
        arm = new MetaTTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap);
        canUseClaw = true;
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
        double turn = gamepad1.left_stick_x;
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
            if(gamepad1.right_bumper){
                arm.suck();
            }else if(gamepad1.x && canUseClaw){
                arm.open();
                clawCooldown();
            }

        }
    }
    private void clawCooldown() {
        canUseClaw = false;
        TimerTask enableClaw = new TimerTask() {
            @Override
            public void run() {
                canUseClaw = true;
            }
        };
        getTimer().schedule(enableClaw, (long) (CLAW_COOLDOWN_SECONDS * 1000));
    }

    @Override
    protected void onStop() {
    }
}
