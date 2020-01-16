package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;

import teamcode.common.AbstractOpMode;
import teamcode.state.Constants;


@Autonomous(name = "colorSensorTest")
public class ColorSensorTest extends AbstractOpMode {

    private ColorSensor sensor;

    @Override
    protected void onInitialize() {
        sensor = hardwareMap.get(ColorSensor.class, Constants.INTAKE_COLOR_SENSOR);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            telemetry.clear();
            telemetry.addData("red: ", sensor.red());
            telemetry.addData("green: ", sensor.green());
            telemetry.addData("blue: ", sensor.blue());
            telemetry.addData("argb: ", sensor.argb());
            telemetry.update();

        }
    }

    @Override
    protected void onStop() {
    }

}
