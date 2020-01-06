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

    private GPS gps;
    private DriveSystem driveSystem;

    @Override
    protected void onInitialize() {
        Vector2D startPosition = new Vector2D(24, 24);
        double startRotation = 0;
        gps = new GPS(hardwareMap, startPosition, startRotation);
        driveSystem = new DriveSystem(hardwareMap, gps, startPosition, startRotation);
    }

    @Override
    protected void onStart() {
        driveSystem.goTo(new Vector2D(24, 4 * 24), 0.5);
        driveSystem.goTo(new Vector2D(4.5 * 24, 4 * 24), 0.5);
        driveSystem.goTo(new Vector2D(4.5 * 24, 24), 0.5);
        driveSystem.goTo(new Vector2D(24, 24), 0.5);
    }

    @Override
    protected void onStop() {

    }
}
