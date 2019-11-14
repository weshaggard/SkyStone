package teamcode.common;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.impl.MetaTTArm;

@TeleOp(name = "Rotation Calibration")
public class RotationCalibration extends TTOpMode{

    public MetaTTArm arm;
    public TTDriveSystem driveSystem;


    @Override
    protected void onInitialize() {
        arm = new MetaTTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap, 24.4);
    }

    @Override
    protected void onStart() {
        driveSystem.turn(360, 1);
    }

    @Override
    protected void onStop() {

    }

}
