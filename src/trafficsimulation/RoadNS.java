package trafficsimulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


/*==============================================================================
Implement NS model based on paper Two-lane traffic rules for cellular Automata
*==============================================================================*/

public class RoadNS {
    
    public static int ROAD_SIZE;                      // number of cells
    public static int NUM_FAST_CARS;
    public static int NUM_SLOW_CARS;
    public static int NUM_ITERATIONS;
    public static boolean HAS_BROKEN_CAR;
    
    public static double CELL_LENGTH = 7.5;           // meter
    public static int MAX_SPEED_FAST_CAR = 5;
    public static int MAX_SPEED_SLOW_CAR = 3;
    public static double PROBABILITY_FLUCTUATION = .25;
        
    public static int NUM_LANES = 2;
    public static int RIGHT_LANE = 1;
    public static int LEFT_LANE = 2;
    
    public static int NUM_TYPE_CAR = 2;
    public static int TYPE_CAR_SLOW = 1;
    public static int TYPE_CAR_FAST = 2;
    
    public static boolean DEBUG = true;
    public static int DISTANCE_TO_LOOK_AHEAD = 16;
            
    private ArrayList<CarNS> cars;                // contains cars on the road

    private int[] rightLane;                    // values: current speed of car (or -1 if no car)
    private int[] leftLane;

    private int[] helperRight;
    private int[] helperLeft;
    
    
    public RoadNS(int roadLength, int numFastCars, int numSlowCars, int numIterations, boolean hasBrokenCar){
//         Input:
//            roadLength       length of road in meter, should be a multiple of 7.5 x 1000, 7.5 is cell length of NS model, multiple of km 
//            numFastCars      number of fast cars
//            numSlowCars      number of slow cars
//            numIterations    number of iterations or measure time
//            hasBrokenCar     True/False, indicating if road is blocked or not
        
        // parameters of the model
        ROAD_SIZE = (int)(roadLength/CELL_LENGTH);
        NUM_FAST_CARS = numFastCars;
        NUM_SLOW_CARS = numSlowCars;
        NUM_ITERATIONS = numIterations;
        HAS_BROKEN_CAR = hasBrokenCar;
        
        // Initialise variables
        cars = new ArrayList<>();
        rightLane = new int[ROAD_SIZE];
        leftLane = new int[ROAD_SIZE];

        helperRight = new int[ROAD_SIZE];
        helperLeft = new int[ROAD_SIZE];

        for (int i = 0; i < rightLane.length; i++) {
            rightLane[i] = -1;
            leftLane[i] = -1;
        }
        
        generateCars();
        printTrafficSituation();
    }

    
    /*==============================================================================
    Generate cars for the model
    - position of car, lane of car, speed of car are randomly generated
    - distance between a car and the car in front of it (same lane) is 2 x speed of the car
    *==============================================================================*/
    public void generateCars(){
        // randomly generate all the cars
        
        int rightLane_dummyPosition = 0, leftLane_dummyPosition = 0, dummyPosition = 0, slow_generated = 0, fast_generated = 0, lane, type_of_car;
        CarNS tmpC;
        Random r = new Random();
        int meanDistance = (int) (ROAD_SIZE * 2 / (NUM_FAST_CARS + NUM_SLOW_CARS));
        
        for (int i = 0; i < NUM_FAST_CARS + NUM_SLOW_CARS; i++) {
            
            // randomly choose lane
            lane = r.nextInt(NUM_LANES) + 1;
            
            // retrieve position for the (soon to be generated) car to be placed
            if (lane == RIGHT_LANE) dummyPosition = rightLane_dummyPosition;
            else dummyPosition = leftLane_dummyPosition;

            // randomly choose the type of car (unless the limit is reached)
            if (slow_generated == NUM_SLOW_CARS)            // limit is reached
                type_of_car = TYPE_CAR_FAST;
            else if (fast_generated == NUM_FAST_CARS)       // limit is reached
                type_of_car = TYPE_CAR_SLOW;
            else type_of_car = r.nextInt(NUM_TYPE_CAR) + 1; // randomly

            // generate the car and add it to the list of cars
            if (type_of_car == TYPE_CAR_SLOW) {
                tmpC = new SlowCarNS(i, lane, dummyPosition);
                slow_generated++;
            } else {
                tmpC = new FastCarNS(i, lane, dummyPosition);
                fast_generated++;
            }
            cars.add(tmpC);

            // save data to the road structure (lanes)
            if (lane == RIGHT_LANE) rightLane[dummyPosition] = tmpC.getSpeed();
            else leftLane[dummyPosition] = tmpC.getSpeed();

            // follow the 2-seconds rule with randomness, so cars distance will be between [1.5 speed, 2.5 speed]
//            dummyPosition = Math.floorMod(dummyPosition + tmpC.getSpeed() + tmpC.getSpeed() / 2 + r.nextInt(tmpC.getSpeed()), ROAD_SIZE);

            //
            dummyPosition = Math.floorMod(dummyPosition + r.nextInt(meanDistance) + 1, ROAD_SIZE);

            // set the position for the next car
            if (lane == RIGHT_LANE) rightLane_dummyPosition = dummyPosition;
            else leftLane_dummyPosition = dummyPosition;
        }
        
        // generate a car that will break down (it will become an obstacle on the road)
        if (HAS_BROKEN_CAR) {
//            tmpC = new BrokenCar(r.nextInt(9) + 1, r.nextInt(2) + 1, dummyPosition);
//            cars.add(tmpC);
//            
//            // TODO: randomly select blocked lane
//            rightLane[dummyPosition] = tmpC.getSpeed();
        }        

        // debug
        System.out.println("Lane 1\n" + Arrays.toString(rightLane) + "\n");
        System.out.println("Lane 2\n" + Arrays.toString(leftLane) + "\n");        
    }

    public void nextState() {
        // CALCULATE NEW STATE /////////////////////////////////////////////////
        // clear helper lanes
        for (int i = 0; i < helperRight.length; i++) {
            helperRight[i] = -1;
            helperLeft[i] = -1;
        }

        // move cars (check rules on current road and save new positions in next road)
        for (CarNS car : cars) {
            moveCar(car);
        }

        // END OF CALCULATE NEW STATE //////////////////////////////////////////
        //
        // set new state
        rightLane = helperRight.clone();
        leftLane = helperLeft.clone();
    }

    /*
     * Prints the current state of the road to the console.
     */
    public void printTrafficSituation() {
        String traffic_rightLane = "|", traffic_leftLane = "|";

        for (int i = 0; i < rightLane.length; i++) {
            traffic_rightLane += toSymbol(rightLane[i]);
            traffic_leftLane += toSymbol(leftLane[i]);
        }

        traffic_rightLane += "|";
        traffic_leftLane += "|";

//        System.out.println(traffic_rightLane + "\n" + traffic_leftLane + "\n");
        System.out.println(traffic_leftLane + "\n" + traffic_rightLane + "\n");
    }

    /*
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
        for (CarNS c : cars) {
            totalDistance += c.getTraveledDistance();
        }
        double flow = ((double) totalDistance) / (numIterations * ROAD_SIZE * 2 * (NUM_SLOW_CARS + NUM_FAST_CARS));
        System.out.println("Flow per lane: " + flow);
    }

    /*
     *
     * @return The list of cars in the road.
     */
    public ArrayList<CarNS> getCars() {
        return cars;
    }

    /*
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

    private SpeedDistance getStatusWithFrontCar(int lane, int position) {
        int speed = 0, distance = 0;
        int[] arr;
        
        if (lane==RIGHT_LANE) arr = rightLane;
        else arr = leftLane;
            
        for (int i = position + 1; i < ROAD_SIZE; i++) {
            if (arr[i] != -1){
                speed = arr[i];
                distance = i - position - 1;
                return new SpeedDistance(speed, distance);
            }          
        }

        // the consideration car is at the end of the road, therefore continue searching from the beginning of the lane
        for (int i = 0; i < ROAD_SIZE; i++) {
            if (arr[i] != -1){
                speed = arr[i];
                distance = i + ROAD_SIZE - position - 1;
                return new SpeedDistance(speed, distance);              
            }
        }
        
        return null;    // stm wrong
    }

    private SpeedDistance getStatusWithBehindCar(int lane, int position) {
        int speed = 0, distance = 0;
        int[] arr;
        
        if (lane==RIGHT_LANE) arr = rightLane;
        else arr = leftLane;
            
        for (int i = position - 1; i >= 0; i--) {
            if (arr[i] != -1){
                speed = arr[i];
                distance = position - i - 1;
                return new SpeedDistance(speed, distance);
            }          
        }

        // the consideration car is at the begining of the road, therefore continue searching from the end of the lane
        for (int i = ROAD_SIZE-1; i >= 0; i--) {
            if (arr[i] != -1){
                speed = arr[i];
                distance = position + ROAD_SIZE - i - 1;
                return new SpeedDistance(speed, distance);              
            }
        }
        
        return null;    // stm wrong
    }
    
    private void moveCar(CarNS car) {
        int lane = car.getLane();
        int position = car.getPosition();
        int speed = car.getSpeed();
        int otherLane = Math.floorMod(2*lane,3);

        SpeedDistance withCarFront = getStatusWithFrontCar(lane, position);
        SpeedDistance withCarFrontNextLane = getStatusWithFrontCar(otherLane, position-1);
        SpeedDistance withCarBehindNextLane = getStatusWithBehindCar(otherLane, position+1);
        
        int newSpeed = car.adaptSpeed(withCarFront, withCarFrontNextLane, withCarBehindNextLane);

        int newPosition = Math.floorMod(position + newSpeed, ROAD_SIZE);
        int newLane = car.lane;
        
        // commit the changes
//        car.setLane(newLane);
        car.setPosition(newPosition);
        //Setting the new speed is already done during the call to adaptSpeed();

        if (RoadNS.DEBUG) 
            System.out.println("Car " + car.getID() + " old speed " + speed + " new speed " + newSpeed + " old lane " + lane + " new lane " + newLane + "\n");
        
        if (newLane == LEFT_LANE) {
            helperLeft[newPosition] = newSpeed;
        } else {
            helperRight[newPosition] = newSpeed;
        }        
    }
    
}


