package teamcode.league3;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import teamcode.common.Vector2D;
import teamcode.common.Vector3D;

public class Vision {

    private static final String VUFORIA_KEY = "AQR2KKb/////AAABmcBOjjqXfkjtrjI9/Ps5Rs1yoVMyJe0wdjaX8pHqOaPu2gRcObwPjsuWCCo7Xt52/kJ4dAZfUM5Gy73z3ogM2E2qzyVObda1EFHZuUrrYkJzKM3AhY8vUz6R3fH0c/R9j/pufFYAABOAFoc5PtjMQ2fbeFI95UYXtl0u+6OIkCUJ3Zw71tvoD9Fs/cOiLB45FrWrxHPbinEhsOlCTWK/sAC2OK2HuEsBFCebaV57vKyATHW4w2LMWEZaCByHMk9RJDR38WCqivXz753bsiBVMbCzPYzwzc3DKztTbK8/cXqPPBLBKwU8ls0RN52akror1xE9lPwwksMXwJwolpyIZGnZngWcBWX4lLH+HlDNZ8Qm";
    private static final String ASSET_NAME = "Skystone";

    public enum VisionSource {
        PHONE, WEBCAM
    }

    private final HardwareMap hardwareMap;
    private VuforiaTrackables trackables;

    public Vision(HardwareMap hardwareMap, VisionSource source) {
        this.hardwareMap = hardwareMap;
        createVuforia(source);
    }

    private void createVuforia(VisionSource source) {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().
                getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        // the default camera name points to the phone
        if (source == VisionSource.WEBCAM) {
            parameters.cameraName = hardwareMap.get(CameraName.class, "webcam");
        }

        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters);
        trackables = vuforia.loadTrackablesFromAsset(ASSET_NAME);
        trackables.activate();
    }

    /**
     * Returns the "top-down" position of a detected SkyStone.
     */
    public Vector2D getSkystonePosition() {
        VuforiaTrackable skystone = trackables.get(0);
        OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) skystone.getListener()).getPose();
        if (pose == null) {
            return null;
        }
        VectorF position = pose.getTranslation();
        double x = position.get(0);
        double y = position.get(1);
        return new Vector2D(x, y);
    }

}
