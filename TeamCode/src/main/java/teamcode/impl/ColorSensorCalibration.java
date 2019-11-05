package teamcode.impl;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

import teamcode.common.League1TTArm;
import teamcode.common.TTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;

@TeleOp(name = "Color Sensor Calibration")
public class ColorSensorCalibration extends TTOpMode {
    private TTDriveSystem driveSystem;
    private League1TTArm arm;
    private SkystoneColorSensing skystoneSensor;


    @Override
    protected void onInitialize() {
        arm = new League1TTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap);
        skystoneSensor = new SkystoneColorSensing(hardwareMap);
    }

    @Override
    protected void onStart() {
        driveSystem.vertical(24, 0.5);
        while(driveSystem.isBusy()) {
            telemetry.addData("Luminosity status", skystoneSensor.getSkystoneDetector().alpha());
            telemetry.addData("rgb values", skystoneSensor.getSkystoneDetector().argb());
            telemetry.update();
        }
    }

    @Override
    protected void onStop() {
        skystoneSensor.getSkystoneDetector().enableLed(false);
    }
}
