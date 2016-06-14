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
    public static int MAX_ACCELERATION = 1;               // default is 1 in NS
    public static int MAX_SPEED_FAST_CAR = 5;
    public static int MAX_SPEED_SLOW_CAR = 3;    
    public static double PROBABILITY_FLUCTUATION = .25;
    public static int DISTANCE_TO_LOOK_AHEAD = 7;
    public static int SLACK = 3;
    public static boolean APPLY_SYMMETRIC_RULE = true; 

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
        
        // parameters related to road segment
        double cellLength = 7.5;            // in meter
        double roadLength = 7.5;                // in km, should be a multiple of cellLength 
        ROAD_SIZE = (int)(roadLength*1000/cellLength);   // number of cells
        
        // to use in the same scale with our model, set cellLength = 3, 
        // consider changing the MAX_ACCELERATION, masSpeedsFast, maxSpeedsSlow as well
        
        
        // parameters for NS model        
        APPLY_SYMMETRIC_RULE = true;                // paper section VIII-B
        MAX_ACCELERATION = 1;                       // consider changing this value if cell length is modified
                                // to use the basic model in paper section VI-C, set slack=0 and APPLY_SYMMETRIC_RULE = false
        int[] arrDistanceLookAhead = {7};   // {7, 16}
        int[] slacks = {3};                 // {3, 9}
        
        double[] trafficDensities = {40, 80, 120, 160, 200};
        double[] fastCarRatios = {.9}; //{0,0.25,0.50,0.75,1.0};
        int[] maxSpeedsSlow = {3};
        int[] maxSpeedsFast = {5};
        boolean[] brokenCar = {true};
        int numRepetitions = 5;                     // repeat each model xxx times
        
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
                                    for (int rep=0; rep < numRepetitions; rep++) {
                            
                                        MAX_SPEED_FAST_CAR = fast;
                                        MAX_SPEED_SLOW_CAR = slow;

                                        DISTANCE_TO_LOOK_AHEAD = distance;
                                        SLACK = slack;

                                        totalCars = (int) (roadLength  * density);
                                        NUM_FAST_CARS = (int) (ratio * totalCars);
                                        NUM_SLOW_CARS = totalCars - NUM_FAST_CARS;
                                        HAS_BROKEN_CAR = broken;
                                        if (HAS_BROKEN_CAR && NUM_SLOW_CARS==0){
                                            NUM_SLOW_CARS ++;   // broken car is counted in number of slow cars
                                            NUM_FAST_CARS --;
                                        }

                                        DENSITY = density;
                                        BREAKING_DOWN_PROBABILITY = broken ? 0.3 : 0.0;

                                        startTime = System.nanoTime();
                                        simulation.initialiseSimulation(NUMBER_OF_ITERATIONS);
                                        writer.println(simulation.runSimulation());             // save to file
                                        
                                        System.out.println("Running one simulation: " + (System.nanoTime()-startTime)/Math.pow(10, 9) + "seconds");

//                                        // for debugging
//                                        writer.close();
//                                        return;
                                    }
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
