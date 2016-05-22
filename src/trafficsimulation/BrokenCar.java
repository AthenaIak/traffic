package trafficsimulation;

import java.awt.*;
import java.util.Random;

public class BrokenCar extends Car {

    private double breakDownProb;
    private boolean isBrokenDown;

    public BrokenCar(int ID, int speed, int lane, int position, double breakDownProb) {
        super(ID, speed, lane, position);

        maximumSpeed = 4;
        maximumAcceleration = 1;
        maximumDeceleration = 1;
        this.breakDownProb = breakDownProb;
        isBrokenDown = false;

        Random r = new Random();
        color = new Color(0, 255, 0);
    }

    @Override
    public int adaptSpeed(int speedOfFront, int distanceToFront, int speedOfFrontNextLane, int distanceToFrontNextLane, 
            int speedOfBehindNextLane, int distanceToBehindNextLane) {
        
        if (TrafficSimulation.DEBUG)
            System.out.print("Car " + ID + " lane " + lane + " pos " + position + " speed " + speed + 
                    ", sFront " + speedOfFront + ", dFront " + distanceToFront + 
                    ", sFrontNext " + speedOfFrontNextLane + ", dFrontNext " + distanceToFrontNextLane + 
                    ", sBehindNext " + speedOfBehindNextLane + " dBehindNext " + distanceToBehindNextLane +
                    ". Broken car.\n");
        
        return 0;
    }
    
//    public int adaptSpeed(int distanceToFront, int speedOfFront) {
//    public int adaptSpeed(int distanceToFront, int speedOfFront, int speedOfFrontNextLane, int gapNextLane) {
//
//        // The car may break down at this moment.
//        if (!isBrokenDown) {
//            double r = Math.random();
//            if (r < breakDownProb) {
//                isBrokenDown = true;
//                System.out.println("Car breaks down");
//                if (speed > 0) {
//                    speed -= Math.min(maximumDeceleration,speed);
//                    return 0;
//                } else {
//                    return 0;
//                }
//            } else {
//                //We need to decelerate
//                if (speedOfFront < speed) {
//                    //In this timestep there is enough room, but we may only assume ther isn't in the next.
//                    //If the speed difference is too big, we need to start decelerating now.
//                    if (distanceToFront == speed && (speed - speedOfFront) > maximumDeceleration) {
//                        speed -= Math.min(maximumDeceleration, speed - speedOfFront - maximumDeceleration);
//                    } //There is already too little room now, so we need to decelerate.
//                    else if (distanceToFront < speed) {
//                        speed -= Math.min(maximumDeceleration, speed - speedOfFront);
//                    }
//                }
//
//                // The car in front is driving faster than this car.
//                else if (speedOfFront > speed) {
//                    //The distance to the next car is within the limit, but it will expand.
//                    //If it expands enough, we may accelerate.
//                    if (speed <= distanceToFront) {
//                        speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, (distanceToFront + (speedOfFront - speed)) - speed));
//                    }
//                    // The distance to the next car is greater than our speed; we may accelerate
//                    else {
//                        //The car may only accelerate if the gap created by the speed difference is at least as big as the speed.
//                        if (speedOfFront - speed + distanceToFront > speed) {
//                            speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, speedOfFront - speedOfFront + distanceToFront));
//                        }
//                    }
//                } else {
//                    //speed of two cars is equal. Only if the distance between the both is large enough, the car may accelerate.
//                    if (speed < distanceToFront) {
//                        //the car is allowed to accelerate if it is not driving at maximum speed already.
//                        //the maximum amount of acceleration is determined both by the physical limits of the car and the
//                        //distance to the car ahead.
//                        speed += Math.min(maximumSpeed - speed, Math.min(maximumAcceleration, distanceToFront - speed));
//                    }
//                }
//                return speed;
//            }
//        // The car has broken down and is either decelerating at maxDecelartionspeed, or it has come to a full stop.
//        } else {
//            if (speed > 0) {
//                speed -= Math.min(maximumDeceleration,speed);
//                return 0;
//            } else {
//                return 0;
//            }
//        }
//
//    }
    
    }
