package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Point;
import teamcode.common.Utils;
import teamcode.common.Vector2D;
import teamcode.test.odometry.OdometryWheelsFinal;

public class DriveSystem {

    private final DcMotor frontLeft, frontRight, rearLeft, rearRight;
    private final GPS gps;
    /**
     * 0: front left (rear encoder)
     * 1: front right (left encoder
     * 2: rear left (right encoder)
     * 3. rear right
     */
    private DcMotor[] motors;
    private DriveMotion driveMotion;
    private OdometryWheelsFinal wheels;

    public enum DriveMotion {
        VERTICAL, LATERAL, TURN, STOP
    }

      public driveSystem(GPS gps){
        this.gps = gps;
        resetEncoders();
        wheels = new OdometryWheelsFinal(AbstractOpMode.currentOpMode(),new Point(100, 100) ,this, 0);
        driveMotion = null;
    }

    private DcMotor getMotorByName(HardwareMap hardwareMap, String motorName) {
        return hardwareMap.get(DcMotor.class, motorName);
    }

    private void correctDirections() {
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void vertical(double inches, double power) {
        Vector2D currentPosition = gps.getPosition();
        double rotation = gps.getRotation();
    }

    public void lateral(double inches, double power) {
        driveMotion = DriveMotion.LATERAL;
        //int ticks = (int)(inches * Constants.DRIVE_LATERAL_INCHES_TO_TICKS);

        while(!nearTarget() && AbstractOpMode.currentOpMode().opModeIsActive()){
        }
    }

    public void rotate(double degrees, double power) {

    }

    public void goTo(Vector2D targetPosition, double power) {
        while (AbstractOpMode.currentOpMode().opModeIsActive()) {

        }
    }

    public void continuous(Vector2D velocity, double turnSpeed) {
        // multiply the x component by -1 (not sure why this is necessary but it is)
        Vector2D velocity0 = new Vector2D(-velocity.getX(), velocity.getY());
        double direction = velocity0.getDirection();

        double maxPow = Math.sin(Math.PI / 4);
        double power = velocity0.magnitude() / maxPow;

        double angle = direction - Math.PI / 4;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        frontLeft.setPower(power * sin + turnSpeed);
        frontRight.setPower(power * cos - turnSpeed);
        rearLeft.setPower(power * cos + turnSpeed);
        rearRight.setPower(power * sin - turnSpeed);
    }

    public void brake() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        rearLeft.setPower(0);
        rearRight.setPower(0);
    }

    public boolean nearTarget() {
        return Utils.motorNearTarget(motors[0], Constants.DRIVE_TICK_ERROR_TOLERANCE) &&
                Utils.motorNearTarget(motors[1], Constants.DRIVE_TICK_ERROR_TOLERANCE) &&
                Utils.motorNearTarget(motors[2], Constants.DRIVE_TICK_ERROR_TOLERANCE);
    }

}
