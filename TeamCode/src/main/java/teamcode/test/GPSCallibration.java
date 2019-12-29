package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@TeleOp(name = "GPS Calibration")
public class GPSCallibration extends AbstractOpMode {

    private GPS gps;
    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = Vector2D.zero();
        double startBearing = 0;
        gps = new GPS(hardwareMap, startPosition, startBearing);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startBearing);
    }

    @Override
    protected void onStart() {
        double speed = 0.25;
        Vector2D target1 = new Vector2D(24 * 4, 0);
        Vector2D[] targets = {target1};
        for (Vector2D target : targets) {
            driveSystem.goTo(target, speed);
        }
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
