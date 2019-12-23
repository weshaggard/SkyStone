package teamcode.league3;

public class Constants {

    // hardware device names
    public static final String FRONT_LEFT_DRIVE_NAME = "FrontLeftDrive";
    public static final String FRONT_RIGHT_DRIVE_NAME = "FrontRightDrive";
    public static final String REAR_LEFT_DRIVE_NAME = "RearLeftDrive";
    public static final String REAR_RIGHT_DRIVE_NAME = "RearRightDrive";
    public static final String LEFT_VERTICAL_ODOMETER_NAME = "LeftVerticalOdometer";
    public static final String RIGHT_VERTICAL_ODOMETER_NAME = "RightVerticalOdometer";
    public static final String HORIZONTAL_ODOMETER_NAME = "HorizontalOdometer";

    // drive system
    public static final double DRIVE_SPEED_REDUCTION_DISTANCE_INCHES = 0;
    public static final double DRIVE_MIN_REDUCED_SPEED = 0.2;
    public static final double DRIVE_OFFSET_TOLERANCE_INCHES = 5000;
    public static final double DRIVE_OFFSET_TOLERANCE_DEGREES = 20;

    //Odometry
    public static final double ODOMETER_TICKS_TO_INCHES = 0.00090744101;
    public static final double TURN_CORRECTION_INTENSITY = 0.1;
    // can be calibrated further for precision
    public static final double HORIZONTAL_ODOMETER_ROTATION_TO_HORIZONTAL_TICK_OFFSET = 1.15;
    public static final double VERTICAL_ODOMETER_TICKS_TO_RADIANS = 9212.34563283;

}
