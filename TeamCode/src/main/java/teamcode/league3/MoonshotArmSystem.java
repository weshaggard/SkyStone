package teamcode.league3;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class MoonshotArmSystem {

    private DcMotor intakeLeft, intakeRight;
    private DcMotor leftWinch, rightWinch;

    private Servo boxTransfer;
    private Servo foundationGrabberLeft, foundationGrabberRight;
    private ColorSensor intakeSensor;

    //Run Pulley's at 40%!!!

    public MoonshotArmSystem(HardwareMap hardwareMap){
        intakeLeft = hardwareMap.get(DcMotor.class, Constants.LEFT_INTAKE_WHEEL);
        intakeRight = hardwareMap.get(DcMotor.class, Constants.RIGHT_INTAKE_WHEEL);
        leftWinch = hardwareMap.get(DcMotor.class, Constants.LEFT_WINCH);
        rightWinch = hardwareMap.get(DcMotor.class, Constants.RIGHT_WINCH);
        boxTransfer = hardwareMap.get(Servo.class, Constants.BOX_TRANSFER);
        foundationGrabberLeft = hardwareMap.get(Servo.class, Constants.LEFT_FOUNDATION_GRABBER);
        foundationGrabberRight = hardwareMap.get(Servo.class, Constants.RIGHT_FOUNDATION_GRABBER);
        intakeSensor = hardwareMap.get(ColorSensor.class, Constants.INNTAKE_COLOR_SENSOR);


    }


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
