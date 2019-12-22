package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.Utils;
import teamcode.common.Vector2D;

public class DriveSystem {

    private final DcMotor frontLeft, frontRight, rearLeft, rearRight;
    private final GPS gps;
    /**
     * The position in inches that the robot should try to reach. The target must be stored separate from the
     * GPS's current location due to errors in positioning that may accumulate.
     */
    private Vector2D targetPosition;
    /**
     * The bearing that the robot should try to reach in degrees. The target must be stored separate from the
     * GPS's current rotation due to errors in rotation that may accumulate.
     */
    private double targetRotation;

    public DriveSystem(HardwareMap hardwareMap, GPS gps, Vector2D currentPosition, double currentRotation) {
        frontLeft = hardwareMap.dcMotor.get(Constants.FRONT_LEFT_DRIVE_NAME);
        frontRight = hardwareMap.dcMotor.get(Constants.FRONT_RIGHT_DRIVE_NAME);
        rearLeft = hardwareMap.dcMotor.get(Constants.REAR_LEFT_DRIVE_NAME);
        rearRight = hardwareMap.dcMotor.get(Constants.REAR_RIGHT_DRIVE_NAME);
        correctDirections();
        this.gps = gps;
        targetPosition = currentPosition;
        targetRotation = currentRotation;
    }

    private void correctDirections() {
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void vertical(double inches, double power) {
        Vector2D translation = Vector2D.fromAngleMagnitude(targetRotation, inches);
        targetPosition = targetPosition.add(translation);
        while (!near(targetPosition, targetRotation)) {
            // add in correction
            double targetX = 0;
            double targetY = 0;
            Vector2D velocity = new Vector2D(0, 0);
            continuous(velocity, 0);
        }
        brake();
    }

    public void lateral(double inches, double power) {

    }

    public void rotate(double degrees, double power) {
        targetRotation = targetRotation + degrees;
        double radians = Math.toRadians(degrees);
        double currentRotation = gps.getRotation();
        double turnAngle = radians - currentRotation;
    }

    public void goTo(Vector2D targetPosition, double speed) {
        this.targetPosition = targetPosition;
        while (!near(targetPosition, targetRotation)) {
            Vector2D currentPosition = gps.getPosition();
            double currentRotation = gps.getRotation();
            Vector2D translation = targetPosition.subtract(currentPosition);

            // Reduce power when approaching target position.
            double distanceToTarget = translation.magnitude();
            double powerMultiplier = speed * getModulatedPower(speed, distanceToTarget);

            // Account for the orientation of the robot.
            Vector2D velocity = translation.rotate(-currentRotation).normalized().multiply(powerMultiplier);
            double turnSpeed = (currentRotation - targetRotation) * Constants.TURN_CORRECTION_INTENSITY;
            continuous(velocity, turnSpeed);
        }
    }

    private double getModulatedPower(double maxSpeed, double distanceToTarget) {
        if (distanceToTarget < Constants.DRIVE_SPEED_REDUCTION_DISTANCE_INCHES) {
            return Math.min(maxSpeed, Utils.lerp(Constants.DRIVE_MIN_REDUCED_SPEED,
                    1, distanceToTarget /
                            Constants.DRIVE_SPEED_REDUCTION_DISTANCE_INCHES));
        } else {
            return maxSpeed;
        }
    }

    public void continuous(Vector2D velocity, double turnSpeed) {
        // Not sure why this is necessary, but it works. If it ain't broke, don't fix it.
        Vector2D velocity0 = new Vector2D(-velocity.getX(), velocity.getY());
        double direction = velocity0.getDirection();

        double maxPow = Math.sin(Math.PI / 4);
        double power = velocity.magnitude() / maxPow;

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

    /**
     * Returns true if the robot is near the specified position and rotation, false otherwise.
     */
    private boolean near(Vector2D position, double rotation) {
        Vector2D currentPosition = gps.getPosition();
        Vector2D positionOffset = position.subtract(currentPosition);
        double currentRotation = gps.getRotation();
        double rotationOffset = rotation - currentRotation;
        return Math.abs(positionOffset.getX()) < Constants.DRIVE_OFFSET_TOLERANCE_INCHES &&
                Math.abs(positionOffset.getY()) < Constants.DRIVE_OFFSET_TOLERANCE_INCHES &&
                Math.abs(Math.toDegrees(rotationOffset)) < Constants.DRIVE_OFFSET_TOLERANCE_DEGREES;
    }

}
