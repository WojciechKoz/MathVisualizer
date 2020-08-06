import java.awt.*;
import java.util.ArrayList;

public class MatrixCartesianPlane extends CartesianPlane {
    private ArrayList<Sample> projected = new ArrayList<>();
    private GraphicsMatrix2x2 matrix;
    private boolean gridVisible, eigenVisible, projectVisible, determinantVisible, transposeVisible, inverseVisible;

    MatrixCartesianPlane(Graphics2D g2, int width, int height, Panel panel) {
        super(g2, width, height, panel);

        gridVisible = true;
        eigenVisible = false;
        projectVisible = true;
        determinantVisible = false;
        transposeVisible = false;
        inverseVisible = false;
    }

    @Override
    void initComponents() {

    }

    @Override
    void initSideMenu() {
        matrix = new GraphicsMatrix2x2(1,0,0,1);

        String[] buttonLabels = new String[] {"Grid", "Matrix Grid", "Determinant", "Transpose",
                "Inverse", "Eigenvectors", "Projected", "Menu"};

        menu = new SideMenu(g2, width/9, height);
        menu.addButtons(buttonLabels, height/20);
        menu.addMatrixLabel(matrix, height/10.0);
        menu.addValueLabel("Det", "1", height/20.0);
        menu.addValueLabel("Lambda 1", "0", height/20.0);
        menu.addValueLabel("Lambda 2", "0", height/20.0);
    }

    @Override
    public void draw() {
        drawLines();

        if(determinantVisible) matrix.drawDeterminant(g2, this);
        matrix.drawBasis(g2, scale, camera, true);
        matrix.drawAxes(g2, scale, camera, this);
        if(gridVisible) matrix.drawGrid(g2, scale, camera, this);
        if(eigenVisible) matrix.drawEigenlines(g2, this);
        if(transposeVisible) matrix.drawTranspose(g2, this);
        if(inverseVisible) matrix.drawInverse(g2, this);

        drawSamples();
        if(projectVisible) { for(Sample s: projected) s.draw(camera, scale, g2); }

        menu.draw();
    }

    @Override
    public void onRightClick(double mouseX, double mouseY) {
        super.onRightClick(mouseX, mouseY);
        update();
    }

    @Override
    public void onLeftClick(double mouseX, double mouseY) {
        int index = select(mouseX, mouseY);
        if(index != -1) {
            samples.get(index).setMoving(true);
        } else {
            matrix.selectBasis(simulationX(mouseX), simulationY(mouseY));
        }
        menu.onLeftClick(mouseX, mouseY);
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(matrix.isSelected()) {
            matrix.moveBasis(simulationX(mouseX), simulationY(mouseY));
        } else if(!super.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY)){
            return false;
        }
        update();
        return true;
    }

    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        matrix.deselect();
        super.onLeftMouseButtonReleased(mouseX, mouseY);
    }

    void menuOptions(double mx, double my) {
        switch(menu.onReleased(mx, my)) {
            case "Grid": linesVisibility = !linesVisibility; break;
            case "Matrix Grid": gridVisible = !gridVisible; break;
            case "Determinant": determinantVisible = !determinantVisible; break;
            case "Transpose": transposeVisible = !transposeVisible; break;
            case "Inverse": inverseVisible = !inverseVisible; break;
            case "Eigenvectors": eigenVisible = !eigenVisible; break;
            case "Projected": projectVisible = !projectVisible; break;
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
        for(Sample sample: projected) {
            menu.removeSampleLabel(sample);
        }

        projected.clear();
        for(Sample s: samples) {
            Sample s_prod = matrix.project(s);
            s_prod.setColor(new Color(s.getColor().getRed(), s.getColor().getGreen(), s.getColor().getBlue(), 130));
            projected.add(s_prod);
            menu.addSampleLabel(s_prod, height/20.0);
        }

        menu.updateLabel("Det", Double.toString(MathUtils.round(matrix.det(), 2)));
        menu.updateLabel("Lambda 1", Double.toString(MathUtils.round(matrix.realEig()[4], 2)));
        menu.updateLabel("Lambda 2", Double.toString(MathUtils.round(matrix.realEig()[5], 2)));
    }
}
