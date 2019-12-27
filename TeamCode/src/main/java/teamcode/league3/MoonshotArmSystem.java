package teamcode.league3;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class MoonshotArmSystem {

    private DcMotor intakeLeft, intakeRight;
    private DcMotor leftWinch, rightWinch;

    private Servo boxTransfer;
    private Servo foundationGrabberLeft, foundationGrabberRight;
    private ColorSensor intakeSensor;



    //2 intake motors
    //1 transfer servo (0, 0.5)
    //2 servos linked (1 servo)(0, 1)
    //2 grabber servos
    //2 winch motors (opposite directions)
    //2 foundation servos
    //Possibly Capstone Servo
    //Color Sensor
    //Touch Sensor?(Lift)
    //through bore encoder on the Winch


}
