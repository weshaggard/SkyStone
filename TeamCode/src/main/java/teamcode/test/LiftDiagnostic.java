package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.state.Constants;
import teamcode.state.MoonshotArmSystem;


@TeleOp(name = "Lift Diagnostic")
public class LiftDiagnostic extends AbstractOpMode {

    private MoonshotArmSystem arm;

    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        arm.extend();
        while (opModeIsActive()) {
            if (gamepad1.dpad_up) {
                arm.setLiftHeight(arm.getLiftHeight() + 4);
            } else if (gamepad1.dpad_down) {
                arm.setLiftHeight(arm.getLiftHeight() - 4);
            }
        }
    }

    @Override
    protected void onStop() {

    }
}
