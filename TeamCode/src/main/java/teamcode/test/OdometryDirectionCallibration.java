package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import teamcode.common.AbstractOpMode;
import teamcode.league3.Constants;

@Disabled
@Autonomous(name = "Odometry Direction Callibration")
/**
 * Use to ensure that all odometers are working and running in the expected direction.
 */
public class OdometryDirectionCallibration extends AbstractOpMode {

    @Override
    protected void onInitialize() {
    }

    @Override
    protected void onStart() {
        DcMotor leftVerticalOdometer = hardwareMap.dcMotor.get(Constants.LEFT_VERTICAL_ODOMETER_NAME);
        DcMotor rightVerticalOdometer = hardwareMap.dcMotor.get(Constants.RIGHT_VERTICAL_ODOMETER_NAME);
        DcMotor horizontalOdometer = hardwareMap.dcMotor.get(Constants.HORIZONTAL_ODOMETER_NAME);
        leftVerticalOdometer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightVerticalOdometer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horizontalOdometer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        while (opModeIsActive()) {
            telemetry.addData("left position", leftVerticalOdometer.getCurrentPosition());
            telemetry.addData("right position", rightVerticalOdometer.getCurrentPosition());
            telemetry.addData("horizontal position", horizontalOdometer.getCurrentPosition());
            telemetry.update();
        }
    }

    @Override
    protected void onStop() {
    }

}
