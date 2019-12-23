package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.Utils;
import teamcode.common.Vector2D;

/**
 * Always call shutdown() in onStop() of AbstractOpMode.
 */
public class GPS {

    /**
     * Whether or not this GPS should continue to update positions.
     */
    private boolean active;
    /**
     * Position stored in ticks. When exposed to external classes, it is represented in inches.
     */
    private volatile Vector2D position;
    /**
     * In radians, unit circle style.
     */
    private volatile double rotation;
    private final DcMotor leftVertical, rightVertical, horizontal;
    private double prevLeftVerticalPos, prevRightVerticalPos, prevHorizontalPos;

    public GPS(HardwareMap hardwareMap, Vector2D currentPosition, double currentBearing) {
        active = true;
        this.position = currentPosition;
        this.rotation = bearingToRadians(currentBearing);
        leftVertical = hardwareMap.dcMotor.get(Constants.LEFT_VERTICAL_ODOMETER_NAME);
        rightVertical = hardwareMap.dcMotor.get(Constants.RIGHT_VERTICAL_ODOMETER_NAME);
        horizontal = hardwareMap.dcMotor.get(Constants.HORIZONTAL_ODOMETER_NAME);
        prevLeftVerticalPos = 0;
        prevRightVerticalPos = 0;
        prevHorizontalPos = 0;
        correctDirections();
        resetEncoders();
        Thread positionUpdater = new Thread() {
            @Override
            public void run() {
                while (active) {
                    updateLocation();
                }
            }
        };
        positionUpdater.start();
    }

    private void correctDirections() {
        horizontal.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void resetEncoders() {
        leftVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void updateLocation() {
        double leftVerticalPos = leftVertical.getCurrentPosition();
        double rightVerticalPos = rightVertical.getCurrentPosition();
        double deltaLeftVertical = leftVerticalPos - prevLeftVerticalPos;
        double deltaRightVertical = rightVerticalPos - prevRightVerticalPos;

        double deltaRotTicks = (deltaRightVertical - deltaLeftVertical);
        rotation += deltaRotTicks / Constants.VERTICAL_ODOMETER_TICKS_TO_RADIANS;
        rotation = Utils.wrapAngle(rotation);

        double horizontalPos = horizontal.getCurrentPosition();
        double deltaHorizontal = horizontalPos - prevHorizontalPos + deltaRotTicks *
                Constants.HORIZONTAL_ODOMETER_ROTATION_TO_HORIZONTAL_TICK_OFFSET;
        double averageDeltaVertical = (deltaLeftVertical + deltaRightVertical) / 2;

        double y = position.getY() + averageDeltaVertical * Math.sin(rotation) +
                deltaHorizontal * Math.cos(rotation);
        double x = position.getX() + averageDeltaVertical * Math.cos(rotation) +
                deltaHorizontal * Math.sin(rotation);

        position.setX(x);
        position.setY(y);

        prevLeftVerticalPos = leftVerticalPos;
        prevRightVerticalPos = rightVerticalPos;
        prevHorizontalPos = horizontalPos;
    }

    /**
     * Returns the position of the robot as read by the odometers. In inches
     */
    public Vector2D getPosition() {
        return position.multiply(Constants.ODOMETER_TICKS_TO_INCHES);
    }

    /**
     * Returns the rotation in radians, unit circle style.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Returns the robot's bearing in degrees.
     */
    public double getBearing() {
        return radiansToBearing(rotation);
    }

    private double radiansToBearing(double radians) {
        // 0 -> 90
        // pi/2 -> 0
        // pi -> 270
        // 3pi/2 -> 180
        return radians;
    }

    private double bearingToRadians(double bearing) {
        return 0;
    }

    /**
     * Invoke this in AbstractOpMode.onStop().
     */
    public void shutdown() {
        active = false;
    }

}
