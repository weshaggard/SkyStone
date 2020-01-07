package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@TeleOp(name = "GPS Rotation Callibration")
public class GPSRotationCallibration extends AbstractOpMode {

    private GPS gps;
    private DriveSystem drive;

    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, Vector2D.zero(), 0);
        drive = new DriveSystem(hardwareMap, gps, Vector2D.zero(), 0);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            telemetry.addData("rotation", gps.getRotation());
            telemetry.update();
            double turn = gamepad1.right_stick_x * -0.5;
            drive.continuous(Vector2D.zero(), turn);
        }
    }

    @Override
    protected void onStop() {

    }

}
