package teamcode.league3;

public class Constants {

    // hardware device names
    public static final String FRONT_LEFT_DRIVE = "FrontLeftDrive";
    public static final String FRONT_RIGHT_DRIVE = "FrontRightDrive";
    public static final String REAR_LEFT_DRIVE = "RearLeftDrive";
    public static final String REAR_RIGHT_DRIVE = "RearRightDrive";
    public static final String LEFT_VERTICAL_ODOMETER = "LeftVerticalOdometer";
    public static final String RIGHT_VERTICAL_ODOMETER = "RightVerticalOdometer";
    public static final String HORIZONTAL_ODOMETER = "HorizontalOdometer";
    public static final String ARM_LINEAR_EXTENSION = "LinearExtension"; //motor that controls the slide for the arm
    public static final String ARM_WINCH = "Winch";
    public static final String ARM_CLAW = "Claw";
    public static final String LEFT_INTAKE = "LeftIntake";
    public static final String RIGHT_INTAKE = "RightIntake";
    public static final String INTAKE_SENSOR = "IntakeSensor";
    public static final String LEFT_FOUNDATION_GRABBER = "LeftFoundationGrabber";
    public static final String RIGHT_FOUDNATION_GRABBER = "RightFoundationGrabber";


    public static final double DRIVE_VERTICAL_INCHES_TO_TICKS = 1102;
    public static final int DRIVE_TICK_ERROR_TOLERANCE = 500;

    //to be calibrated
    public static final double DRIVE_LATERAL_INCHES_TO_TICKS = 551;
    public static final double DRIVE_DEGREES_TO_TICKS = 160;

    //arm constants
    public static final double RAPIER_INCHES_TO_TICKS = 322.60869565217;
    public static final double WINCH_TICKS_TO_INCHES = 140.9333333333; //uncalibrated



    //Speed constants mostly for odometry but to potentially be used for auto
    public static final double TURN_SPEED = 0.5;
    public static final double LATERAL_SPEED = 0.5;
    public static final double VERTICAL_SPEED = 0.5;
    public static final double WINCH_INCHES_TO_TICKS = 146.972519894;

    public static final double CLAW_OPEN_POSITION = 0;

}
