package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class NormalCar extends Car{

    public NormalCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);

        maximumSpeed = 3;
        maximumAcceleration = 1;
        maximumDeceleration = 1;
        
        Random r = new Random();
        color = new Color(r.nextInt(130), r.nextInt(130), 255);
    }
    
    public NormalCar(int ID, int lane, int position) {
        super(ID, lane, position);

        Random r = new Random();
        
        // make max_speed of car not always the same
        if (r.nextFloat()<0.5){
            maximumSpeed = TrafficSimulation.MAXIMUM_SPEED_BY_LAW_OF_NORMAL_CARS - 1;
        }else{
            maximumSpeed = TrafficSimulation.MAXIMUM_SPEED_BY_LAW_OF_NORMAL_CARS + r.nextInt(1);
        }
        maximumAcceleration = 1;
        maximumDeceleration = 2;
        
        color = new Color(r.nextInt(130), r.nextInt(130), 255);
        speed = maximumSpeed - r.nextInt(1);    // around the max speed at beginning
    }
    
}
