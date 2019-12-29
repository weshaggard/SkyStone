package teamcode.test.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.league3.Vision;

@TeleOp(name = "Vision Test")
public class VisionTest extends AbstractOpMode {

    private Vision vision;

    @Override
    public void onInitialize() {
        Debug.log("Press A to switch between phone and webcam.");
        vision = new Vision(hardwareMap, Vision.VisionSource.WEBCAM);
    }

    @Override
    protected void onStart() {
        boolean aDown = false;
        while (opModeIsActive()) {
            if (gamepad1.a && !aDown) {
                aDown = true;
                switchVisionSource();
            } else if (!gamepad1.a && aDown) {
                aDown = false;
            }
        }
    }

    private void switchVisionSource() {
        Vision.VisionSource currentSource = vision.getVisionSource();
        Vision.VisionSource newSource;
        if (currentSource == Vision.VisionSource.PHONE) {
            newSource = Vision.VisionSource.WEBCAM;
        } else {
            newSource = Vision.VisionSource.PHONE;
        }
        vision.setVisionSource(newSource);
        Debug.log("Switching source to " + newSource);
    }

    @Override
    public void onStop() {
    }

}
