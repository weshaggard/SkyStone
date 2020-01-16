package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.state.Constants;


@Autonomous(name = "ServoOptimization")
public class ServoOptimization extends AbstractOpMode {

    private Servo servo;

    @Override
    protected void onInitialize() {
        servo = hardwareMap.servo.get(Constants.CAPSTONE_SERVO);
    }

    @Override
    protected void onStart() {
        Debug.log(1.0);
        servo.setPosition(1); // left closed
        Utils.sleep(1000);
        Debug.log(0.0);
        servo.setPosition(0.0);

        while (opModeIsActive()) ;
    }

    @Override
    protected void onStop() {

    }
}
