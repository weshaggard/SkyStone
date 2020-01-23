package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.state.DriveSystem;
import teamcode.state.GPS;


@TeleOp(name= "Localizer Test")
public class LocalizationTest extends AbstractOpMode {

    DriveSystem drive;
    GPS gps;
    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, new Vector2D(0,0), 0);
        //drive = new DriveSystem(hardwareMap, gps, new Vector2D(0,0), 0);
    }

    @Override
    protected void onStart() {
        new Thread(){
            public void run(){
                while(opModeIsActive()){
                   double  turnSpeed = -gamepad1.left_stick_x;
                    Vector2D velocity = new Vector2D(-gamepad1.right_stick_x, gamepad1.right_stick_y);
                    drive.continuous(velocity, turnSpeed);
                }
            }
        }.start();
        while(opModeIsActive()){
            telemetry.addData("Position: ", gps.getPosition());
            telemetry.addData("Rotation: ", gps.getRotation());
            telemetry.update();
        }
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }
}
