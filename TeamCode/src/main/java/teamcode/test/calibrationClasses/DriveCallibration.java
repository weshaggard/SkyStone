package teamcode.test.calibrationClasses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.league2.DriveSystemLeague2;

@Autonomous(name = "Drive Callibration")
public class DriveCallibration extends AbstractOpMode {

    DriveSystemLeague2 driveSystem;

    @Override
    protected void onInitialize() {
        driveSystem = new DriveSystemLeague2(hardwareMap);
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
