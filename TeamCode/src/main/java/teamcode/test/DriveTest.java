package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@Autonomous(name = "Drive Test")
public class DriveTest extends AbstractOpMode {

    GPS gps;
    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, Vector2D.zero(), Math.PI);
        driveSystem = new DriveSystem(hardwareMap, gps);
    }

    @Override
    protected void onStart() {
        Vector2D target = new Vector2D(15000, 50000);
        driveSystem.goTo(target, 0.2);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }
}
