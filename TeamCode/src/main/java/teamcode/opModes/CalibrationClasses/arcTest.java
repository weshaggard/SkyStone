package teamcode.opModes.CalibrationClasses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.obsolete.MetaTTArm;
import teamcode.robotComponents.TTArmSystem;
import teamcode.robotComponents.TTDriveSystem;

@Autonomous(name = "arcTest")
public class arcTest extends AbstractOpMode {

    private TTDriveSystem driveSystem;
    private TTArmSystem arm;

    @Override
    protected void onInitialize() {
        arm = new TTArmSystem(this);
        driveSystem = new TTDriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        driveSystem.frontArc(true, 0.6, 90);
    }

    @Override
    protected void onStop() {

    }
}
