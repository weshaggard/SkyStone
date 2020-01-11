package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.league3.Constants;

@Disabled
@Autonomous(name="ServoOptimizationc ")
public class ServoOptimization extends AbstractOpMode {

    private Servo servo;

    @Override
    protected void onInitialize() {
        servo = hardwareMap.servo.get(Constants.FRONT_GRABBER);
    }

    @Override
    protected void onStart() {
        servo.setPosition(1);
        while(opModeIsActive());
    }

    @Override
    protected void onStop() {

    }
}
