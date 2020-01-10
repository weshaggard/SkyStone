package teamcode.league3;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;

public class MoonshotArmSystem {

    private static final double INTAKE_POWER = 1;
    private static final double BOX_FLAT_POSITION = 0.5;
    private static final double BOX_RAMPED_POSITION = 0.37;
    private static final double BACK_GRABBER_OPEN_POSITION = 0.9;
    private static final double BACK_GRABBER_CLOSED_POSITION = 0.5;
    private static final double FRONT_GRABBER_OPEN_POSITION = 0.9;
    private static final double FRONT_GRABBER_INTAKE_POSITION = 0.84;
    private static final double FRONT_GRABBER_CLOSED_POSITION = 1;
    private static final double FOUNDATION_GRABBER_RIGHT_OPEN_POSITION = 0;
    private static final double FOUNDATION_GRABBER_LEFT_OPEN_POSITION = 0;
    private static final double PULLEY_RETRACTED_POSITION = 0;
    private static final double PULLEY_EXTENDED_POSITION = 1.0;

    private DcMotor intakeLeft, intakeRight;
    private DcMotor frontWinch, backWinch;
    private Servo pulley;
    private Servo boxTransfer;
    private Servo foundationGrabberLeft, foundationGrabberRight;
    private Servo backGrabber, frontGrabber;
    private ColorSensor intakeSensor;

    private boolean intaking;

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
        backGrabber.setPosition(BACK_GRABBER_OPEN_POSITION);
        frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);
        pulley.setPosition(PULLEY_RETRACTED_POSITION);
    }

    private void correctMotors() {
        backWinch.setDirection(DcMotorSimple.Direction.REVERSE);

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
        suck(0);
        boxTransfer.setPosition(BOX_FLAT_POSITION);
        Debug.log("transfer case down");
        backGrabber.setPosition(BACK_GRABBER_CLOSED_POSITION);
        Utils.sleep(1500);
        pulley.setPosition(0.077 * 4);
        frontGrabber.setPosition(FRONT_GRABBER_CLOSED_POSITION);
    }

    /**
     * Stop the robot from intaking while in intake sequence.
     */
    public void cancelIntakeSequence() {
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
        pulley.setPosition(0.077 * 2 + pulley.getPosition());
        frontGrabber.setPosition(1);
    }

    public void dumpStone() {
        pulley.setPosition(1);
        //frontGrabber.setPosition();
    }

    public void score() {
        frontGrabber.setPosition(0.64);
        pulley.setPosition(1 - (0.077 * 2));
        backGrabber.setPosition(0.9);
        Utils.sleep(250);
        pulley.setPosition(0);
    }

    public void lift(double power) {
        if (power == 0) {
            // brake power
            power = 0.125;
        }
        frontWinch.setPower(power);
        backWinch.setPower(power);
    }

    public void suck(double power) {
        intakeLeft.setPower(power);
        intakeRight.setPower(-power);
    }

    private boolean intakeFull() {
        int green = intakeSensor.green();
        return green > 600;
    }

    public void clampFoundation() {
        foundationGrabberLeft.setPosition(1);
        foundationGrabberRight.setPosition(0);
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


}
