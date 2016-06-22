package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class FastCar extends Car {

    /**
     * Creates a car.
     * @param speed The current speed of the car.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     * @param logLength The size of the array that will log the speed of each
     * step of the car.
     */
    public FastCar(int speed, int lane, int position, int logLength) {
        super(speed, lane, position, logLength); // calls the parent constructor
        maximumSpeed = TrafficSimulation.MAX_SPEED_FAST_CAR;
        maximumAcceleration = TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION;
        maximumDeceleration = TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION;

        Random r = new Random();
        color = new Color(255, r.nextInt(130), r.nextInt(80));
    }

    /**
     * Creates a car. Decides speed randomly.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     * @param logLength The size of the array that will log the speed of each
     * step of the car.
     */
    public FastCar(int lane, int position, int logLength) {
        super(lane, position, logLength); // calls the parent constructor
        maximumSpeed = TrafficSimulation.MAX_SPEED_FAST_CAR;
        maximumAcceleration = TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION;
        maximumDeceleration = TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION;

        Random r = new Random();
        speed = r.nextInt(maximumSpeed);
        color = new Color(255, r.nextInt(130), r.nextInt(80));
    }
}
