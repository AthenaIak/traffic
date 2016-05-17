package trafficsimulation;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;

public class Animation extends Applet implements Runnable {

    public Thread t;
    private final Road road = new Road();
    private static final int cooldown = 1000; //cooldown between steps of the simulation

    @Override
    public void start() {
        if (t == null) {
            t = new Thread(this, "New Thread");//New side Thread created on start of applet.
            t.start();
        }
    }

    @Override
    public void stop() {
        if (t != null) {
            t = null;//On stop of applet the created thread is destroyed.
        }
    }

    @Override
    public void run() {
        Thread t1 = Thread.currentThread();

        while (t == t1) {
            road.nextState();
            repaint();
            try {
                Thread.sleep(cooldown);
            } catch (Exception e) {
            }
        }
    }

    int carWidth = 30, carHeight = 10;

    @Override
    public void paint(Graphics g) {
        setBackground(new Color(240, 240, 185));
        g.setColor(Color.BLACK);

        //Draw the road (y: 
        g.fillRect(0, 100, TrafficSimulation.ROAD_SIZE * carWidth, carHeight * 2 + 10); // xpos, ypos, width, height

        //Draw the line.
        g.setColor(Color.white);
        for (int i = 0; i < TrafficSimulation.ROAD_SIZE * carWidth / 20; i++) {
            g.drawLine(i * 20, 100 + carHeight + 5, i * 20 + 10, 100 + carHeight + 5);
        }

        //Draw 4 colored cars using filled round rectangles.
        for (Car c : road.getCars()) {
            // 2 colours for the two types of cars
//            if (c.getClass().toString().equals("class trafficsimulation.NormalCar"))
//                g.setColor(Color.blue);
//            else if (c.getClass().toString().equals("class trafficsimulation.FastCar"))
//                g.setColor(Color.red);
            
            // individual colour for each car
            g.setColor(c.getColor());
            if (c.getLane() == 1)
                g.fillRoundRect(c.getPosition() * carWidth, 117, carWidth - 5, carHeight, 2, 2);
            if (c.getLane() == 2)
                g.fillRoundRect(c.getPosition() * carWidth, 103, carWidth - 5, carHeight, 2, 2);
        }
    }

}
