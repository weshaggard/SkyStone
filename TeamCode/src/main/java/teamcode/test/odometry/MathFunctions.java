package teamcode.test.odometry;

public class MathFunctions {

    /**
     * Keeps the angle within a certian domain to avoid redundant rotation
     * @param angle the angle in radians
     * @return
     */
    public static double angleWrap(double angle){
        // System.out.println("here out");
        while(angle < Math.toRadians(-180)){
            angle += Math.toRadians(360);
        }

        while(angle > Math.toRadians(180)){
            angle -= Math.toRadians(360);
        }

        return angle;
    }

}
