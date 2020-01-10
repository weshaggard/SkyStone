package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.league3.Constants;

@Autonomous(name = "Drive Motor Identification")
public class DriveMotorTestIdentification extends AbstractOpMode {

    @Override
    protected void onInitialize() {
    }

    @Override
    protected void onStart() {
        DcMotor frontLeft = hardwareMap.dcMotor.get(Constants.FRONT_LEFT_DRIVE_NAME);
        DcMotor frontRight = hardwareMap.dcMotor.get(Constants.FRONT_RIGHT_DRIVE_NAME);
        DcMotor rearLeft = hardwareMap.dcMotor.get(Constants.REAR_LEFT_DRIVE_NAME);
        DcMotor rearRight = hardwareMap.dcMotor.get(Constants.REAR_RIGHT_DRIVE_NAME);

        Debug.log("front left");
        frontLeft.setPower(0.2);
        sleep(2000);

        Debug.log("front right");
        frontRight.setPower(0.2);
        sleep(2000);

        Debug.log("rear left");
        rearLeft.setPower(0.2);
        sleep(2000);

        Debug.log("rear right");
        rearRight.setPower(0.2);
        sleep(2000);
    }

    @Override
    protected void onStop() {

    }
}
