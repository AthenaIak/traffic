package trafficsimulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class TrafficSimulation {

    // GLOBAL CONSTANTS ////////////////////////////////////////////////////////
    // SIMULATION DETAILS
    public static final int ROAD_SIZE = 150; // number of cells in each lane
    public static final int SIMULATION_STEP_COOLDOWN = 1000;
    public static final int CAR_WIDTH = 10;
    // BROKEN CAR CONSTANTS 
    public static double BREAKING_DOWN_PROBABILITY = 0.3;
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
    public static int NUMBER_OF_ITERATIONS;
    public static boolean GLOBAL_SPEED_RULE;
    public static int GLOBAL_MAX_SPEED;
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        DENSITY = 0.1;
        MAX_NORMAL_CAR_SPEED = 8;
        MAX_FAST_CAR_SPEED = 11;
        FAST_CAR_RATIO = 0.25;
        NUMBER_OF_NORMAL_CARS = (int) (DENSITY * 2 * ROAD_SIZE * (1 - FAST_CAR_RATIO));
        NUMBER_OF_FAST_CARS = (int) (DENSITY * 2 * ROAD_SIZE * FAST_CAR_RATIO);
        
        GLOBAL_SPEED_RULE = false;
        GLOBAL_MAX_SPEED = 3;
        
        //Don't make this value zero, or it'll crash
        NUMBER_OF_ITERATIONS = 1000;
        
        
        String toFile = "";
        PrintWriter writer = new PrintWriter("simulations.txt","UTF-8");

 
        
        double[] trafficDensities = {0.10,0.20,0.30,0.40};
        double[] fastCarRatios = {0,0.25,0.50,0.75,1.0};
        int[] maxSpeedsSlow = {3,6,9};
        int[] maxSpeedsFast = {4,8,11};
        boolean[] globalRules = {false,true};
        boolean[] brokenCar = {false,true};
        
        AnimatedSimulation simulation = new AnimatedSimulation();
        
        for (double density : trafficDensities) {
            for (double ratio : fastCarRatios) {
                for (int slow : maxSpeedsSlow) {
                    for (int fast : maxSpeedsFast) {
                        for (boolean global : globalRules) {
                            for (boolean broken : brokenCar) {
                                DENSITY = density;
                                MAX_NORMAL_CAR_SPEED = slow;
                                MAX_FAST_CAR_SPEED = fast;
                                FAST_CAR_RATIO = ratio;
                                GLOBAL_SPEED_RULE = global;
                                BREAKING_DOWN_PROBABILITY = broken ? 0.3 : 0.0;
                                simulation.initialiseSimulation(NUMBER_OF_ITERATIONS);
                                writer.println(simulation.runSimulation());
                            }
                        }
                    }
                }
            }
        }
        writer.close();
   
    }
    
}
