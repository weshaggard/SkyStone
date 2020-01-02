package teamcode.test.test;


import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;

import teamcode.common.AbstractOpMode;
import teamcode.league3.Constants;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@Autonomous(name= "Straight")
public class StraightTest extends AbstractOpMode {
    DriveSystem drive;
    DcMotor leftEncoder;
    DcMotor rightEncoder;

    @Override
    protected void onInitialize() {
        drive = new DriveSystem(hardwareMap);
        leftEncoder = hardwareMap.get(DcMotor.class, Constants.REAR_LEFT_DRIVE_NAME);
        rightEncoder = hardwareMap.get(DcMotor.class, Constants.FRONT_RIGHT_DRIVE_NAME);
    }

    @Override
    protected void onStart() {
        leftEncoder.setTargetPosition(1102 * 24);
        rightEncoder.setTargetPosition(1102 * 24);
        drive.setPower(0.3, 0.3, 0.3,0.3);
        while(!(Math.abs(leftEncoder.getCurrentPosition() - leftEncoder.getTargetPosition()) < 500 && Math.abs(rightEncoder.getCurrentPosition() - rightEncoder.getTargetPosition()) < 500));
        drive.brake();

    }

    @Override
    protected void onStop() {

    }
}
