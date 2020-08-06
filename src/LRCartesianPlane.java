import java.awt.*;
import java.util.ArrayList;

import static java.lang.StrictMath.abs;

public class LRCartesianPlane extends CartesianPlane {
    private double a, b;
    private boolean lineVisible, errorVisible;

    LRCartesianPlane(Graphics2D g2, int width, int height, Panel mainPanel) {
        super(g2, width, height, mainPanel);
        lineVisible = true;
        errorVisible = true;
    }

    void initSideMenu() {
        String[] buttonLabels = new String[] {"Grid", "Line", "Errors", "Menu"};

        menu = new SideMenu(g2, width/9, height);
        menu.addButtons(buttonLabels, height/20);
        menu.addValueLabel("y", "0x + 0", height/20.0);
        menu.addValueLabel("Error", "0", height/20.0);
    }

    @Override
    public void draw() {
        drawLines();

        drawSamples();

        if(samples.size() > 1 && lineVisible) {
            drawRegressionLine();
        }

        menu.draw();
    }

    @Override
    public void onRightClick(double mouseX, double mouseY) {
        super.onRightClick(mouseX, mouseY);
        if(samples.size() > 1) {
            update();
        }
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(super.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY) && samples.size() > 1) {
            update();
        }
        return true;
    }

    void menuOptions(double mx, double my) {
        switch(menu.onReleased(mx, my)) {
            case "Grid": linesVisibility = !linesVisibility; break;
            case "Line": lineVisibleToggle(); break;
            case "Errors": errorVisibleToggle(); break;
            case "Menu": panel.changeGraphics("Menu");
        }
    }

    void lineVisibleToggle() { lineVisible = !lineVisible; }

    void errorVisibleToggle() { errorVisible = !errorVisible; }

    boolean colorSelectedSample(Color col, int value) {
        return super.colorSelectedSample(col, value);
    }

    void drawRegressionLine() {
        g2.setColor(new Color(0, 255, 0));
        g2.setStroke(new BasicStroke(3));
        drawStraightLine(a, b);

        if(errorVisible) {
            drawErrors();
        }
    }

    void drawErrors() {
        g2.setColor(new Color(255,0,0,130));
        for(Sample sample: samples) {
            int difference =  (int)((a*sample.getX() + b - sample.getY())*scale);

            if(difference > 0) {
                g2.fillRect((int) screenX(sample.getX()), (int) screenY(a * sample.getX() + b), difference, difference);
            } else {
                g2.fillRect((int) screenX(sample.getX()) + difference, (int) screenY(sample.getY()), -difference, -difference);
            }
        }
    }

    void update() {
        ArrayList<Double> coefficients = MathUtils.linearRegressionCoefficients(samples);
        a = coefficients.get(0);
        b = coefficients.get(1);

        menu.updateLabel("y", MathUtils.round(a, 2)+"x " + (b > 0 ? "+ " : "- ") + MathUtils.round(abs(b), 2));

        double error = 0;
        for(Sample sample: samples) {
            error += (sample.getY() - a*sample.getX()-b) * (sample.getY() - a*sample.getX()-b);
        }
        menu.updateLabel("Error", Double.toString(MathUtils.round(error, 2)));
    }
}
