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
 12          36  129.6
 13          39  140.4

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

    public Car(int ID, int lane, int position) {
        this.ID = ID;
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
        lane = Math.floorMod(2*lane,3);
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
            System.out.println("Car " + ID + " at lane " + lane + ": Current speed " + speed + ", speed of next " + speed_of_next + ", distance to next " + distance_to_next + "\n");
            if (distance_to_next == speed && (speed - speed_of_next) > maximumDeceleration) {
                speed -= Math.min(maximumDeceleration, speed - speed_of_next - maximumDeceleration);
            } //There is already too little room now, so we need to decelerate.
            else if (distance_to_next < speed) {
                speed -= Math.min(maximumDeceleration, speed - speed_of_next);
            }
            System.out.println("Adjusted speed " + speed + "\n");
        } 

        // The car in front is driving faster than this car.
        else if (speed_of_next > speed) {
            //The distance to the next car is within the limit, but it will expand.
            //If it expands enough, we may accelerate.
            if (speed <= distance_to_next) {
                speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, (distance_to_next + (speed_of_next - speed)) - speed));
            } 
            // The distance to the next car is greater than our speed; we may accelerate
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


}
