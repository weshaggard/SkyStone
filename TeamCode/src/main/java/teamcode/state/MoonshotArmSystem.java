package teamcode.state;

import android.media.MediaPlayer;
import android.text.method.Touch;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;

public class MoonshotArmSystem {

    private static final double INTAKE_POWER = 0.85;
    private static final double BOX_FLAT_POSITION = 0.5;
    private static final double BOX_RAMPED_POSITION = 0.37;
    private static final double BACK_GRABBER_OPEN_POSITION = 0.9;
    private static final double BACK_GRABBER_CLOSED_POSITION = 0.5;
    private static final double FRONT_GRABBER_OPEN_POSITION = 0.64;
    private static final double FRONT_GRABBER_INTAKE_POSITION = 0.84;
    private static final double FRONT_GRABBER_CLOSED_POSITION = 1;
    private static final double FOUNDATION_GRABBER_RIGHT_OPEN_POSITION = 1;
    private static final double FOUNDATION_GRABBER_LEFT_OPEN_POSITION = 0;
    private static final double FOUNDATION_GRABBER_RIGHT_CLOSED_POSITION = 0;
    private static final double FOUNDATION_GRABBER_LEFT_CLOSED_POSITION = 1;

    private static final double PULLEY_RETRACTED_POSITION = 0;
    private static final double PULLEY_EXTENDED_POSITION = 0.32;
    private static final double PULLEY_PRIMED_POSITION = 0.0924;

    private static final double WINCH_MOTOR_INCHES_TO_TICKS = 1000;
    private static final int WINCH_TOLERANCE_TICKS = 500;

    private DcMotor intakeLeft, intakeRight;
    private DcMotor frontWinch, backWinch;
    private DcMotor liftEncoder;
    private Servo pulley;
    private Servo boxTransfer;
    private Servo foundationGrabberLeft, foundationGrabberRight;
    private Servo backGrabber, frontGrabber;
    private Servo capstoneServo;
    private ColorSensor intakeSensor;
    private TouchSensor liftSensor;


    private boolean intaking;

    public MoonshotArmSystem(HardwareMap hardwareMap) {
        intakeLeft = hardwareMap.get(DcMotor.class, Constants.LEFT_INTAKE);
        intakeRight = hardwareMap.get(DcMotor.class, Constants.RIGHT_INTAKE);
        frontWinch = hardwareMap.get(DcMotor.class, Constants.LEFT_WINCH);
        backWinch = hardwareMap.get(DcMotor.class, Constants.RIGHT_WINCH);
        liftEncoder = hardwareMap.get(DcMotor.class, Constants.LIFT_ENCODER_NAME);
        boxTransfer = hardwareMap.get(Servo.class, Constants.BOX_TRANSFER);
        foundationGrabberLeft = hardwareMap.get(Servo.class, Constants.LEFT_FOUNDATION_GRABBER);
        foundationGrabberRight = hardwareMap.get(Servo.class, Constants.RIGHT_FOUNDATION_GRABBER);
        backGrabber = hardwareMap.get(Servo.class, Constants.BACK_GRABBER);
        frontGrabber = hardwareMap.get(Servo.class, Constants.FRONT_GRABBER);
        pulley = hardwareMap.get(Servo.class, Constants.PULLEY_SERVO);
        capstoneServo = hardwareMap.get(Servo.class, Constants.CAPSTONE_SERVO);
        intakeSensor = hardwareMap.get(ColorSensor.class, Constants.INTAKE_COLOR_SENSOR);
        //liftSensor = hardwareMap.get(TouchSensor.class, Constants.LIFT_TOUCH_SENSOR);
        foundationGrabberState = FoundationGrabberState.OPEN;
        correctMotors();
        resetServos();
    }

    private void resetServos() {
        foundationGrabberLeft.setPosition(FOUNDATION_GRABBER_LEFT_OPEN_POSITION);
        foundationGrabberRight.setPosition(FOUNDATION_GRABBER_RIGHT_OPEN_POSITION);
        boxTransfer.setPosition(BOX_FLAT_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);
        pulley.setPosition(PULLEY_RETRACTED_POSITION);
        capstoneServo.setPosition(0.98);
    }

    private void correctMotors() {
        backWinch.setDirection(DcMotorSimple.Direction.REVERSE);

        liftEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    /**
     * puts the block into a scoring position, NOTE that this does NOT raise to a scored position
     *
     * @Param powerLeft, the power of the left suck motor
     * @Param powerRight, the power of the right suck motor
     */
    public void intakeSequence() {
        suck(INTAKE_POWER);
        boxTransfer.setPosition(BOX_RAMPED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
        intaking = true;
        Debug.log("ENTERING suck loop");
        while (!intakeFull() && AbstractOpMode.currentOpMode().opModeIsActive()) {
            if (!intaking) {
                suck(0);
                // this means cancelIntakeSequence() has been called.
                return;
            }
            suck(INTAKE_POWER);
            Debug.log("intaking");
        }
        primeToScore();
    }

    public void primeToScore() {
        suck(0);
        boxTransfer.setPosition(BOX_FLAT_POSITION);
        Debug.log("transfer case down");
        backGrabber.setPosition(BACK_GRABBER_CLOSED_POSITION);
        Utils.sleep(700);
        pulley.setPosition(PULLEY_PRIMED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);
    }

    public void extend() {
        pulley.setPosition(PULLEY_EXTENDED_POSITION);
        Debug.log("here");
    }

    public void retract() {
        pulley.setPosition(PULLEY_RETRACTED_POSITION);
    }

    /**
     * Stop the robot from intaking while in intake sequence.
     */
    public void cancelIntakeSequence() {
        Debug.log("cancelling intake");
        boxTransfer.setPosition(BOX_FLAT_POSITION);
        intaking = false;
    }

    public void setFrontGrabberPosition(boolean open) {
        if (open) {
            frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
        } else {
            frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);
        }
    }


    public void attemptToAdjust() throws InterruptedException {
        pulley.setPosition(0.1);
        frontGrabber.setPosition(0.6);
        pulley.setPosition(0);
        Thread.currentThread().sleep(100);
        frontGrabber.setPosition(1);
        pulley.setPosition(0.077 * 1.5);

    }

    public void dumpStone() {
        pulley.setPosition(PULLEY_EXTENDED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
    }

    public void score() {
        frontGrabber.setPosition(0.64);
        pulley.setPosition(0.27);
        backGrabber.setPosition(0.9);
        Utils.sleep(250);
        pulley.setPosition(0);
    }

    public void lift(double power, boolean brake) {
        if (power == 0 && brake) {
            // brake power
            power = 0.1;
        }
        if (power < 0 && brake) {
            power /= 4.0;
        }

        frontWinch.setPower(-power);
        backWinch.setPower(-power);
    }

    public void encoderLift(double inches, double power){
        Debug.clear();
        int ticks = (int)(inches * WINCH_MOTOR_INCHES_TO_TICKS);
        Debug.log(ticks);
        liftEncoder.setTargetPosition(-ticks);
        liftEncoder.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontWinch.setPower(-power);
        backWinch.setPower(-power);
        Debug.log("entering winch loop");
        while(!winchNearTarget());{
            Debug.log("current: "+ liftEncoder.getCurrentPosition());
            Debug.log("target: "+ liftEncoder.getTargetPosition());
        }
        Debug.clear();
        //double brakePower = calculateBrakePower(inches + 9);
        frontWinch.setPower(-0.125);
        backWinch.setPower(-0.125);

    }

    private double calculateBrakePower(double currentInches) {
        //this is derived from Newtons universal law of gravity, Gm1m2 / r^2
        double numerator = 19.95468777;
        double denominator = Math.pow(currentInches * 0.0254, 2);
        double force =  numerator / denominator;
        return force;
    }



    private boolean winchNearTarget(){
        return (Math.abs(liftEncoder.getCurrentPosition() - liftEncoder.getTargetPosition()) < WINCH_TOLERANCE_TICKS);
    }

    public void suck(double power) {
        intakeLeft.setPower(power);
        intakeRight.setPower(-power);
    }


    private boolean intakeFull() {
        int green = intakeSensor.green();
        return green > 900;
    }


    public void capstoneScoring() {
        pulley.setPosition(0.077 * 2);
        capstoneServo.setPosition(0.98);
    }



    public void outtakeServoPos() {

    }

    public void initCapstoneServo() {
        capstoneServo.setPosition(0.5);
    }

    public DcMotor getLiftEncoder() {
        return liftEncoder;
    }

    private enum FoundationGrabberState {
        CLOSED, OPEN
    }

    private FoundationGrabberState foundationGrabberState;

    public void adjustFoundation() {
        if (foundationGrabberState == FoundationGrabberState.OPEN) {
            foundationGrabberLeft.setPosition(FOUNDATION_GRABBER_LEFT_CLOSED_POSITION);
            foundationGrabberRight.setPosition(FOUNDATION_GRABBER_RIGHT_CLOSED_POSITION);
            foundationGrabberState = FoundationGrabberState.CLOSED;
        } else {
            foundationGrabberLeft.setPosition(FOUNDATION_GRABBER_LEFT_OPEN_POSITION);
            foundationGrabberRight.setPosition(FOUNDATION_GRABBER_RIGHT_OPEN_POSITION);
            foundationGrabberState = FoundationGrabberState.OPEN;
        }
    }


    //2 suck motors
    //1 transfer servo (0, 0.5)
    //2 servos linked (1 servo)(0, 1)
    //2 grabber servos
    //2 winch motors (opposite directions)
    //2 foundation servos
    //Possibly Capstone Servo
    //Color Sensor
    //Touch Sensor?(Lift)
    //through bore encoder on the Winch


    // making it work with auto by copy pasting in a super janky way cuz we outta time

    public void intakeSequenceAUTO() {
        suck(INTAKE_POWER);
        boxTransfer.setPosition(BOX_RAMPED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
        intaking = true;
        while (!intakeFull() && intaking && AbstractOpMode.currentOpMode().opModeIsActive()) ;
        suck(0);
        boxTransfer.setPosition(BOX_FLAT_POSITION);
    }

    public void primeToScoreAUTO() {
        backGrabber.setPosition(BACK_GRABBER_CLOSED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);
        Utils.sleep(500);
        pulley.setPosition(1);
    }

    public void scoreAUTO() {
        frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
        Utils.sleep(500);
        pulley.setPosition(0);
    }

}
