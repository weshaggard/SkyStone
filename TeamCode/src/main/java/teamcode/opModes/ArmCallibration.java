package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.robotComponents.TTArmSystem;

@Autonomous(name = "Arm Callibration")
public class ArmCallibration extends AbstractOpMode {

    private static final int TICKS = 1500;
    private static final double POWER = 0.5;

    private TTArmSystem arm;

    @Override
    protected void onInitialize() {
        arm = new TTArmSystem(this);
    }

    @Override
    protected void onStart() {
        Debug.log("Raising: " + TICKS);
        arm.lift(4, POWER);
        while (opModeIsActive()) ;
    }

    @Override
    protected void onStop() {

    }
}
