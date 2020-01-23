package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.state.Constants;
import teamcode.state.GPS;


@Autonomous(name = "Odometry Diagnostic")
/**
 * Use to ensure that all odometers are working and running in the expected direction.
 */
public class OdometryDirectionCallibration extends AbstractOpMode {

    GPS gps;
    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, new Vector2D(9, 9), 2*Math.PI);
    }

    @Override
    protected void onStart() {
//        DcMotor leftVerticalOdometer = hardwareMap.dcMotor.get(Constants.LEFT_VERTICAL_ODOMETER_NAME);
//        DcMotor rightVerticalOdometer = hardwareMap.dcMotor.get(Constants.RIGHT_VERTICAL_ODOMETER_NAME);
//        DcMotor horizontalOdometer = hardwareMap.dcMotor.get(Constants.HORIZONTAL_ODOMETER_NAME);
//        leftVerticalOdometer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rightVerticalOdometer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        horizontalOdometer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        while (opModeIsActive()) {
            int[] position = gps.getCurrentPositions();
//            telemetry.addData("left: ", position[0]);
//            telemetry.addData("right: ", position[1]);
//            telemetry.addData("horizontal: ", position[2]);
//            telemetry.addData("Position: ", gps.getPosition());
//            telemetry.addData("Rotation", gps.getRotation());
//            //telemetry.addData("horizontal position", -horizontalOdometer.getCurrentPosition());
//            telemetry.update();
        }
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
