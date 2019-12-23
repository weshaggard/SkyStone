package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@TeleOp(name = "GPS Callibration")
public class GPSCallibration extends AbstractOpMode {

    private GPS gps;
    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = Vector2D.zero();
        double startBearing = Math.PI / 2;
        gps = new GPS(hardwareMap, startPosition, startBearing);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startBearing);
    }

    @Override
    protected void onStart() {
        double speed = 0.25;
        Vector2D target1 = new Vector2D(24, 0);
        Vector2D target2 = new Vector2D(24, 24);
        Vector2D target3 = new Vector2D(0, 24);
        Vector2D target4 = new Vector2D(0, 0);
        driveSystem.goTo(target1, speed);
        driveSystem.goTo(target2, speed);
        driveSystem.goTo(target3, speed);
        driveSystem.goTo(target4, speed);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
