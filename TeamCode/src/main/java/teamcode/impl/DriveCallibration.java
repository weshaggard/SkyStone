package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

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
        driveSystem.turn(360 * 5, 0.6);
    }

    @Override
    protected void onStop() {

    }

}
