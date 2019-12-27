package teamcode.league3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;

@Autonomous(name="Calibration")
public class HardpathCalibration extends AbstractOpMode {
    private GPS gps;
    private DriveSystem driveSystem;
    @Override




    protected void onInitialize() {
        gps = new GPS(hardwareMap, Vector2D.zero(), 0);
        driveSystem = new DriveSystem(hardwareMap, gps, Vector2D.zero(), 0);
    }

    @Override
    protected void onStart() {
        try {
            driveSystem.vertical(20, 0.25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
