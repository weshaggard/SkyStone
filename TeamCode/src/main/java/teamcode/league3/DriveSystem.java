package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.common.Vector2D;

public class DriveSystem {

    /**
     * 0: front left (rear encoder)
     * 1: front right (left encoder
     * 2: rear left (right encoder)
     * 3. rear right
     */
    private DcMotor[] motors;
    private DriveMotion driveMotion;

    private enum DriveMotion {
        VERTICAL, LATERAL, TURN
    }

    public DriveSystem(HardwareMap hardwareMap) {
        motors = new DcMotor[]{
                getMotorByName(hardwareMap, Constants.FRONT_LEFT_DRIVE),
                getMotorByName(hardwareMap, Constants.FRONT_RIGHT_DRIVE),
                getMotorByName(hardwareMap, Constants.REAR_LEFT_DRIVE),
                getMotorByName(hardwareMap, Constants.REAR_RIGHT_DRIVE)
        };
        correctDirections();
        resetEncoders();
        driveMotion = null;
    }

    private DcMotor getMotorByName(HardwareMap hardwareMap, String motorName) {
        return hardwareMap.get(DcMotor.class, motorName);
    }

    private void correctDirections() {
        motors[1].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[3].setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void resetEncoders() {
        for (DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    /**
     * Provides access to the internal motors.
     */
    public DcMotor[] getMotors() {
        return motors;
    }

    public void vertical(double inches, double power) {
        int ticks = (int) (inches * Constants.DRIVE_VERTICAL_INCHES_TO_TICKS);

        while (!nearTarget() && AbstractOpMode.currentOpMode().opModeIsActive()) {
            double x = (motors[0].getTargetPosition() - motors[0].getCurrentPosition()) / 5000.0 * power;
            Debug.log("x=" + x);
            double y = power;
            Vector2D velocity = new Vector2D(x, y);
            continuous(velocity, 0);
//            Debug.clear();
//            Debug.log("left: current=" + motors[1].getCurrentPosition() + ", target=" + motors[1].getTargetPosition());
//            Debug.log("right: current=" + motors[2].getCurrentPosition() + ", target=" + motors[2].getTargetPosition());
//            Debug.log("rear: current=" + motors[0].getCurrentPosition() + ", target=" + motors[0].getTargetPosition());
        }
        brake();
    }

    public void lateral(double inches, double power) {

    }

    public void rotate(double degrees, double power) {
    }

    public void continuous(Vector2D velocity, double turnSpeed) {
        double direction = velocity.getDirection();

        double maxPow = Math.sin(Math.PI / 4);
        double power = velocity.magnitude() / maxPow;

        double angle = direction - Math.PI / 4;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        motors[0].setPower(power * sin + turnSpeed);
        motors[1].setPower(power * cos - turnSpeed);
        motors[2].setPower(power * cos + turnSpeed);
        motors[3].setPower(power * sin - turnSpeed);
    }

    public void brake() {
        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
        driveMotion = null;
    }

    private void setTarget(DriveMotion driveMotion, int leftTicks, int rightTicks, int rearTicks) {
        this.driveMotion = driveMotion;
        leftTicks += motors[1].getTargetPosition();
        motors[1].setTargetPosition(leftTicks);

        rightTicks += motors[2].getTargetPosition();
        // so that it runs in the right direction
        rightTicks = -rightTicks;
        motors[2].setTargetPosition(rightTicks);

        rearTicks += motors[0].getTargetPosition();
        motors[0].setTargetPosition(rearTicks);
    }

    public double getProgress() {
        switch (driveMotion) {
            case VERTICAL:
                return 0;
            case LATERAL:
                return 0;
            case TURN:
                return 0;
            default:
                return 1;
        }
    }

    private boolean nearTarget() {
        return Utils.motorNearTarget(motors[0], Constants.DRIVE_TICK_ERROR_TOLERANCE) &&
                Utils.motorNearTarget(motors[1], Constants.DRIVE_TICK_ERROR_TOLERANCE) &&
                Utils.motorNearTarget(motors[2], Constants.DRIVE_TICK_ERROR_TOLERANCE);
    }

}
