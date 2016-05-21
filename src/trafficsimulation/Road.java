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

        Random r = new Random();
        
        int numCars = TrafficSimulation.NUMBER_OF_NORMAL_CARS + TrafficSimulation.NUMBER_OF_FAST_CARS;
        int [] lanes = new int[numCars];
        
        int numCarsInLane1 = 0;
        int numCarsInLane2 = 0;
        for (int i = 0; i < numCars; i++) {
            // randomly choose lane for cars
            lanes[i] = r.nextInt(2) + 1;
            if (lanes[i]==1) 
                numCarsInLane1++;
            else
                numCarsInLane2++;
        }

        // determine the distance between cars in each lane
        int distanceLane1;
        int distanceLane2;        
        if (numCarsInLane1 <= 1)
            distanceLane1 = TrafficSimulation.ROAD_SIZE / 2;    // middle of the road on that lane
        else
            distanceLane1 = (TrafficSimulation.ROAD_SIZE-10)/(numCarsInLane1-1);                    
        if (numCarsInLane2 <= 1)
            distanceLane2 = TrafficSimulation.ROAD_SIZE / 2;    // middle of the road on that lane
        else
            distanceLane2 = (TrafficSimulation.ROAD_SIZE-10)/(numCarsInLane2-1);

        // randomly generate a car depending on rules (to avoid collisions)
        int l1_dummyPosition = 0, l2_dummyPosition = 0, dummyPosition = 0;
        Car tmpC;
        int normal_generated = 0, fast_generated = 0;
        int lane, type_of_car, speed;
        numCarsInLane1 = 0;
        numCarsInLane2 = 0;
        for (int i = 0; i < numCars; i++) {
            // randomly choose lane
            if (lanes[i] == 1){
                dummyPosition = numCarsInLane1 * distanceLane1;
                numCarsInLane1 ++;
            }
            else {
                dummyPosition = numCarsInLane2 * distanceLane2;
                numCarsInLane2 ++;
            }

            // randomly choose the type of car (unless we the limit is reached)
            if (normal_generated == TrafficSimulation.NUMBER_OF_NORMAL_CARS)
                type_of_car = 2;
            else if (fast_generated == TrafficSimulation.NUMBER_OF_FAST_CARS)
                type_of_car = 1;
            else 
                type_of_car = r.nextInt(2) + 1;

            // create car
            if (type_of_car == 1) { // normal car to be generated
                tmpC = new NormalCar(i, lanes[i], dummyPosition);
                normal_generated++;
            } else { // fast car to be generated
                tmpC = new FastCar(i, lanes[i], dummyPosition);
                fast_generated++;
            }
            cars.add(tmpC);

            // save data to the road structure (lanes)
            if (lanes[i] == 1) l1[dummyPosition] = tmpC.getSpeed();
            else l2[dummyPosition] = tmpC.getSpeed();
        }
//        tmpC = new BrokenCar(TrafficSimulation.NUMBER_OF_FAST_CARS + TrafficSimulation.NUMBER_OF_NORMAL_CARS, r.nextInt(9) + 1, 1, dummyPosition, 0.3);
//        cars.add(tmpC);
//        l1[dummyPosition] = tmpC.getSpeed();
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
        int distanceToFront = Math.floorMod(nextCarPosition - position - 1, TrafficSimulation.ROAD_SIZE);
        int speedOfFront;
        int otherLane;
        if (lane == TrafficSimulation.RIGHT_LANE) {
            speedOfFront = l1[nextCarPosition];
            if (l2[position] != -1)
                otherLane = -1;     // another car is running in parallel in the other lane
            else
                otherLane = TrafficSimulation.LEFT_LANE;
        }
        else {
            speedOfFront = l2[nextCarPosition];
            if (l1[position] != -1)
                otherLane = -1;     // another car is running in parallel in the other lane
            else
                otherLane = TrafficSimulation.RIGHT_LANE;
        }
        int newSpeed;
        if (otherLane == -1)
            newSpeed = car.adaptSpeed(distanceToFront, speedOfFront, TrafficSimulation.MAXIMUM_SPEED_SYSTEM, 0);
        else{
            int positionOfFrontNextLane = getPositionOfNextCar(position, otherLane);
            int positionOfBehindNextLane;
            int speedOfFrontNextLane;
            if (otherLane == TrafficSimulation.RIGHT_LANE){
                speedOfFrontNextLane = l1[positionOfFrontNextLane];
                positionOfBehindNextLane = getPositionOfCarBehind(l1, otherLane);
            }
            else{
                speedOfFrontNextLane = l2[positionOfFrontNextLane];
                positionOfBehindNextLane = getPositionOfCarBehind(l2, otherLane);
            }
            newSpeed = car.adaptSpeed(distanceToFront, speedOfFront, speedOfFrontNextLane, positionOfFrontNextLane-positionOfBehindNextLane);
        }

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

    // get position of the car behind the given position current_pos on the given lane
    private int getPositionOfCarBehind(int[] lane, int current_pos) {
        for (int i = current_pos + TrafficSimulation.ROAD_SIZE; i > current_pos; i--) {
            int correct_i = Math.floorMod(i, TrafficSimulation.ROAD_SIZE);
            if (lane[correct_i] != -1) {
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
        System.out.println(traffic_l1 + "\t" + traffic_l2 + "\n");
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
