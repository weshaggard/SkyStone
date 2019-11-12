package teamcode.impl;

import android.text.method.Touch;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import java.util.TimerTask;

import teamcode.common.TTHardwareComponentNames;

public class MetaTTArm {
    private static final double CLAW_OPEN_POS = 0.5;
    private static final double CLAW_CLOSE_POS = 1.0;
    private static final double WRIST_OPEN_POS = 1.0;
    private static final double WRIST_CLOSE_POS = 0.0;
    private final CRServo leftIntake, rightIntake;
    //private final DcMotor armLift;
    private final Servo armClaw, armWrist;
    private int presetIndex;
    //preset index in inches should be 4 inches * preset index should equal the total lift height
    //private TouchSensor armSensor;

    //TODO need to calibrate this value
    public MetaTTArm(HardwareMap hardwareMap){
        rightIntake = hardwareMap.get(CRServo.class, TTHardwareComponentNames.INTAKE_RIGHT);
        leftIntake = hardwareMap.get(CRServo.class, TTHardwareComponentNames.INTAKE_LEFT);
    //    armLift = hardwareMap.get(DcMotor.class, TTHardwareComponentNames.ARM_LIFT);
        armClaw = hardwareMap.get(Servo.class, TTHardwareComponentNames.ARM_CLAW);
        armWrist = hardwareMap.get(Servo.class, TTHardwareComponentNames.ARM_WRIST);

      //  armSensor = hardwareMap.get(TouchSensor.class, TTHardwareComponentNames.INTAKE_TOUCH_SENSOR);
        presetIndex = 0;
    }

    public void suck(double power){
        leftIntake.setPower(power);
        rightIntake.setPower(power);

        // while(!armSensor.isPressed()){
        //stall until the touch sensor is pressed
        //}
        //raise();
    }
    public void raise(){
        //armClaw.setPosition(ARM_CLOSE_POS);
        //armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        presetIndex++;
        //armLift.setTargetPosition(1000 * presetIndex);
        //TODO arbetrary value need to adjust this later
        //while(armLift.isBusy()) {
          //  armLift.setPower(1);
        //}
        //armLift.setPower(0);
    }

    public void lower(){
        //armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        presetIndex++;
        //armLift.setTargetPosition(-1000 * presetIndex);
        //TODO arbetrary value need to adjust this later
        //while(armLift.isBusy()) {
          //  armLift.setPower(-1);
        //}
        //armLift.setPower(0);
    }

    public void adjustClawPos(){
        if(armClaw.getPosition() == CLAW_CLOSE_POS) {
            armClaw.setPosition(CLAW_OPEN_POS);
        }else{
            armClaw.setPosition(CLAW_CLOSE_POS);
        }

    }
    public void rotate() {
        if(armWrist.getPosition() == WRIST_CLOSE_POS) {
            armWrist.setPosition(WRIST_OPEN_POS);
        }else{
            armWrist.setPosition(WRIST_CLOSE_POS);
        }
    }


    public void spit(float power) {
        leftIntake.setPower(-power);
        rightIntake.setPower(-power);
    }


}
