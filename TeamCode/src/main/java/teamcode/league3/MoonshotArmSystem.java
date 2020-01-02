package teamcode.league3;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class MoonshotArmSystem {


    private DcMotor intakeLeft, intakeRight;
    private DcMotor frontWinch, backWinch;
    private Servo pulley;
    private Servo boxTransfer;
    private Servo foundationGrabberLeft, foundationGrabberRight;
    private Servo backGrabber, frontGrabber;
    private ColorSensor intakeSensor;

    private static final double BOX_FLAT_POSITION = 0.6;
    private static final double BOX_RAMPED_POSITION = 0;
    private static final double BACK_GRABBER_OPEN_POSITION = 0.5;
    private static final double BACK_GRABBER_CLOSED_POSITION = 1.0;
    private static final double FRONT_GRABBER_OPEN_POSITION = 0.5;
    private static final double FRONT_GRABBER_CLOSED_POSITION = 1.0;
    private static final double FOUNDATION_GRABBER_RIGHT_OPEN_POSITION = 0;
    private static final double FOUNDATION_GRABBER_LEFT_OPEN_POSITION = 0;
    private static final double PULLEY_RETRACTED_POSITION = 0;
    private static final double PULLEY_EXTENDED_POSITION = 1.0;

    private static final double WINCH_INCHES_TO_TICKS = 150;
    private static final int WINCH_TOLERANCE_TICKS = 20;



    //Run Pulley's at 40%!!!

    public MoonshotArmSystem(HardwareMap hardwareMap) {
        intakeLeft = hardwareMap.get(DcMotor.class, Constants.LEFT_INTAKE_WHEEL);
        intakeRight = hardwareMap.get(DcMotor.class, Constants.RIGHT_INTAKE_WHEEL);
        frontWinch = hardwareMap.get(DcMotor.class, Constants.LEFT_WINCH);
        backWinch = hardwareMap.get(DcMotor.class, Constants.RIGHT_WINCH);
        boxTransfer = hardwareMap.get(Servo.class, Constants.BOX_TRANSFER);
        foundationGrabberLeft = hardwareMap.get(Servo.class, Constants.LEFT_FOUNDATION_GRABBER);
        foundationGrabberRight = hardwareMap.get(Servo.class, Constants.RIGHT_FOUNDATION_GRABBER);
        backGrabber = hardwareMap.get(Servo.class, Constants.BACK_GRABBER);
        frontGrabber = hardwareMap.get(Servo.class, Constants.FRONT_GRABBER);
        pulley = hardwareMap.get(Servo.class, Constants.PULLEY_SERVO);
        intakeSensor = hardwareMap.get(ColorSensor.class, Constants.INTAKE_COLOR_SENSOR);
        correctMotors();
        resetServos();

    }


    private void resetServos() {
        foundationGrabberLeft.setPosition(FOUNDATION_GRABBER_LEFT_OPEN_POSITION);
        foundationGrabberRight.setPosition(FOUNDATION_GRABBER_RIGHT_OPEN_POSITION);
        pulley.setPosition(PULLEY_RETRACTED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
        boxTransfer.setPosition(BOX_RAMPED_POSITION);



    }

    private void correctMotors() {
        frontWinch.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }


    /**
     * puts the block into a scoring position, NOTE that this does NOT raise to a scored position
     * @Param powerLeft, the power of the left intake motor
     * @Param powerRight, the power of the right intake motor
     */
    public void intake(double powerLeft, double powerRight){
        while(!intakeFull()){
            suck(powerLeft, powerRight);
        }
        boxTransfer.setPosition(BOX_FLAT_POSITION);
        backGrabber.setPosition(BACK_GRABBER_CLOSED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);


    }

    public void score(int presetHeight, double power){
        pulley.setPosition(PULLEY_EXTENDED_POSITION);
        lift(presetHeight * 4, power);
        frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
    }

    private void lift(int inches, double power) {
        int ticks = (int)(inches * WINCH_INCHES_TO_TICKS);
        backWinch.setTargetPosition(ticks);
        backWinch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontWinch.setPower(power);
        backWinch.setPower(power);
        while(!nearHeight());
        frontWinch.setPower(0);
        backWinch.setPower(0);
    }

    private boolean nearHeight() {
        return Math.abs(frontWinch.getTargetPosition() - frontWinch.getCurrentPosition()) < WINCH_TOLERANCE_TICKS &&
                Math.abs(backWinch.getTargetPosition() - backWinch.getCurrentPosition()) < WINCH_TOLERANCE_TICKS;
    }

    private void suck(double powerLeft, double powerRight) {
        intakeLeft.setPower(powerLeft);
        intakeRight.setPower(powerRight);

    }

    private boolean intakeFull() {
        int red = intakeSensor.red();
        int green = intakeSensor.green();
        int blue = intakeSensor.blue();
        return red > 400;
    }

    public void goToHome(double power) {
        lift(0, power);
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
