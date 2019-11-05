package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.League1TTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;

@Autonomous(name = "MatchOneRed")
public class MatchOneRed extends TTOpMode {
    private TTDriveSystem driveSystem;
    private League1TTArm arm;

    @Override
    public void onInitialize(){
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new League1TTArm(hardwareMap);
    }

    @Override
    public void onStart(){
        driveSystem.lateral(-8, 0.5);
    }

    @Override
    public void onStop(){

    }
}
