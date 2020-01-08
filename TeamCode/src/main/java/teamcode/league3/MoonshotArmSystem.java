package teamcode.league3;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.Debug;

public class MoonshotArmSystem {


    private DcMotor intakeLeft, intakeRight;
    private DcMotor frontWinch, backWinch;
    private Servo pulley;
    private Servo boxTransfer;
    private Servo foundationGrabberLeft, foundationGrabberRight;
    private Servo backGrabber, frontGrabber;
    private ColorSensor intakeSensor;

    private static final double BOX_FLAT_POSITION = 0.5;
    private static final double BOX_RAMPED_POSITION = 0.37;
    private static final double BACK_GRABBER_OPEN_POSITION = 0.9;
    private static final double BACK_GRABBER_CLOSED_POSITION = 0.5;
    private static final double FRONT_GRABBER_OPEN_POSITION = 0.68;
    private static final double FRONT_GRABBER_INTAKE_POSITION = 0.84;
    private static final double FRONT_GRABBER_CLOSED_POSITION = 1;
    private static final double FOUNDATION_GRABBER_RIGHT_OPEN_POSITION = 0;
    private static final double FOUNDATION_GRABBER_LEFT_OPEN_POSITION = 0;
    private static final double PULLEY_RETRACTED_POSITION = 0;
    private static final double PULLEY_EXTENDED_POSITION = 1.0;

    private static final double WINCH_INCHES_TO_TICKS = 150;
    private static final int WINCH_TOLERANCE_TICKS = 20;
    private static final double WINCH_LOWER_BOUND = 0;
    private static final double SKYSTONE_HEIGHT_INCHES = 4.25;
    private static final double WINCH_UPPER_BOUND = 8 * SKYSTONE_HEIGHT_INCHES;
    private double currentHeightInches;



    //Run Pulley's at 40%!!!

    public MoonshotArmSystem(HardwareMap hardwareMap) {
        intakeLeft = hardwareMap.get(DcMotor.class, Constants.LEFT_INTAKE);
        intakeRight = hardwareMap.get(DcMotor.class, Constants.RIGHT_INTAKE);
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
        boxTransfer.setPosition(BOX_FLAT_POSITION);
        backGrabber.setPosition(BACK_GRABBER_CLOSED_POSITION);
        //backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION); //what it should be
        frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);
        pulley.setPosition(PULLEY_RETRACTED_POSITION);

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
    public void intake(double power){
        Debug.log("START intake");
        boxTransfer.setPosition(BOX_RAMPED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
        Debug.log("ENTERING intake loop");
        while(!intakeFull()){
            suck(power);
            Debug.log("intaking");
        }
        suck(0);
        boxTransfer.setPosition(BOX_FLAT_POSITION);
        Debug.log("transfer case down");
        backGrabber.setPosition(0.5); //Back CLOSED POS
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pulley.setPosition(0.077 * 4);
        frontGrabber.setPosition(1); //front CLOSED POS
    }

    public void attemptToAdjust(){
        pulley.setPosition(0.077 * 2 + pulley.getPosition());
        frontGrabber.setPosition(1);
    }

    public void dumpStone(){
        pulley.setPosition(1);
        //frontGrabber.setPosition();
    }

    public void primeToScore(double presetHeight, double power)  {
        pulley.setPosition(1);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lift((presetHeight * SKYSTONE_HEIGHT_INCHES), power);


    }

    public void score(double power){
        frontGrabber.setPosition(0.64);
        pulley.setPosition(1 - (0.077 * 2));
        backGrabber.setPosition(0.9);
        goToHome(power);
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pulley.setPosition(0);
    }

    public void lift(double inches, double power) {
        int ticks = (int)(inches * WINCH_INCHES_TO_TICKS);
        currentHeightInches += inches;
        if(currentHeightInches < WINCH_LOWER_BOUND){
            currentHeightInches -= inches;
            Debug.log("Attempted to go a negative position");
            return;
        }
        if(currentHeightInches > WINCH_UPPER_BOUND){
            Debug.log("Attempted to stack higher than allowed");
            return;
        }
        backWinch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
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

    public void suck(double power) {
        Debug.log("suck Power: " + power);
        intakeLeft.setPower(-power);
        intakeRight.setPower(-power);
    }

    private boolean intakeFull() {
        int red = intakeSensor.red();
        int green = intakeSensor.green();
        int blue = intakeSensor.blue();
        //Debug.log(green);
        return green > 700;
    }

    public void goToHome(double power) {
        lift(-currentHeightInches, power);
    }

    public void clampFoundation() {
        foundationGrabberLeft.setPosition(1);
        foundationGrabberRight.setPosition(0);
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
