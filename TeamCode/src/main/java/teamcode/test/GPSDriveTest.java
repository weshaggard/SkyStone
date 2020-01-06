package teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.internal.ftdi.eeprom.FT_EEPROM_232H;

import teamcode.common.AbstractOpMode;
import teamcode.common.Debug;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@Autonomous(name = "GPS Drive Test")
public class GPSDriveTest extends AbstractOpMode {

    private static final double SPEED = 0.5;

    private GPS gps;
    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(24, 24);
        double startRotation = Math.PI / 2;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
    }

    @Override
    protected void onStart() {
        driveSystem.goTo(new Vector2D(24, 4 * 24), SPEED);
        driveSystem.setRotation(0, SPEED);
        driveSystem.goTo(new Vector2D(4.5 * 24, 4 * 24), SPEED);
        driveSystem.setRotation(-Math.PI / 2, SPEED);
        driveSystem.goTo(new Vector2D(4.5 * 24, 24), SPEED);
        driveSystem.setRotation(-Math.PI, SPEED);
        driveSystem.goTo(new Vector2D(24, 24), SPEED);
        driveSystem.setRotation(-3 * Math.PI / 2, SPEED);
    }

    @Override
    protected void onStop() {

    }
}
