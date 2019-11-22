package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import teamcode.common.AbstractOpMode;
import teamcode.robotComponents.TTDriveSystem;

@Autonomous(name = "Drive Callibration")
public class DriveCallibration extends AbstractOpMode {

    TTDriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
    }

    @Override
    protected void onStart() {
        driveSystem.vertical(96, 0.6);
    }

    @Override
    protected void onStop() {

    }

}
