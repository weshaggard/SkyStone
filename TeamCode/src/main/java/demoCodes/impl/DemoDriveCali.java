package demoCodes.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import demoCodes.common.DemoDrive;
import demoCodes.common.TTOpMode;

@Autonomous(name = "Demo Drive Calibration")
public class DemoDriveCali extends TTOpMode {

    private DemoDrive driveSystem;

    @Override
    public void onInitialize(){
        driveSystem = new DemoDrive(hardwareMap);

    }

    @Override
    public void onStart(){
        driveSystem.turn(360 * 5, 0.25);
    }

    @Override
    public void onStop(){

    }
}
