package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.league3.DriveSystem;

@Autonomous(name = "Drive Test")
public class DriveTest extends AbstractOpMode {

    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        driveSystem = new DriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        Debug.log("going forward 12 inches at 0.1 power");
        driveSystem.vertical(12, 0.1);
    }

    @Override
    protected void onStop() {
    }
}
