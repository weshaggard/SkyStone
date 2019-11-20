package teamcode.opModes.Odometry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.common.Vector2D;

public class SecondDraftOdometryWheels {
    private final DcMotor xWheel;
    private final DcMotor yWheel;
    private final long apex;
    private final double INCHES_FROM_CENTER_X;
    private Vector2D current;

    private int encoderValueX;
    private int previousEncoderValueX;

    private int encoderValueY;
    private int previousEncoderValueY;

    public SecondDraftOdometryWheels(HardwareMap hardwareMap, double inches){
        xWheel = hardwareMap.get(DcMotor.class, "Odometry X Wheel");
        yWheel = hardwareMap.get(DcMotor.class, "Odometry Y Wheel");
        apex = System.currentTimeMillis();
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

    public void goToPosistion(double deltaX, double deltaY, double desiredOrientation, double power){
        Vector2D distanceVector = new Vector2D(deltaX, deltaY);

    }


    /*
    input a ideal robot path, omega(w) and velocity V
    gives ideal odometry wheel values, convert to ticks using a tick constant inverted
    met by drive wheel movement unitl Odometry wheel satisfied.  while(!odometryWheel.nearTarget());
    Drive wheel directions and velocities will give us a movement in some x y w direction
    increase the scalar quantity of each drive wheel quantity by some scalar

     */

}
