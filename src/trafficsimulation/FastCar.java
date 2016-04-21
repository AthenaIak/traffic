package trafficsimulation;

public class FastCar extends Car {

    public FastCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);

        maximumSpeed = 11;
        maximumAcceleration = 5;
        maximumDeceleration = 5;
    }

}
