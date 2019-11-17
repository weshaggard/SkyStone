package teamcode.common;

import com.qualcomm.robotcore.hardware.Servo;

public class Utils {

    private Utils() {
    }

    public static boolean servoNearPosition(Servo servo, double position, double errorTolerance) {
        errorTolerance = Math.abs(errorTolerance);
        Debug.log("pos = " + servo.getPosition() + ", target = " + position);
        return Math.abs(servo.getPosition() - position) <= errorTolerance;
    }

}
