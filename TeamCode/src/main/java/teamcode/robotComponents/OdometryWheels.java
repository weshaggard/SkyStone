package teamcode.robotComponents;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

import teamcode.common.Vector2;


public class OdometryWheels {
    private DcMotor XWheel;
    private DcMotor YWheel;
    private List<Vector2> compositeComponents;
    private List<Vector2> path;
    private long apex;
    private int compositeTickX;
    private int compositeTickY;


    public OdometryWheels(HardwareMap hardwareMap, List<Vector2> path){
        XWheel = hardwareMap.get(DcMotor.class, "Xwheel Odometry");
        YWheel = hardwareMap.get(DcMotor.class, "YWheel Odometry");
        apex = System.currentTimeMillis();
        this.path = path;
        XWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        YWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        XWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        YWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void updateComposite(){
        int currentTickX = XWheel.getCurrentPosition();
        int currentTickY = YWheel.getCurrentPosition();
        compositeTickX += currentTickX;
        compositeTickY += currentTickY;
        long currentTime = System.currentTimeMillis() - apex;
        double xVelocity = (((double)currentTickX) / ((double) currentTime));
        double yVelocity = (((double)currentTickY) / ((double) currentTime));
        Vector2 velocity = new Vector2(xVelocity, yVelocity);
        compositeComponents.add(velocity);
        comparePaths();
    }

    private void comparePaths() {
        for(int i = 0; i < compositeComponents.size(); i++){
            if(compositeComponents.get(i).getDirection() == path.get(i).getDirection()){
                //good case, nothing is necessary
            }else{
                double direction = path.get(i).angleBetween(compositeComponents.get(i));

                //bad case, need to self correct.
            }
        }
    }
}
