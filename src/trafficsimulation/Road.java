package trafficsimulation;

import java.util.ArrayList;
import java.util.Random;

public class Road {

    private final ArrayList<Car> cars;

    private int[] rightLane; // lane 1 values: current speed of car (or -1 if no car)
    private int[] leftLane; // lane 2

    private final int[] helperRightLane;
    private final int[] helperLeftLane;

    private boolean isStable;

    /**
     * Creates a road with cars.
     * @param numIterations The number of states the simulation is set to have
     * (0 if infinite).
     */
    public Road(int numIterations) {
        // initialise variables
        cars = new ArrayList<>();
        leftLane = new int[TrafficSimulation.ROAD_SIZE];
        rightLane = new int[TrafficSimulation.ROAD_SIZE];

        helperLeftLane = new int[TrafficSimulation.ROAD_SIZE];
        helperRightLane = new int[TrafficSimulation.ROAD_SIZE];

        for (int i = 0; i < leftLane.length; i++) {
            leftLane[i] = -1;
            rightLane[i] = -1;
        }

        // randomly generate all the cars
        int leftLane_dummyPosition = 0, rightLane_dummyPosition = 0, dummyPosition = 0, normal_generated = 0, fast_generated = 0, lane, type_of_car;
        Car tmpC;
        Random r = new Random();

        // generate a car that will break down (it will become an obstacle on the road)
        if (TrafficSimulation.BREAKING_DOWN_PROBABILITY != 0) {
            lane = r.nextInt(2) + 1;
            tmpC = new BrokenCar(lane, dummyPosition, numIterations);
            cars.add(tmpC);
            if (lane == 1) rightLane[dummyPosition] = tmpC.getSpeed();
            else leftLane[dummyPosition] = tmpC.getSpeed();
        }

        for (int i = 0; i < TrafficSimulation.NUM_SLOW_CARS + TrafficSimulation.NUM_FAST_CARS; i++) {
            // randomly choose a lane
            lane = r.nextInt(2) + 1;
            // retrieve position for the (soon to be generated) car to be placed
            if (lane == 1) {
                dummyPosition = leftLane_dummyPosition;
            } else {
                dummyPosition = rightLane_dummyPosition;
            }

            // randomly choose the type of car (unless the limit is reached)
            if (normal_generated == TrafficSimulation.NUM_SLOW_CARS) {
                type_of_car = 2;
            } else if (fast_generated == TrafficSimulation.NUM_FAST_CARS) {
                type_of_car = 1;
            } else {
                type_of_car = r.nextInt(2) + 1;
            }

            // generate the car and add it to the list of cars
            if (type_of_car == 1) {
                tmpC = new SlowCar(lane, dummyPosition, numIterations);
                normal_generated++;
            } else {
                tmpC = new FastCar(lane, dummyPosition, numIterations);
                fast_generated++;
            }
            cars.add(tmpC);

            // save data to the road structure (lanes)
            if (lane == 1) {
                leftLane[dummyPosition] = tmpC.getSpeed();
            } else {
                rightLane[dummyPosition] = tmpC.getSpeed();
            }

            // set the position for the next car that will be generated in the same lane
            dummyPosition = Math.floorMod(dummyPosition + tmpC.getSpeed() + tmpC.getSpeed() / 2 + r.nextInt(tmpC.getSpeed() + 1), TrafficSimulation.ROAD_SIZE);
            if (lane == 1) {
                leftLane_dummyPosition = dummyPosition;
            } else {
                rightLane_dummyPosition = dummyPosition;
            }
        }

        isStable = false;

    }

    /**
     * Calculates and moves the road to the next state (the state of the next
     * time instance).
     * @return True if the system is stable.
     */
    public boolean nextState() {
        boolean helperLegalMoveCheck = true;

        // CALCULATE NEW STATE /////////////////////////////////////////////////
        // clear helper lanes
        for (int i = 0; i < helperRightLane.length; i++) {
            helperRightLane[i] = -1;
            helperLeftLane[i] = -1;
        }

        // move cars (check rules on current road and save new positions in next road)
        for (Car car : cars) {
            if (!car.move(rightLane, leftLane)) {
                helperLegalMoveCheck = false;
            }
            if (car.getLane() == 1) {
                helperRightLane[car.getPosition()] = car.getSpeed();
            } else {
                helperLeftLane[car.getPosition()] = car.getSpeed();
            }
        }
        // END OF CALCULATE NEW STATE //////////////////////////////////////////

        // set new state
        rightLane = helperRightLane.clone();
        leftLane = helperLeftLane.clone();

        // set isStable as true only if all the cars had legal moves while moving 
        // to the next state of the simulation
        if (helperLegalMoveCheck) {
            if (!isStable) {
                // when the system is stable, clear all distances traveled until this stable state
                for (Car c : cars) {
                    c.clearTraveledDistance();
                }
                isStable = true;
            }
        }
        return isStable;
    }

    /**
     * Prints the current state of the road to the console.
     */
    public void printTrafficSituation() {
        String traffic_l1 = "|", traffic_l2 = "|";

        for (int i = 0; i < rightLane.length; i++) {
            traffic_l1 += toSymbol(rightLane[i]);
            traffic_l2 += toSymbol(leftLane[i]);
        }

        traffic_l1 += "|";
        traffic_l2 += "|";

        System.out.println(traffic_l1 + "\n" + traffic_l2 + "\n");
    }

    /**
     *
     * @return The list of cars in the road.
     */
    public ArrayList<Car> getCars() {
        return cars;
    }

    /**
     * Helper method for printTrafficSituation(). Converts speeds with more than
     * 2 digits to characters (Hex encoding). Does not change 1 digit speeds. If
     * the speed is -1 it returns an underscore.
     *
     * @param speed An integer speed or -1 if no car is present.
     * @return A character that represents the input speed.
     */
    private char toSymbol(int speed) {
        if (speed >= 0 && speed <= 9) {
            return Character.forDigit(speed, 10);
        } else {
            switch (speed) {
                case -1:
                    return '_';
                case 10:
                    return 'A';
                case 11:
                    return 'B';
                case 12:
                    return 'C';

                case 13:
                    return 'D';
                case 14:
                    return 'E';
                case 15:
                    return 'F';
                default:
                    return '?';
            }
        }
    }
}
