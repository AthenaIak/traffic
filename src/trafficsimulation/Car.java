package trafficsimulation;

import java.awt.Color;

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

    public Car(int ID, int speed, int lane, int position) {
        this.ID = ID;
        this.speed = speed;
        this.lane = lane;
        this.position = position;
        this.leftBlinker = false;
        this.rightBlinker = false;
    }

    public int getID() {
        return ID;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
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

    /**
     *
     * @param l1 Current state of lane 1
     * @param l2 Current state of lane 2
     * @return true if the car moved (a move was possible)
     */
    public boolean move(int[] l1, int[] l2) {
        int desiredPosition = speed + maximumAcceleration < maximumSpeed ? position + speed + maximumAcceleration : position + maximumSpeed;
        int minimumPosition = speed - maximumDeceleration > 0 ? position + speed - maximumDeceleration : position;

        System.out.println("(" + lane + "," + position + "," + speed + ") " + desiredPosition + " " + minimumPosition);

        // retrive neighborhood info (position and speed of the 3 important neighbors)
        int nextCarSameLpos = getPositionOfNextCar(lane == 1 ? l1 : l2, position);
        int nextCarDiffLpos = getPositionOfNextCar(lane == 1 ? l2 : l1, position);
        int prevCarDiffLpos = getPositionOfPrevCar(lane == 1 ? l2 : l1, position);

        if (nextCarDiffLpos != -1) {
            int nextCarDiffLspeed = lane == 1 ? l2[nextCarDiffLpos] : l1[nextCarDiffLpos];
        }
        if (prevCarDiffLpos != -1) {
            int prevCarDiffLspeed = lane == 1 ? l2[prevCarDiffLpos] : l1[prevCarDiffLpos];
        }

        int nextCarSameLspeed, futureNextCarPos, distance, nextSpeed, minFutureGap, worstCaseGap;
        // iterate through all possible positions (starting from the most desired)
        for (int nextPosition = desiredPosition; nextPosition >= minimumPosition; nextPosition--) { //nextPosition is not rounded to road size yet
            nextSpeed = nextPosition - position;

            // check if this position is possible for the current lane
            if (nextCarSameLpos == -1) { // there is no other car on the lane, so moving is possible
                position = Math.floorMod(nextPosition, TrafficSimulation.ROAD_SIZE);
                speed = nextSpeed;
                // lane = lane;
                return true;
            }

            nextCarSameLspeed = lane == 1 ? l1[nextCarSameLpos] : l2[nextCarSameLpos];
            futureNextCarPos = nextCarSameLpos + nextCarSameLspeed - TrafficSimulation.GLOBAL_MAXIMUM_DECELERATION; // worst case senario
            minFutureGap = Math.floorDiv(nextSpeed, maximumDeceleration) + 1; // minimum gap so that no crashes (maximumDeceleration of the current car)

            if (nextCarSameLpos < position) //comes full circle
                worstCaseGap = futureNextCarPos + TrafficSimulation.ROAD_SIZE - nextPosition;
            else
                worstCaseGap = futureNextCarPos - nextPosition;
            if (worstCaseGap >= minFutureGap) { // then moving there is possible
                position = Math.floorMod(nextPosition, TrafficSimulation.ROAD_SIZE);
                speed = nextSpeed;
                // lane = lane;
                return true;
            }
            // TODO: examine for different lane
        }

        return false;
    }
//- calculate desired position:
//    new desired position = prev_pos + speed + max_acc
//    then loop through: speed + max_acc to speed - max_dec:
//        TODO: Check whether there are no cars in between the closest desired position and the current position. If that is the case, then we have a collision.
//        possible = ((next_car_pos + next_car_speed - max_dec) - (gap - desired_pos + speed + max_acc)) >= 0
//            where gap = (floor((new_speed+max_acc) / max_dec) + 1)
//                where new_speed = desired_position - position
//        if possible:
//            return desired_pos
//        else:
//            check different lane at same position - incorporate position/speed of previous car on other lane:
//                possible = prev_car_position + speed + max_acc + gap - position <= 0 (?)
//
//    return impossible (?)
//
//- calculate and update speed, lane, position based on desired position

    private int getPositionOfNextCar(int[] lane, int current_pos) {
        for (int i = current_pos + 1; i < TrafficSimulation.ROAD_SIZE + current_pos; i++) {
            int correct_i = Math.floorMod(i, TrafficSimulation.ROAD_SIZE);
            if (lane[correct_i] != -1) {
                return correct_i;
            }
        }
        return -1;
    }

    private int getPositionOfPrevCar(int[] lane, int current_pos) {
        for (int i = current_pos + TrafficSimulation.ROAD_SIZE; i > current_pos; i--) {
            int correct_i = Math.floorMod(i, TrafficSimulation.ROAD_SIZE);
            if (lane[correct_i] != -1) {
                return correct_i;
            }
        }
        return -1;
    }

    /**
     * This method adjusts the speed of the current car,
     * depending of the distance to and the speed of the car in front.
     *
     * @param distance_to_next : distance to car in front.
     * @param speed_of_next : speed in car in front.
     */
    public int adaptSpeed(int distance_to_next, int speed_of_next) {
        //We need to decelerate
        if (speed_of_next < speed) {
            //In this timestep there is enough room, but we may only assume ther isn't in the next.
            //If the speed difference is too big, we need to start decelerating now.
            if (distance_to_next == speed && (speed - speed_of_next) > maximumDeceleration) {
                speed -= Math.min(maximumDeceleration, speed - speed_of_next - maximumDeceleration);
            } //There is already too little room now, so we need to decelerate.
            else if (distance_to_next < speed) {
                speed -= Math.min(maximumDeceleration, speed - speed_of_next);
            }
        } // The car in front is driving faster than this car.
        else if (speed_of_next > speed) {
            //The distance to the next car is within the limit, but it will expand.
            //If it expands enough, we may accelerate.
            if (speed <= distance_to_next) {
                speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, (distance_to_next + (speed_of_next - speed)) - speed));
            } // The distance to the next car is greater than our speed; we may accelerate
            else {
                //The car may only accelerate if the gap created by the speed difference is at least as big as the speed.
                if (speed_of_next - speed + distance_to_next > speed) {
                    speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, speed_of_next - speed_of_next + distance_to_next));
                }
            }
        } else {
            //speed of two cars is equal. Only if the distance between the both is large enough, the car may accelerate.
            if (speed < distance_to_next) {
                //the car is allowed to accelerate if it is not driving at maximum speed already.
                //the maximum amount of acceleration is determined both by the physical limits of the car and the
                //distance to the car ahead.
                speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, distance_to_next - speed));
            }
        }
        return speed;
    }

    @Override
    public String toString() {
        String str;
        if (this.getClass().toString().equals("class trafficsimulation.NormalCar"))
            str = "N ";
        else if (this.getClass().toString().equals("class trafficsimulation.FastCar"))
            str = "F ";
        else
            str = "E ";

        return "(" + str + lane + "," + position + "," + speed + ") ";
    }

//                g.setColor(Color.blue);
//            else if (c.getClass().toString().equals("class trafficsimulation.FastCar"))
}
