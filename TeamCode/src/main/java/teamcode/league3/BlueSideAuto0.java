package teamcode.league3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Vector2D;

@Autonomous(name = "Blue Side Auto 0")
public class BlueSideAuto0 extends AbstractOpMode {

    private static final double SPEED = 1;

    private DriveSystem drive;
    private MoonshotArmSystem arm;
    private Timer timer1;
    private Timer timer2;
    private SkyStoneConfiguration skyStoneConfig;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(16, 31);
        double startRotation = 0;
        GPS gps = new GPS(hardwareMap, startPosition, startRotation);
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
        double y1 = 60 - skyStoneNum * 8;
        double rotation;
        if (skyStoneNum == 1) {
            y1 -= 18;
            rotation = Math.PI / 2;
        } else {
            rotation = -Math.PI / 2;
        }

        drive.goTo(new Vector2D(36, y1), SPEED);
        if (firstRun) {
            arm.setFrontGrabberPosition(true);
        }
        drive.setRotation(rotation, SPEED);
        drive.goTo(new Vector2D(60, y1), SPEED);

        // go in for stone
        TimerTask startIntakeTask = new TimerTask() {
            @Override
            public void run() {
                arm.intakeSequence();
            }
        };
        timer1.schedule(startIntakeTask, 0);
        Debug.log("START driving forward to capture stone");
        drive.vertical(8, SPEED);
        Debug.log("DONE driving forward to capture stone");

        TimerTask cancelIntakeTask = new TimerTask() {
            @Override
            public void run() {
                arm.cancelIntakeSequence();
            }
        };
        // cancel intake after some time
        timer2.schedule(cancelIntakeTask, 3000);

        // come out
        drive.goTo(new Vector2D(36, y1), SPEED);
        drive.setRotation(-Math.PI / 2, SPEED);
    }

    private void scoreStone() {
        drive.goTo(new Vector2D(36, 84), SPEED);
        arm.score();

        // score sequence
    }

    private void park() {
        // Debug.log("Parking");
        drive.goTo(new Vector2D(36, 72), SPEED);
    }

    @Override
    protected void onStop() {
        timer1.cancel();
        timer2.cancel();
    }

}
