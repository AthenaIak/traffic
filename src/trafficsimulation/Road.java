package trafficsimulation;

import java.util.ArrayList;

public class Road {

    private ArrayList<Car> cars; // I chose arraylist so that it is extendable for non-circular road

    private int[] l1; // lane 1 values: current speed of car (or -1 if no car)
    private int[] l2; // lane 2

    private int[] helperL1;
    private int[] helperL2;

    public Road() {
        cars = new ArrayList<>();
        l1 = new int[TrafficSimulation.ROAD_SIZE];
        l2 = new int[TrafficSimulation.ROAD_SIZE];

        helperL1 = new int[TrafficSimulation.ROAD_SIZE];
        helperL2 = new int[TrafficSimulation.ROAD_SIZE];

        // randomly generate a car depending on rules (to avoid collisions)
        int dummyPosition = 0;
        Car tmpC;
        for (int i = 0; i < TrafficSimulation.NUMBER_OF_NORMAL_CARS; i++) {
            // randomly choose lane
            // randomly choose a value. position of prev car (chosen lane) + 11 + value = position of new car
            // randomly choose a speed
            tmpC = new FastCar(i, 1, 1, dummyPosition);
            cars.add(tmpC);

            if (tmpC.getLane() == 1) l1[dummyPosition] = tmpC.getSpeed();
            else l2[dummyPosition] = tmpC.getSpeed();
            dummyPosition += 5;
        }

    }

    public void nextState() {
        // CALCULATE NEW STATE /////////////////////////////////////////////////
        // clear helper lanes
        for (int i : helperL1) {
            helperL1[i] = -1;
            helperL2[i] = -1;
        }

        // move cars (check rules on current road and save new positions in next road)
        for (Car car : cars)
            moveCar(car);
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
        int newLane;
        int newPosition;
        int newSpeed;
        
        // TODO: calculate these variables
        
        // commit the changes
        car.setLane(newLane);
        car.setPosition(newPosition);
        car.setSpeed(newSpeed);
        if(newLane==1) helperL1[newPosition] = newSpeed;
        else helperL2[newPosition] = newSpeed; 
    }

}
