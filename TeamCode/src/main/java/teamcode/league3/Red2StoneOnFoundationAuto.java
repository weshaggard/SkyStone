package teamcode.league3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Utils;
import teamcode.common.Vector2D;

@Autonomous(name = "Red 2 Stone on Foundation")
public class Red2StoneOnFoundationAuto extends AbstractOpMode {

    private static final double SPEED = 1;

    private GPS gps;
    private DriveSystem drive;
    private MoonshotArmSystem arm;
    private Timer timer1;
    private Timer timer2;
    private SkyStoneConfiguration skyStoneConfig;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(144 - 9, 38.5);
        double startRotation = Math.PI;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
        arm = new MoonshotArmSystem(hardwareMap);
        timer1 = new Timer();
        timer2 = new Timer();
        VisionOnInit vision = new VisionOnInit(hardwareMap);
        VisionOnInit.SkystonePos skystonePos = null;
        while (!opModeIsActive()) {
            skystonePos = vision.vuforiascan(false, false);
            Debug.log(skystonePos);
        }
        skyStoneConfig = skyStoneConfigForPos(skystonePos);
    }

    private SkyStoneConfiguration skyStoneConfigForPos(VisionOnInit.SkystonePos pos) {
        if (pos != null) {
            switch (pos) {
                case LEFT:
                    return SkyStoneConfiguration.ONE_FOUR;
                case CENTER:
                    return SkyStoneConfiguration.TWO_FIVE;
                case RIGHT:
                    return SkyStoneConfiguration.THREE_SIX;
            }
        }
        // if nothing is detected, guess
        return SkyStoneConfiguration.TWO_FIVE;
    }

    @Override
    protected void onStart() {
        intakeStone(true);
        scoreStone(true);
        intakeStone(false);
        scoreStone(true);
        park();
    }

    /**
     * @param firstStone true if grabbing first SkyStone, false otherwise
     */
    private void intakeStone(boolean firstStone) {
        int skyStoneNum = firstStone ? skyStoneConfig.getFirstStone() : skyStoneConfig.getSecondStone();
        double y;
        double rotation;
        if (skyStoneNum == 4) {
            // grabbing the first stone unleashes devastation, so go for 6th
            // stone (which is farther from devastation)
            skyStoneNum = 6;
        }
        if (skyStoneNum == 1) {
            y = 28;
            rotation = Math.PI / 2;
        } else {
            y = 68 - skyStoneNum * 8;
            rotation = 3 * Math.PI / 2;
            if (!firstStone) {
                rotation -= Math.toRadians(15);
            }
        }
        drive.goTo(new Vector2D(144 - 32, y), SPEED);

        if (firstStone) {
            arm.initCapstoneServo();
            arm.setFrontGrabberPosition(true);
        }

        drive.setRotation(rotation, SPEED);
        double x = 144 - 52;
        if (!firstStone) {
            // x -= 4; // to account for consistent error
        }
        drive.goTo(new Vector2D(x, y), SPEED);

        // go in for stone
        TimerTask startIntakeTask = new TimerTask() {
            @Override
            public void run() {
                arm.intakeSequenceAUTO();
            }
        };
        timer1.schedule(startIntakeTask, 0);
        drive.vertical(8, SPEED);

        TimerTask cancelIntakeTask = new TimerTask() {
            @Override
            public void run() {
                arm.cancelIntakeSequence();
            }
        };
        // cancel intake after some time if no stone has been found
        timer2.schedule(cancelIntakeTask, 3000);

        // come out
        drive.goTo(new Vector2D(144 - 36, y), SPEED);
        drive.setRotation(3 * Math.PI / 2, SPEED);

        TimerTask primeToScoreTask = new TimerTask() {
            @Override
            public void run() {
                arm.primeToScoreAUTO();
            }
        };
        timer1.schedule(primeToScoreTask, 0);
    }

    private void scoreStone(boolean firstStone) {
        drive.goTo(new Vector2D(144 - 36, 106), SPEED);
        // place stone on foundation
        arm.scoreAUTO();
    }

    private void park() {
        // Debug.log("Parking");
        drive.goTo(new Vector2D(144 - 36, 72), SPEED);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
        timer1.cancel();
        timer2.cancel();
    }

}
