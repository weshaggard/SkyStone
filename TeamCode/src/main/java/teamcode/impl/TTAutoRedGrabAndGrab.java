package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;
import java.util.TimerTask;

import teamcode.common.BoundingBox2D;
import teamcode.common.League1TTArm;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;
import teamcode.common.TTVision;
import teamcode.common.Vector2;

@Autonomous(name = "TT Auto Red Grab And Grab")
public class TTAutoRedGrabAndGrab extends TTOpMode {

    /**
     * A bounding box which is used to see if a skystone is in the center of the camera's view.
     */
    private static final BoundingBox2D SKYSTONE_BOUNDING_BOX = new BoundingBox2D(0, 0, 720, 1280);

    private TTDriveSystem driveSystem;
    private League1TTArm arm;
    private TTVision vision;
    private int skystonePos;

    @Override
    protected void onInitialize() {
        driveSystem = new TTDriveSystem(hardwareMap);
        arm = new League1TTArm(hardwareMap);
        vision = new TTVision(hardwareMap);
        vision.enable();
    }

    @Override
    protected void onStart() {
        initArm();
        skystonePos = locateSkystone();
        if(skystonePos == 6) {
            grabSkyStone(6);
            moveToStone(3);
            grabSkyStone(3);
            driveSystem.vertical(-30, 1);
        } else if(skystonePos == 5){
            grabSkyStone(5);
            moveToStone(2);
            grabSkyStone(2);
            driveSystem.vertical(-30, 1);
        } else {
            grabSkyStone(4);
        }
        driveSystem.brake();
    }


    /**
     * Opens the claw and lowers the arm for starting position.
     */
    private void initArm() {
        arm.openClaw();
        arm.lower(1);
    }

    /**
     * Approaches the skystone and records its position. Returns 4 if the skystones are in the first and fourth
     * slots. Returns 5 if the skystones are in the second and fifth slots. Returns 6 if the skystones
     * are in the third and sixth slots.
     */
    /**
     * Approaches the skystone and records its position. Returns 4 if the skystones are in the first and fourth
     * slots. Returns 5 if the skystones are in the second and fifth slots. Returns 6 if the skystones
     * are in the third and sixth slots.
     */
    private int locateSkystone() {
        driveSystem.vertical(22.5, 0.3);
        driveSystem.lateral(-2, 0.3);
        if (seesSkystone()) {
            driveSystem.lateral(2, 0.3);
            return 6;
        }
        driveSystem.lateral(-6.5, 0.3);
        sleep(1000);
        if (seesSkystone()) {
            return 5;
        }
        driveSystem.lateral(-8, 0.3);
        return 4;
    }

    /**
     * Returns true if the skystone is in the center of the camera's field of view.
     */
    private boolean seesSkystone() {
        List<Recognition> recognitions = vision.getRecognitions();
        for (Recognition recognition : recognitions) {
            if (recognition.getLabel().equals(TTVision.LABEL_SKYSTONE)) {
                Vector2 center = TTVision.getCenter(recognition);
                if (SKYSTONE_BOUNDING_BOX.contains(center)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*Starts from the starting pos and moves grab the block
      at that specific block pos then faces the foundation
     */
    private void grabSkyStone(int stoneNum) {
        driveSystem.vertical(10.5, 0.6);
        arm.closeClaw();
        sleep(700);
        arm.liftTimed(0.15, 0.5);
        driveSystem.vertical(-11, 0.6);
        driveSystem.turn(90, 0.6);
        moveToPlacedFoundation(stoneNum);
    }

    //Moves towards the foundation and turns to face it
    private void moveToPlacedFoundation(int stoneNum) {
        if(stoneNum > 3) {
            scheduleSeeSkyBridge();
            driveSystem.vertical(94 - stoneNum * 8, 0.6);
        } else {
            scheduleSeeSkyBridge();
            driveSystem.vertical(94 - stoneNum * 8, 1);
            driveSystem.turn(-5, 0.6);
        }
        driveSystem.vertical(16, 0.6);
        arm.openClaw();
    }

    private void moveToStone(int stoneNum) {
        driveSystem.vertical(-5, 0.6);
        arm.lower(1);
        driveSystem.vertical(-104 + stoneNum * 8, 0.6);
        driveSystem.turn(-90, 0.6);
        driveSystem.vertical(3,0.6);
    }

    private void scheduleSeeSkyBridge() {
        TimerTask scan = new TimerTask() {
            @Override
            public void run() {
                liftAfterSkybridge(0.5, 0.7);
            }
        };
        TTOpMode.currentOpMode().getTimer().schedule(scan, 0);
    }

    public void liftAfterSkybridge(double time, double power){
        int timesPassed = 0;
        while(timesPassed < 2) {
            if(arm.isRed(arm.getSkyBridgeSensor()) || arm.isBlue(arm.getSkyBridgeSensor())){
                timesPassed++;
            }
        }
        Telemetry telemetry = TTOpMode.currentOpMode().telemetry;
        telemetry.addData("Times Saw: ", timesPassed);
        telemetry.update();

        if(timesPassed == 2){
            arm.liftTimed(time, power);
        }

    }


    @Override
    protected void onStop() {

    }


}
