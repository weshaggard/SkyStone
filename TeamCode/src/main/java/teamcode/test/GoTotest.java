package teamcode.test;


import com.acmerobotics.roadrunner.drive.Drive;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;

@Autonomous(name= "goto")
public class GoTotest extends AbstractOpMode {
    DriveSystem drive;
    GPS gps;
    @Override
    protected void onInitialize() {
        gps = new GPS(hardwareMap, new Vector2D(39.5, 7.5), 0);
        drive = new DriveSystem(hardwareMap, gps, new Vector2D(39.5, 7.5), 0);

    }

    @Override
    protected void onStart() {
        drive.lateral(30, 0.5);
    }

    @Override
    protected void onStop() {

    }
}
