import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that simulates Principal Component Analysis.
 * All math of this algorithm are implemented in Matrix2x2 and MathUtils.
 */
public class PCACoordinateSystem extends CoordinateSystem {
    private final ArrayList<Sample> projected = new ArrayList<>();
    private GraphicsMatrix2x2 covarianceMatrix;
    private boolean covarianceMatrixVisibility, eigenvectorsVisibility, projectedSamplesVisibility;

    PCACoordinateSystem(Graphics2D g2, int width, int height, Panel panel) {
        super(g2, width, height, panel);
        menuName = "Visualizations";

        covarianceMatrixVisibility = true;
        eigenvectorsVisibility = true;
        projectedSamplesVisibility = true;
    }

    /**
     * Creates covariance matrix. Since there are no samples
     * at the beginning of simulation covariance matrix is a zero matrix ( to avoid dividing by 0 )
     */
    @Override
    void initComponents() {
        super.initComponents();
        messageWindow = new MessageWindow(this, "data/PCA-Sim-About");
        covarianceMatrix = new GraphicsMatrix2x2(MathUtils.covarianceMatrix(samples));
        covarianceMatrix.setName("Cov");
    }

    /**
     * Initializes all side menu with buttons
     */
    void initSideMenu() {
        String[] buttonsLabels = new String[] {
                StringsResources.covMatrix(),
                StringsResources.eigenvectors(),
                StringsResources.projected()
        };
        Boolean[] buttonsValues = new Boolean[] {true, true, true};

        menu.addCheckBoxButtons(buttonsLabels, buttonsValues, height/20);
        menu.addMatrixLabel(covarianceMatrix, height/10.0);
    }

    /**
     * Draws cartesian plane lines and samples.
     * If corresponding visibility variable is true then
     * draws basis of covariance matrix, its eigenvectors and projected samples.
     * At the end menu is drawn.
     */
    @Override
    public void draw() {
        drawLines();

        if(covarianceMatrixVisibility) {
            covarianceMatrix.drawBasis(g2, this, false);
        }

        if(eigenvectorsVisibility) {
            covarianceMatrix.drawEigenvectors(g2, this);
        }

        drawSamples();
        if(projectedSamplesVisibility) { for(Sample s: projected) s.draw(camera, scale, g2); }

        drawInterface();
    }

    /**
     * Performs CartesianPlane.onRightClick and after that refreshes covariance matrix and projected samples
     * since right click adds or removes a sample and changes the simulation state.
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onRightClick(double mouseX, double mouseY) {
        super.onRightClick(mouseX, mouseY);
        covarianceMatrix.setValues(MathUtils.covarianceMatrix(samples));
        update();
    }

    /**
     * Performs CartesianPlane.onMouseDragged. If it returns true (means that some sample was moved)
     * refreshes the covariance matrix and projected samples.
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
        if(label.equals(StringsResources.covMatrix())) {
            covarianceMatrixVisibility = !covarianceMatrixVisibility;
        } else if(label.equals(StringsResources.eigenvectors())) {
            eigenvectorsVisibility = !eigenvectorsVisibility;
        } else if(label.equals(StringsResources.projected())) {
            projectedSamplesVisibility = !projectedSamplesVisibility;
        } else {
            super.menuOptions(label);
        }
    }

    /**
     * tries to color a sample and if it succeed then refreshes simulation
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
     * Calculates covariance matrix and its eigenvectors.
     * Removes all buttons in side menu related with projected samples and clear an old list of projected samples.
     * Projects samples onto the eigenvector which has a bigger eigenvalue (TODO add 2D projection)
     * Adds all buttons corresponding to the new projected samples.
     * projected samples are created every time when simulation is changed because there is no connection
     * between sample and its projection.
     */
    @Override
    public void update() {
        covarianceMatrix.setValues(MathUtils.covarianceMatrix(samples));

        for(Sample sample: projected) {
            menu.removeSampleLabel(sample);
        }

        projected.clear();

        // choose eigenvector which has bigger eigenvalue
        List<Double> projection = covarianceMatrix.greaterEigenvector();

        for(Sample s: samples) {
            double projected_x = MathUtils.dotProd(s.values(), projection);
            Sample newSample = new Sample(projected_x, 0,
                    new Color(s.getColor().getRed(), s.getColor().getGreen(), s.getColor().getBlue(), 130));
            projected.add(newSample);
            menu.addSampleLabel(newSample, height/20.0, false);
        }
    }
}
