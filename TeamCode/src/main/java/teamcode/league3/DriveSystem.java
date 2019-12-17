package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.AbstractOpMode;
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
        double rotation = gps.getRotation();
    }

    public void lateral(double inches, double power) {

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

    private boolean nearTarget() {
        return false;
    }

}
