package trafficsimulation;

import java.awt.Color;
import java.util.Random;

public class NormalCar extends Car{

    public NormalCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);

        maximumSpeed = 9;
        maximumAcceleration = 3;
        maximumDeceleration = 3;
        
        Random r = new Random();
        color = new Color(r.nextInt(130), r.nextInt(130), 255);
    }
    
}
