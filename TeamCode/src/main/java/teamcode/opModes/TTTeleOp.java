package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.robotComponents.League1TTArm;
import teamcode.robotComponents.MetaTTArm;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.common.Vector2D;

@TeleOp(name = "TT TeleOp")
public class TTTeleOp extends AbstractOpMode {

    private static final double TURN_SPEED_MODIFIER = 0.6;
    private static final double STRAIGHT_SPEED_MODIFIER = 0.75;
    private static final double CLAW_COOLDOWN_SECONDS = 0.5;
    private final int SCORING_TICKS = 2100;

    private TTDriveSystem driveSystem;
    private MetaTTArm arm;
    private boolean canUseClaw;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new MetaTTArm(this);
        canUseClaw = true;
    }

    @Override
    protected void onStart() {
        new ArmInputListener().start();
        while (opModeIsActive()) {
            driveUpdate();
        }
    }

    protected void onStop() {

    }

    private void driveUpdate() {
        double vertical = gamepad1.right_stick_y * STRAIGHT_SPEED_MODIFIER;
        double horizontal = gamepad1.right_stick_x;
        double turn = -gamepad1.left_stick_x * TURN_SPEED_MODIFIER;
        if (gamepad1.right_bumper) {
            vertical = vertical / STRAIGHT_SPEED_MODIFIER;
            Vector2D velocity = new Vector2D(horizontal, vertical);
            driveSystem.continuous(velocity, turn);
        } else {
            Vector2D velocity = new Vector2D(horizontal, vertical);
            driveSystem.continuous(velocity, turn);
        }
    }

    private class ArmInputListener extends Thread {

        @Override
        public void run() {
            while (opModeIsActive()) {
                try {
                    armUpdate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void armUpdate()  throws InterruptedException{

            if(gamepad1.right_trigger > 0){
                boolean abort = true;
                //can intake is for the implementation of an abort button
                arm.intake(1);
                while (!arm.intakeIsFull()){
                    if(gamepad1.left_bumper){
                        arm.intake(0);
                        abort = false;
                        break;
                    }
                }
                if(abort) {
                    arm.intake(0.0);
                    arm.setClawPosition(false);
                    sleep(500);
                    arm.lift(SCORING_TICKS, 0.5);
                    arm.extendWristIncrementally();
                    arm.lift(-SCORING_TICKS, 0.5);
                }
            }else if(gamepad1.left_trigger > 0){
                arm.intake(gamepad1.left_trigger);
            }else if(gamepad1.x){
                if(arm.intakeIsFull()) {
                    arm.setClawPosition(true);
                    sleep(2000);
                    arm.lift(SCORING_TICKS, 0.5);
                    arm.setWristPosition(false);
                    arm.lift(-SCORING_TICKS, 0.5);
                    sleep(1000);
                }else{
                    arm.setClawPosition(true);
                }
            }else if(gamepad1.b){
                arm.lift(SCORING_TICKS - arm.getLiftHeight(), 1);
            }else if (gamepad1.dpad_down) {
                while(gamepad1.dpad_down) {
                    arm.lift(-100, -0.5);
                }
            } else if (gamepad1.dpad_up) {
                while (gamepad1.dpad_up){
                    arm.lift(100, 0.5);
                }
            }else if(gamepad1.dpad_right){
                arm.setWristPosition(true);
            }else if(gamepad1.dpad_left){
                arm.setWristPosition(false);
            }
            arm.intake(0);
            Debug.log(arm.getLiftHeight());
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

    }



}
