package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import teamcode.common.League1TTArm;
import teamcode.common.TTOpMode;

@TeleOp(name = "SkyBridgeSensorTest")
public class SkyBridgeSensorTest extends TTOpMode {

    private League1TTArm arm;

    @Override
    public void onInitialize(){
        arm = new League1TTArm(hardwareMap);
    }

    @Override
    public void onStart(){
        while(opModeIsActive()) {
            ColorSensor sensor = arm.getSkyBridgeSensor();
            int r0 = 382;
            int b0 = 248;
            int r = sensor.red() - r0;
            int b = sensor.blue() - b0;

            int redSideTargetR = -230;
            int redSideTargetB = -152;

            int blueSideTargetR = -235;
            int blueSideTargetB = -137;

            int tolerance = 20;

            boolean isRed = Math.abs(r - redSideTargetR) < tolerance && Math.abs(b - redSideTargetB) < tolerance;
            boolean isBlue = Math.abs(r - blueSideTargetR) < tolerance && Math.abs(b - blueSideTargetB) < tolerance;
            Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
            telemetry.addData("Red:" , r);
            telemetry.addData("Blue:" , b);
            telemetry.addData("IsBlue:" , isBlue);
            telemetry.addData("IsRed:" , isRed);
            telemetry.update();
        }
    }

    public void skyBridgeSensorTest(){

        Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
        telemetry.addData("Sees Red?:" , arm.isRed(arm.getSkyBridgeSensor()));
        telemetry.addData("Sees Blue?:" , arm.isBlue(arm.getSkyBridgeSensor()));
        telemetry.update();

    }

    @Override
    public void onStop(){

    }
}
