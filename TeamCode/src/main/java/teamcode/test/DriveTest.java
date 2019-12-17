package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;
import teamcode.test.OdometryWheelsFinal;

@Autonomous(name = "Drive Test")
public class DriveTest extends AbstractOpMode {

    GPS gps;
    private DriveSystem driveSystem;
    private OdometryWheelsFinal wheels;

    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, Vector2D.zero(), 0);
        driveSystem = new DriveSystem(hardwareMap, gps);
        wheels = new OdometryWheelsFinal(this, new Point(100, 100), driveSystem, 0);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            Debug.log(gps.getPosition());
        }
        //Debug.log("going forward 12 inches at 0.1 power");
        driveSystem.vertical(12, 0.1, wheels);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }
}
