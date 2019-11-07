package teamcode.impl;

import android.text.method.Touch;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import java.util.TimerTask;

import teamcode.common.TTHardwareComponentNames;

public class MetaTTArm {
    private static final double CLAW_OPEN_POS = 0.4;
    private static final double ARM_CLOSE_POS = 0.0;
    private final DcMotor leftIntake, rightIntake;
    private final DcMotor armLift;
    private final Servo armClaw;
    private int presetIndex;
    //preset index in inches should be 4 inches * preset index should equal the total lift height
    private TouchSensor armSensor;
    private final double INCHES_TO_TICKS = 20;
    //TODO need to calibrate this value
    public MetaTTArm(HardwareMap hardwareMap){
        leftIntake = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.INTAKE_LEFT);
        rightIntake = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.INTAKE_RIGHT);
        armLift = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.ARM_LIFT);
        armClaw = hardwareMap.get(Servo.class, TTHardwareComponentNames.ARM_CLAW);
        armSensor = hardwareMap.get(TouchSensor.class, TTHardwareComponentNames.INTAKE_TOUCH_SENSOR);
        presetIndex = 0;
    }

    public void suck(){
        leftIntake.setPower(1);
        rightIntake.setPower(-1);
        while(!armSensor.isPressed()){
        //stall until the touch sensor is pressed
        }
        raise();
    }
    public void raise(){
        armClaw.setPosition(ARM_CLOSE_POS);
        armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        presetIndex++;
        armLift.setTargetPosition(1000 * presetIndex);
        while(armLift.isBusy()) {
            armLift.setPower(1);
        }
        armLift.setPower(0);
    }

    public void lower(){
        armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        presetIndex++;
        armLift.setTargetPosition(-1000 * presetIndex);
        while(armLift.isBusy()) {
            armLift.setPower(-1);
        }
        armLift.setPower(0);
    }

    public void open(){
        armClaw.setPosition(CLAW_OPEN_POS);
    }


}
