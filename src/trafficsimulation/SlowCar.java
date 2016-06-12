package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class SlowCar extends Car {

    /**
     * Creates a car.
     * @param speed The current speed of the car.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     */
    public SlowCar(int speed, int lane, int position) {
        super(speed, lane, position); // calls the parent constructor

        maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED;
        maximumAcceleration = 1;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;

        Random r = new Random();
        color = new Color(r.nextInt(130), r.nextInt(130), 255);
    }

    /**
     * Creates a car. Decides speed randomly.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     */
    public SlowCar(int lane, int position) {
        super(lane, position);
        Random r = new Random();

        // make max_speed of car not always the same
//        if (r.nextFloat()<0.5){
//            maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED - 1;
//        }else{
//            maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED + r.nextInt(1);
//        }
        // let's try if it works without this randomness
        maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED;
        speed = maximumSpeed - r.nextInt(1);    // around the max speed at beginning

        maximumAcceleration = 1;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;

        color = new Color(0, r.nextInt(130), 255);
    }

}
