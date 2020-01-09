package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;
import teamcode.league3.MoonshotArmSystem;
import teamcode.league3.VisionOnInit;

@Autonomous(name = "Blue Side Auto 0")
public class BlueSideAuto0 extends AbstractOpMode {

    private static final double SPEED = 0.4;

    private DriveSystem drive;
    private MoonshotArmSystem arm;
    private SkyStoneConfiguration skyStoneConfig;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(9, 31);
        double startRotation = Math.toRadians(7);
        GPS gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
        arm = new MoonshotArmSystem(hardwareMap);
        VisionOnInit vision = new VisionOnInit(hardwareMap);
        VisionOnInit.SkystonePos skystonePos = null;
        while (!opModeIsActive()) {
            skystonePos = vision.vuforiascan(false, false);
        }
        skyStoneConfig = skyStoneConfigForPos(skystonePos);
        Debug.log("Config: " + skyStoneConfig);
    }

    private SkyStoneConfiguration skyStoneConfigForPos(VisionOnInit.SkystonePos pos) {
        switch (pos) {
            case LEFT:
                return SkyStoneConfiguration.THREE_SIX;
            case CENTER:
                return SkyStoneConfiguration.TWO_FIVE;
            case RIGHT:
                return SkyStoneConfiguration.ONE_FOUR;
            default:
                return null;
        }
    }

    @Override
    protected void onStart() {
        intakeStone(true);
        scoreStone();
        intakeStone(false);
        scoreStone();
        park();
    }

    /**
     * @param firstRun true if grabbing first SkyStone, false otherwise
     */
    private void intakeStone(boolean firstRun) {
        if (!firstRun) {
            drive.setRotation(0, SPEED);
        }

        Vector2D skyStoneLocation = new Vector2D(44, 52);
        int skyStoneNum = firstRun ? skyStoneConfig.getFirstStone() : skyStoneConfig.getSecondStone();
        skyStoneLocation.subtract(new Vector2D(0, skyStoneNum * 8));

        Debug.log("Going to SkyStone at " + skyStoneLocation);
        drive.goTo(skyStoneLocation, SPEED);

        arm.suck(1);
        sleep(1000);
        arm.suck(0);
        // intake sequence

        // back up
        Debug.log("Backing up");
        drive.vertical(-8, SPEED);
        Debug.log("Facing foundation");
        drive.setRotation(Math.PI / 2, SPEED);
    }

    private void scoreStone() {
        Debug.log("Scoring");
        drive.goTo(new Vector2D(36, 120), SPEED);

        // score sequence
    }

    private void park() {
        Debug.log("Parking");
        drive.goTo(new Vector2D(36, 72), SPEED);
    }

    @Override
    protected void onStop() {

    }

}
