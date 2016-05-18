package trafficsimulation;

public class TrafficSimulation {

    public static final int NUMBER_OF_NORMAL_CARS = 15;
    public static final int NUMBER_OF_FAST_CARS = 10;
    public static final int MAXIMUM_SPEED_OF_FAST_CARS = 11;
    public static final int MAXIMUM_SPEED_OF_NORMAL_CARS = 9;
    public static final int ROAD_SIZE = MAXIMUM_SPEED_OF_NORMAL_CARS * NUMBER_OF_NORMAL_CARS + MAXIMUM_SPEED_OF_FAST_CARS * NUMBER_OF_FAST_CARS;         // approx. number of cars x maximum speed

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        Road road = new Road();
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
