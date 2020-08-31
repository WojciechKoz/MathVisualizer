import java.awt.*;
import java.util.ArrayList;

public class KNNCartesianPlane extends CartesianPlane{
    private boolean distancesVisibility, ringsVisibility;
    private ArrayList<KNNInterface> interfaces = new ArrayList<>();

    KNNCartesianPlane(Graphics2D g2, int width, int height, Panel mainPanel) {
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
     * initializes the menu with buttons {Grid, Line, Errors, Menu}
     * and labels {line coefficient, error measure}
     */
    void initSideMenu() {
        String[] buttonsLabels = new String[] {"Distances", "Rings"};
        Boolean[] buttonsValues = new Boolean[] {false, false};

        menu.addCheckBoxButtons(buttonsLabels, buttonsValues, height/20);
        menu.addSlider("k", 1, 10, height/10.0, true);
    }

    /**
     * draws cartesian plane lines, samples,
     *
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
        samples.get(samples.size() - 1).setColor(new Color(130, 130, 130));
    }


    /**
     * Performs onRightClick from CartesianPlane
     * if there are at least 2 samples updates the whole simulation
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onRightClick(double mouseX, double mouseY) {
        super.onRightClick(mouseX, mouseY);
        update();
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        super.onMouseMoved(mouseX, mouseY, prevMouseX, prevMouseY);
        for(KNNInterface inter: interfaces) {
            inter.onMouseMoved(mouseX, mouseY, this);
        }
    }

    /**
     * Performs onMouseDragged from CartesianPlane. If it returns true then update the simulation.
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
     * Sliders are supported inside @code{menu.onReleased} method since they haven't got
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
     * performs colorSelectedSample from cartesianPlane and if it returns true
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
     * Draws regression line and if error visibility variable is set to true then
     * error is also drawn
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
