package trafficsimulation;

import java.awt.*;
import java.util.Random;

public class BrokenCar extends Car {

    private double breakDownProb = 0.05;
    private double getFixedProb = 0.05;
    private boolean isBrokenDown;
    private Random r;

    public BrokenCar(int ID, int speed, int lane, int position, double breakDownProb) {
        super(ID, speed, lane, position);

        maximumSpeed = 4;
        maximumAcceleration = 1;
        maximumDeceleration = 1;
        this.breakDownProb = breakDownProb;
        isBrokenDown = false;

        r = new Random();
        color = new Color(0, 255, 0);
    }

    @Override
    public boolean move(int[] l1, int[] l2) {
        boolean moved;
        float rand;

        if (isBrokenDown) { // if it broke decelerate by 1 until it stops
            speed = speed == 0 ? 0 : speed - 1;
            position = Math.floorMod(position + speed, TrafficSimulation.ROAD_SIZE);
            moved = true;
        } else moved = super.move(l1, l2); // if not broken, proceed as normal

        // Set up things for next move:
        rand = r.nextFloat();
        if (rand < breakDownProb) // car breaks down with a small probability
            isBrokenDown = true;

        if (TrafficSimulation.REPAIR_BROKEN_CAR && rand > 1 - getFixedProb) // car gets fixed with a small probability
            isBrokenDown = false;

        return moved;
    }

}
