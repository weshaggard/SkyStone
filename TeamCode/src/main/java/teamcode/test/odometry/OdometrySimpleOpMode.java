package teamcode.test.odometry;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Point;
import teamcode.league3.DriveSystem;


@Autonomous(name= "Odometry test simple")
public class OdometrySimpleOpMode extends AbstractOpMode {

    DriveSystem driveSystem;
    OdometryWheelsFinal wheels;
    Thread odometerUpdate;
    @Override
    protected void onInitialize() {
        wheels = new OdometryWheelsFinal(this, new Point(100, 100), 0);
        driveSystem = new DriveSystem(this.hardwareMap, wheels);
    }

    @Override
    protected void onStart() {
        odometerUpdate.start();
        driveSystem.omniMovement(40, 0, 0.3);
    }

    @Override
    protected void onStop() {
        try {
            Thread.sleep(500);
            odometerUpdate.sleep(500);
        }catch( InterruptedException e){
            e.printStackTrace();
        }
    }
}
