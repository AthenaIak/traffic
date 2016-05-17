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
    public int adaptSpeed(int distance_to_next, int speed_of_next) {

        //We need to decelerate
        if (distance_to_next < speed) {
            speed -= Math.min(maximumDeceleration, speed-distance_to_next);
        }
        else if (distance_to_next == speed) {
            return speed;
        }

        // distance_to_next > speed ==> We may accelerate
        else {
            speed += Math.min(maximumAcceleration, Math.min(distance_to_next - speed, maximumSpeed - speed));
        }
        return speed;
    }
}
