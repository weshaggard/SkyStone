package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.state.MoonshotArmSystem;

@TeleOp(name="LiftTest")
public class LiftEncoderTest extends AbstractOpMode {
    private MoonshotArmSystem arm;

    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        //arm.extend();
        arm.intakeSequence();
        arm.extend();
        try {
            Thread.currentThread().sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        arm.setLiftHeight(8);
        while(opModeIsActive());

    }

    @Override
    protected void onStop() {

    }
}
