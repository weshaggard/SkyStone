package teamcode.league3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import teamcode.common.AbstractOpMode;

@Autonomous(name="arm")
public class ArmSystemTest extends AbstractOpMode {
    private ArmSystem arm;
    @Override
    protected void onInitialize() {
        arm = new ArmSystem(hardwareMap);

    }

    @Override
    protected void onStart() {
        arm.slide(1.0);
        arm.slide(1.0);
    }

    @Override
    protected void onStop() {

    }
}
