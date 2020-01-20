package teamcode.state;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.common.Vector2D;

public class MoonshotArmSystem {

    private static final double INTAKE_POWER = 1;
    private static final double BOX_FLAT_POSITION = 0.5;
    private static final double BOX_RAMPED_POSITION = 0.37;
    private static final double BACK_GRABBER_OPEN_POSITION = 0.9;
    private static final double BACK_GRABBER_CLOSED_POSITION = 0.5;
    private static final double FRONT_GRABBER_OPEN_POSITION = 0.64;
    private static final double FRONT_GRABBER_INTAKE_POSITION = 0.84;
    private static final double FRONT_GRABBER_CLOSED_POSITION = 1;
    private static final double FOUNDATION_GRABBER_RIGHT_OPEN_POSITION = 1;
    private static final double FOUNDATION_GRABBER_LEFT_OPEN_POSITION = 0;
    private static final double FOUNDATION_GRABBER_RIGHT_CLOSED_POSITION = 0.5;
    private static final double FOUNDATION_GRABBER_LEFT_CLOSED_POSITION = 0.5;


    private static final double PULLEY_RETRACTED_POSITION = 0;
    private static final double PULLEY_EXTENDED_POSITION = 0.33;
    private static final double PULLEY_PRIMED_POSITION = 0.0924;

    private static final double WINCH_MOTOR_INCHES_TO_TICKS = 1591.2;
    private static final int WINCH_TOLERANCE_TICKS = 500;
    private static final double MAX_WINCH_HEIGHT_INCHES = 32;
    private static final double MAX_WINCH_POWER = 1;
    private static final double WINCH_BRAKE_POWER = 0.15;
    private static final double WINCH_BRAKE_POWER_DROOPING = 0.10; // chrisitian likes this better but i wwant to keep the original b/c it is a functional braking power
    private static final double WINCH_BRAKE_POWER_HIGH = 0.12;
    private static final double WINCH_ACCELERATION_POWER_REDUCTION_THRESHOLD_TICKS = 4 * WINCH_MOTOR_INCHES_TO_TICKS;
    private static final double WINCH_DECELERATION_POWER_REDUCTION_THRESHOLD_TICKS = 8 * WINCH_MOTOR_INCHES_TO_TICKS;
    private static final double MIN_REDUCED_WINCH_POWER = 0.7;
    private static final double WINCH_DESCENT_POWER_MULTIPILIER = 0.25;


    private DcMotor intakeLeft, intakeRight;
    private DcMotor frontWinch, backWinch;
    private DcMotor liftEncoder;
    private LiftLocalizer localizer;
    private Servo pulley;
    private Servo boxTransfer;
    private Servo foundationGrabberLeft, foundationGrabberRight;
    private Servo backGrabber, frontGrabber;
    //private Servo capstoneServo;
    private ColorSensor intakeSensor;

    private int targetWinchPositionTicks = 0;
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
        //capstoneServo = hardwareMap.get(Servo.class, Constants.CAPSTONE_SERVO);
        intakeSensor = hardwareMap.get(ColorSensor.class, Constants.INTAKE_COLOR_SENSOR);
        //liftSensor = hardwareMap.get(TouchSensor.class, Constants.LIFT_TOUCH_SENSOR);
        foundationGrabberState = FoundationGrabberState.OPEN;
        localizer = new LiftLocalizer();
        correctMotors();
        resetServos();
    }

     class LiftLocalizer{

        final int startingEncoderValue;
        double stoneNum;
        double currentInches;


        LiftLocalizer(){
            startingEncoderValue = liftEncoder.getCurrentPosition();
            stoneNum = 1;
            Thread localizerUpdate = new Thread(){
                @Override
                public void run(){
                    while(AbstractOpMode.currentOpMode().opModeIsActive() && !AbstractOpMode.currentOpMode().isStopRequested()){
                        update();
                    }
                }
            };
            localizerUpdate.start();

        }

        public LiftLocalizer getLocalizer(){
            return localizer;
        }

        synchronized void update(){
            int currentLiftEncoderValue = liftEncoder.getCurrentPosition();
            if(isNearStart(currentLiftEncoderValue)){
                //determines if this is the zero
                stoneNum = 1;

            }
            currentInches = (double)currentLiftEncoderValue  / WINCH_MOTOR_INCHES_TO_TICKS;
            stoneNum = (currentInches + 4.95) / 4.1;
//            Debug.log("Ticks: " + currentLiftEncoderValue);
//            Debug.log("StoneNum: " + stoneNum);
//            Debug.log("CurrentInches:" + inches);


        }

        boolean isNearStart(int currentPosition){
            return currentPosition > startingEncoderValue - WINCH_TOLERANCE_TICKS && currentPosition < startingEncoderValue + WINCH_TOLERANCE_TICKS;
        }

    }

    /*
    base height: 1.25 in
    X          y
    0          1
    3.25       2
    7.35       3

    x = 4.1(y - 2) + 3.25
    y = (x + 4.95) / 4.1

     */

    private void resetServos() {
        foundationGrabberLeft.setPosition(FOUNDATION_GRABBER_LEFT_OPEN_POSITION);
        foundationGrabberRight.setPosition(FOUNDATION_GRABBER_RIGHT_OPEN_POSITION);
        boxTransfer.setPosition(BOX_FLAT_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);
        pulley.setPosition(PULLEY_RETRACTED_POSITION);
        //capstoneServo.setPosition(0.98);
    }

    private void correctMotors() {
        frontWinch.setDirection(DcMotorSimple.Direction.REVERSE);

        liftEncoder.setDirection(DcMotorSimple.Direction.REVERSE);
        liftEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    public void snapBack(boolean roundUp){
        double roundedStoneNum;
        if(roundUp) {
            roundedStoneNum = Math.ceil(localizer.stoneNum);
        }else{
            roundedStoneNum = Math.floor(localizer.stoneNum);
        }


        double stoneNumDifference = roundedStoneNum - localizer.stoneNum ;
        if(stoneNumDifference < 0.01) {
            if (roundUp) {
//                stoneNumDifference += 1;
                roundedStoneNum += 1;
            } else {
//                stoneNumDifference -= 1;
                roundedStoneNum -= 1;
            }
        }


//        double liftHeightInches = stoneNumDifference * Constants.STONE_HEIGHT_INCHES;
        double liftHeightInches = 3.25 + Constants.STONE_HEIGHT_INCHES * (roundedStoneNum - 2);
        Debug.clear();
        Debug.log("GetLiftHeight" + getLiftHeight());
        Debug.log("CurrentInches" + localizer.currentInches);
        Debug.log("StoneNum: " + localizer.stoneNum);
        Debug.log("RoundedStoneNum" + roundedStoneNum);
        Debug.log("LiftHeightIn: " + liftHeightInches);
        setLiftHeight(liftHeightInches);
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


    public void attemptToAdjust() {
        pulley.setPosition(0.1);
        frontGrabber.setPosition(0.6);
        pulley.setPosition(0);
        Utils.sleep(100);
        frontGrabber.setPosition(1);
        pulley.setPosition(0.077 * 1.5);
    }

    public void dumpStone() {
        pulley.setPosition(PULLEY_EXTENDED_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_OPEN_POSITION);
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
    }

    public void score() {
        frontGrabber.setPosition(0.5);
        pulley.setPosition(0.27);
        backGrabber.setPosition(0.9);

    }

    public void reset(){

        while(!localizer.isNearStart(liftEncoder.getCurrentPosition())){
            liftContinuously(-0.5);
        }
        liftContinuously(0);
        Utils.sleep(250);
        pulley.setPosition(0);
        frontGrabber.setPosition(0.63);
        //frontGrabber.setPosition(0.9);

    }

    public void resetArmPosition(){
        frontGrabber.setPosition(0.63);
        pulley.setPosition(0.27);
        backGrabber.setPosition(0.9);
        Utils.sleep(250);
        pulley.setPosition(0);

    }

    public void liftContinuously(double power) {
        if (power == 0) {
            if(localizer.stoneNum > 8.5){
                Debug.log("Very High up");
                power = WINCH_BRAKE_POWER_HIGH;
            }else {
                power = WINCH_BRAKE_POWER_DROOPING;
            }
        } else if (power < 0) {
            power *= WINCH_DESCENT_POWER_MULTIPILIER;
        }
        frontWinch.setPower(power);
        backWinch.setPower(power);
    }

    /**
     * @param inches 0 is the lowest position the arm can go to
     */
    public void setLiftHeight(double inches) {
        inches = Math.max(0, inches);
        inches = Math.min(MAX_WINCH_HEIGHT_INCHES, inches);
        int newTargetTicks = (int) (inches * WINCH_MOTOR_INCHES_TO_TICKS);
        int currentWinchPosition;
        int ticksFromStart;
        int ticksToTarget;
        liftEncoder.setTargetPosition(newTargetTicks);
        while (!Utils.motorNearTarget(liftEncoder, WINCH_TOLERANCE_TICKS) &&
                AbstractOpMode.currentOpMode().opModeIsActive()) {
            currentWinchPosition = liftEncoder.getCurrentPosition();
            ticksFromStart = currentWinchPosition - targetWinchPositionTicks;
            ticksToTarget = liftEncoder.getTargetPosition() - currentWinchPosition;
            Debug.clear();
            Debug.log("from start: " + ticksFromStart);
            Debug.log("to target:" + ticksToTarget);
            double modulatedPower = getModulatedWinchPower(ticksFromStart, ticksToTarget);
            Debug.log(modulatedPower);
            frontWinch.setPower(modulatedPower);
            backWinch.setPower(modulatedPower);
        }
        targetWinchPositionTicks = newTargetTicks;
        brakeWinches();
    }



    private double getModulatedWinchPower(double ticksFromStart, double ticksToTarget) {
        double accelerationPower;
        double decelerationPower;
        if (Math.abs(ticksFromStart) < WINCH_ACCELERATION_POWER_REDUCTION_THRESHOLD_TICKS) {
            accelerationPower = Utils.lerp(MIN_REDUCED_WINCH_POWER, 1,
                    Math.abs(ticksFromStart) / WINCH_ACCELERATION_POWER_REDUCTION_THRESHOLD_TICKS);
        } else {
            accelerationPower = MAX_WINCH_POWER;
        }
        if (ticksToTarget < WINCH_DECELERATION_POWER_REDUCTION_THRESHOLD_TICKS) {
            decelerationPower = Utils.lerp(MIN_REDUCED_WINCH_POWER, 1,
                    Math.abs(ticksToTarget) / WINCH_DECELERATION_POWER_REDUCTION_THRESHOLD_TICKS);
        } else {
            decelerationPower = MAX_WINCH_POWER;
        }
        double sign = Math.signum(ticksToTarget);
        if (sign == 0.0) {
            sign = 1;
        }
        accelerationPower *= sign;
        decelerationPower *= sign;
        if (accelerationPower < 0) {
            accelerationPower *= WINCH_DESCENT_POWER_MULTIPILIER;
        }
        if (decelerationPower < 0) {
            decelerationPower *= WINCH_DESCENT_POWER_MULTIPILIER;
        }
        Debug.log("acceleration: " + accelerationPower);
        Debug.log("deceleration: " + decelerationPower);
        if (Math.abs(accelerationPower) < Math.abs(decelerationPower)) {
            return accelerationPower;
        } else {
            return decelerationPower;
        }
    }

    public double getLiftHeight() {
        return liftEncoder.getCurrentPosition() / WINCH_MOTOR_INCHES_TO_TICKS;
    }

    public void brakeWinches() {
        frontWinch.setPower(WINCH_BRAKE_POWER);
        backWinch.setPower(WINCH_BRAKE_POWER);
    }

    private double calculateBrakePower(double currentInches) {
        //this is derived from Newtons universal law of gravity, Gm1m2 / r^2
        double numerator = 19.95468777;
        double denominator = Math.pow(currentInches * 0.0254, 2);
        double force = numerator / denominator;
        return force;
    }


    private boolean winchNearTarget() {
        return (Math.abs(liftEncoder.getCurrentPosition() - liftEncoder.getTargetPosition()) < WINCH_TOLERANCE_TICKS);
    }

    public void suck(double power) {
        intakeLeft.setPower(power);
        intakeRight.setPower(-power);
    }


    private boolean intakeFull() {
        int green = intakeSensor.green();
        Debug.log("Green: " + green);
        return green > 900;
    }


    public void capstoneScoring() {
        pulley.setPosition(0.077 * 2);
        //capstoneServo.setPosition(0.98);
    }


    public void outtakeServoPos() {

    }

    public void initCapstoneServo() {
        //capstoneServo.setPosition(0.5);
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
