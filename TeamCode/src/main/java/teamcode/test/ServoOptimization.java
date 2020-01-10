package teamcode.test;

import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.league3.Constants;

public class ServoOptimization extends AbstractOpMode {

    private Servo frontGrabber;

    @Override
    protected void onInitialize() {
        frontGrabber = hardwareMap.servo.get(Constants.FRONT_GRABBER);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }
}
