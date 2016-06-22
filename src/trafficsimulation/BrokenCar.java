package trafficsimulation;

import java.awt.*;
import java.util.Random;

public class BrokenCar extends Car {

    private final double breakDownProb = TrafficSimulation.BREAKING_DOWN_PROBABILITY;
    private final double getFixedProb = TrafficSimulation.GETTING_REPAIRED_PROBABILITY;
    private boolean isBrokenDown;

    /**
     * Creates a car. Decides speed randomly.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     * @param logLength The size of the array that will log the speed of each
     * step of the car.
     */
    public BrokenCar(int lane, int position, int logLength) {
        super(lane, position, logLength); // calls the parent constructor

        Random r;
        r = new Random();
        color = new Color(0, 255, 0);

        maximumSpeed = TrafficSimulation.MAX_SPEED_SLOW_CAR;
        maximumAcceleration = TrafficSimulation.MAX_ACCELERATION_SLOW_CAR;
        maximumDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION;
        speed = r.nextInt(maximumSpeed);
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
        float rand = new Random().nextFloat();

        if (isBrokenDown) { // if it broke decelerate until it stops
            speed = speed == 0 ? 0 : speed - maximumDeceleration;
            position = Math.floorMod(position + speed, TrafficSimulation.ROAD_SIZE);
            moved = true;
        } else {
            moved = super.move(l1, l2); // if not broken, proceed as normal cars do
        }

        // Set up things for next move:
        if (isBrokenDown) {
            if (getFixedProb > 0 && rand > 1 - getFixedProb) { // car gets fixed with a small probability
                fixCar();
            }
        } else if (rand < breakDownProb) { // car breaks down with a small probability
            breakCar();
        }

        return moved;
    }

    /**
     * Breaks the car and lets the controller know that it is broken.
     */
    private void breakCar() {
        isBrokenDown = true;
        TrafficSimulation.CAR_IS_CURRENTLY_BROKEN = true;
    }

    /**
     * Fixes the car and lets the controller know that it is fixed (works
     * correctly only when there exists only one broken car in the whole
     * system).
     */
    private void fixCar() {
        isBrokenDown = false;
        TrafficSimulation.CAR_IS_CURRENTLY_BROKEN = false;
    }

}
