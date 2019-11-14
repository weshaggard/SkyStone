package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2;
import teamcode.robotComponents.League1TTArm;
import teamcode.robotComponents.TTDriveSystem;

@TeleOp(name = "Drive Train Test Op")
public class DriveTrainTestOp extends AbstractOpMode {

    //private static final double TURN_SPEED_MODIFIER = 0.3;
    //private static final double REDUCED_DRIVE_SPEED = 0.4;
    private static final double CLAW_COOLDOWN_SECONDS = 0.5;

    private TTDriveSystem driveSystem;
    private League1TTArm arm;
    //private TapeColorSensing tapeColorSensing;

    private boolean canUseClaw;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        //arm = new League1TTArm(hardwareMap);
        //tapeColorSensing = new TapeColorSensing(hardwareMap);
        canUseClaw = true;
    }

    @Override
    protected void onStart() {
        //new ArmInputListener().start();
        while (opModeIsActive()) {
            driveUpdate();
        }
    }

    protected void onStop() {
    }

    private void driveUpdate() {
        double vertical = gamepad1.right_stick_y;
        double horizontal = gamepad1.right_stick_x;
        double turn = gamepad1.left_stick_x;
        Vector2 velocity = new Vector2(horizontal, vertical);
        driveSystem.continuous(velocity, turn);
        //if(tapeColorSensing.seesBlueTape() || tapeColorSensing.seesRedTape()){
           // telemetry.addData("status,", "sees the tape");
            //telemetry.update();
            //arm.raise(1);
        //}
    }

    private class ArmInputListener extends Thread {

        @Override
        public void run() {
            while (opModeIsActive()) {
                armUpdate();
            }
        }

        private void armUpdate() {
            if (gamepad1.y) {
                arm.raise(1);
            } else if (gamepad1.a) {
                arm.lower(1);
            } else if (gamepad1.b) {
                arm.liftTimed(0.75, 0.5);
            }
            if (gamepad1.dpad_up) {
                arm.liftContinuous(0.5);
            }
            if (gamepad1.dpad_down) {
                arm.liftContinuous(-0.5);
            } else {
                arm.liftContinuous(0.0);
            }
            if (gamepad1.x && canUseClaw) {
                if (arm.clawIsOpen()) {
                    arm.closeClaw();
                    //telemetry.addData("claw posistion ", arm.getClaw().getPosition());
                    //telemetry.update();
                } else {
                    arm.openClaw();
                    //telemetry.addData("claw posistion ", arm.getClaw().getPosition());
                    //telemetry.update();
                }
                clawCooldown();
            }
            if(gamepad1.left_bumper && canUseClaw){
                if(arm.clawIsMid()){
                    arm.closeClaw();
                } else {
                    arm.midClaw();
                }
                clawCooldown();
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

    }

}
