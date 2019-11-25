package teamcode.obsolete;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;

import teamcode.robotComponents.TTHardwareComponentNames;

public class TapeColorSensing {
    private final ColorSensor tapeDetector;
    //Blue fields
    private final int BLUE_RGB = 210;
    private final int BLUE_BRIGHTNESS = 100;
    //Red Fields
    private final int RED_RGB = 360;
    private final int RED_BRIGHTNESS = 900;

    //reference code, hue < 60 || hue > 320 for red and hue > 120 || hue < 260
    public TapeColorSensing(HardwareMap hardwareMap) {
        tapeDetector = hardwareMap.get(ColorSensor.class, TTHardwareComponentNames.TAPE_COLOR_SENSOR);
        tapeDetector.setI2cAddress(I2cAddr.create8bit(0x3c));
        tapeDetector.enableLed(true);
    }

    public boolean seesRedTape() {

        if (tapeDetector.argb()  <= 60 && tapeDetector.argb() >= 320) {
            return true;
        }
        return false;
    }

    public LiftColor tapeColor(){
            int r = tapeDetector.red();
            int b = tapeDetector.blue();
            if (r < 800 && b < 800 && r > 250 && b > 250) {
                if (r > b) {
                    return LiftColor.RED;
                } else if (b > r) {
                    return LiftColor.BLUE;
                }
            }
            return LiftColor.NONE;
        }

    public ColorSensor getTapeDetector(){
        return tapeDetector;
    }

    public enum LiftColor{
        NONE, BLUE, RED
    }

}