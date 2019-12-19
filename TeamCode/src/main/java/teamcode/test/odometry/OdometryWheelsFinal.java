package teamcode.test.odometry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.AbstractOpMode;
import teamcode.common.Point;
import teamcode.league3.Constants;
import teamcode.league3.DriveSystem;

public class OdometryWheelsFinal {

    //wheels
    public final DcMotor leftOdoXWheel;
    public final DcMotor rightOdoXWheel;
    public final DcMotor yOdoWheel;

    //information about the robot's global position
    private static Point globalRobotPosition;
    //currentGlobalDirection is in Radians
    private double currentGlobalDirection;


    //information about the driveSystem
    private DriveSystem driveSystem;
    private DriveSystem.DriveMotion motion;

    //current encoder values
    private int currentEncoderLeft;
    private int currentEncoderRight;
    private int currentEncoderY;

    //previous encoder values
    private int previousEncoderLeft;
    private int previousEncoderRight;
    private int previousEncoderY;


    //change in encoder values, may not need to be a field
    private int deltaLeft;
    private int deltaRight;
    private int deltaY;

    private final double TICKS_TO_CENTIMETERS = 1102 * 2.54;
    private final double INCHES_TO_CENTIMETERS = 1/2.54;


    /**
     * Constructs an odometryWheel object
     * @param opMode the current opMode the program is running
     * @param globalRobotPosition the point the robot is on in CM as an X,Y point
     * @param driveSystem the DriveSystem being used during the opMode
     * @param startingAngle the starting angle of the robot in DEGREES, everything is handled inside this class as Radians however
     */
    public OdometryWheelsFinal(AbstractOpMode opMode, Point globalRobotPosition, DriveSystem driveSystem, double startingAngle){
        HardwareMap hardwareMap = opMode.hardwareMap;
        leftOdoXWheel = hardwareMap.get(DcMotor.class, "LeftOdoWheelX");
        rightOdoXWheel = hardwareMap.get(DcMotor.class, "rightOdoWheelX");
        yOdoWheel = hardwareMap.get(DcMotor.class, "yOdoWheel");
        this.globalRobotPosition = globalRobotPosition;
        this.driveSystem = driveSystem;
        this.motion = DriveSystem.DriveMotion.STOP;
        currentGlobalDirection = Math.toRadians(startingAngle);
    }

    //in addition to tracking the robot, you are also correcting for all form of manipulation
    public void update(){
            deltaLeft = currentEncoderLeft - previousEncoderLeft;
            deltaRight = currentEncoderRight - previousEncoderRight;
            deltaY = currentEncoderY - previousEncoderY;
            double deltaCentimetersX = (deltaLeft + deltaRight) / 2 * TICKS_TO_CENTIMETERS;
            double deltaCentimetersY = deltaY * TICKS_TO_CENTIMETERS;
            double absoluteAngle = Math.atan2(deltaCentimetersY, deltaCentimetersX);
            double deltaAngle = angleWrapper(absoluteAngle - currentGlobalDirection);
            globalRobotPosition = new Point(globalRobotPosition.x + deltaCentimetersX, globalRobotPosition.y + deltaCentimetersY);
            driveSystem.rotate(deltaAngle, Constants.TURN_SPEED);
    }

    //ensures the angle is between -180 and 180 degrees while keeping it in radians
    public double angleWrapper(double angle){
        while(angle > Math.toRadians(180)){
            angle -= Math.toRadians(360);
        }
        while(angle < Math.toRadians(180)){
            angle += Math.toRadians(360);
        }
        return angle;
    }

    public void updateDriveMotion(DriveSystem.DriveMotion motion){
        this.motion = motion;
    }


    public boolean nearTarget(){
        return Math.abs(leftOdoXWheel.getCurrentPosition() - leftOdoXWheel.getTargetPosition()) < Constants.DRIVE_TICK_ERROR_TOLERANCE  &&
                Math.abs(rightOdoXWheel.getCurrentPosition() - leftOdoXWheel.getTargetPosition()) < Constants.DRIVE_TICK_ERROR_TOLERANCE &&
                Math.abs(yOdoWheel.getCurrentPosition() - yOdoWheel.getTargetPosition()) < Constants.DRIVE_TICK_ERROR_TOLERANCE;
    }

    public Point getGlobalRobotPosition(){
        return globalRobotPosition;
    }

    public double getWorldAngleRads(){
        return currentGlobalDirection;
    }



}
