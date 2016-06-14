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
//    public SlowCar(int speed, int lane, int position, int logLength) {
//        super(speed, lane, position, logLength); // calls the parent constructor
//
//        maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED;
//        maximumAcceleration = 1;
//        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;
//
//        Random r = new Random();
//        color = new Color(r.nextInt(130), r.nextInt(130), 255);
//    }

    /**
     * Creates a car. Decides speed randomly.
     * @param lane          The current lane of the car.
     * @param position      The current position of the car.
     * @param logLength     The number of iterations
     * @param limitSpeed    constraint speed at this time
     */
    public SlowCar(int lane, int position, int logLength, int limitSpeed) {
        super(lane, position, logLength);
        Random r = new Random();

        // make max_speed of car not always the same
//        if (r.nextFloat()<0.5){
//            maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED - 1;
//        }else{
//            maximumSpeed = TrafficSimulation.MAX_NORMAL_CAR_SPEED + r.nextInt(1);
//        }
        // let's try if it works without this randomness
        maximumSpeed = TrafficSimulation.MAX_SPEED_SLOW_CAR;
//        speed = maximumSpeed - r.nextInt(1);    // around the max speed at beginning
        speed = Math.min(maximumSpeed, limitSpeed) - r.nextInt(2);            // [maxSpeed-1, maxSpeed]


        maximumAcceleration = TrafficSimulation.MAX_ACCELERATION_SLOW_CAR;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;

        color = new Color(0, r.nextInt(130), 255);
    }

}
