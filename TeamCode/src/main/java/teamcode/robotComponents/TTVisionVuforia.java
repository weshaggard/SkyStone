package teamcode.robotComponents;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.VuMarkTarget;
import com.vuforia.Vuforia;

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

import teamcode.common.Debug;

public class TTVisionVuforia {

    private static final String VUFORIA_KEY = "AQR2KKb/////AAABmcBOjjqXfkjtrjI9/Ps5Rs1yoVMyJe0wdjaX8pHqOaPu2gRcObwPjsuWCCo7Xt52/kJ4dAZfUM5Gy73z3ogM2E2qzyVObda1EFHZuUrrYkJzKM3AhY8vUz6R3fH0c/R9j/pufFYAABOAFoc5PtjMQ2fbeFI95UYXtl0u+6OIkCUJ3Zw71tvoD9Fs/cOiLB45FrWrxHPbinEhsOlCTWK/sAC2OK2HuEsBFCebaV57vKyATHW4w2LMWEZaCByHMk9RJDR38WCqivXz753bsiBVMbCzPYzwzc3DKztTbK8/cXqPPBLBKwU8ls0RN52akror1xE9lPwwksMXwJwolpyIZGnZngWcBWX4lLH+HlDNZ8Qm";
    private static final String ASSET_NAME = "Skystone";
    public static final String LABEL_SKYSTONE = "TargetElement";

    private VuforiaLocalizer vuforia;
    private VuforiaTrackables trackables;

    public TTVisionVuforia(HardwareMap hardwareMap) {
        this(hardwareMap, CameraType.PHONE);
    }

    public TTVisionVuforia(HardwareMap hardwareMap, CameraType cameraType) {
        createVuforia(cameraType);
    }

    private VuforiaLocalizer createVuforia(CameraType cameraType) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        if (cameraType == CameraType.WEBCAM) {
            parameters.cameraName = ClassFactory.getInstance().getCameraManager().getAllWebcams().get(0);
        } else {
            parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        }

        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters);
        trackables = vuforia.loadTrackablesFromFile(ASSET_NAME);
        trackables.activate();
        return vuforia;
    }

    public OpenGLMatrix getSkystoneTransform() {
        VuforiaTrackable skystone = trackables.get(0);
        if (skystone == null) {
            Debug.log(null);
            return null;
        }
        Debug.log(skystone.getName());
        Debug.log(skystone.getLocation());
        return skystone.getLocation();
    }

    public enum CameraType {
        PHONE, WEBCAM
    }

}
