package trafficsimulation;

import java.awt.*;
import java.util.Random;

public class BrokenCar extends Car {

    private final double breakDownProb = TrafficSimulation.BREAKING_DOWN_PROBABILITY;
    private final double getFixedProb = TrafficSimulation.GETTING_REPAIRED_PROBABILITY;
    private boolean isBrokenDown;
    private Random r;

    /**
     * Creates a car. Decides speed randomly.
     *
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     * @param logLength The number of iterations
     * @param limitSpeed constraint speed at this time
     */
    public BrokenCar(int limitSpeed, int lane, int position, int logLength) {
        super(lane, position, logLength); // calls the parent constructor

        r = new Random();
        color = new Color(0, 255, 0);

        maximumSpeed = TrafficSimulation.MAX_SPEED_SLOW_CAR;
        maximumAcceleration = TrafficSimulation.MAX_ACCELERATION_SLOW_CAR;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;
        speed = Math.min(maximumSpeed, limitSpeed) - r.nextInt(2);            // [maxSpeed-1, maxSpeed]
        isBrokenDown = false;
    }

    /**
     * Moves if it is not currently broken. If broken, it decelerates until it
     * stops. It remains stopped until it is repaired (then it moves normally
     * again).
     *
     * @param l1 Right lane.
     * @param l2 Left lane.
     * @return True if it moved (or remained stopped) without a conflict.
     */
    @Override
    public boolean move(int[] l1, int[] l2) {
        boolean moved;
        float rand;

        if (isBrokenDown) { // if it broke decelerate by 1 until it stops
            speed = speed == 0 ? 0 : speed - 1;
            position = Math.floorMod(position + speed, TrafficSimulation.ROAD_SIZE);
            moved = true;
        } else {
            moved = super.move(l1, l2); // if not broken, proceed as normal
        }
        // Set up things for next move:
        rand = r.nextFloat();
        if (isBrokenDown) {
            if (getFixedProb > 0 && rand > 1 - getFixedProb) { // car gets fixed with a small probability
                isBrokenDown = false;
                TrafficSimulation.GLOBAL_SPEED_RULE = false;
            }
        } else if (rand < breakDownProb) { // car breaks down with a small probability
            isBrokenDown = true;
            TrafficSimulation.GLOBAL_SPEED_RULE = true;
        }
        return moved;
    }

}
