import java.awt.*;
import static java.lang.StrictMath.abs;

/**
 * Class that simulates linear regression algorithm.
 * For the math standing behind the algorithm look at MathUtils class.
 */
public class LRCoordinateSystem extends CoordinateSystem {
    // a and b are coefficients of best fitting line
    protected double a, b;
    protected boolean regressionLineVisibility, errorVisibility;

    LRCoordinateSystem(Graphics2D g2, int width, int height, Panel mainPanel) {
        super(g2, width, height, mainPanel);
        menuName = "Visualizations";

        regressionLineVisibility = true;
        errorVisibility = true;
    }

    /**
     * Initializes the message window
     */
    @Override
    void initComponents() {
        super.initComponents();
        messageWindow = new MessageWindow(this, "data/Linear-Reg-Sim-About");
    }

    /**
     * Initializes the buttons that are related to linear regression simulation
     */
    void initSideMenu() {
        String[] buttonsLabels = new String[] {StringsResources.line(), StringsResources.errors()};
        Boolean[] buttonsValues = new Boolean[] {true, true};

        menu.addCheckBoxButtons(buttonsLabels, buttonsValues, height/20);
        menu.addValueLabel("y", "0x + 0", height/20.0);
        menu.addValueLabel(StringsResources.error(), "0", height/20.0);
    }

    /**
     * draws coordinate system lines, samples,
     * and, if there are at least two samples, best straight line
     * at the end menu and message window are drawn
     */
    @Override
    public void draw() {
        drawLines();

        drawSamples();

        if(samples.size() > 1) {
            drawRegressionLine();
        }

        drawInterface();
    }

    /**
     * Performs onRightClick from CoordinateSystem
     * if there are at least 2 samples updates the whole simulation
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
     * Performs onMouseDragged from CoordinateSystem. If it returns true then update the simulation.
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
     * checks if some of buttons in menu are pressed
     * if so then performs some action related to that pressed button.
     * Sliders are supported inside {@code menu.onReleased} method since they haven't got
     * any specific action and all of them behave the same way.
     * @param label - label of pressed button
     */
    void menuOptions(String label) {
        if(label.equals(StringsResources.line())) {
            regressionLineVisibility = !regressionLineVisibility;
        } else if(label.equals(StringsResources.errors())) {
            errorVisibility = !errorVisibility;
        } else {
            super.menuOptions(label);
        }
    }

    /**
     * Draws regression line and if error visibility variable is set to true then
     * error is also drawn
     */
    void drawRegressionLine() {
        if(regressionLineVisibility){
            g2.setColor(DrawUtils.green);
            g2.setStroke(new BasicStroke(3));
            drawStraightLine(a, b);
        }

        if(errorVisibility) {
            drawErrors();
        }
    }

    /**
     * Draws the visualization of the error. They are squares with sides equal to difference of y of sample and
     * of y value of the line with the same x as sample.
     */
    void drawErrors() {
        g2.setColor(DrawUtils.transparentRed);
        for(Sample sample: samples) {
            int difference =  (int)((a*sample.getX() + b - sample.getY())*scale);

            if(difference > 0) {
                g2.fillRect((int) screenX(sample.getX()), (int) screenY(a * sample.getX() + b), difference, difference);
            } else {
                g2.fillRect((int) screenX(sample.getX()) + difference, (int) screenY(sample.getY()), -difference, -difference);
            }
        }
    }

    /**
     * Updates the simulation. Finds new best straight line, calculates new error and updates labels in the menu.
     */
    @Override
    public void update() {
        double[] coefficients = MathUtils.fitLinearRegressionModel(samples);
        a = coefficients[0];
        b = coefficients[1];

        menu.updateLabel("y", MathUtils.round(a, 2)+"x " + (b > 0 ? "+ " : "- ") + MathUtils.round(abs(b), 2));

        double error = 0;
        for(Sample sample: samples) {
            error += (sample.getY() - a*sample.getX()-b) * (sample.getY() - a*sample.getX()-b);
        }
        menu.updateLabel(StringsResources.error(), Double.toString(MathUtils.round(error, 2)));
    }
}
