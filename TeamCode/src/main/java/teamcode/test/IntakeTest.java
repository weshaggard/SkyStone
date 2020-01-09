package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.league3.MoonshotArmSystem;


@Autonomous(name= "IntakeTest")
public class IntakeTest extends AbstractOpMode {
    MoonshotArmSystem arm;


    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);

    }

    @Override
    protected void onStart() {
        arm.intake(1);
        while(opModeIsActive());
    }

    @Override
    protected void onStop() {

    }
}
