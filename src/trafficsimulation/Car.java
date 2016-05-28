package trafficsimulation;

import java.awt.Color;
import java.util.Random;

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

    protected int ID;
    protected int speed; // current
    protected int lane; // current
    protected int position; // current
    protected boolean leftBlinker;
    protected boolean rightBlinker;
    protected int maximumSpeed;
    protected int maximumAcceleration;
    protected int maximumDeceleration;
    protected Color color;

    protected int traveledDistance;

    public Car(int ID, int speed, int lane, int position) {
        this.ID = ID;
        this.speed = speed;
        this.lane = lane;
        this.position = position;
        this.leftBlinker = false;
        this.rightBlinker = false;
    }

    // see FastCar.java, NormalCar.java
    public Car(int ID, int lane, int position) {
        this.ID = ID;
        this.lane = lane;
        this.position = position;
        this.leftBlinker = false;
        this.rightBlinker = false;
        this.traveledDistance = 0;
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
        int desiredPosition = speed + maximumAcceleration < maximumSpeed ? position + speed + maximumAcceleration : position + maximumSpeed;
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
                // lane = lane;
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
     * @return
     */
    private boolean prevCarIsFarEnough(int nextPosition, int prevCarPos, int prevCarSpeed) {
        if (prevCarPos == -1) return true;

        int worstCaseGap, moves, minFutureGap, futurePrevCarSpeed, prevCarDeceleration;

        // version 1 - reject because it's too small sometimes and creates conflict (we haven't calculated gap properly
//        int minFutureGap = Math.floorDiv(prevCarSpeed + TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION, TrafficSimulation.GLOBAL_MINIMUM_DECELERATION) + 1;
        //version 2 - reject because makes the cars queue (requires too big a gap)
//        prevCarDeceleration = TrafficSimulation.GLOBAL_MINIMUM_DECELERATION; // worst case senario
//        futurePrevCarSpeed = prevCarSpeed + TrafficSimulation.GLOBAL_MAXIMUM_ACCELERATION; // worst case senario
//        moves = Math.floorDiv(futurePrevCarSpeed+1, prevCarDeceleration); // moves required for full stop (worst case senario)
//        minFutureGap = moves * futurePrevCarSpeed - ((moves - 1) * moves) / 2 * prevCarDeceleration + 1; // minimum gap so that no crashes occur (maximumDeceleration of the current car)
//
        // version 3 - ignorant cars think all the other cars are like them - does not cause conflicts, but slow cars queue (and all cars wait for a long time)
        futurePrevCarSpeed = prevCarSpeed + maximumAcceleration;
        moves = Math.floorDiv(futurePrevCarSpeed + 1, maximumDeceleration);
        minFutureGap = moves * futurePrevCarSpeed - ((moves - 1) * moves) / 2 * maximumDeceleration + 1;

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
     * @return
     */
    private boolean nextCarIsFarEnough(int nextPosition, int nextSpeed, int nextCarPos, int nextCarSpeed) {

        if (nextCarPos == -1) { // there is no other car on the lane, so moving is possible
            position = Math.floorMod(nextPosition, TrafficSimulation.ROAD_SIZE);
            speed = nextSpeed;
            // lane = lane;
            return true;
        }

        int futureNextCarPos, minFutureGap, worstCaseGap, moves;
        futureNextCarPos = nextCarPos + nextCarSpeed - TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION; // worst case senario
//        minFutureGap = Math.floorDiv(nextSpeed, maximumDeceleration) + 1; // minimum gap so that no crashes occur (maximumDeceleration of the current car)
        moves = Math.floorDiv(nextSpeed + 1, maximumDeceleration); // moves required for full stop
        minFutureGap = moves * nextSpeed - ((moves - 1) * moves) / 2 * maximumDeceleration + 1; // minimum gap so that no crashes occur (maximumDeceleration of the current car)
        // ((moves - 1) * moves) / 2 is 1+2+...+k = k*(k+1)/2 where k = moves-1

        if (nextCarPos < position) //comes full circle
            worstCaseGap = futureNextCarPos + TrafficSimulation.ROAD_SIZE - nextPosition;
        else
            worstCaseGap = futureNextCarPos - nextPosition;

        // return true if the worst possible gap is at least as big as the minimum allowed gap
        return worstCaseGap >= minFutureGap;
    }

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

//    /**
//     * This method adjusts the speed of the current car,
//     * depending of the distance to and the speed of the car in front.
//     *
//     * @param distance_to_next : distance to car in front.
//     * @param speed_of_next : speed in car in front.
//     */
//    public int adaptSpeed(int distance_to_next, int speed_of_next) {
//        //We need to decelerate
//        if (speed_of_next < speed) {
//
//            //In this timestep there is enough room, but we may only assume ther isn't in the next.
//            //If the speed difference is too big, we need to start decelerating now.
//            if (distance_to_next == speed && (speed - speed_of_next) > maximumDeceleration) {
//                speed -= Math.min(maximumDeceleration, speed - speed_of_next - maximumDeceleration);
//            } //There is already too little room now, so we need to decelerate.
//            else if (distance_to_next < speed) {
//                speed -= Math.min(maximumDeceleration, speed - speed_of_next);
//            }
//
//        } // The car in front is driving faster than this car.
//        else if (speed_of_next > speed) {
//            //The distance to the next car is within the limit, but it will expand.
//            //If it expands enough, we may accelerate.
//            if (speed <= distance_to_next) {
//                speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, (distance_to_next + (speed_of_next - speed)) - speed));
//            } // The distance to the next car is greater than our speed; we may accelerate
//            else {
//                //The car may only accelerate if the gap created by the speed difference is at least as big as the speed.
//                if (speed_of_next - speed + distance_to_next > speed) {
//                    speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, speed_of_next - speed_of_next + distance_to_next));
//                }
//            }
//        } else {
//            //speed of two cars is equal. Only if the distance between the both is large enough, the car may accelerate.
//            if (speed < distance_to_next) {
//                //the car is allowed to accelerate if it is not driving at maximum speed already.
//                //the maximum amount of acceleration is determined both by the physical limits of the car and the
//                //distance to the car ahead.
//                speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, distance_to_next - speed));
//            }
//        }
//
//        return speed;
//    }
    public int getID() {
        return ID;
    }

    public int getTraveledDistance() {
        return traveledDistance;
    }

    public int getSpeed() {
        return speed;
    }

    private void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getLane() {
        return lane;
    }

    public void switchLane() {
        lane = Math.floorMod(2 * lane, 3);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public boolean isLeftBlinkerOn() {
        return leftBlinker;
    }

    public void setLeftBlinker(boolean leftBlinker) {
        this.leftBlinker = leftBlinker;
    }

    public boolean isRightBlinkerOn() {
        return rightBlinker;
    }

    public void setRightBlinker(boolean rightBlinker) {
        this.rightBlinker = rightBlinker;
    }

    public int getMaximumAcceleration() {
        return maximumAcceleration;
    }

    public int getMaximumDeceleration() {
        return maximumDeceleration;
    }

    public int getMaximumSpeed() {
        return maximumSpeed;
    }

    public Color getColor() {
        return color;
    }
    
    private String getType(){
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

//                g.setColor(Color.blue);
//            else if (c.getClass().toString().equals("class trafficsimulation.FastCar"))
}
