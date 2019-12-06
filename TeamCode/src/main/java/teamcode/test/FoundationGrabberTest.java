package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.league2.ArmSystemLeague2;

@TeleOp(name = "Grabber Test")
public class FoundationGrabberTest extends AbstractOpMode {
    ArmSystemLeague2 arm;

    @Override
    protected void onInitialize() {
        arm = new ArmSystemLeague2(hardwareMap);
        Debug.log("Press Y to open grabbers");
        Debug.log("Press A to close grabbers");
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            if (gamepad1.y) {
                arm.toggleFoundationGrabbers(true);
            } else if (gamepad1.a) {
                arm.toggleFoundationGrabbers(false);
            }
        }
    }

    @Override
    protected void onStop() {
    }
}
