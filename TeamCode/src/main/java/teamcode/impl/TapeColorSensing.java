package teamcode.impl;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TapeColorSensing {
    private ColorSensor tapeDetector;
    //TODO change the name of this color sensor
    //Blue fields
    private final int BLUE_RGB = 210;
    private final int BLUE_BRIGHTNESS = 100;
    //Red Fields
    private final int RED_RGB = 360;
    private final int RED_BRIGHTNESS = 900;

    //reference code, hue < 60 || hue > 320 for red and hue > 120 || hue < 260
    public TapeColorSensing(HardwareMap hardwareMap) {
        tapeDetector = hardwareMap.get(ColorSensor.class, "Skystone Vision");
        //tapeDetector.enableLed(true);
    }

    public boolean seesRedTape() {

        if (tapeDetector.argb()  <= 60 && tapeDetector.argb() >= 320) {
            return true;
        }
        return false;
    }

    public boolean seesBlueTape() {
        if (tapeDetector.argb()  >= 120 && tapeDetector.argb() <= 260) {
            return true;
        }
        return false;
    }

}