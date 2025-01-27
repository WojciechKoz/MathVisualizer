import java.awt.*;
import static java.lang.StrictMath.abs;

/**
 * Class that simulates logistic regression algorithm.
 * Even if that class contains the whole logistic regression model it hasn't got any math inside.
 * For the algorithms look at MathUtils class.
 */
public class LogCoordinateSystem extends CoordinateSystem {
    // a and b are coefficient of separation line
    private double a;
    private double b;
    // wx and wy are weights of that model, bias is a free coefficient
    private double wx;
    private double wy;
    private double bias;
    private boolean separationLineVisibility, weightsVisibility;
    // important to keep track if changes occurred
    private int epochs = 0;
    private double eta = 0.0;

    LogCoordinateSystem(int width, int height, Panel mainPanel) {
        super(width, height, mainPanel);
        menuName = "Visualizations";

        separationLineVisibility = true;
        weightsVisibility = false;
    }

    /**
     * Initializes the message window
     */
    void initComponents() {
        super.initComponents();
        messageWindow = new MessageWindow(this, "data/"+StringsResources.languageShortcut()+"/Logistic-Reg-Sim-Help");
    }

    /**
     * Initializes the buttons in the side menu that are related to logistic regression simulation
     */
    void initSideMenu() {
        String[] buttonsLabels = new String[] {StringsResources.line(), StringsResources.weights()};
        Boolean[] buttonsValues = new Boolean[] {true, false};

        menu.addCheckBoxButtons(buttonsLabels, buttonsValues, STANDARD_BUTTON_HEIGHT);
        menu.addSlider(StringsResources.eta(), 0.001, 1.2, 1.1*STANDARD_BUTTON_HEIGHT, false);
        menu.addSlider(StringsResources.epochs(), 1, 200, 1.1*STANDARD_BUTTON_HEIGHT, true);

        menu.addValueLabel("w", "[0, 0]", STANDARD_BUTTON_HEIGHT);
        menu.addValueLabel(StringsResources.bias(), "0", STANDARD_BUTTON_HEIGHT);
        menu.addValueLabel("y", "0x + 0", STANDARD_BUTTON_HEIGHT);
    }

    /**
     * draws coordinate system lines, all samples and if there are
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
                DrawUtils.g2.setStroke(new BasicStroke(2));
                DrawUtils.g2.setColor(DrawUtils.lightGreen);
                drawVector(wx, wy);
            }
        }

        drawInterface();
    }

    /**
     * adds new Sample but always with '0' class which means that it's neutral and its color is gray therefore no update
     * @param x - Initial x coordinate (in cartesian plane simulation)
     * @param y - Initial y coordinate (in cartesian plane simulation)
     */
    void addNewSample(double x, double y) {
        super.addNewSample(x, y);
        samples.get(samples.size() - 1).setCategory(0);
        samples.get(samples.size() - 1).setColor(DrawUtils.sampleColors[0]);
    }

    /**
     * performs onRightClick method from CoordinateSystem but refresh all simulation
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
     * Performs onMouseDragged from Coordinate system and if it returns true (meaning that
     * something was changed and it might has an impact on the simulation) updates the whole simulation
     * if neutral sample was moved then no update
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     * @return always true
     */
    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(super.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY) && samples.size() > 1) {
            // hyperparameters are different - have to update
            if((int)menu.readValueFromSlider(StringsResources.epochs()) != epochs ||
                    menu.readValueFromSlider(StringsResources.eta()) != eta) {
                update();
                return true;
            }

            for(Sample s: samples) {
                if(s.isMoving()) {
                    if(s.category() != 0) {
                        update();
                    } else {
                        predict();
                    }
                    return true;
                }
            }

        }
        return true;
    }

    /**
     * checks if some of buttons in menu are pressed
     * if so then performs some action related to that pressed button.
     * Sliders are supported inside {@code menu.onReleased} method since they haven't got
     * any specific action and all of them behave the same way.
     * @param label - label of pressed button
     */
    void menuOptions(String label) {
        if(label.equals(StringsResources.line())) {
            separationLineVisibility = !separationLineVisibility;
        } else if(label.equals(StringsResources.weights())) {
            weightsVisibility = !weightsVisibility;
        } else {
            super.menuOptions(label);
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
     * Finds best weights for current samples using method {@code MathUtils.LogisticRegressionParameters}
     * Saves weights and bias, updates labels and finds the coefficients of separation line.
     * At the end predicts class for all neutral samples.
     */
    @Override
    public void update() {
        if(!twoClassesExists()) {
            return;
        }

        epochs = (int)menu.readValueFromSlider(StringsResources.epochs());
        eta = menu.readValueFromSlider(StringsResources.eta());

        double[] neuron = MathUtils.fitLogisticRegressionModel(samples, epochs, eta);

        wx = neuron[0];
        wy = neuron[1];
        bias = neuron[2];

        a = -wx/wy;
        b = -bias /wy;

        menu.updateLabel("w", "["+MathUtils.round(wx, 2)+", "+MathUtils.round(wy, 2)+"]");
        menu.updateLabel(StringsResources.bias(), Double.toString(MathUtils.round(bias, 2)));
        menu.updateLabel("y", MathUtils.round(a, 2)+"x " + (b > 0 ? "+ " : "- ") + MathUtils.round(abs(b), 2));

        // predictions
        predict();
    }

    /**
     *  Predicts class of all neutral samples
     *  Used at the end of update method and when neutral sample was moved
     */
    void predict() {
        for(Sample sample: samples) {
            if(sample.category() == 0) {
                int predictedCategory = MathUtils.sigmoid(wx*sample.getX() + wy*sample.getY() + bias) > 0.5 ? 1:0;
                sample.setPredictedColor(predictedCategory == 1 ? DrawUtils.lightRed : DrawUtils.lightBlue);
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
        DrawUtils.g2.setColor(DrawUtils.yellow);
        DrawUtils.g2.setStroke(new BasicStroke(3));
        drawStraightLine(a, b);
    }
}
