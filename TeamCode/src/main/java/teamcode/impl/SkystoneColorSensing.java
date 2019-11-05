package teamcode.impl;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SkystoneColorSensing {
    private ColorSensor skystoneDetector;
    //Skystone Fields
    private final int BLACK_RGB = 10;
    private  final int BLACK_BRIGHTNESS = 100;
    //Normal Stone Fields
    private final int YELLOW_BRIGHTNESS = 900;



    public SkystoneColorSensing(HardwareMap hardwareMap){
        skystoneDetector = hardwareMap.get(ColorSensor.class, "Skystone Vision");
        skystoneDetector.enableLed(true);
    }
    public boolean SeesSkystone(){
        if(skystoneDetector.argb() < BLACK_RGB){
            return true;
        }
        return false;
    }

    public boolean nearSkystone() {
        if(skystoneDetector.alpha() < BLACK_BRIGHTNESS){
            return true;
        }
        return false;
    }
    public boolean seesNormalStone(){
        if(skystoneDetector.argb() > 50 && skystoneDetector.argb() < 70){
            return true;
        }
        return false;
    }
    public boolean nearNormalStone(){
        if(skystoneDetector.alpha() > YELLOW_BRIGHTNESS){
            return true;
        }
        return false;
    }

    public ColorSensor getSkystoneDetector(){
        return skystoneDetector;
    }
}
