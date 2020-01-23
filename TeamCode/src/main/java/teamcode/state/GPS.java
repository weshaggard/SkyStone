package teamcode.state;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;


import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.test.revextensions2.ExpansionHubEx;
import teamcode.test.revextensions2.RevBulkData;

/**
 * Always call shutdown() in onStop() of AbstractOpMode.
 */
public class GPS {

    private static final double ODOMETER_TICKS_TO_INCHES = 1 / 1102;
    private static final double HORIZONTAL_ODOMETER_ROTATION_OFFSET_TICKS = 0.4;
    private static final double VERTICAL_ODOMETER_TICKS_TO_RADIANS = 0.00006714153;
    private static final int LEFT_VERTICAL_ODOMETER_DIRECTION = -1;
    private static final int RIGHT_VERTICAL_ODOMETER_DIRECTION = -1;
    private static final int HORIZONTAL_ODOMETER_DIRECTION = -1;
    private static final long UPDATE_INTERVAL = 100;
    private long lastUpdateTime;

    /**
     * Whether or not this GPS should continue to update positions.
     */
    private boolean active;
    /**
     * Position stored in ticks. When exposed to external classes, it is represented in inches.
     */
    private Vector2D position;
    /**
     * In radians, unit circle style.
     */
    private double rotation;
    private final DcMotor leftVertical, rightVertical, horizontal;
    private double prevLeftVerticalPos, prevRightVerticalPos, prevHorizontalPos;

    private ExpansionHubEx hub1;
    private ExpansionHubEx hub2;
    private RevBulkData data1;
    private RevBulkData data2;

    /**
     * @param position in inches
     * @param rotation in radians
     */
    public GPS(HardwareMap hardwareMap, Vector2D position, double rotation) {
        hub1 = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 1");
        hub2 = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2");
        this.position = position.multiply(ODOMETER_TICKS_TO_INCHES);
        this.rotation = rotation;
        leftVertical = hardwareMap.dcMotor.get(Constants.LEFT_VERTICAL_ODOMETER_NAME);
        rightVertical = hardwareMap.dcMotor.get(Constants.RIGHT_VERTICAL_ODOMETER_NAME);
        horizontal = hardwareMap.dcMotor.get(Constants.HORIZONTAL_ODOMETER_NAME);
        prevLeftVerticalPos = 0;
        prevRightVerticalPos = 0;
        prevHorizontalPos = 0;
        resetEncoders();
        active = true;
        Thread positionUpdater = new Thread() {
            @Override
            public void run() {
                lastUpdateTime = System.currentTimeMillis();
                while (active) {
                    updateLocation();
                }
            }
        };
        positionUpdater.start();
    }

    private void resetEncoders() {
        leftVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void updateLocation() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastUpdateTime > UPDATE_INTERVAL) {
            data1 = hub1.getBulkInputData();
            data2 = hub2.getBulkInputData();
            lastUpdateTime = currentTime;
        }
        double leftVerticalPos = LEFT_VERTICAL_ODOMETER_DIRECTION * data1.getMotorCurrentPosition(leftVertical);
        double rightVerticalPos = RIGHT_VERTICAL_ODOMETER_DIRECTION * data2.getMotorCurrentPosition(rightVertical);
//        double leftVerticalPos = LEFT_VERTICAL_ODOMETER_DIRECTION * leftVertical.getCurrentPosition();
//        double rightVerticalPos = RIGHT_VERTICAL_ODOMETER_DIRECTION * rightVertical.getCurrentPosition();
        double deltaLeftVertical = leftVerticalPos - prevLeftVerticalPos;
        double deltaRightVertical = rightVerticalPos - prevRightVerticalPos;

        double deltaRotTicks = (deltaRightVertical - deltaLeftVertical);
        double newRotation = rotation + deltaRotTicks * VERTICAL_ODOMETER_TICKS_TO_RADIANS;
        rotation = newRotation;

        double horizontalPos = HORIZONTAL_ODOMETER_DIRECTION * data1.getMotorCurrentPosition(horizontal);
 //       double horizontalPos = HORIZONTAL_ODOMETER_DIRECTION * horizontal.getCurrentPosition();
        double rotationOffset = deltaRotTicks * HORIZONTAL_ODOMETER_ROTATION_OFFSET_TICKS;
        double deltaHorizontal = horizontalPos - prevHorizontalPos - rotationOffset;
        double averageDeltaVertical = (deltaLeftVertical + deltaRightVertical) / 2;

        double y = position.getY() + averageDeltaVertical * Math.sin(rotation) -
                deltaHorizontal * Math.cos(rotation);
        double x = position.getX() + averageDeltaVertical * Math.cos(rotation) +
                deltaHorizontal * Math.sin(rotation);




        Vector2D newPosition = new Vector2D(x, y);
        position = newPosition;

        prevLeftVerticalPos = leftVerticalPos;
        prevRightVerticalPos = rightVerticalPos;
        prevHorizontalPos = horizontalPos;
    }

    /**
     * Returns the position of the robot as read by the odometers. In inches
     */
    public Vector2D getPosition() {
        return position.multiply(ODOMETER_TICKS_TO_INCHES);
    }


    public int[] getCurrentPositions(){
        return new int[]{leftVertical.getCurrentPosition() * LEFT_VERTICAL_ODOMETER_DIRECTION, RIGHT_VERTICAL_ODOMETER_DIRECTION *rightVertical.getCurrentPosition(), HORIZONTAL_ODOMETER_DIRECTION* horizontal.getCurrentPosition()};
    }

    /**
     * Returns the rotation in radians.
     */
    public double getRotation() {
        return rotation;
    }

    public double getRotationDegrees(){
        return Math.toDegrees(rotation);
    }

    /**
     * Invoke this in AbstractOpMode.onStop().
     */
    public void shutdown() {
        active = false;
    }

}
