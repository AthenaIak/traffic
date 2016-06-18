package trafficsimulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class TrafficSimulation {

    // GLOBAL CONSTANTS ////////////////////////////////////////////////////////
    // SIMULATION DETAILS
    public static final int ROAD_SIZE = 1000; // number of cells in each lane
    public static final int SIMULATION_STEP_COOLDOWN = 100;
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
//        DENSITY = 0.1;
//        MAX_SPEED_SLOW_CAR = 8;
//        MAX_SPEED_FAST_CAR = 11;
//        FAST_CAR_RATIO = 0.25;
//        NUM_SLOW_CARS = (int) (DENSITY * 2 * ROAD_SIZE * (1 - FAST_CAR_RATIO));
//        NUM_FAST_CARS = (int) (DENSITY * 2 * ROAD_SIZE * FAST_CAR_RATIO);
        
        GLOBAL_SPEED_RULE = false;
        GLOBAL_MAX_SPEED = 3;
        
        //Don't make this value zero, or it'll crash
        NUMBER_OF_ITERATIONS = 3600;
        
        
        String toFile = "";
        PrintWriter writer = new PrintWriter("simulations.csv","UTF-8");

        int totalCars;
        
        double[] trafficDensities = {0.05,0.1,0.15,.3,.4};
        double[] fastCarRatios = {0,0.25,0.50,0.75,1.0};
        int[] maxSpeedsSlow = {3,6,9};
        int[] maxSpeedsFast = {4,8,11};
        boolean[] globalRules = {false,true};
        boolean[] brokenCar = {true, false};
        
        AnimatedSimulation simulation = new AnimatedSimulation();

//        DENSITY = 0.2;
//        totalCars = (int) (ROAD_SIZE  * DENSITY);
//        FAST_CAR_RATIO = 0.5;
//        NUM_FAST_CARS = (int) (FAST_CAR_RATIO * totalCars);
//        NUM_SLOW_CARS = totalCars - NUM_FAST_CARS;
//        System.out.println("Num fast car: " + NUM_FAST_CARS + ", Num slow car: " + NUM_SLOW_CARS);
//        MAX_SPEED_SLOW_CAR = 6;
//        MAX_SPEED_FAST_CAR = 9;
//        GLOBAL_SPEED_RULE = true;
//        if (GLOBAL_SPEED_RULE) {
//            GLOBAL_MAX_SPEED = (int) (0.75 * MAX_SPEED_SLOW_CAR);
//        }
//        BREAKING_DOWN_PROBABILITY = true ? 0.3 : 0.0;
//        simulation.initialiseSimulation(NUMBER_OF_ITERATIONS);
//        writer.println(simulation.runSimulation());
       

        
        writer.println("model,ith run, road_block, max_speed_slow, max_speed_fast, fast_car_ratio, density, total_all_cars_distance, total_slow_cars_distance, total_fast_cars_distance, worst_case_distance_slow_cars, worst_cast_distance_fast_cars, best_case_distance_slow_car, best_case_distance_fast_car,num_slow_cars,num_fast_cars");
        for (double density : trafficDensities) {
            for (double ratio : fastCarRatios) {
                for (int slow : maxSpeedsSlow) {
                    for (int fast : maxSpeedsFast) {
                        for (boolean global : globalRules) {
                            for (boolean broken : brokenCar) {
                                DENSITY = density;
                                totalCars = (int) (ROAD_SIZE  * density);
                                NUM_FAST_CARS = (int) (ratio * totalCars);
                                NUM_SLOW_CARS = totalCars - NUM_FAST_CARS;
                                if (slow <= fast) {
                                    FAST_CAR_RATIO = ratio;
                                    GLOBAL_SPEED_RULE = global;
                                    if (global) {
                                        GLOBAL_MAX_SPEED = (int) (0.75 * slow);
                                    }
                                    MAX_SPEED_SLOW_CAR = slow;
                                    MAX_SPEED_FAST_CAR = fast;
                                    BREAKING_DOWN_PROBABILITY = broken ? 0.3 : 0.0;
                                    for (int i = 0; i < 5; i++) {
                                        simulation.initialiseSimulation(NUMBER_OF_ITERATIONS);
                                        writer.println(simulation.runSimulation(i));    
                                    }
                                    System.out.println("5 things" + broken);
                                }
                                writer.flush();
                            }
                        }
                    }
                }
            }
        }
        writer.close();
    }
}
