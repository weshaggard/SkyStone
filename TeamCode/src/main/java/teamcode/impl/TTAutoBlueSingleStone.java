package teamcode.impl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

import teamcode.common.BoundingBox2D;
import teamcode.common.League1TTArm;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.TTDriveSystem;
import teamcode.common.TTOpMode;
import teamcode.common.TTVision;
import teamcode.common.Vector2;

@Autonomous(name = "Blue Single Stone")
public class TTAutoBlueSingleStone extends TTOpMode {

    /**
     * A bounding box which is used to see if a SkyStone is in the center of the camera's view.
     */
    private static final BoundingBox2D SKYSTONE_BOUNDING_BOX = new BoundingBox2D(0, 0, 720, 1280);

    private TTDriveSystem driveSystem;
    private League1TTArm arm;
    private TTVision vision;

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
        SkyStoneConfiguration skyStoneConfig = determineSkystoneConfig();
        grabStone();
        stoneToFoundation(skyStoneConfig.getSecondStone());
        pullFoundationAndApproachTape();
    }

    /**
     * Opens the claw and lowers the arm for starting position.
     */
    private void initArm() {
        arm.openClaw();
        arm.lower(1);
    }

    /**
     * Approaches the second SkyStone to determine the configuration of the SkyStones.
     */
    private SkyStoneConfiguration determineSkystoneConfig() {
        driveSystem.vertical(20, 0.5);
        driveSystem.lateral(2, 0.5);
        if (seesSkyStone()) {
            driveSystem.lateral(2, 0.5);
            return SkyStoneConfiguration.THREE_SIX;
        }
        driveSystem.lateral(8, 0.3);
        sleep(1500);
        if (seesSkyStone()) {
            driveSystem.lateral(1.5, 0.5);
            return SkyStoneConfiguration.TWO_FIVE;
        }
        driveSystem.lateral(9.5, 0.3);
        return SkyStoneConfiguration.ONE_FOUR;
    }

    /**
     * Returns true if the SkyStone is in the center of the camera's field of view.
     */
    private boolean seesSkyStone() {
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

    /**
     * Robot grabs the stone in front of it and backs out.
     */
    private void grabStone() {
        driveSystem.vertical(14.5, 0.7);
        arm.closeClaw();
        sleep(750);
        arm.liftTimed(0.25, 0.3);
        sleep(500);
        driveSystem.vertical(-27.5, 0.7);
    }

    /**
     * Robot drives towards the foundation from the stone area and turns to face it.
     *
     * @param stone the position of the stone that was grabbed in the stone area
     */
    private void stoneToFoundation(int stone) {
        driveSystem.turn(-90, 0.25);
        driveSystem.vertical(120.5 - stone * 8, 0.7);
        driveSystem.turn(90, 0.7);
        arm.liftTimed(1, 0.5);
        driveSystem.vertical(32, 0.6);
        sleep(250);
        arm.openClaw();
    }

    private void pullFoundationAndApproachTape() {
        driveSystem.lateral(-4.5, 0.7);
        driveSystem.vertical(2, 0.5);
        arm.lower(1);
        sleep(250);
        driveSystem.vertical(-60.5, 0.5);
        arm.liftTimed(1, 0.5);
        sleep(250);
        arm.closeClaw();
        driveSystem.lateral(41.5, 0.7);
        arm.lower(1);
    }

    @Override
    protected void onStop() {
        vision.disable();
    }


}

