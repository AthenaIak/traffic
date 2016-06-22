package trafficsimulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrafficSimulation {

    // GLOBAL VARIABLES ////////////////////////////////////////////////////////
    // SIMULATION DETAILS
    public static int ROAD_SIZE = 150; // number of cells in each lane
    public static int NUMBER_OF_ITERATIONS = 1000;
    public static int SIMULATION_STEP_COOLDOWN = 100;
    public static int CAR_WIDTH = 10;
    public static final boolean COLLECT_DATA = false;
    public static boolean PRINT_TO_CONSOLE = false;

    // CAR CAPABILITIES
    public static final int GLOBAL_MAXIMUM_DECELERATION = 2;
    public static final int GLOBAL_MINIMUM_DECELERATION = 1;
    public static final int GLOBAL_MAXIMUM_ACCELERATION = 2;
    public static final int MAX_ACCELERATION_SLOW_CAR = 1;
    public static int MAX_SPEED_SLOW_CAR = 9;
    public static int MAX_SPEED_FAST_CAR = 11;

    // CARS ON THE ROAD
    public static double DENSITY = 0.15;
    public static double FAST_CAR_RATIO = 0.5; // fast/total cars
    public static int NUM_SLOW_CARS;
    public static int NUM_FAST_CARS;

    // BROKEN CAR 
    public static double BREAKING_DOWN_PROBABILITY = 0.1;
    public static double GETTING_REPAIRED_PROBABILITY = 0;

    // GLOBAL SPEED RULE
    public static boolean GLOBAL_SPEED_RULE = true;
    public static boolean CAR_IS_CURRENTLY_BROKEN;
    public static int GLOBAL_MAX_SPEED;
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (!COLLECT_DATA) {
            readArguments(args);
            NUM_FAST_CARS = (int) (FAST_CAR_RATIO * (int) (ROAD_SIZE * DENSITY));
            NUM_SLOW_CARS = (int) (ROAD_SIZE * DENSITY) - NUM_FAST_CARS;
            if (GLOBAL_SPEED_RULE) {
                GLOBAL_MAX_SPEED = (int) (0.75 * MAX_SPEED_SLOW_CAR);
            }

            AnimatedSimulation simulation = new AnimatedSimulation();
            System.out.println("Num fast car: " + NUM_FAST_CARS + ", Num slow car: " + NUM_SLOW_CARS);

            simulation.initialiseSimulation(NUMBER_OF_ITERATIONS);
            String result = simulation.runSimulation();

            System.out.println(result);
        } else {
            String filename = "simulations.csv";
            if (exportData(filename))
                System.out.println("Data exported successfully.");
            else
                System.out.println("An error occured while writting data. Please make sure that " + filename + " is not currently used by any other program.");
        }
    }

    /**
     * This method reads the input arguments and tweaks the simulation
     * parameters as the user desires.
     * @param args The command line arguments.
     */
    private static void readArguments(String[] args) {
        String parName;
        int parValue;
        for (String arg : args) {
            parName = arg.split("=")[0];
            switch (parName) {
                case "iterations":
                    try {
                        NUMBER_OF_ITERATIONS = Integer.parseInt(arg.split("=")[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be an integer. Parameter set to 1000.");
                        NUMBER_OF_ITERATIONS = 1000;
                    }
                    break;
                case "density":
                    try {
                        DENSITY = Double.parseDouble(arg.split("=")[1]);
                        if (DENSITY < 0 || DENSITY > 0.6) {
                            System.err.println("Parameter \"" + parName + "\" must be a positive double less than 0.6. Parameter set to 0.15.");
                            DENSITY = 0.15;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be a positive double less than 0.6. Parameter set to 0.15.");
                        DENSITY = 0.15;
                    }
                    break;
                case "fastcars":
                    try {
                        FAST_CAR_RATIO = Double.parseDouble(arg.split("=")[1]);
                        if (FAST_CAR_RATIO < 0 || FAST_CAR_RATIO > 1) {
                            System.err.println("Parameter \"" + parName + "\" must be a positive double at most 1. Parameter set to 0.5.");
                            FAST_CAR_RATIO = 0.5;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be a positive double at most 1. Parameter set to 0.5.");
                        FAST_CAR_RATIO = 0.5;
                    }
                    break;
                case "maxslow":
                    try {
                        MAX_SPEED_SLOW_CAR = Integer.parseInt(arg.split("=")[1]);
                        if (MAX_SPEED_SLOW_CAR < 0 || MAX_SPEED_SLOW_CAR > 15) {
                            System.err.println("Parameter \"" + parName + "\" must be a positive integer at most 15. Parameter set to 9.");
                            MAX_SPEED_SLOW_CAR = 9;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be a positive integer at most 15. Parameter set to 9.");
                        MAX_SPEED_SLOW_CAR = 9;
                    }
                    break;
                case "maxfast":
                    try {
                        MAX_SPEED_FAST_CAR = Integer.parseInt(arg.split("=")[1]);
                        if (MAX_SPEED_FAST_CAR < 0 || MAX_SPEED_FAST_CAR > 15) {
                            System.err.println("Parameter \"" + parName + "\" must be a positive integer at most 15. Parameter set to 11.");
                            MAX_SPEED_FAST_CAR = 11;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be a positive integer at most 15. Parameter set to 11.");
                        MAX_SPEED_FAST_CAR = 11;
                    }
                    break;
                case "breakprob":
                    try {
                        BREAKING_DOWN_PROBABILITY = Double.parseDouble(arg.split("=")[1]);
                        if (BREAKING_DOWN_PROBABILITY < 0 || BREAKING_DOWN_PROBABILITY > 1) {
                            System.err.println("Parameter \"" + parName + "\" must be a positive double at most 1. Parameter set to 0.1.");
                            BREAKING_DOWN_PROBABILITY = 0.1;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be a positive double at most 1. Parameter set to 0.1.");
                        BREAKING_DOWN_PROBABILITY = 0.1;
                    }
                    break;
                case "repairprob":
                    try {
                        GETTING_REPAIRED_PROBABILITY = Double.parseDouble(arg.split("=")[1]);
                        if (GETTING_REPAIRED_PROBABILITY < 0 || GETTING_REPAIRED_PROBABILITY > 1) {
                            System.err.println("Parameter \"" + parName + "\" must be a positive double at most 1. Parameter set to 0.05.");
                            GETTING_REPAIRED_PROBABILITY = 0.05;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be a positive double at most 1. Parameter set to 0.05.");
                        GETTING_REPAIRED_PROBABILITY = 0.05;
                    }
                    break;
                case "speedrule":
                    switch (arg.split("=")[1]) {
                        case "no":
                            GLOBAL_SPEED_RULE = false;
                            break;
                        case "yes":
                            GLOBAL_SPEED_RULE = true;
                            break;
                        default:
                            System.err.println("Parameter \"" + parName + "\" can either take the value \"yes\" or \"no\". Parameter set to \"yes\".");
                            GLOBAL_SPEED_RULE = true;
                            break;
                    }
                    break;
                case "roadlength":
                    try {
                        ROAD_SIZE = Integer.parseInt(arg.split("=")[1]);
                        if (ROAD_SIZE < 100) {
                            System.err.println("Parameter \"" + parName + "\" must be a positive integer at least 100. Parameter set to 150.");
                            ROAD_SIZE = 150;
                        }
                        if (ROAD_SIZE > 9999) {
                            System.err.println("WOW Huge road size! (\"" + parName + "\"), but as you wish. Parameter set to " + ROAD_SIZE + "! \n(DISCLAIMER: The simulation may not run or be too slow)");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be a positive integer at least 100. Parameter set to 150.");
                        ROAD_SIZE = 150;
                    }
                    break;
                case "cooldown":
                    try {
                        SIMULATION_STEP_COOLDOWN = Integer.parseInt(arg.split("=")[1]);
                        if (SIMULATION_STEP_COOLDOWN > 2000) {
                            System.err.println("Warning: Big cooldown between steps (\"" + parName + "\"). but as you wish. Parameter set to " + SIMULATION_STEP_COOLDOWN + "! \n(DISCLAIMER: The simulation may be too boring)");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be a positive integer. Parameter set to 100.");
                        SIMULATION_STEP_COOLDOWN = 100;
                    }
                    break;
                case "carwidth":
                    try {
                        CAR_WIDTH = Integer.parseInt(arg.split("=")[1]);
                        if (CAR_WIDTH < 10) {
                            System.err.println("Parameter \"" + parName + "\" must be an integer between 10 and 30. Parameter set to 10.");
                            CAR_WIDTH = 10;
                        }
                        if (CAR_WIDTH > 30) {
                            System.err.println("Parameter \"" + parName + "\" must be an integer between 10 and 30. Parameter set to 30.");
                            CAR_WIDTH = 30;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Parameter \"" + parName + "\" must be an integer between 10 and 30. Parameter set to 30.");
                        CAR_WIDTH = 30;
                    }
                    break;
                case "console":
                    switch (arg.split("=")[1]) {
                        case "no":
                            PRINT_TO_CONSOLE = false;
                            break;
                        case "yes":
                            PRINT_TO_CONSOLE = true;
                            break;
                        default:
                            System.err.println("Parameter \"" + parName + "\" can either take the value \"yes\" or \"no\". Parameter set to \"no\".");
                            PRINT_TO_CONSOLE = false;
                            break;
                    }
                    break;
                default:
                    System.err.println("Parameter \"" + parName + "\" does not exist in the list of possible parameters. Parameter ignored.");
                    break;
            }
        }
    }

    /**
     * This method runs simulations using different combinations of parameters
     * and writes statistics for each simulation to a file.
     * @param filename The name of the file data will be written to.
     * @return True if
     */
    private static Boolean exportData(String filename) {
        Boolean success;
        AnimatedSimulation simulation = new AnimatedSimulation();
        int totalCars;
        NUMBER_OF_ITERATIONS = 3600;
        ROAD_SIZE = 1000;

        double[] trafficDensities = {0.05, 0.1, 0.15, .3, .4};
        double[] fastCarRatios = {0, 0.25, 0.50, 0.75, 1.0};
        int[] maxSpeedsSlow = {3, 6, 9};
        int[] maxSpeedsFast = {4, 8, 11};
        boolean[] globalRules = {false, true};
        boolean[] brokenCar = {true, false};

        PrintWriter writer;
        try {
            writer = new PrintWriter(filename, "UTF-8");
            writer.println("model, road_block, max_speed_slow, max_speed_fast, fast_car_ratio, density, total_all_cars_distance, total_slow_cars_distance, total_fast_cars_distance, worst_case_distance_slow_cars, worst_cast_distance_fast_cars, best_case_distance_slow_car, best_case_distance_fast_car,num_slow_cars,num_fast_cars,global_speed_rule");
            for (double density : trafficDensities) {
                for (double ratio : fastCarRatios) {
                    for (int slow : maxSpeedsSlow) {
                        for (int fast : maxSpeedsFast) {
                            for (boolean global : globalRules) {
                                for (boolean broken : brokenCar) {
                                    DENSITY = density;
                                    totalCars = (int) (ROAD_SIZE * density);
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
                                        simulation.initialiseSimulation(NUMBER_OF_ITERATIONS);
                                        writer.println(simulation.runSimulation());
                                    }
                                    writer.flush();
                                }
                            }
                        }
                    }
                }
            }
            writer.close();
            success = true;
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(TrafficSimulation.class.getName()).log(Level.SEVERE, null, ex);
            success = false;
        }
        return success;
    }
}
