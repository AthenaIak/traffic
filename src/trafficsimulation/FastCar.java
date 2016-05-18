package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class FastCar extends Car {

    public FastCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);
        maximumSpeed = 6;
        maximumAcceleration = 2;
        maximumDeceleration = 2;

        Random r = new Random();
        color = new Color(255, r.nextInt(130), r.nextInt(130));
    }

    public FastCar(int ID, int lane, int position) {
        super(ID, lane, position);
        maximumSpeed = TrafficSimulation.MAXIMUM_SPEED_OF_FAST_CARS;
        maximumAcceleration = 2;
        maximumDeceleration = 2;

        Random r = new Random();
        color = new Color(255, r.nextInt(130), r.nextInt(130));
        speed = maximumSpeed - r.nextInt(1);    // around the max speed at beginning
    }
    
}
