package teamcode.test.odometry;

public class MathFunctions {

    /**
     * Keeps the angle within a certian domain to avoid redundant rotation
     * @param angle the angle in radians
     * @return
     */
    public static double angleWrap(double angle){
        // System.out.println("here out");
        angle = Math.toDegrees(angle);
        while(angle < -180){
            angle += 360;
        }

        while(angle > 180){
            angle -= 360;
        }

        return Math.toRadians(angle);
    }

}
