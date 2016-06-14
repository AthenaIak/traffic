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
    
    public static final int MAX_ACCELERATION_SLOW_CAR = 1;
    ////////////////////////////////////////////////////////////////////////////

    // CONFIGURATIONS //////////////////////////////////////////////////////////
    public static int MAX_SPEED_SLOW_CAR;
    public static int MAX_SPEED_FAST_CAR;
    public static double DENSITY;
    public static double FAST_CAR_RATIO; // fast/total cars
    public static int NUM_SLOW_CARS;
    public static int NUM_FAST_CARS;
    public static int NUMBER_OF_ITERATIONS;
    public static boolean GLOBAL_SPEED_RULE;
    public static int GLOBAL_MAX_SPEED;
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        DENSITY = 0.1;
        MAX_SPEED_SLOW_CAR = 8;
        MAX_SPEED_FAST_CAR = 11;
        FAST_CAR_RATIO = 0.25;
        NUM_SLOW_CARS = (int) (DENSITY * 2 * ROAD_SIZE * (1 - FAST_CAR_RATIO));
        NUM_FAST_CARS = (int) (DENSITY * 2 * ROAD_SIZE * FAST_CAR_RATIO);
        
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
        boolean[] brokenCar = {true, false};
        
        AnimatedSimulation simulation = new AnimatedSimulation();
        
        for (double density : trafficDensities) {
            for (double ratio : fastCarRatios) {
                for (int slow : maxSpeedsSlow) {
                    for (int fast : maxSpeedsFast) {
                        for (boolean global : globalRules) {
                            for (boolean broken : brokenCar) {
                                DENSITY = density;
                                MAX_SPEED_SLOW_CAR = slow;
                                MAX_SPEED_FAST_CAR = fast;
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
