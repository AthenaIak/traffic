package trafficsimulation;

import java.util.ArrayList;

public class TrafficSimulation {
    public static final int ROAD_SIZE = 100;
    public static final int NUMBER_OF_NORMAL_CARS = 10;
    public static final int NUMBER_OF_FAST_CARS = 5;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // simple example of use
        ArrayList<Car> cars = new ArrayList<>();
        cars.add(new FastCar(1, 5, 1, 1));
        cars.add(new NormalCar(2, 3, 2, 8));

        for (Car car : cars) {
            p("" + car.getMaximumAcceleration());
        }
    }

    /**
     *
     * @param s String to be printed.
     */
    private static void p(String s) {
        System.out.println(s);
    }

}
