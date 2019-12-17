package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.Vector2D;

public class DriveSystem {

    private final DcMotor frontLeft, frontRight, rearLeft, rearRight;
    private final GPS gps;

    public DriveSystem(HardwareMap hardwareMap, GPS gps) {
        frontLeft = hardwareMap.dcMotor.get(Constants.FRONT_LEFT_DRIVE);
        frontRight = hardwareMap.dcMotor.get(Constants.FRONT_RIGHT_DRIVE);
        rearLeft = hardwareMap.dcMotor.get(Constants.REAR_LEFT_DRIVE);
        rearRight = hardwareMap.dcMotor.get(Constants.REAR_RIGHT_DRIVE);
        correctDirections();
        this.gps = gps;
    }

    private void correctDirections() {
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void vertical(double inches, double power) {
        Vector2D currentPosition = gps.getPosition();
        double currentRotation = gps.getRotation();
        Vector2D translation = Vector2D.fromAngleMagnitude(currentRotation, inches);
        Vector2D target = currentPosition.add(translation);
        continuous(Vector2D.forward().multiply(power), 0);
        while (!nearTargetPosition(target, currentRotation)) ;
        brake();
    }

    public void lateral(double inches, double power) {

    }

    public void rotate(double degrees, double power) {
        double radians = Math.toRadians(degrees);
        double currentRotation = gps.getRotation();
        double turnAngle = radians - currentRotation;
    }

    public void goTo(Vector2D targetPosition, double power) {
        Vector2D currentPos = gps.getPosition();
        double currentRot = gps.getRotation();
        Vector2D translation = targetPosition.subtract(currentPos);
        double turnAngle = translation.getDirection() - currentRot;
        double distance = translation.magnitude();
        rotate(Math.toDegrees(turnAngle), power);
        vertical(distance, power);
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

    private boolean nearTargetPosition(Vector2D targetPosition, double targetRotation) {
        Vector2D currentPosition = gps.getPosition();
        Vector2D positionOffset = targetPosition.subtract(currentPosition);
        double currentRotation = gps.getRotation();
        double rotationOffset = targetRotation - currentRotation;
        return positionOffset.getX() < Constants.DRIVE_OFFSET_TOLERANCE_INCHES &&
                positionOffset.getY() < Constants.DRIVE_OFFSET_TOLERANCE_INCHES &&
                Math.toDegrees(rotationOffset) < Constants.DRIVE_OFFSET_TOLERANCE_DEGREES;
    }

}
