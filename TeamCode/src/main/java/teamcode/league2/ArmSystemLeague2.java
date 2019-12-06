package teamcode.league2;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import teamcode.common.AbstractOpMode;
import teamcode.common.Utils;

import static teamcode.common.Utils.servoNearPosition;
import static teamcode.common.Utils.sleep;

public class ArmSystemLeague2 {
    // 529.2 ticks per revolution
    private static final double LIFT_INCHES_TO_TICKS = 146.972519894;
    private static final int LIFT_POSITION_ERROR_TOLERANCE = 100;

    private static final double WRIST_EXTENDED_POSITION = 0.0;
    private static final double WRIST_RETRACTED_POSITION = 1.0;
    private static final double WRIST_POSITION_ERROR_TOLERANCE = 0.05;
    private static final double WRIST_TICK_DELTA = 0.08;

    private static final double CLAW_OPEN_POSITION = 0.2;
    private static final double CLAW_CLOSED_POSITION = 1.0;

    private static final double LEFT_GRABBER_OPEN_POSITION = 0;
    private static final double LEFT_GRABBER_CLOSED_POSITION = 1;
    private static final double RIGHT_GRABBER_OPEN_POSITION = 1;
    private static final double RIGHT_GRABBER_CLOSED_POSITION = 0;

    private static final double SERVO_POSITION_ERROR_TOLERANCE = 0.05;

    private final DcMotor lift;
    private final Servo wrist, claw;
    private final Servo leftGrabber, rightGrabber;
    private final DcMotor leftIntake, rightIntake;
    private final ColorSensor intakeSensor;
    private final TouchSensor liftSensor;

    public ArmSystemLeague2(HardwareMap hardwareMap) {
        lift = hardwareMap.get(DcMotor.class, HardwareComponentNamesLeague2.ARM_LIFT);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftSensor = hardwareMap.get(TouchSensor.class, HardwareComponentNamesLeague2.ARM_LIFT_SENSOR);
        leftIntake = hardwareMap.get(DcMotor.class, HardwareComponentNamesLeague2.INTAKE_LEFT);
        rightIntake = hardwareMap.get(DcMotor.class, HardwareComponentNamesLeague2.INTAKE_RIGHT);
        wrist = hardwareMap.get(Servo.class, HardwareComponentNamesLeague2.ARM_WRIST);
        claw = hardwareMap.get(Servo.class, HardwareComponentNamesLeague2.ARM_CLAW);
        intakeSensor = hardwareMap.get(ColorSensor.class, HardwareComponentNamesLeague2.INTAKE_SENSOR);
        leftGrabber = hardwareMap.get(Servo.class, HardwareComponentNamesLeague2.LEFT_GRABBER);
        rightGrabber = hardwareMap.get(Servo.class, HardwareComponentNamesLeague2.RIGHT_GRABBER);
    }

    /**
     * Returns the current setLiftHeight height in inches.
     *
     * @return the current setLiftHeight height in inches.
     */
    public double getLiftHeight() {
        int ticks = lift.getCurrentPosition();
        return (ticks / LIFT_INCHES_TO_TICKS);
    }

    public void setLiftHeight(double inches, double power) {
        int ticks = (int) (inches * LIFT_INCHES_TO_TICKS);
        lift.setTargetPosition(ticks);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(power);
        while (!Utils.motorNearTarget(lift, LIFT_POSITION_ERROR_TOLERANCE) &&
                AbstractOpMode.currentOpMode().opModeIsActive()) ;
        lift.setPower(0.0);
    }

    /**
     * No longer uses the touch sensor.
     */
    @Deprecated
    public void resetLift() {
        setLiftHeight(0, 1);
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

    public boolean clawIsOpen() {
        return Utils.servoNearPosition(claw, CLAW_OPEN_POSITION, SERVO_POSITION_ERROR_TOLERANCE);
    }

    public void setClawPosition(boolean open) {
        if (open) {
            claw.setPosition(CLAW_OPEN_POSITION);
        } else {
            claw.setPosition(CLAW_CLOSED_POSITION);
        }
    }

    /**
     * @param power sucks if positive, spits if negative
     */
    public void intake(double power) {
        leftIntake.setPower(-power);
        rightIntake.setPower(power);
    }

    public void intake(double leftPower, double rightPower) {
        leftIntake.setPower(-leftPower);
        rightIntake.setPower(rightPower);
    }

    public boolean intakeIsFull() {
        intakeSensor.enableLed(true);
        int red = intakeSensor.red();
        return red > 400;
    }

    public void toggleFoundationGrabbers(boolean open) {
        if (open) {
            leftGrabber.setPosition(LEFT_GRABBER_OPEN_POSITION);
            rightGrabber.setPosition(RIGHT_GRABBER_OPEN_POSITION);
        } else {
            leftGrabber.setPosition(LEFT_GRABBER_CLOSED_POSITION);
            rightGrabber.setPosition(RIGHT_GRABBER_CLOSED_POSITION);
        }
    }

    public boolean foundationGrabbersAreOpen() {
        return servoNearPosition(leftGrabber, LEFT_GRABBER_OPEN_POSITION, SERVO_POSITION_ERROR_TOLERANCE);
    }

}
