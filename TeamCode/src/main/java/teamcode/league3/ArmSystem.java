package teamcode.league3;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Const;

import teamcode.common.Debug;

public class ArmSystem {
    private static final int TICK_ERROR_TOLERANCE = 10; // probably needs to be adjusted
    private static final double CLAW_CLOSE_POS = 0.75;
    private final DcMotor winch;
    private final DcMotor linearExtension;//linear screw thing, old fast boi arm
    private final DcMotor leftIntake, rightIntake;
    private LinearExtensionState linearExtensionState;
    private final Servo claw;
    private final ColorSensor intakeSensor;
    private final Servo leftFoundationGrabber;
    private final Servo rightFoundationGrabber;
    private GrabberState grabberState;
    private ClawState clawState;
    private final int LINEAR_EXTENSION_EXTENDED_POSITION_INCHES = (int)(12 * Constants.RAPIER_INCHES_TO_TICKS);
    private static final double GRABBER_OPEN_POSITION = 0.5;
    private static final double GRABBER_CLOSED_POSITION = 1;
    //422.8 ticks per rev
    //3 inches per rev


    public ArmSystem(HardwareMap hardwareMap) {
        winch = hardwareMap.get(DcMotor.class, Constants.ARM_WINCH);
        linearExtension = hardwareMap.get(DcMotor.class, Constants.ARM_LINEAR_EXTENSION);
        linearExtensionState = LinearExtensionState.RETRACTED;
        claw = hardwareMap.get(Servo.class, Constants.ARM_CLAW);
        leftIntake = hardwareMap.get(DcMotor.class, Constants.LEFT_INTAKE);
        rightIntake = hardwareMap.get(DcMotor.class, Constants.RIGHT_INTAKE);
        intakeSensor = hardwareMap.get(ColorSensor.class, Constants.INTAKE_SENSOR);
        leftFoundationGrabber = hardwareMap.servo.get(Constants.LEFT_FOUNDATION_GRABBER);
        rightFoundationGrabber = hardwareMap.servo.get(Constants.RIGHT_FOUDNATION_GRABBER);
        grabberState = GrabberState.OPEN;
        clawState = ClawState.OPEN;
    }

    public void intake(double power) {
        leftIntake.setPower(-power);
        rightIntake.setPower(power);

    }

    public void intake(double leftPower, double rightPower){
        leftIntake.setPower(-leftPower);
        rightIntake.setPower(rightPower);
    }

    public boolean intakeIsFull(){

        return intakeSensor.red() > 400;
    }

    public void moveToScoreFromIntookStone(int presetNum, final double power) throws InterruptedException {
        adjustClawPosition();
        Thread.currentThread().sleep(500);
        lift(presetNum, power);
        Thread linearExtension = new Thread(){
            @Override
            public void run(){
                slide(power);
            }
        };
        linearExtension.start();
        linearExtension.sleep(500);

    }

    public void adjustLiftHeight(int inches, double power) {
        int ticks = (int)(inches * Constants.WINCH_INCHES_TO_TICKS);
        winch.setTargetPosition(ticks);
        winch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        winch.setPower(power);
        while(!nearTarget(winch));
        winch.setPower(0);
    }



    public void adjustFoundationGrabbers() {
        if(grabberState == GrabberState.OPEN){
            leftFoundationGrabber.setPosition(GRABBER_CLOSED_POSITION);
            rightFoundationGrabber.setPosition(GRABBER_CLOSED_POSITION);
            grabberState = GrabberState.CLOSED;
        }else{
            leftFoundationGrabber.setPosition(GRABBER_OPEN_POSITION);
            rightFoundationGrabber.setPosition(GRABBER_OPEN_POSITION);
            grabberState = GrabberState.OPEN;
        }
    }


    private enum LinearExtensionState{
        EXTENDED, RETRACTED
    }

    private enum GrabberState{
        OPEN, CLOSED
    }

    private enum ClawState{
        OPEN, CLOSED
    }


    public void slide(double power){
        if(linearExtensionState == LinearExtensionState.RETRACTED){
            linearExtension.setTargetPosition(LINEAR_EXTENSION_EXTENDED_POSITION_INCHES);
            linearExtension.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            linearExtension.setPower(power);
            while(!nearTarget(linearExtension)){
                Debug.log(linearExtension.getCurrentPosition());
            }
            linearExtension.setPower(0);
            linearExtensionState = LinearExtensionState.EXTENDED;
        }else{
            linearExtension.setTargetPosition(0);
            linearExtension.setPower(power);
            while(!nearTarget(linearExtension)){
                Debug.log(linearExtension.getCurrentPosition());
            }
            linearExtension.setPower(0);
            linearExtensionState = LinearExtensionState.RETRACTED;
        }
    }

    public void adjustClawPosition(){
        if(clawState == ClawState.OPEN){
            claw.setPosition(CLAW_CLOSE_POS);
            clawState = ClawState.CLOSED;
        }else{
            claw.setPosition(Constants.CLAW_OPEN_POSITION);
            clawState = ClawState.OPEN;
        }
    }



    private boolean nearTarget(DcMotor motor){
        return Math.abs(motor.getCurrentPosition() - motor.getTargetPosition()) < TICK_ERROR_TOLERANCE;
    }


    public void goToHome(double power) {
        Thread linearExtension = new Thread() {
            public void run() {
                if(linearExtensionState == LinearExtensionState.EXTENDED){
                    slide(0.6);
                }
            }
        };
        linearExtension.start();
        //add a sleep on the main thread
        lift(0, power);

    }

    public void lift(int preset, double power) {
        int inches = preset * 4;
        int ticks = (int)(inches * Constants.WINCH_INCHES_TO_TICKS);
        winch.setTargetPosition(ticks);
        winch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        winch.setPower(power);
        while(!nearTarget(winch));
        winch.setPower(0);


    }
}
