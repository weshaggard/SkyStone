package teamcode.test.odometry;

import teamcode.common.Point;

public class Line {
    boolean leftToRight;
    Point p1;
    Point p2;
    double slope;


    /**
     *
     * @param p1 the point leftward on the x axis
     * @param p2 the point rightward on rhe x axis
     * @param leftToRight determines if the line's slope is meant to be interpreted left to right or right to left
     */
    public Line(Point p1, Point p2, boolean leftToRight){
        this.p1 = p1;
        this.p2 = p2;
        this.leftToRight = leftToRight;
        slope = p2.slope(p1);
        if(!leftToRight){
            slope *= -1;
        }
    }

    /**
     *
     * @return returns the y intercept of the line, note that this only functions if you have the line being interpreted left to right
     */
    public double getyIntercept(){
        return p1.y - (slope * p1.x);
    }

    /**
     *
     * @return the angle that is created by the line in Radians
     */
    public double getAngleRads(){
        return Math.atan2(Math.abs(p1.y - p2.y),  Math.abs(p1.x - p2.x));
    }




}
