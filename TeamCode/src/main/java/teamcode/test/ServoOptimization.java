package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.league3.Constants;

@Autonomous(name="ServoOptimizationc ")
public class ServoOptimization extends AbstractOpMode {

    private Servo frontGrabber;

    @Override
    protected void onInitialize() {
        frontGrabber = hardwareMap.servo.get(Constants.BACK_GRABBER);
    }

    @Override
    protected void onStart() {
        frontGrabber.setPosition(1);
        while(!opModeIsActive());
    }

    @Override
    protected void onStop() {

    }
}
