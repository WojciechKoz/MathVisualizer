import java.awt.*;
import java.util.ArrayList;

/**
 * Class that simulates K-nearest neighbours algorithm.
 * Even if that class contains the whole KNN model it hasn't got any math inside.
 * For the algorithms look at MathUtils class.
 * This class uses the KNNInterface which is a graphical class for drawing
 * rings and lines between neutrals samples and their neighbours
 */
public class KNNCoordinateSystem extends CoordinateSystem {
    private boolean distancesVisibility, ringsVisibility;
    private ArrayList<KNNInterface> interfaces = new ArrayList<>();

    KNNCoordinateSystem(Graphics2D g2, int width, int height, Panel mainPanel) {
        super(g2, width, height, mainPanel);
        menuName = "Visualizations";

        distancesVisibility = false;
        ringsVisibility = false;
    }

    /**
     * Initializes the message window
     */
    @Override
    void initComponents() {
        messageWindow = new MessageWindow(width, height, "data/KNN-Sim-About");
    }

    /**
     * Initializes the specific buttons for the side menu
     */
    void initSideMenu() {
        String[] buttonsLabels = new String[] {"Distances", "Rings"};
        Boolean[] buttonsValues = new Boolean[] {false, false};

        menu.addCheckBoxButtons(buttonsLabels, buttonsValues, height/20);
        menu.addSlider("k", 1, 10, height/10.0, true);
    }

    /**
     * draws coordinate system lines, samples, and KNNInterfaces
     * at the end menu and message window are drawn
     */
    @Override
    public void draw() {
        drawLines();
        drawKNNInterface();

        drawSamples();

        menu.draw();
        messageWindow.draw(g2);
    }

    /**
     * adds new Sample but always with '0' class which means that it's neutral and its color is gray.
     * @param x - Initial x coordinate (in cartesian plane simulation)
     * @param y - Initial y coordinate (in cartesian plane simulation)
     */
    void addNewSample(double x, double y) {
        super.addNewSample(x, y);
        samples.get(samples.size() - 1).setCategory(0);
        samples.get(samples.size() - 1).setColor(DrawUtils.sampleColors[0]);
    }

    /**
     * Performs onRightClick from CoordinateSystem
     * and updates the whole simulation
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onRightClick(double mouseX, double mouseY) {
        super.onRightClick(mouseX, mouseY);
        update();
    }

    /**
     * Checks if mouse is above some neutral sample.
     * If so then changes corresponding interface active value to true
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     */
    @Override
    public void onMouseMoved(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        super.onMouseMoved(mouseX, mouseY, prevMouseX, prevMouseY);
        for(KNNInterface inter: interfaces) {
            inter.onMouseMoved(mouseX, mouseY, this);
        }
    }

    /**
     * Performs onMouseDragged from CoordinateSystem. If it returns true then update the simulation.
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     * @return always true
     */
    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(super.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY)) {
            update();
        }
        return true;
    }

    /**
     * checks if some of buttons in menu is pressed
     * if so then performs some action related to that pressed button.
     * Sliders are supported inside {@code menu.onReleased} method since they haven't got
     * any specific action and all of them behave the same way.
     * @param label - label of pressed button
     */
    void menuOptions(String label) {
        switch(label) {
            case "Distances": distancesVisibility = !distancesVisibility; break;
            case "Rings": ringsVisibility = !ringsVisibility; break;
            default: super.menuOptions(label);
        }
    }

    /**
     * performs colorSelectedSample from CoordinateSystem and if it returns true
     * updates the whole simulation.
     * @param col - new Color of selected sample
     * @param value - new value (related to color) of selected sample
     * @return always true
     */
    boolean colorSelectedSample(Color col, int value) {
        if(super.colorSelectedSample(col, value)) {
            update();
        }
        return true;
    }

    /**
     * Draws the interface of KNN algorithm - distances between neutral samples and their neighbours and
     * rings with radii equal to distances between neutral samples and the farthest samples.
     */
    void drawKNNInterface() {
        if(!atLeastOneTrainingSample()) {
            return;
        }

        for(KNNInterface inter: interfaces) {
            inter.drawDistances(g2, this, distancesVisibility);
            inter.drawRing(g2, this, ringsVisibility);
        }
    }

    /**
     * checks if in the data there is at least one sample with known class
     * @return true if there is a training sample otherwise false
     */
    boolean atLeastOneTrainingSample() {
        for(Sample sample: samples) {
            if(sample.category() != 0) return true;
        }
        return false;
    }

    /**
     * Updates the simulation.
     */
    @Override
    public void update() {
        if(atLeastOneTrainingSample()) {
            interfaces = MathUtils.KNNAlgorithm(samples, (int) menu.readValueFromSlider("k"));

            for(KNNInterface inter: interfaces) {
                inter.onMouseMoved(panel.prevMouseX, panel.prevMouseY, this);
            }
        }
    }
}
