package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.League1TTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;

@Autonomous(name = "Drive Callibration")
public class DriveCallibration extends TTOpMode {

    TTDriveSystem driveSystem;
    League1TTArm arm;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new League1TTArm(hardwareMap);
    }

    @Override
    protected void onStart() {
        arm.lower(0.5);
        driveSystem.turn(360 * 5, 0.5);

    }

    @Override
    protected void onStop() {

    }

}
