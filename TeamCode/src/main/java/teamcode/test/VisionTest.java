package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.league3.Vision;

@Autonomous(name = "Vision Test")
public class VisionTest extends AbstractOpMode {

    private Vision vision;

    @Override
    public void onInitialize() {
        vision = new Vision(hardwareMap, Vision.VisionSource.WEBCAM, Vision.CameraOrientation.ZERO);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            Vector2D skyStonePosition = vision.getSkystonePosition();
            if (skyStonePosition != null) {
                Debug.log("SkyStone detected at: " + skyStonePosition);
            }
        }
    }

    @Override
    public void onStop() {
    }

}
