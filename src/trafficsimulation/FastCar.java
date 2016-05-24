package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class FastCar extends Car {

    public FastCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);
        maximumSpeed = TrafficSimulation.MAX_FAST_CAR_SPEED;
        maximumAcceleration = TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION;
        maximumDeceleration = TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION;

        Random r = new Random();
        color = new Color(255, r.nextInt(130), 0);
    }

    // let car determines its speed
    public FastCar(int ID, int lane, int position) {
        super(ID, lane, position);
        Random r = new Random();

        // make max_speed of car not always the same
        if (r.nextFloat()<0.5){
            maximumSpeed = TrafficSimulation.MAX_FAST_CAR_SPEED - r.nextInt(2);
        }else{
            maximumSpeed = TrafficSimulation.MAX_FAST_CAR_SPEED + r.nextInt(2);
        }   
        speed = maximumSpeed - r.nextInt(1);    // around the max speed at beginning
        maximumAcceleration = TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION;
        maximumDeceleration = TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION;

        color = new Color(255, r.nextInt(130), 0);
    }
}
