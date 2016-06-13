package nsSimulation;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class TrafficSimulation {

    // GLOBAL CONSTANTS ////////////////////////////////////////////////////////
    
    // SIMULATION DETAILS
    public static final int SIMULATION_STEP_COOLDOWN = 0;
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

    
    // parameter of NS model
    public static int MAX_SPEED_FAST_CAR = 5;
    public static int MAX_SPEED_SLOW_CAR = 3;    
    public static double PROBABILITY_FLUCTUATION = .25;
    public static int DISTANCE_TO_LOOK_AHEAD = 7;
    public static int SLACK = 3;

    // 
    public static int ROAD_SIZE = 0;                      // number of cells
    public static int NUM_FAST_CARS = 0;
    public static int NUM_SLOW_CARS = 0;
    public static boolean HAS_BROKEN_CAR = false;

   
    public static boolean DEBUG = false;
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
//        DENSITY = 0.1;
//        MAX_NORMAL_CAR_SPEED = 8;
//        MAX_FAST_CAR_SPEED = 11;
//        FAST_CAR_RATIO = 0.25;
//        NUMBER_OF_NORMAL_CARS = (int) (DENSITY * 2 * ROAD_SIZE * (1 - FAST_CAR_RATIO));
//        NUMBER_OF_FAST_CARS = (int) (DENSITY * 2 * ROAD_SIZE * FAST_CAR_RATIO);
       
//        GLOBAL_SPEED_RULE = false;
//        GLOBAL_MAX_SPEED = 3;
        


        //Don't make this value zero, or it'll crash
        NUMBER_OF_ITERATIONS = 2000;
        
        
        AnimatedSimulation simulation = new AnimatedSimulation();
        
        double cellLength = 7.5;            // in meter
        int roadLength = 10;                // in km, should be a multiple of cellLength 
        ROAD_SIZE = (int)(roadLength*1000/cellLength);   // number of cells
        
        double[] trafficDensities = {40, 80, 120, 160, 200};
        double[] fastCarRatios = {.9}; //{0,0.25,0.50,0.75,1.0};
        int[] maxSpeedsSlow = {3};
        int[] maxSpeedsFast = {5};
        boolean[] brokenCar = {false,true};
        int[] arrDistanceLookAhead = {7};
        int[] slacks = {3};
        
        int totalCars;
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("simulations.txt", true)));  // append to file
        long startTime;
        
        for (double density : trafficDensities) {
            for (double ratio : fastCarRatios) {
                for (int slow : maxSpeedsSlow) {
                    for (int fast : maxSpeedsFast) {
                        for (boolean broken : brokenCar) {
                            for (int distance : arrDistanceLookAhead) {
                                for (int slack : slacks) {
                            
                                    MAX_SPEED_FAST_CAR = fast;
                                    MAX_SPEED_SLOW_CAR = slow;

                                    DISTANCE_TO_LOOK_AHEAD = distance;
                                    SLACK = slack;

                                    totalCars = (int) (roadLength  * density);
                                    NUM_FAST_CARS = (int) (ratio * totalCars);
                                    NUM_SLOW_CARS = totalCars - NUM_FAST_CARS;
                                    HAS_BROKEN_CAR = broken;

                                    DENSITY = density;
        //                            MAX_NORMAL_CAR_SPEED = slow;
        //                            MAX_FAST_CAR_SPEED = fast;
        //                            FAST_CAR_RATIO = ratio;
                                    BREAKING_DOWN_PROBABILITY = broken ? 0.3 : 0.0;

                                    startTime = System.nanoTime();
                                    simulation.initialiseSimulation(NUMBER_OF_ITERATIONS);
                                    writer.println(simulation.runSimulation());                                    
                                    System.out.println("Running one simulation: " + (System.nanoTime()-startTime)/Math.pow(10, 9) + "seconds");
                                    
                                    writer.close();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }       
        
        writer.close();
    }
    
}
