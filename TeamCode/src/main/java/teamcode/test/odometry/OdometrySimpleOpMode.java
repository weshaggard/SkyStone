package teamcode.test.odometry;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Point;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;


@Autonomous(name= "Odometry test simple")
public class OdometrySimpleOpMode extends AbstractOpMode {

    DriveSystem driveSystem;
    OdometryWheelsFinal wheels;
    Thread odometerUpdate;
    @Override
    protected void onInitialize() {
        wheels = new OdometryWheelsFinal(this, new Point(0, 0), 0);
        driveSystem = new DriveSystem(this.hardwareMap, wheels);
    }

    @Override
    protected void onStart() {
        while(opModeIsActive()) {
            Debug.clear();
            Debug.log("Position: " + wheels.getGlobalRobotPosition());
            Debug.log("Direction: " + Math.toDegrees(wheels.getWorldAngleRads()));
            Vector2D velocity = new Vector2D(0.4 * gamepad1.left_stick_x,0.4 *  gamepad1.left_stick_y);
            double turnSpeed = gamepad1.right_stick_x * 0.4;
            driveSystem.continuous(velocity, turnSpeed);
        }
        //driveSystem.omniMovement(40, 0, 0.3);
    }

    @Override
    protected void onStop() {
        try {
            Thread.sleep(500);
            odometerUpdate.sleep(500);
        }catch( InterruptedException e){
            e.printStackTrace();
        }
    }
}
