package trafficsimulation;

import java.awt.Color;
import java.util.Random;
import java.util.stream.IntStream;

/*
 speed: 
 cells/t   m/s km/h
 1           3   10,8
 2           6   21,6
 3           9   32,4
 4           12  43,2
 5           15  54
 6           18  64,8
 7           21  75,6
 8           24  86,4
 9           27  97,2
 10          30  108
 11          33  118,8

 Note: If a moment t lasts 1 second, then a cell represents 3 meters 
 (which is also proximately the legth of a car).
 */
public abstract class Car {

    protected int speed; // current
    protected int lane; // current
    protected int position; // current
    protected int maximumSpeed;
    protected int maximumAcceleration;
    protected int maximumDeceleration;
    protected Color color;
    protected int traveledDistance;
    
    protected int[] speedLog;
    protected int numIterations = 0;
    protected int maxReachedSpeed = -1;

    /**
     * Creates a car.
     * @param speed The current speed of the car.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     * @param logLength The number of iterations
     */
//    public Car(int speed, int lane, int position, int logLength) {
//        this.speed = speed;
//        this.lane = lane;
//        this.position = position;
//        this.traveledDistance = 0;
//        this.speedLog = new int[logLength];
//    }

    /**
     * Creates a car.
     * @param lane The current lane of the car.
     * @param position The current position of the car.
     * @param logLength The number of iterations
     */
    public Car(int lane, int position, int logLength) {
        this.lane = lane;
        this.position = position;
        this.traveledDistance = 0;
        this.speedLog = new int[logLength];
    }

    /**
     * This method figures out the next move for the car (and performs the
     * move).
     * @param l1 Current state of lane 1
     * @param l2 Current state of lane 2
     * @return true if a move was possible for the car.
     */
    public boolean move(int[] l1, int[] l2) {
        this.traveledDistance += speed;
        if (numIterations < speedLog.length) {
            speedLog[numIterations] = speed;
            numIterations++; 
        }
        maxReachedSpeed = maxReachedSpeed < speed ? speed : maxReachedSpeed;
        
        //If global max speed holds, stick to that. Otherwise, do as you do.
        int desiredPosition;
        if (TrafficSimulation.GLOBAL_SPEED_RULE) {
            if (speed >= TrafficSimulation.GLOBAL_MAX_SPEED) {
                desiredPosition = position + speed - Math.min(maximumDeceleration, speed - TrafficSimulation.GLOBAL_MAX_SPEED);
            } else{ 
                desiredPosition = speed + maximumAcceleration < TrafficSimulation.GLOBAL_MAX_SPEED ? position + speed + maximumAcceleration : position + TrafficSimulation.GLOBAL_MAX_SPEED;
            }
        } else {
            desiredPosition = speed + maximumAcceleration < maximumSpeed ? position + speed + maximumAcceleration : position + maximumSpeed;
        }
        int minimumPosition = speed - maximumDeceleration > 0 ? position + speed - maximumDeceleration : position;
        
        // retrieve neighborhood info (position and speed of the 3 important neighbors)
            int nextCarSameLpos = getPositionOfNextCar(lane == 1 ? l1 : l2, position);
            int nextCarDiffLpos = getPositionOfNextCar(lane == 1 ? l2 : l1, position);
            int prevCarDiffLpos = getPositionOfPrevCar(lane == 1 ? l2 : l1, position);
            if (prevCarDiffLpos != -1) {
                int prevCarDiffLspeed = lane == 1 ? l2[prevCarDiffLpos] : l1[prevCarDiffLpos];
            }

            int nextCarSameLspeed, nextCarDiffLspeed, nextSpeed;

            Random r = new Random();
            if (lane == 1 && r.nextDouble() < 0.5) desiredPosition--;
            if (lane == 2 && r.nextDouble() < 0.05) desiredPosition--;

            // iterate through all possible positions (starting from the most desired)
            for (int nextPosition = desiredPosition; nextPosition >= minimumPosition; nextPosition--) { //nextPosition is not rounded to road size yet
                nextSpeed = nextPosition - position;

                // check if this position is possible for the current lane
                nextCarSameLspeed = nextCarSameLpos == -1 ? -1 : lane == 1 ? l1[nextCarSameLpos] : l2[nextCarSameLpos];
                if (nextCarIsFarEnough(nextPosition, nextSpeed, nextCarSameLpos, nextCarSameLspeed)) {
                    position = Math.floorMod(nextPosition, TrafficSimulation.ROAD_SIZE); // nextPosition is now rounded to road size
                    speed = nextSpeed;
                    return true;
                }

                // check if this position is possible for the other lane
                int prevCarDiffLspeed = prevCarDiffLpos == -1 ? -1 : lane == 1 ? l2[prevCarDiffLpos] : l1[prevCarDiffLpos];
                if (prevCarIsFarEnough(nextPosition, prevCarDiffLpos, prevCarDiffLspeed)) { // then check if next car (diff lane) is far enough
                    nextCarDiffLspeed = nextCarDiffLpos == -1 ? -1 : lane == 1 ? l2[nextCarDiffLpos] : l1[nextCarDiffLpos];
                    if (nextCarIsFarEnough(nextPosition, nextSpeed, nextCarDiffLpos, nextCarDiffLspeed)) {
                        position = Math.floorMod(nextPosition, TrafficSimulation.ROAD_SIZE);
                        speed = nextSpeed;
                        lane = lane == 1 ? 2 : 1; // switch lane
                        return true;
                    }
                }
            }

            // if no solution found, decelerate as much as possible and stay at the same lane.
            speed = minimumPosition - position;
            position = Math.floorMod(minimumPosition, TrafficSimulation.ROAD_SIZE);
            return false;
    }

    /**
     * Checks if the gap between this car and the previous one (different lane)
     * is enough to allow for a lane switch.
     * @param nextPosition Position this car will have if it makes the desired
     * move.
     * @param prevCarPos Position of the previous car (different lane) right
     * now.
     * @param prevCarSpeed Speed of the previous car (different lane) right now
     * (cells it covered with its previous move).
     * @return True if the car behind is far enough (or false if it isn't).
     */
    private boolean prevCarIsFarEnough(int nextPosition, int prevCarPos, int prevCarSpeed) {
        if (prevCarPos == -1) return true;

        int worstCaseGap, moves, minFutureGap, futurePrevCarSpeed, prevCarDeceleration;

        // ignorant cars think all the other cars are like them - does not cause conflicts, but slow cars queue (and all cars wait for a long time)
        futurePrevCarSpeed = prevCarSpeed + maximumAcceleration;

        // count the number of moves that are required for the car behind to come to full stop
        moves = -Math.floorDiv(-futurePrevCarSpeed, maximumDeceleration); // http://stackoverflow.com/questions/27643616/ceil-conterpart-for-math-floordiv-in-java
        // calculate the number of cells required for the car behind to come to full stop (and add 1 extra cell)
        minFutureGap = moves * futurePrevCarSpeed - ((moves - 1) * moves) / 2 * maximumDeceleration + 1;  // ((moves - 1) * moves) / 2 is 1+2+...+k = k*(k+1)/2 where k = moves-1
        
        // Hue - test reserved gap
//        minFutureGap = 0;
//        if (futurePrevCarSpeed>nextPosition-position)
//            minFutureGap = moves * (futurePrevCarSpeed-nextPosition-position);        

        // minFutureGap is the minimum allowed gap. worstCaseGap is actual gap between the two cars, if *this* car performs the eximined move.
        if (prevCarPos > position) //comes full circle
            worstCaseGap = nextPosition - (prevCarPos + prevCarSpeed + TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION - TrafficSimulation.ROAD_SIZE);
        else
            worstCaseGap = nextPosition - (prevCarPos + prevCarSpeed + TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION);

        // return true if the worst possible gap is at least as big as the minimum allowed gap
        return worstCaseGap >= minFutureGap;
    }

    /**
     * Checks if the gap between this car and the next one is enough to make the
     * desired move possible.
     * @param nextPosition Position this car will have if it makes the desired
     * move.
     * @param nextSpeed Speed this car will have if it makes the desired move
     * (number of cells it will cross).
     * @param nextCarPos Position of the next car right now.
     * @param nextCarSpeed Speed of the next car right now (cells it covered
     * with its previous move).
     * @return True if the car in front is far enough (or false if it isn't).
     */
    private boolean nextCarIsFarEnough(int nextPosition, int nextSpeed, int nextCarPos, int nextCarSpeed) {

        if (nextCarPos == -1) { // there is no other car on the lane, so moving is possible
            position = Math.floorMod(nextPosition, TrafficSimulation.ROAD_SIZE);
            speed = nextSpeed;
            return true;
        }

        int futureNextCarPos, minFutureGap, worstCaseGap, moves;
        futureNextCarPos = nextCarPos + nextCarSpeed - TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION; // worst case senario

        // count the number of moves that are required for the car infront to come to full stop
        moves = -Math.floorDiv(-nextSpeed, maximumDeceleration);
        // calculate the number of cells required for the car infront to come to full stop
        minFutureGap = moves * nextSpeed - ((moves - 1) * moves) / 2 * maximumDeceleration + 1;
        
        // Hue - test reserved gap
//        minFutureGap = 0;
//        if (nextCarSpeed-TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION  < nextSpeed)
//            minFutureGap = moves * (nextSpeed - nextCarSpeed + TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION);

        // minFutureGap is the minimum allowed gap. worstCaseGap is actual gap between the two cars, if *this* car performs the eximined move.
        if (nextCarPos < position) //comes full circle
            worstCaseGap = futureNextCarPos + TrafficSimulation.ROAD_SIZE - nextPosition;
        else
            worstCaseGap = futureNextCarPos - nextPosition;

        // return true if the worst possible gap is at least as big as the minimum allowed gap
        return worstCaseGap >= minFutureGap;
    }

    /**
     * Finds the position of the car directly in front of the specified
     * position, on the specified lane.
     * @param lane The lane that will be checked.
     * @param current_pos The position of "this" car.
     * @return The index of the car in the lane array, or -1 if no car exists.
     */
    private int getPositionOfNextCar(int[] lane, int current_pos) {
        int correct_i;
        for (int i = current_pos + 1; i < TrafficSimulation.ROAD_SIZE + current_pos; i++) {
            correct_i = Math.floorMod(i, TrafficSimulation.ROAD_SIZE);
            if (lane[correct_i] != -1) {
                return correct_i;
            }
        }
        return -1;
    }

    /**
     * Finds the position of the car directly behind (or in parallel) of the
     * specified position, on the specified lane.
     * @param lane The lane that will be checked.
     * @param current_pos The position of "this" car.
     * @return The index of the car in the lane array, or -1 if no car exists.
     */
    private int getPositionOfPrevCar(int[] lane, int current_pos) {
        int correct_i;
        for (int i = current_pos + TrafficSimulation.ROAD_SIZE; i > current_pos; i--) {
            correct_i = Math.floorMod(i, TrafficSimulation.ROAD_SIZE);
            if (lane[correct_i] != -1) {
                return correct_i;
            }
        }
        return -1;
    }

    /**
     * Zeros the traveled distance.
     */
    public void clearTraveledDistance() {
        traveledDistance = 0;
    }

    public int getTraveledDistance() {
        return traveledDistance;
    }

    public int getSpeed() {
        return speed;
    }

    public int getLane() {
        return lane;
    }

    public int getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }
    
    public int getMaxSpeed() {
        return maxReachedSpeed;
    }
    
    public int getTravelDistance() {
        return traveledDistance;
    }

    public String getType() {
        switch (this.getClass().toString()) {
            case "class trafficsimulation.NormalCar":
                return "N";
            case "class trafficsimulation.FastCar":
                return "F";
        }
        return "E";
    }

    @Override
    public String toString() {
        return "(" + getType() + " " + lane + "," + position + "," + speed + ") ";
    }
}


