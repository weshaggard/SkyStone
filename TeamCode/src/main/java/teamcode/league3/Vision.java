package teamcode.league3;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import teamcode.common.Vector2D;

public class Vision {

    private static final String VUFORIA_KEY = "AQR2KKb/////AAABmcBOjjqXfkjtrjI9/Ps5Rs1yoVMyJe0wdjaX8pHqOaPu2gRcObwPjsuWCCo7Xt52/kJ4dAZfUM5Gy73z3ogM2E2qzyVObda1EFHZuUrrYkJzKM3AhY8vUz6R3fH0c/R9j/pufFYAABOAFoc5PtjMQ2fbeFI95UYXtl0u+6OIkCUJ3Zw71tvoD9Fs/cOiLB45FrWrxHPbinEhsOlCTWK/sAC2OK2HuEsBFCebaV57vKyATHW4w2LMWEZaCByHMk9RJDR38WCqivXz753bsiBVMbCzPYzwzc3DKztTbK8/cXqPPBLBKwU8ls0RN52akror1xE9lPwwksMXwJwolpyIZGnZngWcBWX4lLH+HlDNZ8Qm";
    private static final String ASSET_NAME = "Skystone";

    public enum VisionSource {
        PHONE, WEBCAM
    }

    /**
     * Rotations are clockwise from the perspective of behind the camera.
     */
    public enum CameraOrientation {
        ZERO, NINETY, ONE_HUNDRED_EIGHTY, TWO_HUNDRED_SEVENTY
    }

    private final CameraOrientation cameraOrientation;
    private final VuforiaTrackables trackables;

    public Vision(HardwareMap hardwareMap, VisionSource source, CameraOrientation cameraOrientation) {
        this.cameraOrientation = cameraOrientation;
        trackables = createTrackables(hardwareMap, source);
        trackables.activate();
    }

    private VuforiaTrackables createTrackables(HardwareMap hardwareMap, VisionSource source) {
        // monitoring displays camera input to the phone screen
        int cameraMonitorViewId = hardwareMap.appContext.getResources().
                getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        // the default camera name points to the phone
        if (source == VisionSource.WEBCAM) {
            WebcamName webcamName = hardwareMap.get(WebcamName.class, Constants.WEBCAM);
            parameters.cameraName = webcamName;
        }
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters);
        return vuforia.loadTrackablesFromAsset(ASSET_NAME);
    }

    /**
     * Returns the "top-down" position of a detected SkyStone relative to the position of the robot
     * this Vision was created..
     */
    public Vector2D getSkystonePosition() {
        VuforiaTrackable skyStone = trackables.get(0);
        OpenGLMatrix loc = ((VuforiaTrackableDefaultListener) skyStone.getListener()).getUpdatedRobotLocation();
        if (loc == null) {
            return null;
        }
        VectorF position = loc.getTranslation();
        double x;
        double y;
        switch (cameraOrientation) {
            case ZERO:
                x = -position.get(0);
                y = -position.get(1);
                break;
            case NINETY:
                x = -position.get(1);
                y = position.get(0);
                break;
            case ONE_HUNDRED_EIGHTY:
                x = position.get(0);
                y = position.get(1);
                break;
            case TWO_HUNDRED_SEVENTY:
                x = position.get(1);
                y = -position.get(0);
                break;
            default:
                x = 0;
                y = 0;
                break;
        }
        return new Vector2D(x, y);
    }

}
