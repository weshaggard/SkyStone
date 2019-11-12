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
    private TapeColorSensing tapeSensor;


    @Override
    protected void onInitialize() {
        arm = new League1TTArm(hardwareMap);
        driveSystem = new TTDriveSystem(hardwareMap);
        tapeSensor = new TapeColorSensing(hardwareMap);

    }

    @Override
    protected void onStart() {
        /*telemetry.addData("are the 2 sensors equal?", arm.getLiftSensor().getI2cAddress() == tapeSensor.getTapeDetector().getI2cAddress());
        while(tapeSensor.tapeColor() != TapeColorSensing.LiftColor.BLUE){
            driveSystem.vertical(1, 0.5);
            telemetry.addData("status", tapeSensor.getTapeDetector().blue());
            telemetry.update();
        }
        telemetry.addData("status", "found the tape");
        telemetry.addData("tape value", tapeSensor.getTapeDetector().blue());
        telemetry.addData("tape value armSensor", arm.getLiftSensor().blue());
        telemetry.update();
        try {
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }*/

    }

    @Override
    protected void onStop() {

    }
}
