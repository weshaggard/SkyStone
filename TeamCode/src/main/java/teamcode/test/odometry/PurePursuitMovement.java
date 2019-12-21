package teamcode.test.odometry;


import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import teamcode.common.Point;
import teamcode.test.odometry.CurvePoint;
import teamcode.test.odometry.MathFunctions;
import teamcode.test.odometry.MovementVars;
import teamcode.test.odometry.OdometryWheelsFinal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Math.*;
import static java.lang.Math.pow;

public class PurePursuitMovement {

    private static final double DISTANCE_TOLERANCE = 4;
    private static final double Y_POINT_TOLERANCE_INTERSECT = 0.003;
    private static final double X_POINT_TOLERANCE_INTERSECT = 0.003;

    private OdometryWheelsFinal wheels;

    private static String debugConglomerate = "";
    private double DISTANCE_TOLERANCE_X = 10;
    private  double DISTANCE_TOLERANCE_Y = 10;

    //private ArrayList<Point> visitedPoints = new ArrayList();
    //private CurvePoint currentPoint;
    private CurvePoint previousFollowMe;

    public PurePursuitMovement(OdometryWheelsFinal wheels){
        this.wheels = wheels;
    }


    public CurvePoint getFollowPointPath(ArrayList<CurvePoint> path, Point robotLocation, double followRadius) throws FileNotFoundException {
        CurvePoint followMe = new CurvePoint(path.get(0));
        for(int i = 0; i < path.size() - 1; i++){
            CurvePoint startLine = path.get(i);
            CurvePoint endLine = path.get(i + 1);
            //ArrayList<Point> intersections = getCircleLineIntersectionPoint(startLine.toPoint(), endLine.toPoint(),robotLocation,followRadius);
            ArrayList<Point> intersections = lineCircleIntersection(robotLocation, followRadius,startLine.toPoint(), endLine.toPoint());
            double closestAngle = 100000000;
            for(Point currentIntersection: intersections) {
                double angle = atan2(currentIntersection.y - wheels.getGlobalRobotPosition().y, currentIntersection.x - wheels.getGlobalRobotPosition().x);
                double deltaAngle = abs(MathFunctions.angleWrap(angle - wheels.getWorldAngleRads()));
                if(deltaAngle < closestAngle){
                    closestAngle = deltaAngle;
                    followMe.setPoint(currentIntersection);
                }
            }
        }



        return followMe;
    }

    double CENTIMETER_TOLERANCE = 3;
    private boolean isNearVisitedPoint(CurvePoint previous, CurvePoint followMe) {
        double followX = followMe.x;
        double followY = followMe.y;
        double x = previous.x;
        double y = previous.y;
        if((followX < x + CENTIMETER_TOLERANCE && followX > x) && (followY < y + CENTIMETER_TOLERANCE && followY > y) || (followX > x - CENTIMETER_TOLERANCE && followX < x) && (followY > y - CENTIMETER_TOLERANCE && followY < y) || (followX < x + CENTIMETER_TOLERANCE && followX > x) && (followY < y - CENTIMETER_TOLERANCE && followY > y) || (followX > x - CENTIMETER_TOLERANCE && followX < x) && (followY > y - CENTIMETER_TOLERANCE && followY < y)){
            return true;
        }
        return false;

    }

    //TODO implement a method to stop oscillation at the end of the path
    //TODO using a list of path points, figure out where I am in the path using perpendicular lines. Timestamp 15:00 in the GF tutorial

    public Point getPositionInPath(ArrayList<CurvePoint> pathPoints){

        for(int i = 0; i < pathPoints.size() - 1; i++){
            CurvePoint current = pathPoints.get(i);
            if(i + 1 < pathPoints.size()) {
                CurvePoint next = pathPoints.get(i + 1);
                Point nextPoint = next.toPoint();
                Point currentPoint = current.toPoint();
                double slope = currentPoint.slope(nextPoint);
                double perpendicularSlope = 1.0 / slope;
                Point perpendicularPoint = new Point(wheels.getGlobalRobotPosition().x + 1, perpendicularSlope + wheels.getGlobalRobotPosition().y);
            }else{
                if(robotIsNearPoint(current)) {
                    return current.toPoint();
                }else{
                    return null;
                }
            }
        }
        return null;
    }

    private boolean robotIsNearPoint(CurvePoint current) {
        Point currentPoint = current.toPoint();
        return abs(currentPoint.x - wheels.getGlobalRobotPosition().x) < DISTANCE_TOLERANCE_X && abs(currentPoint.y - wheels.getGlobalRobotPosition().y) < DISTANCE_TOLERANCE_Y;
    }

    private boolean isLastPoint(ArrayList<CurvePoint> path, CurvePoint current){
        return path.indexOf(current) == path.size() -1;
    }

    //To be implemented into the new Drive System for league 3

    /**
     *
     * @param allPoints path the robot is pursuing
     * @param followAngle angle the robot should stick to in degrees
     */
    public void followCurve(ArrayList<CurvePoint> allPoints, double followAngle) throws FileNotFoundException {

        //for(int i = 0; i < allPoints.size() - 1; i++){
            //ComputerDebugging.sendLine(new FloatPoint(allPoints.get(i).x, allPoints.get(i).y),new FloatPoint(allPoints.get(i+1).x, allPoints.get(i+1).y));
        //}
        Point secondToLastPoint = allPoints.get(allPoints.size() - 2).toPoint();
        Point lastPoint = allPoints.get(allPoints.size() - 1).toPoint();
        Line lastLine;
        if(secondToLastPoint.x < lastPoint.x){
            lastLine = new Line(secondToLastPoint, lastPoint, true);
        }else{
            lastLine = new Line(secondToLastPoint, lastPoint, false);
        }

        double theta = lastLine.getAngleRads();

        //System.out.println(toDegrees(theta));
        DISTANCE_TOLERANCE_X = allPoints.get(allPoints.size() - 1 ).followDistance * cos(theta);
        DISTANCE_TOLERANCE_Y = allPoints.get(allPoints.size() - 1).followDistance * sin(theta);
        for(int i = 0; i < allPoints.size(); i++) {
            //System.out.println("X: " + Robot.worldXPosition);
            //System.out.println("Y: " + Robot.worldYPosition);
            CurvePoint followMe;
            if(robotIsNearPoint(allPoints.get(allPoints.size() - 1))){
                followMe = allPoints.get(allPoints.size() - 1);
            }else {
                followMe = getFollowPointPath(allPoints, wheels.getGlobalRobotPosition(), allPoints.get(0).followDistance);
            }
            goToPosition(followMe.x, followMe.y, followMe.moveSpeed, followAngle, followMe.turnSpeed);
            //assumes all followDistance is the same, gotta make a findPositionInPath method
            //System.out.println("X Tolerance: " + DISTANCE_TOLERANCE_X);
            //System.out.println("Y Tolerance: " + DISTANCE_TOLERANCE_Y);
            //System.out.println("X: " + abs(getLastPoint(allPoints.get(allPoints.size() - 1).followDistance,allPoints).x - Robot.worldXPosition));
            //System.out.println("Y: " + abs(getLastPoint(allPoints.get(allPoints.size() - 1).followDistance,allPoints).y - Robot.worldYPosition));
            //System.out.println(getLastPoint(allPoints.get(allPoints.size() - 1).followDistance, allPoints).toPoint());
            //System.out.println(new Point(Robot.worldXPosition, Robot.worldYPosition));
            if(robotIsNearPoint(getLastPoint(allPoints.get(allPoints.size() - 1).followDistance, allPoints))){
                //need to find the actual position in the path to avoid ambiguity
                brake();
            }
            //System.out.println("x: " + followMe.x);
            //System.out.println("y: " + followMe.y);

        }
    }

    public CurvePoint getLastPoint(double followRadius, ArrayList<CurvePoint> allPoints){
        Point secondToLastPoint = allPoints.get(allPoints.size() - 2).toPoint();
        Point lastPoint = allPoints.get(allPoints.size() - 1).toPoint();
        Line lastLine;
        if(secondToLastPoint.x < lastPoint.x){
            lastLine = new Line(secondToLastPoint, lastPoint, true);
        }else{
            lastLine = new Line(secondToLastPoint, lastPoint, false);
        }
        double theta = lastLine.getAngleRads();
        double sin = sin(theta);
        double cos = cos(theta);
        CurvePoint endPoint;
        if(lastLine.leftToRight) {
            endPoint = new CurvePoint(lastPoint.x + followRadius * cos, lastPoint.y + followRadius * sin, allPoints.get(0).moveSpeed, allPoints.get(0).turnSpeed, allPoints.get(0).followDistance, allPoints.get(0).slowDownTurnRads, allPoints.get(0).slowDownTurnAmount);

        }else{
            endPoint = new CurvePoint(lastPoint.x - followRadius * cos, lastPoint.y - followRadius * sin, allPoints.get(0).moveSpeed, allPoints.get(0).turnSpeed, allPoints.get(0).followDistance, allPoints.get(0).slowDownTurnRads, allPoints.get(0).slowDownTurnAmount);
        }

        return endPoint;
    }

    private void brake() {
        MovementVars.xSpeed = 0;
        MovementVars.ySpeed = 0;
        MovementVars.turnSpeed = 0;
    }

    /**
     * moves to any point on the field with a defined coordinate plane
     * @param x The X coordinate to be moved to
     * @param y the Y coordinate to be moved to
     * @param power full power the robot should be at
     * @param preferredAngle angle the robot should be moving at in degrees
     */
    public void goToPosition(double x, double y, double power, double preferredAngle, double turnPower){
        //preferredAngle += 45;
        preferredAngle = toRadians(preferredAngle);

        double deltaX = x - wheels.getGlobalRobotPosition().x;

        double deltaY = y - wheels.getGlobalRobotPosition().y;
        double distanceTravelled = hypot(deltaX, deltaY);
        //change in robots position throughout this function
        double absoluteAngle = atan2(deltaY, deltaX);
        //angle of motion
        double relativeAngle = MathFunctions.angleWrap(absoluteAngle - (wheels.getWorldAngleRads() - Math.toRadians(90)));
        //change in angle
        //change in the robots position
        double relativeDistanceX = distanceTravelled * cos(relativeAngle);
        double relativeDistanceY = distanceTravelled * sin(relativeAngle);
        //relative distance the robot is travelling
        double powerX = relativeDistanceX / (abs(relativeDistanceX) + abs(relativeDistanceY));
        double powerY = relativeDistanceY / (abs(relativeDistanceX) + abs(relativeDistanceY));
        //power calculations
        MovementVars.xSpeed = powerX * power;
        MovementVars.ySpeed = powerY * power;
        //visitedPoints.add(new Point(x,y));
        //assigning power to driveTrain
        double relativeTurnAngle = relativeAngle - toRadians(180) + preferredAngle;
        MovementVars.turnSpeed = Range.clip(relativeTurnAngle / toRadians(30), -1, 1) * turnPower;
        if(distanceTravelled < DISTANCE_TOLERANCE){
            MovementVars.turnSpeed = 0;
        }


    }


    int NaNCount = 0;
    int notNaN = 0;
    private ArrayList<Point> lineCircleIntersection(Point circleCenter, double radius, Point linePoint1, Point linePoint2) throws FileNotFoundException {
        //(mx+b)^2 = r^2 + x^2
        //System.out.println(circleCenter.x);
        //System.out.println(circleCenter.y);
        if(Math.abs(linePoint1.y - linePoint2.y) < Y_POINT_TOLERANCE_INTERSECT){
            linePoint1.y = linePoint2.y + Y_POINT_TOLERANCE_INTERSECT;
        }
        if(Math.abs(linePoint1.x - linePoint2.x) < X_POINT_TOLERANCE_INTERSECT){
            linePoint1.x = linePoint2.x + X_POINT_TOLERANCE_INTERSECT;
        }
        double slope1 = linePoint2.slope(linePoint1);


        double x1 = linePoint1.x - circleCenter.x;
        double y1 = linePoint1.y - circleCenter.y;
        double x2 = linePoint2.x - circleCenter.x;
        double y2 = linePoint2.y - circleCenter.y;
        //Defines everything in terms of the Circle center by offsetting it which is added back later, this simplifies the math

        double a =  pow(slope1, 2) + 1.0;
        //double b = 2 * line.slope * line.yIntercept;
        double b = (2 * slope1 * y1) - (2 * pow(slope1,2) * x1);

        //double c = pow((linePoint2.y - linePoint2.x * slope1), 2) + pow(radius, 2);
        double c = (pow(slope1,2) * pow(x1, 2)) - (2.0 * slope1 * y1 * x1) + pow(y1,2) - pow(radius, 2);

        ArrayList<Point> allPoints = new ArrayList();
        try{
//            if(pow(b,2) - 4.0 * a * c < 0){
//                NaNCount++;
//                System.out.println("NaN: " + NaNCount);
//                PrintStream ps = new PrintStream(new File("log2"));
//                debugConglomerate += "center: " + circleCenter + "\n" + "radius: " + radius + "\n" + "" + "point1: " + linePoint1 + "\n" + "point2: " + linePoint2 + "\n" + "A: " + a + "\n" + "B: " + b + "\n" + "C: " + c + "\n";
//                ps.print(debugConglomerate);
//            }else{
//                notNaN++;
//                System.out.println("notNaN: " + notNaN);
//            }
            double discriminant = sqrt((pow(b,2) - 4.0 * a * c));
            //System.out.println(discriminant);
            double xRoot1 = (-b + discriminant) / (2 *a);
            double xRoot2 = (-b - discriminant) / (2 *a);

            double yRoot1 = slope1 * (xRoot1 - x1) + y1;
            double yRoot2 = slope1 * (xRoot2 - x1) + y1;

            //undo the offset from above
            xRoot1 += circleCenter.x;
            yRoot1 += circleCenter.y;
            xRoot2 += circleCenter.x;
            yRoot2 += circleCenter.y;

            double minX = linePoint1.x < linePoint2.x ? linePoint1.x: linePoint2.x;
            double maxX = linePoint1.x > linePoint2.x ? linePoint1.x: linePoint2.x;
            //System.out.println(minX);
            //System.out.println(maxX);
            //System.out.print(xRoot1 + " ");
            //System.out.println(yRoot1);
            //System.out.print(xRoot2 + " ");
            //System.out.println(yRoot2);
            if(xRoot1 > minX && xRoot1 < maxX){
                allPoints.add(new Point(xRoot1, yRoot1));
            }
            if(xRoot2 > minX && xRoot2 < maxX){
                allPoints.add(new Point(xRoot2, yRoot2));
            }
            if(allPoints.contains(new Point(0,0))){
                //System.out.println("weird");
                //System.out.println(allPoints.toString());
                //System.out.println(xRoot1);
                //System.out.println(discriminant);
                //System.exit(69);
            }


        }catch(ArithmeticException e){
        }
        return allPoints;

    }

    public static ArrayList<Point> getCircleLineIntersectionPoint(Point pointA, Point pointB, Point center, double radius) throws FileNotFoundException{
        ArrayList<Point> intersections = new ArrayList<>();
        double baX = pointB.x - pointA.x;
        double baY = pointB.y - pointA.y;
        double caX = center.x - pointA.x;
        double caY = center.y - pointA.y;

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;

        if (disc < 0) {
            return intersections;
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point p1 = new Point(pointA.x - baX * abScalingFactor1, pointA.y
                - baY * abScalingFactor1);
        intersections.add(p1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return intersections;
        }
        Point p2 = new Point(pointA.x - baX * abScalingFactor2, pointA.y
                - baY * abScalingFactor2);
        intersections.add(p2);
        return intersections;
    }


}
