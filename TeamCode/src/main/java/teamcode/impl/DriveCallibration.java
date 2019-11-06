package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;

@Autonomous(name = "Drive Callibration")
public class DriveCallibration extends TTOpMode {

    TTDriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        driveSystem.vertical(100, 0.6);
        while(opModeIsActive()) {
            Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
            telemetry.addData("Front Left:", driveSystem.getMotors()[0].getCurrentPosition());
            telemetry.addData("Front Right:", driveSystem.getMotors()[1].getCurrentPosition());
            telemetry.addData("Back Left:", driveSystem.getMotors()[2].getCurrentPosition());
            telemetry.addData("Back Right:", driveSystem.getMotors()[3].getCurrentPosition());
            telemetry.update();
        }
    }

    @Override
    protected void onStop() {

    }

}
