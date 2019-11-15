package teamcode.robotComponents;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import teamcode.common.BoundingBox2D;
import teamcode.common.Debug;
import teamcode.common.Vector2D;

public class TTVisionVuforia {

    private static final String VUFORIA_KEY = "AQR2KKb/////AAABmcBOjjqXfkjtrjI9/Ps5Rs1yoVMyJe0wdjaX8pHqOaPu2gRcObwPjsuWCCo7Xt52/kJ4dAZfUM5Gy73z3ogM2E2qzyVObda1EFHZuUrrYkJzKM3AhY8vUz6R3fH0c/R9j/pufFYAABOAFoc5PtjMQ2fbeFI95UYXtl0u+6OIkCUJ3Zw71tvoD9Fs/cOiLB45FrWrxHPbinEhsOlCTWK/sAC2OK2HuEsBFCebaV57vKyATHW4w2LMWEZaCByHMk9RJDR38WCqivXz753bsiBVMbCzPYzwzc3DKztTbK8/cXqPPBLBKwU8ls0RN52akror1xE9lPwwksMXwJwolpyIZGnZngWcBWX4lLH+HlDNZ8Qm";
    private static final String ASSET_NAME = "RelicVuMark";
    public static final String LABEL_STONE = "Boring Boy";
    public static final String LABEL_SKYSTONE = "Extra Scory Point Boi";
    public static final String[] LABELS = {LABEL_STONE, LABEL_SKYSTONE};
    private static final double MINIMUM_CONFIDENCE = 0.75;
    private static final String WEBCAM_NAME = "Webcam1";

    private VuforiaTrackables trackables;

    public TTVisionVuforia(HardwareMap hardwareMap) {
        this(hardwareMap, CameraType.PHONE);
    }

    public TTVisionVuforia(HardwareMap hardwareMap, CameraType cameraType) {
        createVuforia(hardwareMap, cameraType);
    }

    public static Vector2D getCenter(Recognition recognition) {
        float x = (recognition.getLeft() + recognition.getRight()) / 2;
        float y = (recognition.getBottom() + recognition.getTop()) / 2;
        return new Vector2D(x, y);
    }

    private VuforiaLocalizer createVuforia(HardwareMap hardwareMap, CameraType cameraType) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        if (cameraType == CameraType.WEBCAM) {
            parameters.cameraName = ClassFactory.getInstance().getCameraManager().getAllWebcams().get(0);
        }
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        Debug.log(1);
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters);
        Debug.log(2);
        VuforiaTrackables trackables = vuforia.loadTrackablesFromFile(ASSET_NAME);
        Debug.log(3);
        trackables.activate();
        Debug.log(4);
        return vuforia;
    }

    public OpenGLMatrix getSkystoneLocation() {
        VuforiaTrackable skystone = trackables.get(0);
        Debug.log(5);
        if (skystone == null) {
            Debug.log("It's null");
            return null;
        }
        Debug.log(6);
        OpenGLMatrix location = skystone.getLocation();
        Debug.log(location);
        return location;
    }

    public enum CameraType {
        PHONE, WEBCAM
    }

}
