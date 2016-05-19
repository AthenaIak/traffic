package trafficsimulation;

public class TrafficSimulation {

    public static final int ROAD_SIZE = 60;
    public static final int NUMBER_OF_NORMAL_CARS = 5;
    public static final int NUMBER_OF_FAST_CARS = 5;
    public static final int MAX_NORMAL_CAR_SPEED = 3;
    public static final int MAX_FAST_CAR_SPEED = 6;
    public static final int GLOBAL_MAXIMUM_DECELERATION = 2;
    public static final int GLOBAL_MINIMUM_DECELERATION = 1;
    public static final int GLOBAL_MAXIMUM_ACCELERATION = 2;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        Road road = new Road();
        road.printTrafficSituation();
        for (int i = 0; i < 30; i++) {
            road.nextState();
            road.printTrafficSituation();
        }
    }
}


/*

 +  Try to find some references to models, so you can compare how realistic this model is.
 +  Study oncoming and exiting ramp.
 +  Implement some sort of  " sleepy drivers" who do not use the speed of the car in front, but
 only the distance to the car in front.
 */
