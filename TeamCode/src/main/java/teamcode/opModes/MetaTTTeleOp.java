package teamcode.opModes;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.robotComponents.MetaTTArm;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.common.Vector2D;


@TeleOp(name =  "Meta Tele Op")
public class MetaTTTeleOp extends AbstractOpMode {
    private static final double WRIST_COOLDOWN_SECONDS = 0.5;
    private MetaTTArm arm;
    private TTDriveSystem driveSystem;
    private static final double CLAW_COOLDOWN_SECONDS = 0.5;
    private boolean canUseClaw;
    private boolean canUseWrist;
    private static final double STRAIGHT_SPEED_MODIFIER = 0.75;
    private static final double TURN_SPEED_MODIFIER = 0.6;
    private static final int LOW_LEVEL_WINCH = 0;
    private static final int MID_LEVEL_WINCH = 1546;
    private static final int MAX_LEVEL_WINCH = 3230;
    private static final double WRIST_OPEN_POS = 1.0;
    private static final double WRIST_MID_POS = 0.5;
    private static final double WRIST_CLOSE_POS = 0.0;



    @Override
    protected void onInitialize() {
        arm = new MetaTTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap);
        canUseClaw = true;
        canUseWrist = true;
        arm.getArmLift().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.getArmLift().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
            Vector2D velocity = new Vector2D(horizontal, vertical);
            driveSystem.continuous(velocity, turn);
        } else {
            Vector2D velocity = new Vector2D(horizontal, vertical);
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
            debug.log(arm.getCurrentLiftTicks());
            if(gamepad1.right_trigger > 0) {
                arm.suck(gamepad1.right_trigger);
            } else if(gamepad1.left_trigger > 0){
                arm.spit(gamepad1.left_trigger);
            } else if(gamepad1.x && canUseClaw) {
                arm.adjustClawPos();
                clawCooldown();
            }
            if(gamepad1.dpad_down) {
                arm.useArm(-0.5);
            } else if(gamepad1.dpad_up) {
                arm.useArm(0.5);
            } else if(gamepad1.dpad_left) {
                arm.useArm(-0.25);
            } else if(gamepad1.dpad_right){
                arm.useArm(0.25);
            } else if(gamepad1.y && canUseWrist){
                arm.liftToTarget(MAX_LEVEL_WINCH, 1);
            } else if(gamepad1.b && canUseWrist) {
                arm.liftToTarget(MID_LEVEL_WINCH, 1);
            } else if (gamepad1.a && canUseWrist){
                arm.liftToTarget(LOW_LEVEL_WINCH, 1);
            }
            arm.spit(0);
            arm.useArm(0);

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
