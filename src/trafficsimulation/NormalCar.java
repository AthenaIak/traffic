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
        color = new Color(0, r.nextInt(130), 255);
    }
    
}
