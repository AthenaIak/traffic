package trafficsimulation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

final public class AnimatedSimulation {

    private Road road;
    private static final int cooldown = TrafficSimulation.SIMULATION_STEP_COOLDOWN; //cooldown between steps of the simulation
    private final int carWidth = TrafficSimulation.CAR_WIDTH;
    private final int carHeight = 10;
    private int numIterations;

    private JFrame frame;
    private DrawPanel drawPanel;
    private int numRuns;

    /**
     * This method initialises and performs the simulation.
     * @param numOfIterations The number of states the simulation will run
     * for. If 0, then it never stops running.
     */
    public void initialiseSimulation(int numOfIterations) {
        // initialise the simulation
        numIterations = numOfIterations;
        road = new Road(numIterations);
        while (true) { // skip states until the system is stable
            if (road.nextState()) break;
        }
        System.out.println("Initial traffic situation:");
        road.printTrafficSituation();

        // set window title and stop running if X is pressed
        if (!TrafficSimulation.COLLECT_DATA) {
            frame = new JFrame("Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // create a panel that will contain the painting
            drawPanel = new DrawPanel();
            drawPanel.setPreferredSize(new Dimension(TrafficSimulation.ROAD_SIZE * carWidth, 300));

            // create a panel that makes the scrollbars appear
            JScrollPane jsp = new JScrollPane(drawPanel);
            // put the painting panel inside the scrollable panel
            jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            // put everything on the frame
            frame.getContentPane().add(BorderLayout.CENTER, jsp);
            frame.setResizable(true);
            frame.setSize(2000, 400);
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        }
    }

    /**
     * Continuously calculates and presents the next state.
     * @return Details about quality of the simulation (flow, distance traveled
     * etc).
     */
    public String runSimulation() {
        numRuns = 0;

        while (numIterations == 0 || numRuns < numIterations) {
            numRuns++;
            road.nextState(); // calculates the next state
            if (!TrafficSimulation.COLLECT_DATA) {
                frame.repaint(); // calls paintComponent(g) to draw the new state
                if (TrafficSimulation.PRINT_TO_CONSOLE) {
                    System.out.println("State #" + numRuns + ":");
                    road.printTrafficSituation();
                }
            }

            try {
                Thread.sleep(cooldown);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("End of simulation.");
        if (!TrafficSimulation.COLLECT_DATA) frame.dispose();

        return calculateMeasures();
    }

    /**
     * Helper panel used to draw the animation. Translates the current data
     * about the cars on the road to an image.
     */
    private class DrawPanel extends JPanel {

        @Override
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
                g.setColor(c.getColor()); // individual colour for each car

                switch (c.getLane()) {
                    case 1: g.fillRoundRect(c.getPosition() * carWidth, 103, carWidth - 5, carHeight, 2, 2);
                        break;
                    case 2: g.fillRoundRect(c.getPosition() * carWidth, 117, carWidth - 5, carHeight, 2, 2);
                        break;
                }

            }
        }
    }

    /**
     * Calculates all the statistics for the simulation.
     *
     * @return The statistics of the simulation.
     */
    private String calculateMeasures() {
        String output;
        //Flow is measures in number of cars passing a certain point.
        //Equivalently: Sum over all cars: number of cells traveled / road size
        int totalDistance = 0;
        int totalSlowDistance = 0;
        int totalFastDistance = 0;

        int maxSpeedSlow = -1;
        int maxSpeedFast = -1;

        int bestFlowFast = -1;
        int bestFlowSlow = -1;
        int worstFlowFast = 999999999;
        int worstFlowSlow = 999999999;

        int numSlow = 0;
        int numFast = 0;

        for (Car c : road.getCars()) {
            switch (c.getType()) {
                case "S":
                    numSlow++;
                    bestFlowSlow = c.getTraveledDistance() > bestFlowSlow ? c.getTraveledDistance() : bestFlowSlow;
                    worstFlowSlow = c.getTraveledDistance() < worstFlowSlow ? c.getTraveledDistance() : worstFlowSlow;
                    totalSlowDistance += c.getTraveledDistance();
                    maxSpeedSlow = c.getMaxReachedSpeed() > maxSpeedSlow ? c.getMaxReachedSpeed() : maxSpeedSlow;
                    break;
                case "F":
                    numFast++;
                    bestFlowFast = c.getTraveledDistance() > bestFlowFast ? c.getTraveledDistance() : bestFlowFast;
                    worstFlowFast = c.getTraveledDistance() < worstFlowFast ? c.getTraveledDistance() : worstFlowFast;
                    totalFastDistance += c.getTraveledDistance();
                    maxSpeedFast = c.getMaxReachedSpeed() > maxSpeedFast ? c.getMaxReachedSpeed() : maxSpeedFast;
                    break;
            }

            totalDistance += c.getTraveledDistance();
        }

        if (TrafficSimulation.COLLECT_DATA) {
            // model, road_block, max_speed_slow, max_speed_fast, fast_car_ratio, density, total_all_cars_distance, total_slow_cars_distance, total_fast_cars_distance, worst_case_distance_slow_cars, worst_cast_distance_fast_cars, best_case_distance_slow_car, best_case_distance_fast_car,num_slow_cars,num_fast_cars,global_speed_rule
            output = "ours," + (TrafficSimulation.BREAKING_DOWN_PROBABILITY == 0 ? "0" : "1") + "," + maxSpeedSlow + "," + maxSpeedFast + ","
                    + TrafficSimulation.FAST_CAR_RATIO + "," + TrafficSimulation.DENSITY + "," + totalDistance + "," + totalSlowDistance + ","
                    + totalFastDistance + "," + worstFlowSlow + "," + worstFlowFast + "," + bestFlowSlow + "," + bestFlowFast + "," + numSlow + ","
                    + numFast + "," + TrafficSimulation.GLOBAL_SPEED_RULE;
        } else {
            output = "RESULTS:\n" + (numSlow == 0 || numFast == 0 ? "" : "ALL CARS: total distance traveled=" + totalDistance + "\n")
                    + (numSlow == 0 ? "" : "SLOW CARS \ttotal distance traveled=" + totalSlowDistance + " \thighest speed=" + maxSpeedSlow + " \tbest flow=" + bestFlowSlow + " \tworst flow=" + worstFlowSlow)
                    + (numFast == 0 ? "" : "FAST CARS \ttotal distance traveled=" + totalFastDistance + " \thighest speed=" + maxSpeedFast + " \tbest flow=" + bestFlowFast + " \tworst flow=" + worstFlowFast);
        }

        return output;
    }
}
