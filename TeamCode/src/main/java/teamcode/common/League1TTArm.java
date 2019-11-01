package teamcode.common;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Timer;
import java.util.TimerTask;

public class League1TTArm {

    private static final double CLAW_OPEN_POS = 0.4;
    private static final double CLAW_CLOSE_POS = 0.0;
    private static final double CLAW_POSITION_ERROR = 0.1;

    private final CRServo lift;
    private final ColorSensor liftSensor, skybridgeSensor;
    private final Servo claw;
    private final Timer timer;

    public League1TTArm(HardwareMap hardwareMap) {
        lift = hardwareMap.get(CRServo.class, TTHardwareComponentNames.ARM_LIFT);
        lift.setDirection(DcMotorSimple.Direction.REVERSE);
        liftSensor = hardwareMap.get(ColorSensor.class, TTHardwareComponentNames.ARM_LIFT_SENSOR);
        skybridgeSensor = hardwareMap.get(ColorSensor.class, TTHardwareComponentNames.SKYBRIDGE_SENSOR);
        claw = hardwareMap.get(Servo.class, TTHardwareComponentNames.ARM_CLAW);
        timer = TTOpMode.currentOpMode().getNewTimer();
    }

    public void testColorSensor(Telemetry telemetry, ColorSensor sensor) {
        int red = sensor.red();
        int blue = sensor.blue();
        telemetry.addData("red", red);
        telemetry.addData("blue", blue);
        LiftColor color = getColor(liftSensor);
        telemetry.addData("color detected", color);
        telemetry.addData("lift power", lift.getPower());
        telemetry.update();
    }

    public void raise(double power) {
        power = Math.abs(power);
        final boolean[] stopLift = new boolean[1];
        scheduleStopLiftFlag(stopLift, 2);
        Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
        while (getColor(liftSensor) != LiftColor.RED && !stopLift[0] && TTOpMode.currentOpMode().opModeIsActive()) {
            //testColorSensor(telemetry);
            lift.setPower(power);
        }
        lift.setPower(0.0);
        //testColorSensor(telemetry, liftSensor);
    }

    public void lower(double power) {
        power = Math.abs(power);
        final boolean[] stopLift = new boolean[1];
        scheduleStopLiftFlag(stopLift, 2);
        Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
        while (getColor(liftSensor) != LiftColor.BLUE && !stopLift[0] && TTOpMode.currentOpMode().opModeIsActive()) {
            //testColorSensor(telemetry);
            lift.setPower(-power);
        }
        lift.setPower(0.0);
        //testColorSensor(telemetry, liftSensor);
    }

    public void liftTimed(double seconds, double power) {
        lift.setPower(power);
        boolean[] stopLift = new boolean[1];
        scheduleStopLiftFlag(stopLift, seconds);
        while (!stopLift[0] && TTOpMode.currentOpMode().opModeIsActive()) ;
        Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
        telemetry.addData("stopLift", stopLift[0]);
        telemetry.update();
        lift.setPower(0.0);
    }

    /**
     * Assigns true to the first element of the array once the timeout period has passed.
     */
    private void scheduleStopLiftFlag(final boolean[] stopLiftFlag, double seconds) {
        TimerTask stop = new TimerTask() {
            @Override
            public void run() {
                stopLiftFlag[0] = true;
            }
        };
        timer.schedule(stop, (long) (seconds * 1000));
    }

    private LiftColor getColor(ColorSensor sensor) {
        int r = sensor.red();
        int b = sensor.blue();
        if (r < 800 && b < 800 && r > 250 && b > 250) {
            if (r > b) {
                return LiftColor.RED;
            } else if (b > r) {
                return LiftColor.BLUE;
            }
        }
        return LiftColor.NONE;
    }

    public boolean isRed(ColorSensor sensor){
        int r = sensor.red();
        Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
        if(r > 350 && r < 800){
            testColorSensor(telemetry, skybridgeSensor);
            return true;
        }
        testColorSensor(telemetry, skybridgeSensor);
        return false;
    }

    public boolean isBlue(ColorSensor sensor){
        int b = sensor.blue();
        Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
        if(b > 350 && b < 800){
            testColorSensor(telemetry, skybridgeSensor);
            return true;
        }
        testColorSensor(telemetry, skybridgeSensor);
        return false;
    }

    public ColorSensor getSkyBridgeSensor(){
        return this.skybridgeSensor;
    }



    public void liftContinuous(double power) {
        lift.setPower(power);
    }

    public void openClaw() {
        claw.setPosition(CLAW_OPEN_POS);
    }

    public void closeClaw() {
        claw.setPosition(CLAW_CLOSE_POS);

    }

    public boolean clawIsOpen() {
        return Math.abs(claw.getPosition() - CLAW_OPEN_POS) < CLAW_POSITION_ERROR;
    }

    public Servo getClaw() {
        return claw;
    }

    private enum LiftColor {
        RED, BLUE, NONE
    }

}
