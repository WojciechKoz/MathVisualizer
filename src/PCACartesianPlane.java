import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PCACartesianPlane extends CartesianPlane {
    private ArrayList<Sample> projected = new ArrayList<>();
    private GraphicsMatrix2x2 cov;
    private boolean covVisible, eigVisible, prodVisible;

    PCACartesianPlane(Graphics2D g2, int width, int height, Panel panel) {
        super(g2, width, height, panel);

        covVisible = true;
        eigVisible = true;
        prodVisible = true;
    }

    void initSideMenu() {
        cov = new GraphicsMatrix2x2(MathUtils.covarianceMatrix(samples));

        String[] buttonLabels = new String[] {"Grid", "Cov Matrix", "Eigenvectors", "Projected", "Menu"};

        menu = new SideMenu(g2, width/9, height);
        menu.addButtons(buttonLabels, height/20);
        menu.addMatrixLabel(cov, height/10.0);
    }

    @Override
    public void draw() {
        drawLines();

        if(covVisible) {
            cov.drawBasis(g2, scale, camera, false);
        }

        if(eigVisible) {
            cov.drawEigenvectors(g2, scale, camera);
        }

        drawSamples();
        if(prodVisible) { for(Sample s: projected) s.draw(camera, scale, g2); }

        menu.draw();
    }

    @Override
    public void onRightClick(double mouseX, double mouseY) {
        super.onRightClick(mouseX, mouseY);
        cov.setValues(MathUtils.covarianceMatrix(samples));
        project();
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(super.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY)) {
            cov.setValues(MathUtils.covarianceMatrix(samples));
            project();
        }
        return true;
    }

    void menuOptions(double mx, double my) {
        switch(menu.onReleased(mx, my)) {
            case "Grid": linesVisibility = !linesVisibility; break;
            case "Cov Matrix": covToggle(); break;
            case "Eigenvectors": eigToggle(); break;
            case "Projected": prodToggle(); break;
            case "Menu": panel.changeGraphics("Menu");
        }
    }

    void covToggle() { covVisible = !covVisible; }

    void eigToggle() { eigVisible = !eigVisible; }

    void prodToggle() { prodVisible = !prodVisible; }

    boolean colorSelectedSample(Color col, int value) {
        if(super.colorSelectedSample(col, value)) {
            project();
        }
        return true;
    }

    void project() {
        for(Sample sample: projected) {
            menu.removeSampleLabel(sample);
        }

        projected.clear();

        // choose eigenvector which has bigger eigenvalue
        List<Double> projection = cov.greaterEigenvector();

        for(Sample s: samples) {
            double projected_x = MathUtils.dotProd(s.values(), projection);
            Sample newSample = new Sample(projected_x, 0,
                    new Color(s.getColor().getRed(), s.getColor().getGreen(), s.getColor().getBlue(), 130));
            projected.add(newSample);
            menu.addSampleLabel(newSample, height/20.0);
        }
    }
}
