package teamcode.league2;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.robotComponents.TTHardwareComponentNames;

import static teamcode.common.Utils.sleep;

public class ArmSystemLeague2 {
    // 529.2 ticks per revolution
    private static final double LIFT_INCHES_TO_TICKS = 162.33;
    private static final double LIFT_POSITION_ERROR_TOLERANCE = 40;

    private static final double WRIST_EXTENDED_POSITION = 0.0;
    private static final double WRIST_RETRACTED_POSITION = 1.0;
    private static final double WRIST_POSITION_ERROR_TOLERANCE = 0.05;
    private static final double WRIST_TICK_DELTA = 0.05;

    private static final double CLAW_OPEN_POSITION = 0.0;
    private static final double CLAW_CLOSE_POSITION = 1.0;
    private static final double CLAW_POSITION_ERROR_TOLERANCE = 0.05;

    private final DcMotor lift;
    private final Servo wrist, claw, leftGrabber, rightGrabber;
    private final DcMotor leftIntake, rightIntake;
    private final ColorSensor intakeSensor;

    private final AbstractOpMode opMode;

    public ArmSystemLeague2(AbstractOpMode opMode) {
        HardwareMap hardwareMap = opMode.hardwareMap;
        lift = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.ARM_LIFT);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftIntake = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.INTAKE_LEFT);
        rightIntake = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.INTAKE_RIGHT);
        wrist = hardwareMap.get(Servo.class, TTHardwareComponentNames.ARM_WRIST);
        claw = hardwareMap.get(Servo.class, TTHardwareComponentNames.ARM_CLAW);
        intakeSensor = hardwareMap.get(ColorSensor.class, TTHardwareComponentNames.INTAKE_SENSOR);
        leftGrabber = hardwareMap.get(Servo.class, TTHardwareComponentNames.LEFT_FOUNDATION_GRABBER);
        rightGrabber = hardwareMap.get(Servo.class, TTHardwareComponentNames.RIGHT_FOUNDATION_GRABBER);
        this.opMode = opMode;
    }

    /**
     * Returns the current lift height in inches.
     * @return the current lift height in inches.
     */
    public double getLiftHeight() {
        int ticks = lift.getCurrentPosition();
        return (ticks / LIFT_INCHES_TO_TICKS);
    }

    public void lift(double inches, double power) {
        int ticks = (int) (inches * LIFT_INCHES_TO_TICKS);
        lift.setTargetPosition(ticks);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(power);
        //while (lift.isBusy());
        while (!liftIsNearTarget()) {
            int target = lift.getTargetPosition();
            int current = lift.getCurrentPosition();
            Debug.log("Target: " + target + ", Current: " + current);
        }
        lift.setPower(0.0);
    }

    private boolean liftIsNearTarget() {
        int target = lift.getTargetPosition();
        int current = lift.getCurrentPosition();
        double ticksFromTarget = Math.abs(target - current);
        return ticksFromTarget <= LIFT_POSITION_ERROR_TOLERANCE;
    }

    public boolean wristIsExtended() {
        return Utils.servoNearPosition(wrist, WRIST_EXTENDED_POSITION,
                WRIST_POSITION_ERROR_TOLERANCE);
    }

    public void setWristPosition(boolean extended) {
        if (extended) {
            wrist.setPosition(WRIST_EXTENDED_POSITION);
        } else {
            wrist.setPosition(WRIST_RETRACTED_POSITION);
        }
    }

    /**
     * IMPORTANT: BE SURE TO UPDATE RELATIONAL OPERATOR IF RETRACTED AND EXTENDED POSITIONS CHANGE.
     */
    public void extendWristIncrementally() {
        double currentPosition = wrist.getPosition();
        while (currentPosition > WRIST_EXTENDED_POSITION) {
            currentPosition -= WRIST_TICK_DELTA;
            wrist.setPosition(currentPosition);
            sleep(100);
        }
    }

    /**
     * Moves the arm components into the posistion which they can be easily scored
     */
    //4,5
    public void moveToScoringPos() {
        intake(0);
        //setClawPosition(false);
        //sleep(2000);
        Timer wrist = opMode.getNewTimer();
        TimerTask wristTask = new TimerTask(){
            @Override
            public void run(){
                setWristPosition(true);
            }
        };
        wrist.schedule(wristTask, 100);
        lift(2100, 1);
        lift(-1500, -1);
        setClawPosition(true);
    }

    public boolean clawIsOpen() {
        return Utils.servoNearPosition(claw, CLAW_OPEN_POSITION, CLAW_POSITION_ERROR_TOLERANCE);
    }

    public void setClawPosition(boolean open) {
        if (open) {
            claw.setPosition(CLAW_OPEN_POSITION);
        } else {
            claw.setPosition(CLAW_CLOSE_POSITION);
        }
    }

    /**
     * @param power sucks if positive, spits if negative
     */
    public void intake(double power) {
        leftIntake.setPower(-power);
        rightIntake.setPower(power);
    }

    public boolean intakeIsFull() {
        intakeSensor.enableLed(true);
        int blue = intakeSensor.blue();
        int red = intakeSensor.red();
        int green = intakeSensor.green();
        Debug.log("Red: " + red);
        Debug.log("Green: " + green);
        Debug.log("Blue: " + blue);
        return red > 2000 && green > 4000;
    }

    public void grabFoundation(boolean open){
        if (open) {
            closeGrabber();
        } else {
            openGrabber();
        }
    }

    private void openGrabber(){
        leftGrabber.setPosition(1);
        rightGrabber.setPosition(0);
    }

    private void closeGrabber(){
        leftGrabber.setPosition(0);
        rightGrabber.setPosition(1);
    }
}
