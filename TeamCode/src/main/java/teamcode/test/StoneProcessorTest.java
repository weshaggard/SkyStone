package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.league3.MoonshotArmSystem;

@Autonomous(name="StoneProcess")
public class StoneProcessorTest extends AbstractOpMode {

    MoonshotArmSystem arm;
    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        arm.intake(1);
    }

    @Override
    protected void onStop() {

    }
}
