package nsSimulation;

import java.awt.Color;
import java.util.Random;

public class FastCarNS extends CarNS {

    /*
    Constructor
    Input:
        ID          ID of car, for debugging purpose
        lane        lane of car at the beginning (initialization)
        position    position of car at the beginning (initialization)
        limitSpeed  max speed at the beginning (initialization) - due to road situation, high density for example
    */
    public FastCarNS(int ID, int lane, int position, int limitSpeed) {
        super(ID, lane, position);
        
        Random r = new Random();
        color = new Color(255, r.nextInt(130), 0);
        maxSpeed = TrafficSimulation.MAX_SPEED_FAST_CAR;
        speed = Math.min(maxSpeed, limitSpeed) - r.nextInt(2);            // [maxSpeed-1, maxSpeed]        
    }
    
}
