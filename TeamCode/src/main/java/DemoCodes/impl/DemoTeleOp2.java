package DemoCodes.impl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import java.util.TimerTask;

import DemoCodes.common.DemoArm;
import DemoCodes.common.DemoArm2;
import DemoCodes.common.DemoDrive;
import teamcode.common.TTOpMode;

@TeleOp(name = "Demo TeleOp 2")
public class DemoTeleOp2 extends TTOpMode {
    private static final double CLAW_COOLDOWN_SECONDS = 0.5;
    private DemoDrive driveSystem;
    private DemoArm2 arm;
    private boolean canUseClaw;
    @Override
    protected void onInitialize() {
        driveSystem = new DemoDrive(hardwareMap);
        arm = new DemoArm2(hardwareMap);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            driveUpdate();
            armUpdate();
        }
    }

    @Override
    protected void onStop() {
    }

    private void driveUpdate() {
        double vertical = gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;
        driveSystem.continuous(vertical, turn);
    }

    private void armUpdate(){
        while(gamepad1.dpad_up){
            arm.rotateUp();
        }
        while(gamepad1.dpad_down){
            arm.rotateDown();
        }
        if(gamepad1.y) {
            arm.raise();
        } else if(gamepad1.a){
            arm.lower();
        }
        if (gamepad1.x && canUseClaw) {
            if (arm.clawIsOpen()) {
                arm.closeClaw();
            } else {
                arm.openClaw();
            }
            clawCooldown();
        }
        arm.stop();
        arm.rotateStop();

    }

    private void clawCooldown() {
        canUseClaw = false;
        TimerTask enableClaw = new TimerTask(){
            @Override
            public void run(){
                canUseClaw = true;
            }
        };
        getTimer().schedule(enableClaw, (long) (CLAW_COOLDOWN_SECONDS * 1000));
    }

}
