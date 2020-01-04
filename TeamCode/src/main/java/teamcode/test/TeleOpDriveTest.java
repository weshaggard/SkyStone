package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@TeleOp(name = "Tele Op Drive Test")
public class TeleOpDriveTest extends AbstractOpMode {

    private GPS gps;
    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = Vector2D.zero();
        double startRotation = Math.PI / 2;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            double x = gamepad1.right_stick_x;
            double y = -gamepad1.right_stick_y;
            double turn = gamepad1.left_stick_x;
            Vector2D velocity = new Vector2D(x, y).multiply(0.4);
            Debug.log(velocity);
            turn *= -0.4;
            Debug.log(turn);
            driveSystem.continuous(velocity, turn);
        }
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
