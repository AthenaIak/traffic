package trafficsimulation;

public class TrafficSimulation {
    public static final boolean DEBUG = false;
    public static final double DENSITY = 0.15; 
//    public static final int ROAD_SIZE = MAXIMUM_SPEED_BY_LAW_OF_NORMAL_CARS * NUMBER_OF_NORMAL_CARS + MAXIMUM_SPEED_BY_LAW_OF_FAST_CARS * NUMBER_OF_FAST_CARS;         // approx. number of cars x maximum speed
    public static final int ROAD_SIZE = 200;
    
    public static final int NUMBER_OF_NORMAL_CARS = 0;
    public static final int NUMBER_OF_FAST_CARS = (int)(2*ROAD_SIZE*DENSITY);
    
    public static final int MAXIMUM_SPEED_SYSTEM = 5;
    public static final int MAXIMUM_SPEED_BY_LAW_OF_FAST_CARS = MAXIMUM_SPEED_SYSTEM ;
    public static final int MAXIMUM_SPEED_BY_LAW_OF_NORMAL_CARS = MAXIMUM_SPEED_SYSTEM;
    
    public static final int DISTANCE_TO_LOOK_AHEAD = MAXIMUM_SPEED_SYSTEM*2;    // distance to consider changing lane or not
    public static final int LEFT_LANE = 1;
    public static final int RIGHT_LANE = 2;
    public static final double PROBABILITY_FLUCTUATION = 0.2;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        Road road = new Road();
        System.out.println("Road density " + (NUMBER_OF_NORMAL_CARS + NUMBER_OF_FAST_CARS)/(float)ROAD_SIZE/2);
        road.printTrafficSituation();
        for (int i = 0; i < 30; i++) {
            road.nextState();
            road.printTrafficSituation();
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}


/*

 +  Try to find some references to models, so you can compare how realistic this model is.
 +  Study oncoming and exiting ramp.
 +  Implement some sort of  " sleepy drivers" who do not use the speed of the car in front, but
 only the distance to the car in front.
 */
