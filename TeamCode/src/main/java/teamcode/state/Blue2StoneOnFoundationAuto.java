package teamcode.state;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Timer;
import java.util.TimerTask;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.SkyStoneConfiguration;
import teamcode.common.Utils;
import teamcode.common.Vector2D;

@Autonomous(name = "Blue 2 Stones on Foundation")
public class Blue2StoneOnFoundationAuto extends AbstractOpMode {

    private static final double SPEED = 1;

    private GPS gps;
    private DriveSystem drive;
    private MoonshotArmSystem arm;
    private Timer timer1;
    private Timer timer2;
    private SkyStoneConfiguration skyStoneConfig;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(9, 38.5);
        double startRotation = 0;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        drive = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
        arm = new MoonshotArmSystem(hardwareMap);
        arm.setBoxTransferPosition(true);
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
        initIntake();
        Debug.log("intake 1");
        intakeStone(true);
        Debug.log("score 1");
        scoreStone(true);
        Debug.log("intake 2");
        intakeStone(false);
        Debug.log("score 2");
        scoreStone(true);
        Debug.log("park");
        park();
        Utils.sleep(5000);
    }

    private void initIntake() {
        arm.suck(-1);
        TimerTask shutOffIntake = new TimerTask() {
            @Override
            public void run() {
                arm.suck(0);
            }
        };
        timer1.schedule(shutOffIntake, 500);
    }

    /**
     * @param firstStone true if grabbing first SkyStone, false otherwise
     */
    private void intakeStone(boolean firstStone) {
        int skyStoneNum = firstStone ? skyStoneConfig.getFirstStone() : skyStoneConfig.getSecondStone();
        if (skyStoneNum == 6) {
            return;
        }
        double y;
        double rotation;
        if (skyStoneNum == 4) {
            // grabbing the first stone unleashes devastation, so go for 6th
            // stone (which is farther from devastation)
            //unnecessary with new intake?
            skyStoneNum = 6;
        }
        if (skyStoneNum == 1) {
            y = 28;
            rotation = Math.PI / 2;
        } else {
            y = 68 - skyStoneNum * 8;
            rotation = -Math.PI / 2;
            if (!firstStone) {
                rotation += Math.toRadians(15);
            }
        }
        drive.goTo(new Vector2D(32, y), SPEED);

        if (firstStone) {
            arm.initCapstoneServo();
            arm.setFrontGrabberPosition(true);
        }

        drive.setRotation(rotation, SPEED);
        double x = 51;
        if (!firstStone) {
            x -= 1.5;
        }
        drive.goTo(new Vector2D(x, y), SPEED);

        // go in for stone
        TimerTask startIntakeTask = new TimerTask() {
            @Override
            public void run() {
                //arm.intakeSequenceAUTO();
            }
        };
        timer1.schedule(startIntakeTask, 0);
        drive.vertical(2, SPEED);

        TimerTask cancelIntakeTask = new TimerTask() {
            @Override
            public void run() {
                arm.cancelIntakeSequence();
            }
        };
        // cancel intake after some time if no stone has been found
        timer2.schedule(cancelIntakeTask, 3000);

        // come out
        drive.goTo(new Vector2D(34, y), SPEED);
        drive.setRotation(-Math.PI / 2, SPEED);

        TimerTask primeToScoreTask = new TimerTask() {
            @Override
            public void run() {
                arm.primeToScoreAUTO();
            }
        };
        timer1.schedule(primeToScoreTask, 0);
    }

    private void scoreStone(boolean firstStone) {
        int skyStoneNum = firstStone ? skyStoneConfig.getFirstStone() : skyStoneConfig.getSecondStone();
        if (skyStoneNum == 6) {
            return;
        }
        drive.goTo(new Vector2D(36, 112), SPEED);
        // place stone on foundation
        arm.score();
        Utils.sleep(500);
        new Thread(){
            public void run(){
                arm.resetArmPosition();
            }
        }.start();

    }

    private void park() {
        drive.goTo(new Vector2D(36, 72), SPEED);
    }

    @Override
    protected void onStop() {
        gps.shutdown();
        timer1.cancel();
        timer2.cancel();
    }

}
