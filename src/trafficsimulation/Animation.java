package trafficsimulation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

final public class Animation {

    private final Road road = new Road();
    private static final int cooldown = TrafficSimulation.SIMULATION_STEP_COOLDOWN; //cooldown between steps of the simulation
    private int carWidth = TrafficSimulation.CAR_WIDTH;
    private int carHeight = 10;
    private int numRuns = 0;

    private JFrame frame;
    private DrawPanel drawPanel;

    /**
     * This method initialises and performs the simulation.
     */
    public void runSimulation() {
        // set window title and stop running if X is pressed
        frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // initialise the simulation
        road.printTrafficSituation();

        // create a panel that will contain the painting
        drawPanel = new DrawPanel();
        drawPanel.setPreferredSize(new Dimension(TrafficSimulation.ROAD_SIZE * carWidth, 300));

        // create a panel that makes the scrollbars appear
        JScrollPane jsp = new JScrollPane(drawPanel);
        // put the painting panel inside the scrollable panel
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        //TODO(optional): add UI.
        // put everything on the frame
        frame.getContentPane().add(BorderLayout.CENTER, jsp);
        frame.setResizable(true);
        frame.setSize(1300, 400);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        // start the simulation
        moveIt();
    }

    /**
     * Loops forever, moving from the current to the next state of the
     * simulation.
     */
    private void moveIt() {
        while (true) {
            numRuns++;

            road.nextState();
            frame.repaint(); // calls paintComponent(g)
            road.printTrafficSituation();

            // print the current flow every 100 iterations
            if (numRuns % 100 == 0)
                road.printFlow(numRuns);

            try {
                Thread.sleep(cooldown);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper panel used to draw the animation. Translates the current data
     * about the cars on the road to an image.
     */
    class DrawPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        public void paintComponent(Graphics g) {
            setBackground(new Color(240, 240, 185));
            g.setColor(Color.BLACK);

            //Draw the road
            g.fillRect(0, 100, TrafficSimulation.ROAD_SIZE * carWidth, carHeight * 2 + 10); // xpos, ypos, width, height

            //Draw the line that separates the two lanes.
            g.setColor(Color.white);
            for (int i = 0; i < TrafficSimulation.ROAD_SIZE * carWidth / 20; i++) {
                g.drawLine(i * 20, 100 + carHeight + 5, i * 20 + 10, 100 + carHeight + 5);
            }

            //Draw all cars as color filled round rectangles.
            for (Car c : road.getCars()) {
                // individual colour for each car
                g.setColor(c.getColor());
                if (c.getLane() == 1)
                    g.fillRoundRect(c.getPosition() * carWidth, 117, carWidth - 5, carHeight, 2, 2);
                if (c.getLane() == 2)
                    g.fillRoundRect(c.getPosition() * carWidth, 103, carWidth - 5, carHeight, 2, 2);
            }
        }
    }
}
