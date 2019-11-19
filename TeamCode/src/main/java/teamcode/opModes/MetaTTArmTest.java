package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.robotComponents.MetaTTArm;
import teamcode.robotComponents.TTHardwareComponentNames;

@Autonomous(name = "Meta Arm Test")
public class MetaTTArmTest extends AbstractOpMode {

    private static final int TICKS = 1800;

    private Timer timer;
    private MetaTTArm arm;

    @Override
    protected void onInitialize() {
        timer = getNewTimer();
        arm = new MetaTTArm(this);
    }

    @Override
    protected void onStart() {
        arm.intake(1.0);
        while (!arm.intakeIsFull()) ;
        arm.intake(0.0);
        arm.setClawPosition(false);
        sleep(2000);
        arm.lift(TICKS, 1.0);
        arm.extendWristIncrementally();
        arm.lift(-TICKS, 1.0);
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
