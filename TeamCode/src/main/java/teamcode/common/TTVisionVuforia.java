package teamcode.common;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.VuMarkTarget;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaSkyStone;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.internal.vuforia.externalprovider.VuforiaWebcam;

import java.util.List;

public class TTVisionVuforia {

    private static final String VUFORIA_KEY = "AQR2KKb/////AAABmcBOjjqXfkjtrjI9/Ps5Rs1yoVMyJe0wdjaX8pHqOaPu2gRcObwPjsuWCCo7Xt52/kJ4dAZfUM5Gy73z3ogM2E2qzyVObda1EFHZuUrrYkJzKM3AhY8vUz6R3fH0c/R9j/pufFYAABOAFoc5PtjMQ2fbeFI95UYXtl0u+6OIkCUJ3Zw71tvoD9Fs/cOiLB45FrWrxHPbinEhsOlCTWK/sAC2OK2HuEsBFCebaV57vKyATHW4w2LMWEZaCByHMk9RJDR38WCqivXz753bsiBVMbCzPYzwzc3DKztTbK8/cXqPPBLBKwU8ls0RN52akror1xE9lPwwksMXwJwolpyIZGnZngWcBWX4lLH+HlDNZ8Qm";
    private static final String ASSET_NAME = "RelicVuMark";
    public static final String LABEL_STONE = "Boring Boy";
    public static final String LABEL_SKYSTONE = "Extra Scory Point Boi";
    public static final String[] LABELS = {LABEL_STONE, LABEL_SKYSTONE};
    private static final double MINIMUM_CONFIDENCE = 0.75;
    private static final String WEBCAM_NAME = "Webcam1";

    private HardwareMap hardwareMap;
    private CameraType cameraType;
    private boolean enabled;
    private VuforiaLocalizer vuforia;

    public TTVisionVuforia(HardwareMap hardwareMap) {
        this(hardwareMap, CameraType.PHONE);
        createVuforia();
    }

    public TTVisionVuforia(HardwareMap hardwareMap, CameraType cameraType) {
        this.hardwareMap = hardwareMap;
        this.cameraType = cameraType;
    }

    public static Vector2 getCenter(Recognition recognition) {
        float x = (recognition.getLeft() + recognition.getRight()) / 2;
        float y = (recognition.getBottom() + recognition.getTop()) / 2;
        return new Vector2(x, y);
    }

    private VuforiaLocalizer createVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        if (cameraType == CameraType.WEBCAM) {
            parameters.cameraName = ClassFactory.getInstance().getCameraManager().getAllWebcams().get(0);
        }
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters);

        VuforiaTrackables trackables = vuforia.loadTrackablesFromFile(ASSET_NAME);
        VuforiaTrackable trackable = trackables.get(0);
        OpenGLMatrix transform = trackable.getLocation();

        return vuforia;
    }

    public static BoundingBox2D getBoundingBox(Recognition recognition) {
        double x1 = recognition.getLeft();
        double y1 = recognition.getTop();
        double x2 = recognition.getRight();
        double y2 = recognition.getBottom();
        return new BoundingBox2D(x1, y1, x2, y2);
    }

    public enum CameraType {
        PHONE, WEBCAM
    }

}
