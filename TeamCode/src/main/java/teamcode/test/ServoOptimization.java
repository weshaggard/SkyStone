package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.state.Constants;


@TeleOp(name = "ServoOptimization")
public class ServoOptimization extends AbstractOpMode {

    private Servo servo;

    @Override
    protected void onInitialize() {
        servo = hardwareMap.servo.get(Constants.FRONT_GRABBER);
    }

    @Override
    protected void onStart() {

        //servo.setPosition(0.63); // front open
        servo.setPosition(0.5);
        Utils.sleep(1000);
        //servo.setPosition(0.0);

        while (opModeIsActive()) ;
    }

    @Override
    protected void onStop() {

    }
}
