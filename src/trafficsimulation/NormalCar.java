package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class NormalCar extends Car{

    public NormalCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);

        maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED;
        maximumAcceleration = 1;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;
        
        Random r = new Random();
        color = new Color(r.nextInt(130), r.nextInt(130), 255);
    }
    
    public NormalCar(int ID, int lane, int position) {
        super(ID, lane, position);
        Random r = new Random();
        
        // make max_speed of car not always the same
        if (r.nextFloat()<0.5){
            maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED - 1;
        }else{
            maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED + r.nextInt(1);
        }
        speed = maximumSpeed - r.nextInt(1);    // around the max speed at beginning

        maximumAcceleration = 1;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;
        
        color = new Color(0, r.nextInt(130), 255);
    }
    
}
