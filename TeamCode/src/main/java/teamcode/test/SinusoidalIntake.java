package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.league3.Constants;
import teamcode.league3.MoonshotArmSystem;


@Autonomous(name= "Sin intake")
public class SinusoidalIntake extends AbstractOpMode {
    MoonshotArmSystem arm;
    long startTime;
    Servo boxTransfer;

    @Override
    protected void onInitialize() {
        arm = new MoonshotArmSystem(hardwareMap);
        boxTransfer = hardwareMap.servo.get(Constants.BOX_TRANSFER);
        startTime = System.currentTimeMillis();
    }


    //amplitude 0.35
    //period 0.3
    @Override
    protected void onStart() {

        while(opModeIsActive()) {
            boxTransfer.setPosition(0.37);
            long elapsedTime = System.currentTimeMillis() - startTime;
            double power = Range.clip(0.35 * Math.sin(elapsedTime) + 0.7, 0.3, 1);
            arm.suck(power);
            Debug.log(power);
            Debug.log("Time: " + elapsedTime);
        }
    }

    @Override
    protected void onStop() {

    }
}
