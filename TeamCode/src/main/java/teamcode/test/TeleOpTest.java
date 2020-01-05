package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.league3.Constants;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;
import teamcode.league3.MoonshotArmSystem;

@TeleOp(name = "Tele Op Test")
public class TeleOpTest extends AbstractOpMode {

    private GPS gps;
    private DriveSystem driveSystem;
    private DcMotor leftIntake;
    private DcMotor rightIntake;
    private Servo delivery;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = Vector2D.zero();
        double startRotation = Math.PI / 2;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
        leftIntake = hardwareMap.dcMotor.get(Constants.LEFT_INTAKE);
        rightIntake = hardwareMap.dcMotor.get(Constants.RIGHT_INTAKE);
        leftIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftIntake.setDirection(DcMotorSimple.Direction.REVERSE);
        rightIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        delivery = hardwareMap.servo.get(Constants.DELIVERY);
    }

    @Override
    protected void onStart() {
        while (opModeIsActive()) {
            double x = gamepad1.right_stick_x;
            double y = -gamepad1.right_stick_y;
            double turn = gamepad1.left_stick_x;
            Vector2D velocity = new Vector2D(x, y).multiply(0.4);
            turn *= -0.4;
            driveSystem.continuous(velocity, turn);

            float in = gamepad1.right_trigger - gamepad1.left_trigger;
            Debug.log("intake power:" + in);
            intake(in);

            if(gamepad1.dpad_up){
                delivery.setPosition(1);
            }else if (gamepad1.dpad_down){
                delivery.setPosition(0);
            }
        }
    }

    private void intake(double power) {
        leftIntake.setPower(power);
        rightIntake.setPower(power);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
    }

}
