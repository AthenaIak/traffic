package trafficsimulation;

import java.awt.*;
import java.util.Random;

public class SleepyCar extends Car {

    public SleepyCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);

        maximumSpeed = 9;
        maximumAcceleration = 3;
        maximumDeceleration = 3;

        Random r = new Random();
        color = new Color(r.nextInt(130), r.nextInt(130), 255);
    }


    /**
     * Method to adapt the speed of the car according to the distance to the next car
     * on the road. This method assumes the driver is sleepy; he does not estimate the speed
     * of the car in front correctly, but merely uses the distance to the car to adapt his speed.
     *
     * @param distance_to_next : distance to car in front.
     * @return : new speed of this car.
     */
    @Override
//    public int adaptSpeed(int distance_to_next, int speed_of_next) {
    public int adaptSpeed(int speedOfFront, int distanceToFront, int speedOfFrontNextLane, int distanceToFrontNextLane, 
            int speedOfBehindNextLane, int distanceToBehindNextLane) {

        //We need to decelerate
        if (distanceToFront < speed) {
            speed -= Math.min(maximumDeceleration, speed-distanceToFront);
        }
        else if (distanceToFront == speed) {
            return speed;
        }

        // distance_to_next > speed ==> We may accelerate
        else {
            speed += Math.min(maximumAcceleration, Math.min(distanceToFront - speed, maximumSpeed - speed));
        }
        return speed;
    }
}
