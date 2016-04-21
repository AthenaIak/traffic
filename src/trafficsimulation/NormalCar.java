/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsimulation;

/**
 *
 * @author Athena
 */
public class NormalCar extends Car{

    public NormalCar(int ID, int speed, int lane, int position) {
        super(ID, speed, lane, position);

        maximumSpeed = 9;
        maximumAcceleration = 3;
        maximumDeceleration = 3;
    }
    
}
