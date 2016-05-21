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
    public int adaptSpeed(int distanceToFront, int speedOfFront, int speedOfFrontNextLane, int gapNextLane) {
        boolean debug=false;
        if (debug)
            System.out.print("Car " + ID + " at lane " + lane + ": Current speed " + speed + ", speed of front " + speedOfFront + ", distance to front " + distanceToFront + ". ");

        int gapToChange = TrafficSimulation.MAXIMUM_SPEED_SYSTEM + 1 + speed;
        
        if (distanceToFront <= TrafficSimulation.DISTANCE_AHEAD){
            // consider changing lane
            if ((lane==TrafficSimulation.RIGHT_LANE) && (speedOfFront <= speed || speedOfFrontNextLane <=speed) && gapNextLane >= gapToChange){
                lane = TrafficSimulation.LEFT_LANE;
                if (debug)
                    System.out.print("Change to left lane. ");
            }
            
            if ((lane==TrafficSimulation.LEFT_LANE) && (speedOfFront > speed && speedOfFrontNextLane > speed) && gapNextLane >= gapToChange){
                lane = TrafficSimulation.RIGHT_LANE;
                if (debug)
                    System.out.println("Change to right lane. ");
            }
        }

        if (speed < TrafficSimulation.MAXIMUM_SPEED_SYSTEM) speed += 1;
        if (speed > distanceToFront) speed = distanceToFront;
        if (speed >= 1){
            Random r = new Random();
            if (r.nextDouble() < TrafficSimulation.PROBABILITY_FLUCTUATION) speed --;
        }
        if (debug)
            System.out.println("Adjusted speed " + speed + "\n");
        return speed;
    }

}
