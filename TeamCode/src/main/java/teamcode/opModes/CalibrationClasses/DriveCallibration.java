package teamcode.opModes.CalibrationClasses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
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
        returnTicks();
        //driveSystem.vertical(-100, 0.6);
        driveSystem.lateral(100, 0.6);
    }

    private void returnTicks() {
        TimerTask returnMotorTicks = new TimerTask() {
            @Override
            public void run() {
                while(opModeIsActive()) {
                    Debug.log("FrontLeft: " + driveSystem.getMotors()[0].getTargetPosition() + ", " + driveSystem.getMotors()[0].getCurrentPosition());
                    Debug.log("FrontRight: " + driveSystem.getMotors()[1].getTargetPosition() + ", " + driveSystem.getMotors()[1].getCurrentPosition());
                    Debug.log("BackLeft: " + driveSystem.getMotors()[2].getTargetPosition() + ", " + driveSystem.getMotors()[2].getCurrentPosition());
                    Debug.log("BackRight: " + driveSystem.getMotors()[3].getTargetPosition() + ", " + driveSystem.getMotors()[3].getCurrentPosition());
                }
            }
        };
        getNewTimer().schedule(returnMotorTicks, 0);
    }

    @Override
    protected void onStop() {

    }

}
