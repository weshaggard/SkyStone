package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.Utils;
import teamcode.common.Vector2D;
import teamcode.test.REVExtensions2.ExpansionHubEx;
import teamcode.test.REVExtensions2.RevBulkData;

/**
 * Always call shutdown() in onStop() of AbstractOpMode.
 */
public class GPS {

    public static final double ODOMETER_INCHES_TO_TICKS = 1102;
    public static final double HORIZONTAL_ODOMETER_ROTATION_OFFSET_TICKS = 0.5;
    public static final double VERTICAL_ODOMETER_TICKS_TO_RADIANS = 9212.3456328;

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
    private final ExpansionHubEx hub;
    private RevBulkData data;
    private final DcMotor leftVertical, rightVertical, horizontal;
    private double prevLeftVerticalPos, prevRightVerticalPos, prevHorizontalPos;

    /**
     * @param currentPosition in inches
     * @param rotation        in radians
     */
    public GPS(HardwareMap hardwareMap, Vector2D currentPosition, double rotation) {
        active = true;
        this.position = currentPosition.multiply(ODOMETER_INCHES_TO_TICKS);
        hub = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2");
        data = hub.getBulkInputData();
        leftVertical = hardwareMap.dcMotor.get(Constants.LEFT_VERTICAL_ODOMETER_NAME);
        rightVertical = hardwareMap.dcMotor.get(Constants.RIGHT_VERTICAL_ODOMETER_NAME);
        horizontal = hardwareMap.dcMotor.get(Constants.HORIZONTAL_ODOMETER_NAME);
        prevLeftVerticalPos = 0;
        prevRightVerticalPos = 0;
        prevHorizontalPos = 0;
        correctEncoderDirections();
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

    private void correctEncoderDirections() {
        horizontal.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void resetEncoders() {
        leftVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void updateLocation() {
        double leftVerticalPos = data.getMotorCurrentPosition(leftVertical);
        double rightVerticalPos = data.getMotorCurrentPosition(rightVertical);
        double deltaLeftVertical = leftVerticalPos - prevLeftVerticalPos;
        double deltaRightVertical = rightVerticalPos - prevRightVerticalPos;

        double deltaRotTicks = (deltaRightVertical - deltaLeftVertical);
        rotation += deltaRotTicks / VERTICAL_ODOMETER_TICKS_TO_RADIANS;
        rotation = Utils.wrapAngle(rotation);

        double horizontalPos = data.getMotorCurrentPosition(horizontal);
        double deltaHorizontal = horizontalPos - prevHorizontalPos + deltaRotTicks *
                HORIZONTAL_ODOMETER_ROTATION_OFFSET_TICKS;
        double averageDeltaVertical = (deltaLeftVertical + deltaRightVertical) / 2;

        double y = position.getY() - averageDeltaVertical * Math.sin(rotation) -
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
        return position.multiply(1 / ODOMETER_INCHES_TO_TICKS);
    }

    /**
     * Returns the rotation in radians.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Invoke this in AbstractOpMode.onStop().
     */
    public void shutdown() {
        active = false;
    }

}