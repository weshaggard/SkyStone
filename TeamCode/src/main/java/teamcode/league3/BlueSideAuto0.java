package teamcode.league3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;

@Autonomous(name = "Blue Side Auto 0")
public class BlueSideAuto0 extends AbstractOpMode {

    private static final double SPEED = 1;

    private DriveSystem drive;
    private MoonshotArmSystem arm;
    private SkyStoneConfiguration skyStoneConfig;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(16, 31);
        double startRotation = 0;
        GPS gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
        arm = new MoonshotArmSystem(hardwareMap);
        VisionOnInit vision = new VisionOnInit(hardwareMap);
        VisionOnInit.SkystonePos skystonePos = null;
        while (!opModeIsActive()) {
            skystonePos = vision.vuforiascan(false, false);
            Debug.log("Config: " + skyStoneConfig);
        }
        skyStoneConfig = skyStoneConfigForPos(skystonePos);
    }

    private SkyStoneConfiguration skyStoneConfigForPos(VisionOnInit.SkystonePos pos) {
        if (pos != null) {
            switch (pos) {
                case LEFT:
                    return SkyStoneConfiguration.THREE_SIX;
                case CENTER:
                    return SkyStoneConfiguration.TWO_FIVE;
                case RIGHT:
                    return SkyStoneConfiguration.ONE_FOUR;
            }
        }
        // if nothing is detected, guess
        return SkyStoneConfiguration.TWO_FIVE;
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
        int skyStoneNum = firstRun ? skyStoneConfig.getFirstStone() : skyStoneConfig.getSecondStone();
        double y = 60 - skyStoneNum * 8;
        drive.goTo(new Vector2D(36, y), SPEED);
        drive.setRotation(-Math.PI / 2, SPEED);
        drive.goTo(new Vector2D(56, y), SPEED);

        arm.suck(0.5);
        sleep(2000);
        arm.suck(0);
        // suck sequence

        // back up
        drive.goTo(new Vector2D(36, y), SPEED);
        drive.setRotation(Math.PI / 2, SPEED);
    }

    private void scoreStone() {
        drive.goTo(new Vector2D(36, 112), SPEED);

        // score sequence
    }

    private void park() {
        // Debug.log("Parking");
        drive.goTo(new Vector2D(36, 72), SPEED);
    }

    @Override
    protected void onStop() {

    }

}
