package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.robotComponents.MetaTTArm;

@Autonomous(name = "Meta Arm Test")
public class MetaTTArmTest extends AbstractOpMode {

    private static final int TICKS = 1800;

    private MetaTTArm arm;

    @Override
    protected void onInitialize() {
        arm = new MetaTTArm(this);
    }

    @Override
    protected void onStart() {
        arm.setClawPosition(false);
        sleep(2000);
        arm.lift(TICKS, 0.5);
        arm.extendWristIncrementally();
        arm.lift(-TICKS, 0.5);
        arm.setClawPosition(true);
        sleep(2000);
        arm.lift(TICKS, 0.5);
        arm.setWristPosition(false);
        arm.lift(-TICKS, 0.5);
        sleep(1000);
    }

    @Override
    protected void onStop() {

    }

}
