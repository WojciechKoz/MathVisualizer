import java.awt.*;
import java.util.ArrayList;

/**
 * Class that simulates 2x2 matrix as a pair of 2-dimensional vectors.
 */
public class MatrixCoordinateSystem extends CoordinateSystem {
    // list of samples which are a product of matrix * sample (sample <- samples)
    private final ArrayList<Sample> projected = new ArrayList<>();
    // simulated matrix
    private GraphicsMatrix2x2 matrix;

    private boolean gridVisibility, eigenvectorsVisibility, projectVisibility,
            determinantVisibility, transposeVisibility, inverseVisibility;

    MatrixCoordinateSystem(Graphics2D g2, int width, int height, Panel panel) {
        super(g2, width, height, panel);
        menuName = "Visualizations";

        gridVisibility = true;
        eigenvectorsVisibility = false;
        projectVisibility = true;
        determinantVisibility = false;
        transposeVisibility = false;
        inverseVisibility = false;
    }

    /**
     * Creates the matrix. Initial state is the identity.
     * Initializes the message window
     */
    @Override
    void initComponents() {
        matrix = new GraphicsMatrix2x2(1,0,0,1);
        messageWindow = new MessageWindow(width, height, "data/Matrix-Sim-About");
    }

    /**
     * Initializes all side menu with buttons and labels
     */
    @Override
    void initSideMenu() {
        String[] buttonsLabels = new String[] {"Matrix Grid", "Determinant", "Transpose",
                "Inverse", "Eigenvectors", "Projected"};
        Boolean[] buttonsValues = new Boolean[] {true, false, false, false, false, true};

        menu.addCheckBoxButtons(buttonsLabels, buttonsValues, height/20);
        menu.addMatrixLabel(matrix, height/10.0);
        menu.addValueLabel("Det", "1", height/20.0);
        menu.addValueLabel("Lambda 1", "0", height/20.0);
        menu.addValueLabel("Lambda 2", "0", height/20.0);
    }

    /**
     * Draws identity grid, samples and axes of the matrix.
     * If corresponding visibility variable is equal to true then draws
     * - matrix grid
     * - eigenvectors as lines ( doesn't draw it in every case. See @code{GraphicalMatrix2x2#drawEigenvectorsLines})
     * - transposition
     * - inverse matrix
     * - determinant
     * - projected vectors
     * At the end draws side menu.
     */
    @Override
    public void draw() {
        drawLines();

        if(determinantVisibility) matrix.drawDeterminant(g2, this);
        matrix.drawBasis(g2, this, true);
        matrix.drawAxes(g2, this);
        if(gridVisibility) matrix.drawGrid(g2, scale, camera, this);
        if(eigenvectorsVisibility) matrix.drawEigenvectorsLines(g2, this);
        if(transposeVisibility) matrix.drawTranspose(g2, this);
        if(inverseVisibility) matrix.drawInverse(g2, this);

        drawSamples();
        if(projectVisibility) { for(Sample s: projected) s.draw(camera, scale, g2); }

        menu.draw();
        messageWindow.draw(g2);
    }

    /**
     * Performs onRightClick from CartesianPlane and updates the simulation
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onRightClick(double mouseX, double mouseY) {
        super.onRightClick(mouseX, mouseY);
        update();
    }

    /**
     * Performs {@code CartesianPlane#onLeftClick} if nothing was moved then checks
     * if some of the basis of the matrix is under the mouse
     * and if so then changes its moving variable to true
     * so it will follow the mouse
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @return always true
     */
    @Override
    public boolean onLeftClick(double mouseX, double mouseY) {
        if(super.onLeftClick(mouseX, mouseY)) {
            matrix.selectBasis(simulationX(mouseX), simulationY(mouseY));
        }
        return true;
    }

    /**
     * If some base or sample is selected moves it in the mouse place.
     * If the sample or base was moved then update the simulation
     *
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     * @return always true
     */
    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(matrix.isSelected()) {
            matrix.moveBase(simulationX(mouseX), simulationY(mouseY));
        } else if(!super.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY)) {
            return true; // if super.onMouseDragged returned false then there is no need to update
                         // since sample has been changed
        }
        update();
        return true;
    }

    /**
     * If mouse is released then deselects matrix basis (even if they wasn't been selected)
     * and performs @code{CartesianPlane#onLeftMouseButtonReleased} to do the same with samples
     * and checks if some button in menu is clicked
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        matrix.deselect();
        super.onLeftMouseButtonReleased(mouseX, mouseY);
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
            case "Matrix Grid": gridVisibility = !gridVisibility; break;
            case "Determinant": determinantVisibility = !determinantVisibility; break;
            case "Transpose": transposeVisibility = !transposeVisibility; break;
            case "Inverse": inverseVisibility = !inverseVisibility; break;
            case "Eigenvectors": eigenvectorsVisibility = !eigenvectorsVisibility; break;
            case "Projected": projectVisibility = !projectVisibility; break;
            default: super.menuOptions(label);
        }
    }

    /**
     * Performs {@code CartesianPlane#colorSelectedSample} if it returns true which means that
     * some sample changed the color and update is necessary since projected sample should has
     * the same color.
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
     * first removes every button which is related with projected samples.
     * then clears the list of projected samples, fill it again with new projected samples
     * and creates new buttons corresponding to list of new projected samples.
     * Label buttons about matrix determinant and eigenvalues are refreshed as well
     */
    @Override
    public void update() {
        for(Sample sample: projected) {
            menu.removeSampleLabel(sample);
        }

        projected.clear();
        for(Sample s: samples) {
            Sample s_prod = matrix.project(s);
            s_prod.setColor(new Color(s.getColor().getRed(), s.getColor().getGreen(), s.getColor().getBlue(), 130));
            projected.add(s_prod);
            menu.addSampleLabel(s_prod, height/20.0, false);
        }

        menu.updateLabel("Det", Double.toString(MathUtils.round(matrix.det(), 2)));
        menu.updateLabel("Lambda 1", Double.toString(MathUtils.round(matrix.realEig()[4], 2)));
        menu.updateLabel("Lambda 2", Double.toString(MathUtils.round(matrix.realEig()[5], 2)));
    }
}
