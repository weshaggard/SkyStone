package teamcode.test.odometry;

import java.util.ArrayList;

import teamcode.common.Debug;
import teamcode.common.Point;
import teamcode.common.Vector2D;
import teamcode.league3.Constants;
import teamcode.league3.DriveSystem;

public class PurePursuitSimple {
    private ArrayList<Point> pointsVisited;
    private OdometryWheelsFinal wheels;
    private DriveSystem driveSystem;

    public PurePursuitSimple(OdometryWheelsFinal wheels, DriveSystem driveSystem){
        this.wheels = wheels;
        this.driveSystem = driveSystem;
    }


    public  void followCurve(ArrayList<Point> path, double power){
        for(int i = 0; i < path.size() - 1; i++){

            goToPosistion(path.get(i), path.get(i + 1), power);
        }

    }
    public void goToPosistion(Point startPoint, Point endPoint, double power){
        double deltaX = endPoint.x - startPoint.x;
        double deltaY = endPoint.y - startPoint.y;
        double distanceHypotenuse = Math.hypot(deltaY, deltaX);
        double xPower = deltaX / distanceHypotenuse * power;
        double yPower = deltaY / distanceHypotenuse * power;
        double absoluteAngleRads = Math.atan2(deltaY, deltaX);
        //double relativeAngleRads = wheels.angleWrapper(absoluteAngleRads - wheels.currentGlobalDirection);
        //driveSystem.rotate(Math.toDegrees(relativeAngleRads), Constants.TURN_SPEED);
        Vector2D velocity = new Vector2D(yPower, xPower);
        while(!driveSystem.nearTarget()){
            driveSystem.continuous(velocity, 0);

        }
        driveSystem.brake();
    }
}
