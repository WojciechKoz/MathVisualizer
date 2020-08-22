import java.awt.*;

import static java.lang.StrictMath.abs;

public class LogCartesianPlane extends CartesianPlane {
    private double a, b, wx, wy, bias;
    private boolean lineVisible, weightVisible;

    LogCartesianPlane(Graphics2D g2, int width, int height, Panel mainPanel) {
        super(g2, width, height, mainPanel);
        lineVisible = true;
        weightVisible = false;
    }

    void initSideMenu() {
        String[] buttonLabels = new String[] {"Grid", "Line", "Weights", "Menu"};
        int heightOfButton = height/20;

        menu = new SideMenu(g2, width/9, height);
        menu.addButtons(buttonLabels, heightOfButton);
        menu.addSlider("ETA", 0.001, 1.2, 1.1*heightOfButton, false);
        menu.addSlider("Epochs", 1, 200, 1.1*heightOfButton, true);

        menu.addValueLabel("w", "[0, 0]", height/20.0);
        menu.addValueLabel("bias", "0", height/20.0);
        menu.addValueLabel("y", "0x + 0", height/20.0);
    }

    @Override
    public void draw() {
        drawLines();

        drawSamples();

        if(samples.size() > 1 && twoClassesExists()) {
            if(lineVisible) drawSeparationLine();

            if(weightVisible) {
                g2.setStroke(new BasicStroke(2));
                g2.setColor(new Color(100, 255, 100));
                drawVector(wx, wy);
            }
        }


        menu.draw();
    }

    void addNewSample(double x, double y) {
        super.addNewSample(x, y);
        samples.get(samples.size() - 1).setCategory(0);
        samples.get(samples.size() - 1).setColor(new Color(130, 130, 130));
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
            case "Line": lineVisible = !lineVisible; break;
            case "Weights": weightVisible = !weightVisible; break;
            case "Menu": panel.changeGraphics("Menu");
        }
    }

    boolean colorSelectedSample(Color col, int value) {
        if(super.colorSelectedSample(col, value)) {
            update();
        }
        return true;
    }

    void update() {
        if(!twoClassesExists()) {
            return;
        }

        double[] neuron = MathUtils.LogisticRegressionParameters(samples,
                (int)menu.readValueFromSlider("Epochs"),
                menu.readValueFromSlider("ETA"));
        wx = neuron[0];
        wy = neuron[1];
        bias = neuron[2];

        a = -wx/wy;
        b = -bias/wy;

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

    void drawSeparationLine() {
        g2.setColor(new Color(255, 255, 0));
        drawStraightLine(a, b);
    }
}
