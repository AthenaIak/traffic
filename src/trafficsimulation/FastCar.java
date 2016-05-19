package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class FastCar extends Car {

    public FastCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);
        maximumSpeed = TrafficSimulation.MAX_FAST_CAR_SPEED;
        maximumAcceleration = 2;
        maximumDeceleration = 2;

        Random r = new Random();
        color = new Color(255, r.nextInt(130), 0);
    }

}
