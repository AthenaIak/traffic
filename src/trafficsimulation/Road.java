package trafficsimulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Road {

    public static int NUM_LANES = 2;
    public static int RIGHT_LANE = 1;
    public static int LEFT_LANE = 2;
    
    public static int NUM_TYPE_CAR = 2;
    public static int TYPE_CAR_SLOW = 1;
    public static int TYPE_CAR_FAST = 2;

    
    private ArrayList<Car> cars; 
    
    private int[] rightLane; // lane 1 values: current speed of car (or -1 if no car)
    private int[] leftLane; // lane 2

    private int[] helperRightLane;
    private int[] helperLeftLane;
    
    private boolean helperLegalMoveCheck;
    
    public boolean isStable;
    
    
    public Road(int numIterations) {
        // Initialise variables
        cars = new ArrayList<>();
        rightLane = new int[TrafficSimulation.ROAD_SIZE];
        leftLane = new int[TrafficSimulation.ROAD_SIZE];

        helperRightLane = new int[TrafficSimulation.ROAD_SIZE];
        helperLeftLane = new int[TrafficSimulation.ROAD_SIZE];

        for (int i = 0; i < rightLane.length; i++) {
            rightLane[i] = -1;
            leftLane[i] = -1;
        }
        
        generateCars(numIterations);
        
        this.isStable = false;
    }

    
    public void generateCars(int numIterations){
        // randomly generate all the cars
		
        int rightLane_dummyPosition = 0, leftLane_dummyPosition = 0, dummyPosition = 0, slow_generated = 0, fast_generated = 0, lane, type_of_car;
        Car tmpC;
        Random r = new Random();
        int limitSpeed = (int) (TrafficSimulation.ROAD_SIZE / (TrafficSimulation.NUM_FAST_CARS + TrafficSimulation.NUM_SLOW_CARS));
        boolean createdBrokenCar = (TrafficSimulation.BREAKING_DOWN_PROBABILITY == 0);
  
        for (int i = 0; i < TrafficSimulation.NUM_SLOW_CARS + TrafficSimulation.NUM_FAST_CARS; i++) {

            // randomly choose the lane (unless the limit is reached)
            if (rightLane_dummyPosition>= TrafficSimulation.ROAD_SIZE)
                lane = LEFT_LANE;
            else if (leftLane_dummyPosition>= TrafficSimulation.ROAD_SIZE)
                lane = RIGHT_LANE;            
            else lane = r.nextInt(NUM_LANES) + 1;            
						
            // retrieve position for the (soon to be generated) car to be placed
            if (lane == RIGHT_LANE) dummyPosition = rightLane_dummyPosition;
            else dummyPosition = leftLane_dummyPosition;

            // randomly choose the type of car (unless the limit is reached)
            if (slow_generated == TrafficSimulation.NUM_SLOW_CARS)            // limit is reached
                type_of_car = TYPE_CAR_FAST;
            else if (fast_generated == TrafficSimulation.NUM_FAST_CARS)       // limit is reached
                type_of_car = TYPE_CAR_SLOW;
            else type_of_car = r.nextInt(NUM_TYPE_CAR) + 1; // randomly

            // generate the car and add it to the list of cars
            if (type_of_car == TYPE_CAR_SLOW) {                
                if (!createdBrokenCar && TrafficSimulation.NUM_SLOW_CARS <= 4*(slow_generated+1)){
                    createdBrokenCar = true;
                    tmpC = new BrokenCar(lane, dummyPosition, numIterations, limitSpeed); // broken car
                    slow_generated++;
                } else {
                    tmpC = new SlowCar(lane, dummyPosition, numIterations, limitSpeed);   // slow car
                    slow_generated++;                    
                }                
            } else {
                tmpC = new FastCar(lane, dummyPosition, numIterations, limitSpeed);
                fast_generated++;
            }
            cars.add(tmpC);

            // save data to the road structure (lanes)
            if (lane == RIGHT_LANE) rightLane[dummyPosition] = tmpC.getSpeed();
            else leftLane[dummyPosition] = tmpC.getSpeed();

            // follow the 2-seconds rule 
            dummyPosition = dummyPosition + 2*tmpC.getSpeed();

            if (lane == RIGHT_LANE) rightLane_dummyPosition = dummyPosition;
            else leftLane_dummyPosition = dummyPosition;
        }

        // generate a car that will break down (it will become an obstacle on the road)
//        if (TrafficSimulation.BREAKING_DOWN_PROBABILITY != 0) {
//            tmpC = new BrokenCar(r.nextInt(2) + 1, dummyPosition, numIterations);
//            cars.add(tmpC);
//            rightLane[dummyPosition] = tmpC.getSpeed();
//        }

        // debug
        System.out.println("Right lane\n" + Arrays.toString(rightLane) + "\n");
        System.out.println("Left lane\n" + Arrays.toString(leftLane) + "\n");        
    }
    
    public void nextState() {
        this.helperLegalMoveCheck = true;
        // TODO: calculate the max speed of each lane right now and enforce a limit

        // CALCULATE NEW STATE /////////////////////////////////////////////////
        // clear helper lanes
        for (int i = 0; i < helperRightLane.length; i++) {
            helperRightLane[i] = -1;
            helperLeftLane[i] = -1;
        }

        // move cars (check rules on current road and save new positions in next road)
        for (Car car : cars) {
            if (!car.move(rightLane, leftLane)) {
                this.helperLegalMoveCheck = false;
                System.out.println("Car decelerated because it had no legal moves! \n" + car);
            }
            if (car.getLane() == 1)
                helperRightLane[car.getPosition()] = car.getSpeed();
            else helperLeftLane[car.getPosition()] = car.getSpeed();
        }
        // END OF CALCULATE NEW STATE //////////////////////////////////////////

        // set new state
        rightLane = helperRightLane.clone();
        leftLane = helperLeftLane.clone();
        
        if (this.helperLegalMoveCheck) {
           this.isStable = true; 
        }
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
        double flow = ((double) totalDistance) / (numIterations * TrafficSimulation.ROAD_SIZE * 2 * (TrafficSimulation.NUM_SLOW_CARS + TrafficSimulation.NUM_FAST_CARS));
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
