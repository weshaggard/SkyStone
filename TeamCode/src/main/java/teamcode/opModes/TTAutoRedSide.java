package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.BoundingBox2D;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.robotComponents.TTArmSystem;
import teamcode.robotComponents.TTDriveSystem;
import teamcode.robotComponents.TTVision;

@Autonomous(name = "TTAutoRedSide")
public class TTAutoRedSide extends AbstractOpMode {

    private static final BoundingBox2D MIDDLE_STONE_BOUNDS = new BoundingBox2D(-10, 0, 110, 0);
    private static final BoundingBox2D RIGHT_STONE_BOUNDS = new BoundingBox2D(120, 0, 500, 0);

    private TTArmSystem arm;
    private TTDriveSystem driveSystem;
    private TTVision vision;
    SkyStoneConfiguration config = null;
    @Override
    protected void onInitialize(){
        arm = new TTArmSystem(this);
        driveSystem = new TTDriveSystem(hardwareMap);
        vision = new TTVision(hardwareMap);
    }
    @Override
    protected void onStart(){
        moveToScanningPosition();
//        Vector3D pos;
//        boolean flag = true;
//        while(flag) {
//            pos = vision.getSkystonePosition();
//            if (pos == null) {
//                continue;
//            } else {
//                flag = false;
//            }
//            double horizontalDistanceFromRobot = pos.getY();
//            config = determineSkystoneConfig(horizontalDistanceFromRobot);
//            Debug.log(config);
//        }
//        if(config.equals(SkyStoneConfiguration.THREE_SIX)) {
//            suckSkystone(6);
//        }
        suckSkystone(6);
    }

    private SkyStoneConfiguration determineSkystoneConfig(double horizontalDistanceFromRobot) {
        Vector2D visionPos = new Vector2D(horizontalDistanceFromRobot, 0);
        if (MIDDLE_STONE_BOUNDS.contains(visionPos)) {
            return SkyStoneConfiguration.TWO_FIVE;
        } else if (RIGHT_STONE_BOUNDS.contains(visionPos)) {
            return SkyStoneConfiguration.ONE_FOUR;
        } else {
            return SkyStoneConfiguration.THREE_SIX;
        }
    }

    private void moveToScanningPosition(){
        driveSystem.vertical(-9, 0.6);
        driveSystem.lateral(-9.5, 0.6);
    }

    private void suckSkystone(int skystoneNum){
        driveSystem.lateral(50.25 - (8 * skystoneNum), 0.6);
        arm.intake(1.0);
        driveSystem.vertical(-25, 0.6);
        if(arm.intakeIsFull()){
            Debug.log("Intake full");
            arm.intake(0);
            driveSystem.turn(360,0.5);
        }

    }
    @Override
    protected void onStop(){

    }
}
