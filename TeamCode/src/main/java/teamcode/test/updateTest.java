package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.state.GPS;

@TeleOp(name="updateTest")
public class updateTest extends AbstractOpMode {
    private GPS gps;


    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, new Vector2D(9,9), Math.toRadians(180));
    }

    @Override
    protected void onStart() {
//        gps.updateLocation();
        while(opModeIsActive());
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }
}
