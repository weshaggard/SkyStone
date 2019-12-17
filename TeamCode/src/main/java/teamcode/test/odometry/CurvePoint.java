package teamcode.test.odometry;

import teamcode.common.Point;

public class CurvePoint {
    double x;
    double y;
    double moveSpeed;
    double turnSpeed;
    double followDistance;
    double pointLength;
    double slowDownTurnRads;
    //slows down robot when overshot
    double slowDownTurnAmount;

    public CurvePoint(double x, double y, double moveSpeed, double turnSpeed, double followDistance, double slowDownTurnDegrees, double slowDownTurnAmount){
        this.x = x;
        this.y = y;
        this.moveSpeed = moveSpeed;
        this.turnSpeed = turnSpeed;
        this.followDistance = followDistance;
        //this.pointLength = pointLength;
        this.slowDownTurnRads = slowDownTurnDegrees;
        this.slowDownTurnAmount = slowDownTurnAmount;
    }

    public CurvePoint(CurvePoint thisPoint){
        x = thisPoint.x;
        y = thisPoint.y;
        moveSpeed = thisPoint.moveSpeed;
        turnSpeed = thisPoint.turnSpeed;
        followDistance = thisPoint.followDistance;
        //pointLength = thisPoint.pointLength;
        slowDownTurnRads = thisPoint.slowDownTurnRads;
        slowDownTurnAmount = thisPoint.slowDownTurnAmount;
    }

    public Point toPoint(){
        return new Point(this.x, this.y);
    }

    public void setPoint(Point point) {
        x = point.x;
        y = point.y;
    }

    public String toString(){
        return "(" + this.x + "," + this.y + ")";
    }
}
