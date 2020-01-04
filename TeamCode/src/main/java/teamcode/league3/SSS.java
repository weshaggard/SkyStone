package teamcode.league3;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import java.util.HashMap;
import java.util.Map;

import teamcode.common.Debug;
import teamcode.common.Utils;

/**
 * Intended for use when the robot is against the wall and the webcam is centered on the three Sky Stones.
 */
public class SSS {

    private static final String VUFORIA_KEY = "AQR2KKb/////AAABmcBOjjqXfkjtrjI9/Ps5Rs1yoVMyJe0wdjaX8pHqOaPu2gRcObwPjsuWCCo7Xt52/kJ4dAZfUM5Gy73z3ogM2E2qzyVObda1EFHZuUrrYkJzKM3AhY8vUz6R3fH0c/R9j/pufFYAABOAFoc5PtjMQ2fbeFI95UYXtl0u+6OIkCUJ3Zw71tvoD9Fs/cOiLB45FrWrxHPbinEhsOlCTWK/sAC2OK2HuEsBFCebaV57vKyATHW4w2LMWEZaCByHMk9RJDR38WCqivXz753bsiBVMbCzPYzwzc3DKztTbK8/cXqPPBLBKwU8ls0RN52akror1xE9lPwwksMXwJwolpyIZGnZngWcBWX4lLH+HlDNZ8Qm";
    /**
     * How many pixels a Sky Stone occupies horizontally on the camera from scanning position.
     */
    private static final int SKY_STONE_PIXEL_WIDTH = 640;

    private final VuforiaLocalizer vuforia;

    public SSS(HardwareMap hardwareMap) {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        //WebcamName webcamName = hardwareMap.get(WebcamName.class, Constants.WEBCAM);
        //   parameters.cameraName = webcamName;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        this.vuforia.setFrameQueueCapacity(1);
    }

    public enum SkystonePosition {
        LEFT, MIDDLE, RIGHT;
    }

    public SkystonePosition getSkyStonePosition() {
        Bitmap bitmap = getBitmap();
        Map<Integer, SkystonePosition> map = new HashMap<>();

        int leftRegionDarkPixelCount = getDarkPixelCount(bitmap, 0, SKY_STONE_PIXEL_WIDTH);
        map.put(leftRegionDarkPixelCount, SkystonePosition.LEFT);
        Debug.log("left: " + leftRegionDarkPixelCount);

        int middleRegionDarkPixelCount = getDarkPixelCount(bitmap, SKY_STONE_PIXEL_WIDTH,
                2 * SKY_STONE_PIXEL_WIDTH);
        map.put(middleRegionDarkPixelCount, SkystonePosition.MIDDLE);
        Debug.log("middle: " + middleRegionDarkPixelCount);

        int rightRegionDarkPixelCount = getDarkPixelCount(bitmap, SKY_STONE_PIXEL_WIDTH * 2,
                SKY_STONE_PIXEL_WIDTH * 3);
        map.put(rightRegionDarkPixelCount, SkystonePosition.RIGHT);
        Debug.log("right: " + rightRegionDarkPixelCount);

        int maxPixelCount = Math.max(leftRegionDarkPixelCount, Math.max(middleRegionDarkPixelCount,
                rightRegionDarkPixelCount));
        return map.get(maxPixelCount);
    }

    private Bitmap getBitmap() {
        Image image = getImage();
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
        bitmap.copyPixelsFromBuffer(image.getPixels());
        return bitmap;
    }

    private Image getImage() {
        try {
            while (true) {
                VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().take();
                long imageCount = frame.getNumImages();
                for (int i = 0; i < imageCount; i++) {
                    Image image = frame.getImage(i);
                    if (image.getFormat() == PixelFormat.RGB_565) {
                        frame.close();
                        return image;
                    }
                }
                frame.close();
            }
        } catch (InterruptedException e) {
            Debug.log("InterruptedException!");
            return null;
        }
    }

    private int getDarkPixelCount(Bitmap bitmap, int startPixel, int endPixel) {
        int darkPixelCount = 0;
        int height = bitmap.getHeight();
        for (int x = startPixel; x < endPixel; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = bitmap.getPixel(x, y);
                if (isDark(pixel)) {
                    darkPixelCount++;
                }
            }
        }
        return darkPixelCount;
    }

    /**
     * Dark like a Sky Stone face.
     */
    private boolean isDark(int pixel) {
        return true;
    }

}
