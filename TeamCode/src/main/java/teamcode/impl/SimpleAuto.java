package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.League1TTArm;
import teamcode.common.TTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;

@Autonomous(name = "simple")
public class SimpleAuto extends TTOpMode {
    private TTDriveSystem driveSystem;
    private League1TTArm arm;
    @Override
    protected void onInitialize() {
        arm = new League1TTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        driveSystem.vertical(30, 1);
    }

    @Override
    protected void onStop() {

    }
}
