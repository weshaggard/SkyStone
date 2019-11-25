package teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.SkyStoneConfiguration;
import teamcode.robotComponents.MetaTTArm;
import teamcode.robotComponents.MetaTTArm2;
import teamcode.robotComponents.TTDriveSystem;


@Autonomous(name = "Meta Blue Auto")
public class TTAutoBlueSide extends AbstractOpMode {
    private MetaTTArm2 arm;
    private TTDriveSystem driveSystem;
    private SkyStoneConfiguration skyStoneConfig;


    @Override
    protected void onInitialize() {
        arm = new MetaTTArm2(this);
        driveSystem = new TTDriveSystem(hardwareMap);
        //TODO need to get vision working, assuming the path is 1
        //also init arm
    }

    private SkyStoneConfiguration determineSkystoneConfig() {
        return SkyStoneConfiguration.ONE_FOUR;
    }

    @Override
    protected void onStart() {
        skyStoneConfig = determineSkystoneConfig();
        if(skyStoneConfig.equals(SkyStoneConfiguration.ONE_FOUR)){
            driveSystem.vertical(-12, 0.7);
            driveSystem.adjustGrabberPos(false);
            driveSystem.vertical(-15, 0.7);
            driveSystem.lateral(3, 0.7);
            driveSystem.vertical(11, 0.7);
            driveSystem.turn(90, 0.5);

        }
    }

    @Override
    protected void onStop() {

    }
}
