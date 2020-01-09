package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;
import teamcode.league3.MoonshotArmSystem;
import teamcode.league3.VisionOnInit;

@Autonomous(name = "Red Side Auto 0")
public class RedSIdeAuto0 extends AbstractOpMode {

    private static final double SPEED = 1;

    private DriveSystem drive;
    private MoonshotArmSystem arm;
    private SkyStoneConfiguration skyStonConig;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(135, 31);
        double startRotation = Math.toRadians(187);
        GPS gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
        arm = new MoonshotArmSystem(hardwareMap);
        VisionOnInit vision = new VisionOnInit(hardwareMap);
        VisionOnInit.SkystonePos skystonePos = null;
        while (!opModeIsActive()) {
            skystonePos = vision.vuforiascan(false, false);
        }
        skyStonConig = skyStoneConfigForPos(skystonePos);
    }

    private SkyStoneConfiguration skyStoneConfigForPos(VisionOnInit.SkystonePos pos) {
        switch (pos) {
            case LEFT:
                return SkyStoneConfiguration.ONE_FOUR;
            case CENTER:
                return SkyStoneConfiguration.TWO_FIVE;
            case RIGHT:
                return SkyStoneConfiguration.THREE_SIX;
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

        Vector2D skyStoneLocation = new Vector2D(100, 52);
        int skyStoneNum = firstRun ? skyStonConig.getFirstStone() : skyStonConig.getSecondStone();
        skyStoneLocation.subtract(new Vector2D(0, skyStoneNum * 8));

        drive.goTo(skyStoneLocation, SPEED);

        arm.suck(1);
        sleep(1000);
        arm.suck(0);
        // intake sequence

        // back up
        drive.vertical(-8, SPEED);
        drive.setRotation(Math.PI / 2, SPEED);
    }

    private void scoreStone() {
        drive.goTo(new Vector2D(108, 120), SPEED);

        // score sequence
    }

    private void park() {
        drive.goTo(new Vector2D(108, 72), SPEED);
    }

    @Override
    protected void onStop() {

    }

}
