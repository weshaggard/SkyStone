package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Utils;
import teamcode.league3.MoonshotArmSystem;

@Disabled
@Autonomous(name = "Auto Intake Test")
public class AutoIntakeTest extends AbstractOpMode {

    @Override
    protected void onInitialize() {
    }

    @Override
    protected void onStart() {
        MoonshotArmSystem arm = new MoonshotArmSystem(hardwareMap);

        Debug.log("intake sequence");
        arm.intakeSequenceAUTO();

        Debug.log("Pausing");
        Utils.sleep(2000);

        Debug.log("Priming to score");
        arm.primeToScoreAUTO();

        Debug.log("scoring");
        arm.scoreAUTO();

        Debug.log("Pausing");
        Utils.sleep(2000);
    }

    @Override
    protected void onStop() {
    }

}
