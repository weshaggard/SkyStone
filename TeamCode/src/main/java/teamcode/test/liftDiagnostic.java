package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.state.MoonshotArmSystem;


@TeleOp(name="The Lift is hecking bad")
public class liftDiagnostic extends AbstractOpMode {
    private MoonshotArmSystem arm;

    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        while(opModeIsActive()){
            telemetry.addData("Current Encoder Value: ", arm.getLiftEncoder().getCurrentPosition());
            telemetry.update();
        }
    }

    @Override
    protected void onStop() {

    }
}
