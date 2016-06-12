package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class FastCarNS extends CarNS {

    public FastCarNS(int ID, int lane, int position) {
        super(ID, lane, position);
        
        Random r = new Random();
        color = new Color(255, r.nextInt(130), 0);
        maxSpeed = RoadNS.MAX_SPEED_FAST_CAR;
        speed = maxSpeed - r.nextInt(2);            // [maxSpeed-1, maxSpeed]        
    }
}
