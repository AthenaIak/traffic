package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class NormalCar extends Car{

    public NormalCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);

        maximumSpeed = 3;
        maximumAcceleration = 1;
        maximumDeceleration = 1;
        
        Random r = new Random();
        color = new Color(r.nextInt(130), r.nextInt(130), 255);
    }
    
}
