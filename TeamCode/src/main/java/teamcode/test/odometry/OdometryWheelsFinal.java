package teamcode.test.odometry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
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

    private final double TICKS_TO_CENTIMETERS = 1102 / 2.54;
    private final double INCHES_TO_CENTIMETERS = 1/2.54;
    public boolean active = true;
    private double time;

    /**
     * Constructs an odometryWheel object
     * @param opMode the current opMode the program is running
     * @param globalRobotPosition the point the robot is on in CM as an X,Y point
     * @param startingAngle the starting angle of the robot in DEGREES, everything is handled inside this class as Radians however, Note that this is also a direction
     */
    public OdometryWheelsFinal(final AbstractOpMode opMode, Point globalRobotPosition, double startingAngle){
        time = opMode.getRuntime();
        HardwareMap hardwareMap = opMode.hardwareMap;
        yOdoWheel = hardwareMap.get(DcMotor.class, "HorizontalOdometer");
        rightOdoXWheel = hardwareMap.get(DcMotor.class, "RightVerticalOdometer");
        leftOdoXWheel = hardwareMap.get(DcMotor.class, "LeftVerticalOdometer");
        this.globalRobotPosition = globalRobotPosition;
        this.motion = DriveSystem.DriveMotion.STOP;
        currentGlobalDirection = Math.toRadians(startingAngle);
        Thread update = new Thread(){
            @Override
            public void run(){
                while(opMode.opModeIsActive()){
                    update();
                }
            }
        };
        update.start();
    }

    /**
     * Updates the globalPosition and direction of the robot in Radians
     * Port 0: yOdoWheel (FrontLeftDrive)
     * Port 1: RightOdoWheelX (FrontRightDrive)
     * Port 2: LeftOdoWheelX (RearLeftDrive)
     */
    public void update(){
        Debug.log("Here");
        currentEncoderLeft = leftOdoXWheel.getCurrentPosition();
        currentEncoderRight = rightOdoXWheel.getCurrentPosition();
        currentEncoderY = yOdoWheel.getCurrentPosition();
        deltaLeft = currentEncoderLeft - previousEncoderLeft;
        deltaRight = currentEncoderRight - previousEncoderRight;
        deltaY = currentEncoderY - previousEncoderY;
        double deltaCentimetersX = (deltaLeft + deltaRight) / 2 * Constants.TICKS_PER_CENTIMETER;
        double deltaCentimetersY = deltaY * Constants.TICKS_PER_CENTIMETER;
        if(deltaLeft != deltaRight){
            //arc based case
            double arcLengthRatio = (double)Math.min(deltaLeft, deltaRight) / (double)Math.max(deltaLeft, deltaRight);
            double radius = 2 * Constants.ODOMETRY_DISTANCE_TO_CENTER  * arcLengthRatio/ (1 - arcLengthRatio);
            double theta = Math.min(deltaLeft, deltaRight) * 2 * Math.PI / 2 * Math.PI * radius;
            currentGlobalDirection = angleWrapper(theta + currentGlobalDirection);
            deltaCentimetersX = radius - radius * Math.cos(currentGlobalDirection);
            deltaCentimetersY = Math.sqrt(2 * radius * deltaCentimetersX - Math.pow(deltaCentimetersX, 2));
        }
        if(deltaCentimetersX == 0) {
            //rotational case, note that this is pivoted around the center so the change in position is ZERO
            if(deltaRight > 0){
                //rotating positve theta
                double theta = deltaRight * TICKS_TO_CENTIMETERS / Constants.ODOMETRY_DISTANCE_TO_CENTER;
                currentGlobalDirection = angleWrapper(currentGlobalDirection + theta);
            }else{
                //rotating negative theta
                double theta = deltaLeft * TICKS_TO_CENTIMETERS / Constants.ODOMETRY_DISTANCE_TO_CENTER;
                currentGlobalDirection = angleWrapper(currentGlobalDirection - theta);
            }
            //double absoluteAngle = Math.atan2(deltaCentimetersY, deltaCentimetersX);
            //double deltaAngle = angleWrapper(absoluteAngle - currentGlobalDirection);
            //global position remains unchanged
        }
        if(currentGlobalDirection > Math.toRadians(90) && currentGlobalDirection < Math.toRadians(180)){
            //case for misoriented chassis in quadrant 2
            globalRobotPosition = new Point(globalRobotPosition.x - deltaCentimetersY, globalRobotPosition.y + deltaCentimetersX);
        }else if(currentGlobalDirection > Math.toRadians(-180) && currentGlobalDirection < Math.toRadians(-90)){
            //case for quadrant 3
            globalRobotPosition = new Point(globalRobotPosition.x - deltaCentimetersX, globalRobotPosition.y - deltaCentimetersY);
        }else if(currentGlobalDirection < 0 && currentGlobalDirection > Math.toRadians(-90)){
            //case for quadrant 4
            globalRobotPosition = new Point(globalRobotPosition.x + deltaCentimetersY, globalRobotPosition.y - deltaCentimetersX);
        }else {
            //ideal case, robots direction of motion is in Quadrant 1
            globalRobotPosition = new Point(globalRobotPosition.x + deltaCentimetersX, globalRobotPosition.y + deltaCentimetersY);
        }
        //currentGlobalDirection = angleWrapper(deltaAngle + currentGlobalDirection);
        previousEncoderLeft = currentEncoderLeft;
        previousEncoderRight = currentEncoderRight;
        previousEncoderY = currentEncoderY;
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
