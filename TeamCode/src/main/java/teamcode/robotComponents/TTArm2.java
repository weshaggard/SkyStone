package teamcode.robotComponents;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import teamcode.common.AbstractOpMode;
import teamcode.common.Utils;

public class TTArm2 {

    private static final double LIFT_POSITION_ERROR_TOLERANCE = 100.0;

    private static final double WRIST_EXTENDED_POSITION = 0.0;
    private static final double WRIST_RETRACTED_POSITION = 1.0;
    private static final double WRIST_POSITION_ERROR_TOLERANCE = 0.05;
    private static final double WRIST_TICK_DELTA = -0.05;

    private static final double CLAW_OPEN_POSITION = 0.4;
    private static final double CLAW_CLOSE_POSITION = 1.0;
    private static final double CLAW_POSITION_ERROR_TOLERANCE = 0.05;

    private final DcMotor lift;
    private final Servo wrist, claw;
    private final DcMotor leftIntake, rightIntake;
    private final TouchSensor intakeSensor;

    public TTArm2(AbstractOpMode opMode) {
        HardwareMap hardwareMap = opMode.hardwareMap;
        lift = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.ARM_LIFT);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftIntake = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.INTAKE_LEFT);
        rightIntake = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.INTAKE_RIGHT);
        wrist = hardwareMap.get(Servo.class, TTHardwareComponentNames.ARM_WRIST);
        claw = hardwareMap.get(Servo.class, TTHardwareComponentNames.ARM_CLAW);
        intakeSensor = hardwareMap.get(TouchSensor.class, TTHardwareComponentNames.INTAKE_SENSOR);
    }

    /**
     * In ticks.
     */
    public int getLiftHeight() {
        return lift.getCurrentPosition();
    }

    public void lift(int ticks, double power) {
        lift.setTargetPosition(ticks);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(power);
        while (!liftIsNearTarget()) ;
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
        double currentPosition = WRIST_RETRACTED_POSITION;
        while (currentPosition >= WRIST_EXTENDED_POSITION) {
            currentPosition += WRIST_TICK_DELTA;
            wrist.setPosition(currentPosition);
            Utils.sleep(100);
        }
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
        power = -power;
        leftIntake.setPower(power);
        rightIntake.setPower(power);
    }

    public boolean intakeIsFull() {
        return intakeSensor.isPressed();
    }

}
