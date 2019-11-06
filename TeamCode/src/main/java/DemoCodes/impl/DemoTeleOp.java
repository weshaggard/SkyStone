package DemoCodes.impl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.TimerTask;

import DemoCodes.common.DemoArm;
import DemoCodes.common.DemoDrive;
import DemoCodes.common.TTOpMode;

@TeleOp(name = "Demo TeleOp")
public class DemoTeleOp extends TTOpMode {

    private static final double CLAW_COOLDOWN_SECONDS = 0.5;

    private DemoDrive driveSystem;
    private DemoArm arm;
    private boolean canUseClaw;

    @Override
    protected void onInitialize() {
        driveSystem = new DemoDrive(hardwareMap);
        arm = new DemoArm(hardwareMap);
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
            arm.raise();
        }
        while(gamepad1.dpad_down){
            arm.lower();
        }
        arm.stop();
        if (gamepad1.x && canUseClaw) {
            Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
            telemetry.addData("Status: " , "Claw Called");
            telemetry.update();
            if (arm.clawIsOpen()) {
                arm.closeClaw();
                telemetry.addData("claw position ", arm.getClaw().getPosition());
                telemetry.update();
            } else {
                arm.openClaw();
                telemetry.addData("claw position ", arm.getClaw().getPosition());
                telemetry.update();
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