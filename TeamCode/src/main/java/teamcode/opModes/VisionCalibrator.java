package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

import teamcode.common.AbstractOpMode;
import teamcode.common.BoundingBox2D;
import teamcode.robotComponents.TTVision;
import teamcode.robotComponents.TTVisionVuforia;

@Autonomous(name = "Vision Calibrator")
public class VisionCalibrator extends AbstractOpMode {

    private TTVisionVuforia vision;

    @Override
    protected void onInitialize() {
        vision = new TTVisionVuforia(hardwareMap);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            vision.getSkystoneLocation();
        }
    }

    @Override
    protected void onStop() {
    }

}
