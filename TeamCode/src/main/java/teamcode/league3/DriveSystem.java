package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Point;
import teamcode.common.Utils;
import teamcode.common.Vector2D;
import teamcode.test.odometry.MathFunctions;
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

      public DriveSystem(HardwareMap hardwareMap, GPS gps){
        this.gps = gps;
        frontLeft = hardwareMap.get(DcMotor.class, Constants.FRONT_LEFT_DRIVE);
        frontRight = hardwareMap.get(DcMotor.class, Constants.FRONT_RIGHT_DRIVE);
        rearLeft = hardwareMap.get(DcMotor.class, Constants.REAR_LEFT_DRIVE);
        rearRight = hardwareMap.get(DcMotor.class, Constants.REAR_RIGHT_DRIVE);
        correctDirections();
        driveMotion = null;
    }


    public DriveSystem(HardwareMap hardwareMap, OdometryWheelsFinal wheels){
        gps = null;
        this.wheels = wheels;
        frontLeft = hardwareMap.get(DcMotor.class, Constants.FRONT_LEFT_DRIVE);
        frontRight = hardwareMap.get(DcMotor.class, Constants.FRONT_RIGHT_DRIVE);
        rearLeft = hardwareMap.get(DcMotor.class, Constants.REAR_LEFT_DRIVE);
        rearRight = hardwareMap.get(DcMotor.class, Constants.REAR_RIGHT_DRIVE);
        correctDirections();

    }



    public DcMotor getMotorByName(HardwareMap hardwareMap, String motorName) {
        return hardwareMap.get(DcMotor.class, motorName);
    }

    private void correctDirections() {
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    /**
     * for omni movement with odometry in auto
     * @param deltaXCentimter the distance travelled in the X
     * @param deltaYCentimeter the distance travelled in the Y
     * @param power the power assigned to the driveTrain
     */
    public void omniMovement(double deltaXCentimter, double deltaYCentimeter, double power){
        Point destination = new Point(wheels.getGlobalRobotPosition().x + deltaXCentimter, wheels.getGlobalRobotPosition().y + deltaYCentimeter);
        double directionRads = wheels.getWorldAngleRads();
        //double angle = MathFunctions.angleWrap(directionRads + Math.atan2(deltaYCentimeter, deltaXCentimter));

        //double turnSpeed
        //Point currentGlobalPoint = wheels.getGlobalRobotPosition();
        while(!robotIsNearPoint(destination)) {
            double directionRelativeToGlobalPosition = Math.atan2(Math.abs(destination.y - wheels.getGlobalRobotPosition().y), Math.abs(destination.x - wheels.getGlobalRobotPosition().x));
            double powerX = deltaXCentimter / (Math.abs(deltaXCentimter) + Math.abs(deltaYCentimeter));
            double powerY = deltaYCentimeter / (Math.abs(deltaXCentimter) + Math.abs(deltaYCentimeter));

            Debug.log(wheels.getGlobalRobotPosition());
            Vector2D velocity = new Vector2D(powerX * power, powerY * power);
            continuous(velocity, 0);
        }
        brake();
    }

    public void vertical(double centimeters, double power) {
        //OdometryWheelsFinal implementation

        //GPS implementation
        //Vector2D currentPosition = gps.getPosition();
        //double rotation = gps.getRotation();
    }

    private boolean robotIsNearPoint(Point current) {
        return Math.abs(wheels.getGlobalRobotPosition().x - current.x) < Constants.TOLERANCE_X && Math.abs(wheels.getGlobalRobotPosition().y - current.y) < Constants.TOLERANCE_Y;
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
