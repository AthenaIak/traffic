package trafficsimulation;

import java.util.ArrayList;
import java.util.Random;

public class Road {

    private ArrayList<Car> cars;

    private int[] l1; // lane 1 values: current speed of car (or -1 if no car)
    private int[] l2; // lane 2

    private int[] helperL1;
    private int[] helperL2;

    public Road() {
        // Initialise variables
        cars = new ArrayList<>();
        l1 = new int[TrafficSimulation.ROAD_SIZE];
        l2 = new int[TrafficSimulation.ROAD_SIZE];

        helperL1 = new int[TrafficSimulation.ROAD_SIZE];
        helperL2 = new int[TrafficSimulation.ROAD_SIZE];

        for (int i = 0; i < l1.length; i++) {
            l1[i] = -1;
            l2[i] = -1;
        }

        // randomly generate all the cars
        int l1_dummyPosition = 0, l2_dummyPosition = 0, dummyPosition = 0, normal_generated = 0, fast_generated = 0, lane, type_of_car;
        Car tmpC;
        Random r = new Random();
        for (int i = 0; i < TrafficSimulation.NUMBER_OF_NORMAL_CARS + TrafficSimulation.NUMBER_OF_FAST_CARS; i++) {
            // randomly choose lane
            lane = r.nextInt(2) + 1;
            // retrieve position for the (soon to be generated) car to be placed
            if (lane == 1) dummyPosition = l1_dummyPosition;
            else dummyPosition = l2_dummyPosition;

            // randomly choose the type of car (unless the limit is reached)
            if (normal_generated == TrafficSimulation.NUMBER_OF_NORMAL_CARS)
                type_of_car = 2;
            else if (fast_generated == TrafficSimulation.NUMBER_OF_FAST_CARS)
                type_of_car = 1;
            else type_of_car = r.nextInt(2) + 1;

            // generate the car and add it to the list of cars
            if (type_of_car == 1) {
                tmpC = new SlowCar(lane, dummyPosition);
                normal_generated++;
            } else {
                tmpC = new FastCar(lane, dummyPosition);
                fast_generated++;
            }
            cars.add(tmpC);

            // save data to the road structure (lanes)
            if (lane == 1) l1[dummyPosition] = tmpC.getSpeed();
            else l2[dummyPosition] = tmpC.getSpeed();

            // follow the 2-seconds rule with randomness, so cars distance will be between [1.5 speed, 2.5 speed]
            dummyPosition = Math.floorMod(dummyPosition + tmpC.getSpeed() + tmpC.getSpeed() / 2 + r.nextInt(tmpC.getSpeed()), TrafficSimulation.ROAD_SIZE);

            if (lane == 1) l1_dummyPosition = dummyPosition;
            else l2_dummyPosition = dummyPosition;
        }

        // generate a car that will break down (it will become an obstacle on the road)
        if (TrafficSimulation.BREAKING_DOWN_PROBABILITY != 0) {
            tmpC = new BrokenCar(r.nextInt(9) + 1, r.nextInt(2) + 1, dummyPosition);
            cars.add(tmpC);
            l1[dummyPosition] = tmpC.getSpeed();
        }
    }

    public void nextState() {
        // TODO: calculate the max speed of each lane right now and enforce a limit

        // CALCULATE NEW STATE /////////////////////////////////////////////////
        // clear helper lanes
        for (int i = 0; i < helperL1.length; i++) {
            helperL1[i] = -1;
            helperL2[i] = -1;
        }

        // move cars (check rules on current road and save new positions in next road)
        for (Car car : cars) {
            if (!car.move(l1, l2)) {
                System.out.println("Car decelerated because it had no legal moves! \n" + car);
            }
            if (car.getLane() == 1)
                helperL1[car.getPosition()] = car.getSpeed();
            else helperL2[car.getPosition()] = car.getSpeed();
        }
        // END OF CALCULATE NEW STATE //////////////////////////////////////////

        // set new state
        l1 = helperL1.clone();
        l2 = helperL2.clone();
    }

    /**
     * Prints the current state of the road to the console.
     */
    public void printTrafficSituation() {
        String traffic_l1 = "|", traffic_l2 = "|";

        for (int i = 0; i < l1.length; i++) {
            traffic_l1 += toSymbol(l1[i]);
            traffic_l2 += toSymbol(l2[i]);
        }

        traffic_l1 += "|";
        traffic_l2 += "|";

        System.out.println(traffic_l1 + "\n" + traffic_l2 + "\n");
    }

    /**
     * Calculates all the statistics for the simulation.
     * @param numIterations The number of states the simulation has.
     * @return The statistics of the simulation.
     */
    public String getStatistics(int numIterations) {
        //TODO implement this method! (calculate flows etc)
        return "not implemented yet";
    }

    // Will probably be deleted (and replaced with getStatistics()).
    public void printFlow(int numIterations) {
        int totalDistance = 0;
        for (Car c : cars) {
            totalDistance += c.getTraveledDistance();
        }
        double flow = ((double) totalDistance) / (numIterations * TrafficSimulation.ROAD_SIZE * 2 * (TrafficSimulation.NUMBER_OF_NORMAL_CARS + TrafficSimulation.NUMBER_OF_FAST_CARS));
        System.out.println("Flow per lane: " + flow);
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
