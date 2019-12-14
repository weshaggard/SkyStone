package teamcode.league3;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedList;
import java.util.Queue;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;

/**
 * Always call shutdown() in onStop() of AbstractOpMode.
 */
public class GPS {

    /**
     * Whether or noto this GPS should continue to update positions.
     */
    private boolean active;
    private final Vector2D currentPosition;
    private final DcMotor leftVertical, rightVertical, horizontal;

    public GPS(HardwareMap hardwareMap, Vector2D currentPosition) {
        active = true;
        this.currentPosition = currentPosition;
        leftVertical = hardwareMap.dcMotor.get(Constants.LEFT_VERTICAL_ODOMETER);
        rightVertical = hardwareMap.dcMotor.get(Constants.RIGHT_VERTICAL_ODOMETER);
        horizontal = hardwareMap.dcMotor.get(Constants.HORIZONTAL_ODOMETER);
        resetEncoders();
        Thread positionUpdater = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (Exception e) {
                }
                while (active) {
                    updatePosition();
                }
            }
        };
        positionUpdater.start();
    }

    private void resetEncoders() {
        leftVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void updatePosition() {
        Telemetry telemetry = AbstractOpMode.currentOpMode().telemetry;
        telemetry.addData("left vertical position: ", leftVertical.getCurrentPosition());
        telemetry.addData("right vertical position: ", rightVertical.getCurrentPosition());
        telemetry.addData("horizontal position: ", horizontal.getCurrentPosition());
        telemetry.update();
    }

    public Vector2D getCurrentPosition() {
        return currentPosition;
    }

    public void shutdown() {
        active = false;
    }

}
