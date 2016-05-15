package trafficsimulation;

import java.util.ArrayList;
import java.util.Random;

public class Road {

    private ArrayList<Car> cars; // I chose arraylist so that it is extendable for non-circular road

    private int[] l1; // lane 1 values: current speed of car (or -1 if no car)
    private int[] l2; // lane 2

    private int[] helperL1;
    private int[] helperL2;

    public Road() {
        // Initialise variables
        cars = new ArrayList<>();
        l1 = new int[TrafficSimulation.ROAD_SIZE];
        l2 = new int[TrafficSimulation.ROAD_SIZE];

        helperL1 = new int[TrafficSimulation.ROAD_SIZE];
        helperL2 = new int[TrafficSimulation.ROAD_SIZE];
        
        for (int i = 0; i < helperL1.length; i++) {
            l1[i] = -1;
            l2[i] = -1;
        }

        // randomly generate a car depending on rules (to avoid collisions)
        int dummyPosition = 0;
        Car tmpC;
        Random r = new Random();
        for (int i = 0; i < TrafficSimulation.NUMBER_OF_NORMAL_CARS; i++) {
            // randomly choose lane
            // randomly choose a value. position of prev car (chosen lane) + 11 + value = position of new car
            // randomly choose a speed

            tmpC = new NormalCar(i, r.nextInt(8) + 1, 1, dummyPosition);
            cars.add(tmpC);
            if (tmpC.getLane() == 1) l1[dummyPosition] = tmpC.getSpeed();
            else l2[dummyPosition] = tmpC.getSpeed();
            dummyPosition = Math.floorMod(dummyPosition + tmpC.getSpeed() + 4, TrafficSimulation.ROAD_SIZE);
        }

    }

    public void nextState() {
        // CALCULATE NEW STATE /////////////////////////////////////////////////
        // clear helper lanes
        for (int i = 0; i < helperL1.length; i++) {
            helperL1[i] = -1;
            helperL2[i] = -1;
        }

        // move cars (check rules on current road and save new positions in next road)
        for (Car car : cars) {
            moveCar(car);
        }

        // END OF CALCULATE NEW STATE //////////////////////////////////////////
        //
        // set new state
        l1 = helperL1.clone();
        l2 = helperL2.clone();
    }

    private void moveCar(Car car) {
        int lane = car.getLane();
        int position = car.getPosition();
        int speed = car.getSpeed();

        //find new position and new speed (if there is a change in speed)
        //For now we assume that the cars don't change lanes.
        int newLane = lane;
        //To know the new speed and position, we need to know the distance to and speed of the car in front.
        int nextCarPosition = getPositionOfNextCar(position, lane);
        int distanceToNextCar = Math.floorMod(nextCarPosition - position - 1, TrafficSimulation.ROAD_SIZE);
        int nextCarSpeed;

        if (lane == 1) nextCarSpeed = l1[nextCarPosition];
        else nextCarSpeed = l2[nextCarPosition];

        int newSpeed = car.adaptSpeed(distanceToNextCar, nextCarSpeed);

        //Position depends on the speed of the car in front
        //and the speed at which that car is driving.
        int newPosition = Math.floorMod(position + speed, TrafficSimulation.ROAD_SIZE);

        // commit the changes
        car.setLane(newLane);
        car.setPosition(newPosition);
        //Setting the new speed is already done during the call to adaptSpeed();
        
        if (newLane == 1) {
            helperL1[newPosition] = newSpeed;
        } else {
            helperL2[newPosition] = newSpeed;
        }
    }

    private int getPositionOfNextCar(int current_pos, int lane) {
        int distance = 0;
        for (int i = current_pos + 1; i < TrafficSimulation.ROAD_SIZE + current_pos; i++) {
            int correct_i = Math.floorMod(i, TrafficSimulation.ROAD_SIZE);
            if (lane == 1 && l1[correct_i] != -1) {
                return correct_i;
            } else if (lane == 2 && l2[correct_i] != -1) {
                return correct_i;
            }
        }
        return -1;
    }

    public void printTrafficSituation() {
        String traffic = "";
        String positions = "";
        for (int i = 0; i < l1.length; i++) {
            if (l1[i] == -1) {
                traffic += "_";
            } else {
                traffic += l1[i];
            }
        }
        traffic += "|";
        System.out.println(traffic);
    }
}
