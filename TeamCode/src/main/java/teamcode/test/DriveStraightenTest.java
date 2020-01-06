package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;

@TeleOp(name = "Drive Straighten Test")
public class DriveStraightenTest extends AbstractOpMode {

    private static final double SPEED = 0.8;

    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        driveSystem = new DriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            Vector2D direction;
            if (gamepad1.dpad_up) {
                direction = Vector2D.up();
            } else if (gamepad1.dpad_down) {
                direction = Vector2D.up().multiply(-1);
            } else if (gamepad1.dpad_left) {
                direction = Vector2D.right().multiply(-1);
            } else if (gamepad1.dpad_right) {
                direction = Vector2D.right();
            } else {
                direction = Vector2D.zero();
            }
            Vector2D velocity = direction.multiply(SPEED);
            driveSystem.continuous(velocity, 0);
        }
    }

    @Override
    protected void onStop() {

    }
}
