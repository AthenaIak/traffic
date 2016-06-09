package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class FastCar extends Car {

    /**
     * Creates a car.
     * @param speed The current speed of the car.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     */
    public FastCar(int speed, int lane, int position) {
        super(speed, lane, position); // calls the parent constructor
        maximumSpeed = TrafficSimulation.MAX_FAST_CAR_SPEED;
        maximumAcceleration = TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION;
        maximumDeceleration = TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION;

        Random r = new Random();
        color = new Color(255, r.nextInt(130), 0);
    }

    /**
     * Creates a car. Decides speed randomly.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     */
    public FastCar(int lane, int position) {
        super(lane, position);
        Random r = new Random();

        // make max_speed of car not always the same
//        maximumSpeed = TrafficSimulation.MAX_FAST_CAR_SPEED - r.nextInt(2);
        // let's try if it works without this randomness
        maximumSpeed = TrafficSimulation.MAX_FAST_CAR_SPEED;

        speed = r.nextInt(maximumSpeed) + 1;
        maximumAcceleration = TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION;
        maximumDeceleration = TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION;

        color = new Color(255, r.nextInt(130), 0);
    }
}
