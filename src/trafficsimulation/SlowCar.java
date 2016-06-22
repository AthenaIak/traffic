package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class SlowCar extends Car {

    /**
     * Creates a car.
     * @param speed The current speed of the car.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     * @param logLength The size of the array that will log the speed of each
     * step of the car.
     */
    public SlowCar(int speed, int lane, int position, int logLength) {
        super(speed, lane, position, logLength); // calls the parent constructor

        maximumSpeed = TrafficSimulation.MAX_SPEED_SLOW_CAR;
        maximumAcceleration = 1;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;

        Random r = new Random();
        color = new Color(r.nextInt(80), r.nextInt(130), 255);
    }

    /**
     * Creates a car. Decides speed randomly.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     * @param logLength The size of the array that will log the speed of each
     * step of the car.
     */
    public SlowCar(int lane, int position, int logLength) {
        super(lane, position, logLength); // calls the parent constructor

        maximumSpeed = TrafficSimulation.MAX_SPEED_SLOW_CAR;
        maximumAcceleration = 1;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;

        Random r = new Random();
        speed = r.nextInt(maximumSpeed);
        color = new Color(r.nextInt(80), r.nextInt(130), 255);
    }
}
