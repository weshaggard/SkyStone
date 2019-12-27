package teamcode.test.RoadRunner;

import com.acmerobotics.roadrunner.drive.Drive;
import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.drive.MecanumDrive;
import com.acmerobotics.roadrunner.followers.TrajectoryFollower;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.kinematics.MecanumKinematics;
import com.acmerobotics.roadrunner.path.heading.ConstantInterpolator;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.ArrayList;
import java.util.List;

import teamcode.common.AbstractOpMode;
import teamcode.common.Vector2D;
import teamcode.league3.Constants;
import teamcode.league3.DriveSystem;
import teamcode.league3.GPS;
import teamcode.test.RoadRunner.mecanum.SampleMecanumDriveREV;
import teamcode.test.RoadRunner.mecanum.SampleMecanumDriveREVOptimized;
import teamcode.test.examples.GlobalCoordinatePositionUpdateSample;


@Disabled
@Autonomous(name="RoadRunner")
public class RoadRunnerTest extends AbstractOpMode {
    SampleMecanumDriveREVOptimized drive;



    @Override
    protected void onInitialize() {
        drive = new SampleMecanumDriveREVOptimized(hardwareMap);
        MecanumConstraints constraints = new MecanumConstraints(new DriveConstraints(0.7, 0.1, 0.05, 0.6, 0.1, 0.05), 8.0);

    }

    @Override
    protected void onStart() {
        Trajectory trajectory = drive.trajectoryBuilder().forward(60).build();
        if(isStopRequested()){
            return;
        }
        drive.followTrajectorySync(trajectory);


    }

    @Override
    protected void onStop() {

    }
}
