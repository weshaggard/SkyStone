package teamcode.test;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.league3.Constants;

@Autonomous(name="ServoTest")
public class ServoOptimization extends AbstractOpMode {


    Servo pulley;
    Servo backGrabber;
    Servo frontGrabber;
    @Override
    protected void onInitialize() {
        pulley = hardwareMap.servo.get(Constants.PULLEY_SERVO);
        backGrabber = hardwareMap.servo.get(Constants.BACK_GRABBER);
        frontGrabber = hardwareMap.servo.get(Constants.FRONT_GRABBER);

    }

    @Override
    protected void onStart() {
//        backGrabber.setPosition(0.5); //Back CLOSED POS
//        try {
//
//            Thread.sleep(2000);
//            //Thread.sleep(250);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        pulley.setPosition(0.077 * 2);
//        frontGrabber.setPosition(1); //front CLOSED POS
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        pulley.setPosition(1);
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        frontGrabber.setPosition(0.64);
//        pulley.setPosition(1 - (0.077 * 2));
//        backGrabber.setPosition(0.9);
//        try {
//            Thread.sleep(1500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        pulley.setPosition(0);
//
          frontGrabber.setPosition(0.84);


        //frontGrabber.setPosition(1); //front CLOSED POS
        //frontGrabber.setPosition(0.68); //Front OPEN POS
        //backGrabber.setPosition(0.9); //Back OPEN POS
        //backGrabber.setPosition(0.5); //Back CLOSED POS
        Debug.log(pulley.getPosition());
        Debug.log(pulley.getPortNumber());
        while(opModeIsActive());
    }

    @Override
    protected void onStop() {

    }
}
