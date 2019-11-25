package teamcode.opModes;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.robotComponents.TTDriveSystem;

@Autonomous(name = "Rotation Calibration")
public class RotationCalibration extends AbstractOpMode {

    public TTDriveSystem driveSystem;


    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        driveSystem.turn(360 * 6, 0.5);
    }

    @Override
    protected void onStop() {

    }

}
