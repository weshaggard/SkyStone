package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@TeleOp(name = "Odometry Drive Callibration")
public class OdometryDriveCallibration extends AbstractOpMode {

    private static final double SPEED = 0.5;

    private GPS gps;
    private DriveSystem drive;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(0, 0);
        double startRotation = 0;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            telemetry.addData("position", gps.getPosition());
            telemetry.addData("rotation", gps.getRotation());
            telemetry.update();
            Vector2D velocity = new Vector2D(gamepad1.right_stick_x, -gamepad1.right_stick_y).multiply(SPEED);
            double turn = -gamepad1.left_stick_x * SPEED;
            drive.continuous(velocity, turn);
        }
    }

    @Override
    protected void onStop() {

    }
}
