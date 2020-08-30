import java.awt.*;
import static java.lang.StrictMath.abs;

/**
 * Class that simulates logistic regression algorithm.
 * Even if that class contains the whole logistic regression model it hasn't got any math inside.
 * For the algorithms look at MathUtils class.
 */
public class LogCartesianPlane extends CartesianPlane {
    // a and b are coefficient of separation line
    private double a;
    private double b;
    // wx and wy are weights of that model
    private double wx;
    private double wy;
    private boolean separationLineVisibility, weightsVisibility;

    LogCartesianPlane(Graphics2D g2, int width, int height, Panel mainPanel) {
        super(g2, width, height, mainPanel);
        menuName = "Visualizations";

        separationLineVisibility = true;
        weightsVisibility = false;
    }

    /**
     * Initializes the message window
     */
    void initComponents() {
        messageWindow = new MessageWindow(width, height, "data/Logistic-Reg-Sim-About");
    }

    /**
     * Initializes all menu with buttons
     * sliders {ETA, Epochs}
     * labels {weights, bias, separation line}
     */
    void initSideMenu() {
        String[] buttonsLabels = new String[] {"Line", "Weights"};
        Boolean[] buttonsValues = new Boolean[] {true, false};
        int heightOfButton = height/20;

        menu.addCheckBoxButtons(buttonsLabels, buttonsValues, heightOfButton);
        menu.addSlider("ETA", 0.001, 1.2, 1.1*heightOfButton, false);
        menu.addSlider("Epochs", 1, 200, 1.1*heightOfButton, true);

        menu.addValueLabel("w", "[0, 0]", heightOfButton);
        menu.addValueLabel("bias", "0", heightOfButton);
        menu.addValueLabel("y", "0x + 0", heightOfButton);
    }

    /**
     * draws cartesian plane lines, all samples and if there are
     * at least 2 samples and one of them is red and the other is blue
     * and if line visibility is set to true then draws separation line
     * also if weight visibility is set to true then weight vector is drawn.
     * at the end menu and message window are drawn.
     */
    @Override
    public void draw() {
        drawLines();

        drawSamples();

        if(samples.size() > 1 && twoClassesExists()) {
            if(separationLineVisibility) drawSeparationLine();

            if(weightsVisibility) {
                g2.setStroke(new BasicStroke(2));
                g2.setColor(new Color(100, 255, 100));
                drawVector(wx, wy);
            }
        }

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
     * performs onRightClick method from Cartesian plane but refresh all simulation
     * only is there are at least 2 samples
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onRightClick(double mouseX, double mouseY) {
        super.onRightClick(mouseX, mouseY);
        if(samples.size() > 1) {
            update();
        }
    }

    /**
     * Performs onMouseDragged from cartesian plane and if it returns true (means that
     * something was changed and it might has an impact on the simulation) updates the whole simulation
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     * @return always true
     */
    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(super.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY) && samples.size() > 1) {
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
            case "Line": separationLineVisibility = !separationLineVisibility; break;
            case "Weights": weightsVisibility = !weightsVisibility; break;
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
     * Updates the whole simulation under the condition that at least one sample is red and one is blue.
     * Finds best weights for current samples using method @code{MathUtils.LogisticRegressionParameters}
     * Saves weights and bias, updates labels and finds the coefficients of separation line.
     * At the end predicts class for all neutral samples.
     */
    @Override
    public void update() {
        if(!twoClassesExists()) {
            return;
        }

        double[] neuron = MathUtils.fitLogisticRegressionModel(samples,
                (int)menu.readValueFromSlider("Epochs"),
                menu.readValueFromSlider("ETA"));
        wx = neuron[0];
        wy = neuron[1];
        double bias = neuron[2];

        a = -wx/wy;
        b = -bias /wy;

        menu.updateLabel("w", "["+MathUtils.round(wx, 2)+", "+MathUtils.round(wy, 2)+"]");
        menu.updateLabel("bias", Double.toString(MathUtils.round(bias, 2)));
        menu.updateLabel("y", MathUtils.round(a, 2)+"x " + (b > 0 ? "+ " : "- ") + MathUtils.round(abs(b), 2));

        // predictions
        for(Sample sample: samples) {
            if(sample.category() == 0) {
                int predictedCategory = MathUtils.sigmoid(wx*sample.getX() + wy*sample.getY() + bias) > 0.5 ? 1:0;
                sample.setPredictedColor(predictedCategory == 1 ? new Color(255, 100, 100) : new Color(100, 100, 255));
            }
        }
    }

    /**
     * checks if at least there is one red sample and one blue
     * @return true if there are red and blue samples otherwise false
     */
    boolean twoClassesExists() {
        boolean pos = false, neg = false;

        for(Sample sample: samples) {
            if(sample.category() == 1) {
                pos = true;
            } else if(sample.category() == 2){
                neg = true;
            }
            if(pos && neg) return true;
        }
        return false;
    }

    /**
     * draws line which separates red and blue samples
     */
    void drawSeparationLine() {
        g2.setColor(new Color(255, 255, 0));
        g2.setStroke(new BasicStroke(3));
        drawStraightLine(a, b);
    }
}
