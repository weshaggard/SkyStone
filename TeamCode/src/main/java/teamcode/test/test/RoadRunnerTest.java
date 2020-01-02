package teamcode.test.test;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.path.Path;
import com.acmerobotics.roadrunner.path.PathBuilder;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryGenerator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import teamcode.league3.DriveConstants;
import teamcode.league3.mecanum.SampleMecanumDriveBase;
import teamcode.league3.mecanum.SampleMecanumDriveREVOptimized;

@Autonomous(name= "sinple")
public class RoadRunnerTest extends LinearOpMode {
    SampleMecanumDriveBase drive;
    Path path;


    @Override
    public void runOpMode() {
        drive = new SampleMecanumDriveREVOptimized(hardwareMap);
        waitForStart();
        path = new PathBuilder(new Pose2d(0,0)).splineTo(new Pose2d(72,0)).splineTo(new Pose2d(72, 0, Math.toRadians(90))).build();
        Trajectory trajectory = TrajectoryGenerator.INSTANCE.generateTrajectory(path, DriveConstants.BASE_CONSTRAINTS);
        drive.followTrajectorySync(trajectory);
        drive.turnSync(Math.toRadians(90));
    }
}
