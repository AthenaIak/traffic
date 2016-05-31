package trafficsimulation;

public class TrafficSimulation {
    
    public static final int ROAD_SIZE = 150;    // length of each lane
    public static final double DENSITY = 0.1;    
    
//    public static final int NUMBER_OF_NORMAL_CARS = 5;
//    public static final int NUMBER_OF_FAST_CARS = 5;
    public static final int NUMBER_OF_NORMAL_CARS = (int)(DENSITY * ROAD_SIZE);
    public static final int NUMBER_OF_FAST_CARS = (int)(DENSITY * ROAD_SIZE);
    
    public static final int SIMULATION_STEP_COOLDOWN = 1000;
    public static final int CAR_WIDTH = 10;
    public static final boolean REPAIR_BROKEN_CAR = false;

    public static final int MAX_NORMAL_CAR_SPEED = 8;
    public static final int MAX_FAST_CAR_SPEED = 11;
    public static final int GLOBAL_MAXIMUM_DECELERATION = 2;
    public static final int GLOBAL_MINIMUM_DECELERATION = 1;
    public static final int GLOBAL_MAXIMUM_ACCELERATION = 2;

    
//    public static final int ROAD_SIZE = MAX_NORMAL_CAR_SPEED * NUMBER_OF_NORMAL_CARS + MAX_FAST_CAR_SPEED * NUMBER_OF_FAST_CARS;         // approx. number of cars x maximum speed
    public static final double THRESHOLD_OBEY_LAW = 0.2;   // greater is obeying law

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Animation().runSimulation();
    }
}


/*

 +  Try to find some references to models, so you can compare how realistic this model is.
 +  Study oncoming and exiting ramp.
 +  Implement some sort of  " sleepy drivers" who do not use the speed of the car in front, but
 only the distance to the car in front.
 */
