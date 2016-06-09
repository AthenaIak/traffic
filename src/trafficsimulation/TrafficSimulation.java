package trafficsimulation;

public class TrafficSimulation {

    // GLOBAL CONSTANTS ////////////////////////////////////////////////////////
    // SIMULATION DETAILS
    public static final int ROAD_SIZE = 150; // number of cells in each lane
    public static final int SIMULATION_STEP_COOLDOWN = 1000;
    public static final int CAR_WIDTH = 10;
    // BROKEN CAR CONSTANTS 
    public static final double BREAKING_DOWN_PROBABILITY = 0;
    public static final double GETTING_REPAIRED_PROBABILITY = 0;
    // MISCELLANEOUS
    public static final int GLOBAL_MAXIMUM_DECELERATION = 2;
    public static final int GLOBAL_MINIMUM_DECELERATION = 1;
    public static final int GLOBAL_MAXIMUM_ACCELERATION = 2;
    ////////////////////////////////////////////////////////////////////////////

    // CONFIGURATIONS //////////////////////////////////////////////////////////
    public static int MAX_NORMAL_CAR_SPEED;
    public static int MAX_FAST_CAR_SPEED;
    public static double DENSITY;
    public static double FAST_CAR_RATIO; // fast/total cars
    public static int NUMBER_OF_NORMAL_CARS;
    public static int NUMBER_OF_FAST_CARS;
    ////////////////////////////////////////////////////////////////////////////

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DENSITY = 0.1;
        MAX_NORMAL_CAR_SPEED = 8;
        MAX_FAST_CAR_SPEED = 11;
        FAST_CAR_RATIO = 0.25;
        NUMBER_OF_NORMAL_CARS = (int) (DENSITY * 2 * ROAD_SIZE * (1 - FAST_CAR_RATIO));
        NUMBER_OF_FAST_CARS = (int) (DENSITY * 2 * ROAD_SIZE * FAST_CAR_RATIO);

        AnimatedSimulation simulation = new AnimatedSimulation();
        simulation.initialiseSimulation();
        simulation.runSimulation(0);

    }
}
