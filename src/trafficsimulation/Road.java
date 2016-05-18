package trafficsimulation;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

public class Road {

    private ArrayList<Car> cars; // I chose arraylist so that it is extendable for non-circular road

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

        for (int i = 0; i < helperL1.length; i++) {
            l1[i] = -1;
            l2[i] = -1;
        }

        // randomly generate a car depending on rules (to avoid collisions)
        int l1_dummyPosition = 0, l2_dummyPosition = 0, dummyPosition = 0;
        Car tmpC;
        Random r = new Random();
        int normal_generated = 0, fast_generated = 0;
        int lane, type_of_car, speed;
        for (int i = 0; i < TrafficSimulation.NUMBER_OF_NORMAL_CARS + TrafficSimulation.NUMBER_OF_FAST_CARS; i++) {
            // randomly choose lane
            lane = r.nextInt(2) + 1;
            if (lane == 1) dummyPosition = l1_dummyPosition;
            else dummyPosition = l2_dummyPosition;

            // randomly choose the type of car (unless we the limit is reached)
            if (normal_generated == TrafficSimulation.NUMBER_OF_NORMAL_CARS)
                type_of_car = 2;
            else if (fast_generated == TrafficSimulation.NUMBER_OF_FAST_CARS)
                type_of_car = 1;
            else type_of_car = r.nextInt(2) + 1;

            // create car
            if (type_of_car == 1) { // normal car to be generated
                tmpC = new NormalCar(i, lane, dummyPosition);
                normal_generated++;
            } else { // fast car to be generated
                tmpC = new FastCar(i, lane, dummyPosition);
                fast_generated++;
            }
            cars.add(tmpC);

            // save data to the road structure (lanes)
            if (lane == 1) l1[dummyPosition] = tmpC.getSpeed();
            else l2[dummyPosition] = tmpC.getSpeed();

            // save the dummy position for the next generated cars
            // distance to next car is between [carSpeed, 2*carSpeed)
            dummyPosition = Math.floorMod(dummyPosition + tmpC.getSpeed() + r.nextInt(tmpC.getSpeed()), TrafficSimulation.ROAD_SIZE);
            if (lane == 1) l1_dummyPosition = dummyPosition;
            else l2_dummyPosition = dummyPosition;
        }
        tmpC = new BrokenCar(TrafficSimulation.NUMBER_OF_FAST_CARS + TrafficSimulation.NUMBER_OF_NORMAL_CARS, r.nextInt(9) + 1, 1, dummyPosition, 0.3);
        cars.add(tmpC);
        l1[dummyPosition] = tmpC.getSpeed();
        System.out.println("Lane 1\n" + Arrays.toString(l1) + "\n");
        System.out.println("Lane 2\n" + Arrays.toString(l2) + "\n");
    }

    public void nextState() {
        // CALCULATE NEW STATE /////////////////////////////////////////////////
        // clear helper lanes
        for (int i = 0; i < helperL1.length; i++) {
            helperL1[i] = -1;
            helperL2[i] = -1;
        }

        // move cars (check rules on current road and save new positions in next road)
        for (Car car : cars) {
            moveCar(car);
        }

        // END OF CALCULATE NEW STATE //////////////////////////////////////////
        //
        // set new state
        l1 = helperL1.clone();
        l2 = helperL2.clone();
    }

    private void moveCar(Car car) {
        int lane = car.getLane();
        int position = car.getPosition();
        int speed = car.getSpeed();

        //find new position and new speed (if there is a change in speed)
        //For now we assume that the cars don't change lanes.
        int newLane = lane;
        //To know the new speed and position, we need to know the distance to and speed of the car in front.
        int nextCarPosition = getPositionOfNextCar(position, lane);
        int distanceToNextCar = Math.floorMod(nextCarPosition - position - 1, TrafficSimulation.ROAD_SIZE);
        int nextCarSpeed;

        if (lane == 1) nextCarSpeed = l1[nextCarPosition];
        else nextCarSpeed = l2[nextCarPosition];

        int newSpeed = car.adaptSpeed(distanceToNextCar, nextCarSpeed);

        //Position depends on the speed of the car in front
        //and the speed at which that car is driving.
        int newPosition = Math.floorMod(position + speed, TrafficSimulation.ROAD_SIZE);

        // commit the changes
        car.setLane(newLane);
        car.setPosition(newPosition);
        //Setting the new speed is already done during the call to adaptSpeed();

        if (newLane == 1) {
            helperL1[newPosition] = newSpeed;
        } else {
            helperL2[newPosition] = newSpeed;
        }
    }

    private int getPositionOfNextCar(int current_pos, int lane) {
        int distance = 0;
        for (int i = current_pos + 1; i < TrafficSimulation.ROAD_SIZE + current_pos; i++) {
            int correct_i = Math.floorMod(i, TrafficSimulation.ROAD_SIZE);
            if (lane == 1 && l1[correct_i] != -1) {
                return correct_i;
            } else if (lane == 2 && l2[correct_i] != -1) {
                return correct_i;
            }
        }
        return -1;
    }

    public void printTrafficSituation() {
        String traffic_l1 = "";
        String positions = "";
        for (int i = 0; i < l1.length; i++) {
            if (l1[i] == -1) {
                traffic_l1 += "_";
            } else {
                traffic_l1 += toHex(l1[i]);
            }
        }

        traffic_l1 += "|";
        String traffic_l2 = "";
        positions = "";
        for (int i = 0; i < l2.length; i++) {
            if (l2[i] == -1) {
                traffic_l2 += "_";
            } else {
                traffic_l2 += toHex(l2[i]);
            }
        }
        traffic_l2 += "|";
        System.out.println(traffic_l1 + "\n" + traffic_l2 + "\n");
    }

    private char toHex(int input) {
        if (input <= 9 && input >= 0) {
            return Character.forDigit(input,10);
        } else {
            switch (input) {
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

                default:
                    return 'F';
            }
        }
    }

    public ArrayList<Car> getCars() {
        return cars;
    }
    
    
}
