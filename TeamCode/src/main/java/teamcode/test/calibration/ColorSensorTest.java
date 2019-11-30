package teamcode.test.calibration;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;

@TeleOp(name = "Color Sensor Test")
public class ColorSensorTest extends AbstractOpMode {

    TTArmSystem arm;

    @Override
    protected void onInitialize() {
        arm = new TTArmSystem(this);
    }

    @Override
    protected void onStart() {
        while(opModeIsActive()){
            arm.intakeIsFull();
        }
    }

    @Override
    protected void onStop() {

    }
}
