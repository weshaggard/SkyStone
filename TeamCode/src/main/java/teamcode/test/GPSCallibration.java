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
        double startBearing = 90;
        gps = new GPS(hardwareMap, startPosition, startBearing);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startBearing);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            Debug.clear();
            Debug.log("GPS location: " + gps.getPosition());
            Debug.log("GPS rotation: " + gps.getRotation());
            double x = gamepad1.right_stick_x;
            double y = -gamepad1.right_stick_y;
            Vector2D velocity = new Vector2D(x, y);
            double turn = gamepad1.left_stick_x;
            velocity = velocity.multiply(0.4);
            turn *= 0.4;
            driveSystem.continuous(velocity, turn);
        }
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
