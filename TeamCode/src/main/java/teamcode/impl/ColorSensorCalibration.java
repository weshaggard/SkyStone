package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.League1TTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;

@Autonomous(name = "Color Sensor Calibration")
public class ColorSensorCalibration extends TTOpMode {
    private TTDriveSystem driveSystem;
    private League1TTArm arm;


    @Override
    protected void onInitialize() {
        arm = new League1TTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        //if(arm.blueTapeListening(1);)
    }

    @Override
    protected void onStop() {
        //skystoneSensor.getSkystoneDetector().enableLed(false);
    }
}
