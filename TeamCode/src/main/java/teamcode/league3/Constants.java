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
    public static final double DRIVE_SPEED_REDUCTION_THRESHOLD_INCHES = 48;
    public static final double DRIVE_TURN_SPEED_REDUCTION_THRESHOLD_RADIANS = 0.0523599;
    public static final double DRIVE_MIN_REDUCED_SPEED = 0.1;
    public static final double DRIVE_OFFSET_TOLERANCE_INCHES = 1;
    public static final double DRIVE_OFFSET_TOLERANCE_RADIANS = 0.0872665;

    // odometry
    public static final double ODOMETER_INCHES_TO_TICKS = 1102;
    public static final double TURN_CORRECTION_SPEED_MULTIPLIER = 1;
    public static final double MAX_TURN_CORRECTION_SPEED = 0.1;
    public static final double HORIZONTAL_ODOMETER_ROTATION_OFFSET_TICKS = 0.5;
    public static final double VERTICAL_ODOMETER_TICKS_TO_RADIANS = 9212.3456328;
}
