package teamcode.test.odometry;



import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;

import static java.lang.Math.*;

public class SecondDraftOdometryWheels {
    private static final double DISTANCE_TOLERANCE = 10;
    private static final double Y_POINT_TOLERANCE = 0.003;
    private static final double X_POINT_TOLERANCE = 0.003;
    private final DcMotor xWheelLeft;
    private final DcMotor xWheelRight;
    private final DcMotor yWheel;
    private final double apex;
    private final double INCHES_FROM_CENTER_X;
    private Vector2D current;

    private int encoderValueX;
    private int previousEncoderValueX;

    private int encoderValueY;
    private int previousEncoderValueY;

    private double globalXPositon;
    private double globalYPosition;
    private double globalDirection;
    //Inches
    //Fields to be moved into the drive System once this is fully implemented
    double xPower;
    double yPower;
    double turnPower;



    public SecondDraftOdometryWheels(AbstractOpMode opMode, double inches){
        HardwareMap hardwareMap = opMode.hardwareMap;
        xWheelLeft = hardwareMap.get(DcMotor.class, "Odometry Left X Wheel");
        xWheelRight = hardwareMap.get(DcMotor.class, "Odometry Right X Wheel");
        yWheel = hardwareMap.get(DcMotor.class, "Odometry Y Wheel");
        globalXPositon = 0;
        globalYPosition = 0;
        globalDirection = 0;
        apex = opMode.getRuntime();
        INCHES_FROM_CENTER_X = inches;
    }

    public void updateGlobalPosistion(){
        int distanceTravelledX = encoderValueX - previousEncoderValueX;
        int distanceTravelledY = encoderValueY - previousEncoderValueY;
        current = new Vector2D(distanceTravelledX, distanceTravelledY);
        double direction = current.getDirection();
        previousEncoderValueX = encoderValueX;
        previousEncoderValueY = encoderValueY;
    }

    /**
     * moves to any point on the field with a defined coordinate plane
     * @param x The X coordinate to be moved to
     * @param y the Y coordinate to be moved to
     * @param power full power the robot should be at
     * @param preferredAngle angle the robot should be moving at
     */
    public void goToPosition(double x, double y, double power, double preferredAngle, double turnPower){
        double deltaX = x - globalXPositon;
        double deltaY = y - globalYPosition;
        double distanceTravelled = pow(deltaX, 2) + pow(deltaY, 2);
        //change in robots position throughout this function
        double absoluteAngle = toDegrees(atan2(deltaY, deltaX));
        double relativeAngle = angleWrapper(absoluteAngle - globalDirection);
        //change in the robots position
        double relativeDistanceX = distanceTravelled * cos(relativeAngle);
        double relativeDistanceY = distanceTravelled * sin(relativeAngle);
        //relative distance the robot is travelling
        double powerX = relativeDistanceX / (abs(relativeDistanceX) + Math.abs(relativeDistanceY));
        double powerY = relativeDistanceY / (abs(relativeDistanceX) + Math.abs(relativeDistanceY));
        //power calculations
        xPower = powerX * power;
        yPower = powerY * power;
        //assigning power to driveTrain
        double relativeTurnAngle = relativeAngle - 180 + preferredAngle;
        this.turnPower = Range.clip(relativeTurnAngle / 30, -1, 1) * turnPower;
        if(distanceTravelled < DISTANCE_TOLERANCE){
            this.turnPower = 0;
        }
        globalDirection += relativeAngle;
        globalXPositon = x;
        globalYPosition = y;
        //updating robot position + orientation
    }

    /**
     * Asserts that the angle stays between 0 and 360
     * @param angle passed in angle
     * @return
     */
    private double angleWrapper(double angle){
        while(angle < -180){
            angle += 360;
        }

        while(angle > 180){
            angle -= 360;
        }

        return angle;
    }

    private ArrayList<Point> lineCircleIntersection(Point circleCenter, double radius, Point linePoint1, Point linePoint2){
        //(mx+b)^2 = r^2 + x^2
        if(Math.abs(linePoint1.y - linePoint2.y) < Y_POINT_TOLERANCE){
            linePoint1.y = linePoint2.y + Y_POINT_TOLERANCE;
        }
        if(Math.abs(linePoint1.x - linePoint2.x) < X_POINT_TOLERANCE){
            linePoint1.x = linePoint2.x + X_POINT_TOLERANCE;
        }

        double slope1 = linePoint2.slope(linePoint1);

        double x1 = linePoint1.x - circleCenter.x;
        double y1 = linePoint1.y - circleCenter.y;
        double x2 = linePoint2.x - circleCenter.x;
        double y2 = linePoint2.y - circleCenter.y;
        //simplifies the math

        double a =  1.0 + pow(slope1, 2);
        double b = (2 * slope1 * y1) - (2 * pow(slope1,2) * x1);
        double c = (pow(slope1,2) * pow(x1, 2)) - (2 * slope1 * y1 * x1) + pow(y1,2);
        return null;
    }


    /*
    input a ideal robot path, omega(w) and velocity V
    gives ideal odometry wheel values, convert to ticks using a tick constant inverted
    met by drive wheel movement unitl Odometry wheel satisfied.  while(!odometryWheel.nearTarget());
    Drive wheel directions and velocities will give us a movement in some x y w direction
    increase the scalar quantity of each drive wheel quantity by some scalar

     */

}
