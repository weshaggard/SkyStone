package teamcode.test;

import teamcode.common.AbstractOpMode;
import teamcode.league3.MoonshotArmSystem;

public class StoneProcessorTest extends AbstractOpMode {


    private MoonshotArmSystem arm;

    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        arm.intakeSequence();
    }

    @Override
    protected void onStop() {

    }
}
