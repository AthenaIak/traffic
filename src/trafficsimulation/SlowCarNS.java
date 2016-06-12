package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class SlowCarNS extends CarNS {

    public SlowCarNS(int ID, int lane, int position) {
        super(ID, lane, position);

        Random r = new Random();
        color = new Color(0, r.nextInt(130), 255);
        maxSpeed = RoadNS.MAX_SPEED_SLOW_CAR;
        speed = maxSpeed - r.nextInt(2);            // [maxSpeed-1, maxSpeed]
    }
   
}
