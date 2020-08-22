import java.awt.*;
import java.util.ArrayList;

import static java.lang.StrictMath.ceil;
import static java.lang.StrictMath.floor;

/**
 * A class that supports cartesian plane simulations. Has basic sideMenu and list of samples.
 * More advanced simulations in cartesian plane inherit of that class
 */
public class CartesianPlane implements GraphicsInterface {
    // the point corresponding to the upper-left corner of the screen in simulation coordinates
    protected Point2D camera;
    // List of samples (points or vectors) in R^2
    protected ArrayList<Sample> samples = new ArrayList<>();
    // length of the unit in pixels
    protected double scale;
    // visibility of lines that represent the integer value of one of the axes of the coordinate system
    protected boolean linesVisibility;
    // Graphics engine
    protected Graphics2D g2;
    // width and height of the screen
    protected int width, height;
    // reference to the upper layer
    protected Panel panel;
    // menu on the left hand side of the screen with some options in simulation
    protected SideMenu menu;

    CartesianPlane(Graphics2D g2, int width, int height, Panel mainPanel) {
        this.g2 = g2;
        this.width = width;
        this.height = height;
        this.panel = mainPanel;

        // 100 pixels = unit in axes
        scale = 100;
        // 0.5 offset looks more aesthetic
        camera = new Point2D(-width/(2*scale) + 0.5, height/(2*scale) + 0.5);

        linesVisibility = true;

        initComponents();
        initSideMenu();
    }

    /**
     * Initializes all objects in simulations (for example Matrix2x2)
     * It's important to keep it before initSideMenu
     * because sometimes we want to have a button with matrix details in it so that matrix has to be created
     */
    void initComponents() { }

    /**
     * Initializes sideMenu with all its buttons
     */
    void initSideMenu() {
        String[] buttonLabels = new String[] {"Grid", "Menu"};

        menu = new SideMenu(g2, width/9, height);
        menu.addButtons(buttonLabels, height/20);
    }

    /**
     * Draws all simulation's objects.
     */
    @Override
    public void draw() {
        drawLines();
        drawSamples();
        menu.draw();
    }

    /**
     * draws main axes and grid of cartesian plane (grid is drawn only if {@code linesVisibility} is true
     */
    void drawLines() {
        if(linesVisibility) {
            g2.setStroke(new BasicStroke(1));
            g2.setColor(new Color(50, 50, 50));
            for (int i = (int) ceil(camera.x); i < ceil(camera.x) + floor(width / scale) + 1; i++) {
                DrawUtils.line((i - camera.x) * scale, 0, (i - camera.x) * scale, height, g2);
            }
            for (int i = (int) ceil(camera.y); i > floor(camera.y) - ceil(height / scale) - 1; i--) {
                DrawUtils.line(0, (camera.y - i) * scale, width, (camera.y - i) * scale, g2);
            }
        }

        g2.setStroke(new BasicStroke(3));
        g2.setColor(new Color(255,255,255));
        DrawUtils.line(-camera.x*scale, 0, -camera.x*scale, height, g2);
        DrawUtils.line(0, camera.y*scale, width, camera.y*scale, g2);
    }

    /**
     * Draws all standard samples
     */
    void drawSamples() {
        for(Sample s: samples) s.draw(camera, scale, g2);
    }

    /**
     * Checks if mouse is over some sample and if so returns its index.
     * If mouse is over many samples then returns first one in the list
     * If mouse is not over any sample, returns -1
     *
     * @param mouseX - mouse x position (in pixels)
     * @param mouseY - mouse y position (in pixels)
     * @return index of first sample under the mouse or -1 if there are no such a sample
     */
    int select(double mouseX, double mouseY) {
        // converts to simulation coordinates to compare it to samples positions
        double x = simulationX(mouseX);
        double y = simulationY(mouseY);

        // iterates over all points and checks if some samples is under the mouse
        for(int i = 0; i < samples.size(); i++) {
            if(samples.get(i).hasInside(x,y)) {
                return i;
            }
        }
        // if no samples is under the mouse returns -1
        return -1;
    }

    /**
     * Converts x mouse position to x in cartesian plane simulation.
     * @param mouseX - mouse x position (in pixels)
     * @return x coordinate in cartesian plane simulation of x value of mouse position
     */
    double simulationX(double mouseX) {
        return mouseX/scale + camera.x;
    }

    /**
     * Converts y mouse position to y in cartesian plane simulation.
     * @param mouseY - mouse y position (in pixels)
     * @return y coordinate in cartesian plane simulation of y value of mouse position
     */
    double simulationY(double mouseY) {
        return -mouseY/scale + camera.y;
    }

    /**
     * Converts x value in cartesian plane simulation to x value on the screen (in pixels)
     * @param xInSimulation - x value of some point in cartesian plane simulation
     * @return - x coordinate on the screen of given x coordinate of some point in cartesian plane simulation
     */
    double screenX(double xInSimulation) {
        return (xInSimulation - camera.x)*scale;
    }

    /**
     * Converts y value in cartesian plane simulation to y value on the screen (in pixels)
     * @param yInSimulation - y value of some point in cartesian plane simulation
     * @return - y coordinate on the screen of given y coordinate of some point in cartesian plane simulation
     */
    double screenY(double yInSimulation) {
        return -(yInSimulation - camera.y)*scale;
    }

    /**
     * changes scale of cartesian plane simulation and move camera towards mouse position
     * Effect is similar to zooming in google maps.
     * @param amount - factor of scales change (can be either 0.95 [zoom out] or 1.05 [zoom in])
     * @param mouseX - current mouse x position on the screen (in pixels)
     * @param mouseY - current mouse y position on the screen (in pixels)
     */
    void changeScale(double amount, double mouseX, double mouseY) {
        double oldScale = scale;
        scale *= amount;
        scale = MathUtils.clamp(scale, 5, 500);

        double xFactor = width/mouseX;
        double yFactor = height/mouseY;
        camera.move(width*(1/(xFactor*oldScale) - 1/(xFactor*scale)), -height*(1/(yFactor*oldScale) - 1/(yFactor*scale)));
    }

    /**
     * Colors and changes class of the sample (using to distinguish samples or separate them in ML simulations)
     * returns true if some sample was coloured otherwise false.
     * It's important when you have to update simulation after changing class of sample (like in logistic regression)
     * @param col - new Color of selected sample
     * @param value - new value (related to color) of selected sample
     * @return true if some sample was coloured otherwise false
     */
    boolean colorSelectedSample(Color col, int value) {
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        int index = select(mouse.x, mouse.y);

        // if index == -1 then no sample is under the mouse
        if(index != -1) {
            samples.get(index).setColor(col);
            samples.get(index).setCategory(value);
            return true;
        }
        return false;
    }

    /**
     * draws a straight line in cartesian plane simulation.
     * Cuts it in borders of the screen and checks if screen contains that line
     * @param a - direct factor of the line
     * @param b - free factor of the line
     */
    void drawStraightLine(double a, double b) {
        double x1, y1, x2, y2;
        double leftSideValue = a*camera.x + b;

        if(camera.y - height/scale < leftSideValue && leftSideValue < camera.y) {
            x1 = 0;
            y1 = screenY(leftSideValue);
        } else if(leftSideValue < camera.y - height/scale) {
            if(a <= 0) return; // line is below the screen
            x1 = screenX((camera.y - height/scale - b)/a);
            y1 = height;
        } else {
            if(a >= 0) return; // line is above the screen
            x1 = screenX((camera.y - b)/a);
            y1 = 0;
        }

        double rightSideValue = a*(camera.x + width/scale) + b;

        if(camera.y - height/scale < rightSideValue && rightSideValue < camera.y) {
            x2 = width;
            y2 = screenY(rightSideValue);
        } else if(rightSideValue < camera.y - height/scale) {
            if(a >= 0) return; // line is below the screen
            x2 = screenX((camera.y - height/scale - b)/a);
            y2 = height;
        } else {
            if(a <= 0) return; // line is above the screen
            x2 = screenX((camera.y - b)/a);
            y2 = 0;
        }
        g2.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
    }

    /**
     * draws a line from the origin of cartesian plane to some point. Line represents a vector.
     * @param x - x value of drawn vector
     * @param y - y value of drawn vector
     */
    void drawVector(double x, double y) {
        DrawUtils.line(screenX(0), screenY(0), screenX(x), screenY(y), g2);
    }

    /**
     * Adds new sample when right mouse button was pressed.
     * Initial place of this new sample is place of mouse position
     * @param x - Initial x coordinate (in cartesian plane simulation)
     * @param y - Initial y coordiante (in cartesian plane simulation)
     */
    void addNewSample(double x, double y) {
        Sample sample = new Sample(x, y);
        samples.add(sample);
        menu.addSampleLabel(sample, height/20.0);
    }

    /**
     * If some sample is under the mouse then removes that sample, if not creates new one in the mouse place
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onRightClick(double mouseX, double mouseY) {
        int toRemove = select(mouseX, mouseY);

        if(toRemove != -1) {
            menu.removeSampleLabel(samples.get(toRemove));
            samples.remove(toRemove);
        } else {
            addNewSample(simulationX(mouseX), simulationY(mouseY));
        }
    }

    @Override
    public void onLeftClick(double mouseX, double mouseY) {
        if(menu.hasInside(mouseX, mouseY)) {
            menu.onLeftClick(mouseX, mouseY);
        } else {
            int index = select(mouseX, mouseY);
            if (index != -1) samples.get(index).setMoving(true);
        }
    }

    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        int index = select(mouseX, mouseY);
        if(index != -1) samples.get(index).setMoving(false);

        menuOptions(mouseX, mouseY);
    }

    void menuOptions(double mx, double my) {
        switch(menu.onReleased(mx, my)) {
            case "Grid": linesVisibility = !linesVisibility; break;
            case "Menu": panel.changeGraphics("Menu");
        }
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(menu.hasInside(mouseX, mouseY)) {
            menu.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY);
            return true;
        }

        for (Sample sample : samples) {
            if (sample.isMoving()) {
                sample.instantMove(simulationX(mouseX), simulationY(mouseY));
                return true;
            }
        }

        camera.move((prevMouseX - mouseX)/scale, (mouseY - prevMouseY)/scale);
        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        menu.onMouseMoved(mouseX, mouseY, prevMouseX, prevMouseY, simulationX(mouseX), simulationY(mouseY));
    }

    @Override
    public void onMouseScrolled(int rotation) {
        Point mouse = MouseInfo.getPointerInfo().getLocation();

        if(menu.hasInside(mouse.x, mouse.y)) {
            menu.onMouseScrolled(rotation);
        } else {
            changeScale(rotation == 1 ? 0.95 : 1.05, mouse.x, mouse.y);
        }
    }

    @Override
    public void onKeyPressed(char key) {
        switch(key) {
            case 'l': case 'L':
                linesVisibility = !linesVisibility;
                break;

            case '0': colorSelectedSample(new Color(130, 130, 130), Character.getNumericValue(key)); break;
            case '1': colorSelectedSample(new Color(100, 100, 255), Character.getNumericValue(key)); break;
            case '2': colorSelectedSample(new Color(255, 100, 100), Character.getNumericValue(key)); break;
            case '3': colorSelectedSample(new Color(100, 255, 100), Character.getNumericValue(key)); break;
            case '4': colorSelectedSample(new Color(200, 200, 50), Character.getNumericValue(key)); break;
            case '5': colorSelectedSample(new Color(200, 50, 200), Character.getNumericValue(key)); break;
            case '6': colorSelectedSample(new Color(50, 200, 200), Character.getNumericValue(key)); break;
        }

    }
}
