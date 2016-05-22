package trafficsimulation;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

public class Road {

    private ArrayList<Car> cars; // I chose arraylist so that it is extendable for non-circular road

    private int[] leftLane;     // lane 1 values: current speed of car (or -1 if no car)
    private int[] rightLane;    // lane 2

    private int[] helperLeft;
    private int[] helperRight;

    public Road() {
        // Initialise variables
        cars = new ArrayList<>();
        leftLane = new int[TrafficSimulation.ROAD_SIZE];
        rightLane = new int[TrafficSimulation.ROAD_SIZE];

        helperLeft = new int[TrafficSimulation.ROAD_SIZE];
        helperRight = new int[TrafficSimulation.ROAD_SIZE];

        for (int i = 0; i < helperLeft.length; i++) {
            leftLane[i] = -1;
            rightLane[i] = -1;
        }

        Random r = new Random();
        
        int numCars = TrafficSimulation.NUMBER_OF_NORMAL_CARS + TrafficSimulation.NUMBER_OF_FAST_CARS;
        int [] lanes = new int[numCars];
        
        int numCarsInLeft = 0;
        int numCarsInRight = 0;
        for (int i = 0; i < numCars; i++) {
            // randomly choose lane for cars
            lanes[i] = r.nextInt(2) + 1;
            if (lanes[i]==TrafficSimulation.LEFT_LANE) 
                numCarsInLeft++;
            else
                numCarsInRight++;
        }

        // determine the distance between cars in each lane
        int distanceLeftLane;
        int distanceRightLane;        
        if (numCarsInLeft <= 1)
            distanceLeftLane = TrafficSimulation.ROAD_SIZE / 2;    // middle of the road on that lane
        else
            distanceLeftLane = (TrafficSimulation.ROAD_SIZE-10)/(numCarsInLeft-1);                    
        if (numCarsInRight <= 1)
            distanceRightLane = TrafficSimulation.ROAD_SIZE / 2;    // middle of the road on that lane
        else
            distanceRightLane = (TrafficSimulation.ROAD_SIZE-10)/(numCarsInRight-1);

        // randomly generate a car depending on rules (to avoid collisions)
        int l1_dummyPosition = 0, l2_dummyPosition = 0, dummyPosition = 0;
        Car tmpC;
        int normal_generated = 0, fast_generated = 0;
        int lane, type_of_car, speed;
        numCarsInLeft = 0;
        numCarsInRight = 0;
        for (int i = 0; i < numCars; i++) {
            // randomly choose lane
            if (lanes[i] == TrafficSimulation.LEFT_LANE){
                dummyPosition = numCarsInLeft * distanceLeftLane;
                numCarsInLeft ++;
            }
            else {
                dummyPosition = numCarsInRight * distanceRightLane;
                numCarsInRight ++;
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
            if (lanes[i] == TrafficSimulation.LEFT_LANE) leftLane[dummyPosition] = tmpC.getSpeed();
            else rightLane[dummyPosition] = tmpC.getSpeed();
        }
        
        // get lane for broken car
        lane = r.nextInt(1) + 1;
        int [] laneOfBroken;
        if (lane==TrafficSimulation.LEFT_LANE)
            laneOfBroken = leftLane;
        else
            laneOfBroken = rightLane;
        
        while (true){
            dummyPosition = r.nextInt(TrafficSimulation.ROAD_SIZE/4);
            if (laneOfBroken[dummyPosition] == -1)
                break;
        }            
        
        tmpC = new BrokenCar(TrafficSimulation.NUMBER_OF_FAST_CARS + TrafficSimulation.NUMBER_OF_NORMAL_CARS, 0, lane, dummyPosition, 0.3);
        cars.add(tmpC);
        laneOfBroken[dummyPosition] = tmpC.getSpeed();
        
        System.out.println("Lane 1\n" + Arrays.toString(leftLane) + "\n");
        System.out.println("Lane 2\n" + Arrays.toString(rightLane) + "\n");
    }

    public void nextState() {
        // CALCULATE NEW STATE /////////////////////////////////////////////////
        // clear helper lanes
        for (int i = 0; i < helperLeft.length; i++) {
            helperLeft[i] = -1;
            helperRight[i] = -1;
        }

        // move cars (check rules on current road and save new positions in next road)
        for (Car car : cars) {
            moveCar(car);
        }

        // END OF CALCULATE NEW STATE //////////////////////////////////////////
        //
        // set new state
        leftLane = helperLeft.clone();
        rightLane = helperRight.clone();
    }

    private void moveCar(Car car) {
        int lane = car.getLane();
        int position = car.getPosition();
        int speed = car.getSpeed();

        int positionOfCarFront = getPositionOfCarFront(lane, position);
        int distanceToFront = getDistanceBetweenCars(positionOfCarFront, position);      
        int speedOfFront;
        int[] otherLane;        
        if (lane == TrafficSimulation.RIGHT_LANE) {
            speedOfFront = getSpeedOfCar(rightLane, positionOfCarFront);
            if (leftLane[position] != -1)
                otherLane = null;     // another car is running in parallel on the other lane
            else
                otherLane = leftLane;
        }
        else {
            speedOfFront = getSpeedOfCar(leftLane, positionOfCarFront);
            if (rightLane[position] != -1)
                otherLane = null;     // another car is running in parallel in the other lane
            else
                otherLane = rightLane;
        }

        int newSpeed;
        if (otherLane == null)
            newSpeed = car.adaptSpeed(speedOfFront, distanceToFront, TrafficSimulation.MAXIMUM_SPEED_SYSTEM, 0, 0, 0);
        else{
            int positionOfFrontNextLane = getPositionOfCarFront(otherLane, position);
            int positionOfBehindNextLane = getPositionOfCarBehind(otherLane, position);
            newSpeed = car.adaptSpeed(speedOfFront, distanceToFront, 
                    getSpeedOfCar(otherLane, positionOfFrontNextLane), getDistanceBetweenCars(positionOfFrontNextLane, position), 
                    getSpeedOfCar(otherLane, positionOfBehindNextLane), getDistanceBetweenCars(positionOfBehindNextLane, position));
        }

        //Position depends on the speed of the car in front
        //and the speed at which that car is driving.
        int newPosition = Math.floorMod(position + newSpeed, TrafficSimulation.ROAD_SIZE);

        int newLane = car.lane;
        
        // commit the changes
        car.setLane(newLane);
        car.setPosition(newPosition);
        //Setting the new speed is already done during the call to adaptSpeed();

        if (newLane == 1) {
            helperLeft[newPosition] = newSpeed;
        } else {
            helperRight[newPosition] = newSpeed;
        }
    }

    private int getSpeedOfCar(int lane, int position) {
        if (lane == TrafficSimulation.LEFT_LANE)
            return getSpeedOfCar(leftLane, position);
        else
            return getSpeedOfCar(rightLane, position);
    }
    
    private int getSpeedOfCar(int[] lane, int position) {
        if (position >= TrafficSimulation.ROAD_SIZE || position < -1){
            System.out.println("Road.getSpeedOfCar() wrong position");
            return -1;
        }
            
        if (position == -1)
            return 0;   // no car
        else
            return lane[position];
    }
    
    private int getDistanceBetweenCars(int current_pos, int posOther) {
        if (posOther == -1)
            return TrafficSimulation.ROAD_SIZE;
        else
            return Math.abs(current_pos - posOther - 1);
    }
    
    private int getPositionOfCarFront(int lane, int current_pos) {
        if (lane == TrafficSimulation.LEFT_LANE)
            return getPositionOfCarFront(leftLane, current_pos);
        else
            return getPositionOfCarFront(rightLane, current_pos);
    }  
    
    private int getPositionOfCarFront(int[] lane, int current_pos) {
        for (int i = current_pos + 1; i < TrafficSimulation.ROAD_SIZE; i++) {
            if (lane[i] != -1)
                return i;
        }      
        return -1;      // no car in front
    }

    private int getPositionOfCarBehind(int lane, int current_pos) {
        if (lane == TrafficSimulation.LEFT_LANE)
            return getPositionOfCarBehind(leftLane, current_pos);
        else
            return getPositionOfCarBehind(rightLane, current_pos);      
    }
    
    private int getPositionOfCarBehind(int[] lane, int current_pos) {
        for (int i = current_pos-1; i >= 0; i--) {
            if (lane[i] != -1)
                return i;
        }
        return -1;      // no car behind
    }
    
    public void printTrafficSituation() {
        String traffic_l1 = "";
        String positions = "";
        for (int i = 0; i < leftLane.length; i++) {
            if (leftLane[i] == -1) {
                traffic_l1 += "_";
            } else {
                traffic_l1 += toHex(leftLane[i]);
            }
        }

        traffic_l1 += "|";
        String traffic_l2 = "";
        positions = "";
        for (int i = 0; i < rightLane.length; i++) {
            if (rightLane[i] == -1) {
                traffic_l2 += "_";
            } else {
                traffic_l2 += toHex(rightLane[i]);
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
