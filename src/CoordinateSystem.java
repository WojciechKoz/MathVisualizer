import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static java.lang.StrictMath.ceil;
import static java.lang.StrictMath.floor;

/**
 * A class that supports coordinate system simulations. Has basic sideMenu and list of samples.
 * Also handles the message window.
 * More advanced simulations in cartesian plane inherit of that class
 */
public class CoordinateSystem implements GraphicsInterface {
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
    // window with text about current simulation
    protected MessageWindow messageWindow;
    // name of menu that will be active after quit this simulation
    protected String menuName;
    protected final int STANDARD_BUTTON_HEIGHT;

    CoordinateSystem(Graphics2D g2, int width, int height, Panel mainPanel) {
        this.g2 = g2;
        this.width = width;
        this.height = height;
        this.panel = mainPanel;
        menuName = "Menu";
        STANDARD_BUTTON_HEIGHT = height/20;

        // 100 pixels = unit in axes
        scale = 100;
        // 0.5 offset looks more aesthetic
        camera = new Point2D(-width/(2*scale) + 0.5, height/(2*scale) + 0.5);

        linesVisibility = true;

        // for some matrices and message window
        initComponents();

        // initializes the side menu with that 4 most basic buttons
        menu = new SideMenu(g2, width/9, height);
        menu.addCheckBoxButtons(new String[]{"Visible"}, new Boolean[] {true}, STANDARD_BUTTON_HEIGHT);
        menu.addButtons(new String[]{"Menu", "About"}, STANDARD_BUTTON_HEIGHT);
        menu.addCheckBoxButtons(new String[]{"Grid"}, new Boolean[] {true},STANDARD_BUTTON_HEIGHT);

        // adds more buttons depending which simulation is running
        initSideMenu();
    }

    /**
     * Initializes all objects in simulations (for example Matrix2x2)
     * It's important to keep it before initSideMenu
     * because sometimes we want to have a button with matrix details in it so that matrix has to be created.
     * Initializes the message window since all simulations have other information.
     */
    void initComponents() { }

    /**
     * Initializes all specified buttons in other cartesian plane simulations
     */
    void initSideMenu() { }

    /**
     * Draws all simulation's objects, side menu and message window
     */
    @Override
    public void draw() {
        drawLines();
        drawSamples();
        menu.draw();

        messageWindow.draw(g2);
    }

    /**
     * draws main axes and grid of cartesian plane (grid is drawn only if {@code linesVisibility} is true
     */
    void drawLines() {
        if(linesVisibility) {
            g2.setStroke(new BasicStroke(1));
            g2.setColor(new Color(50, 50, 50));
            for (int i = (int) ceil(camera.x); i < ceil(camera.x) + floor(width / scale) + 1; i++) {
                DrawUtils.line((i - camera.x) * scale, 0, (i - camera.x) * scale, height);
            }
            for (int i = (int) ceil(camera.y); i > floor(camera.y) - ceil(height / scale) - 1; i--) {
                DrawUtils.line(0, (camera.y - i) * scale, width, (camera.y - i) * scale);
            }
        }

        g2.setStroke(new BasicStroke(3));
        g2.setColor(DrawUtils.white);
        DrawUtils.line(-camera.x*scale, 0, -camera.x*scale, height);
        DrawUtils.line(0, camera.y*scale, width, camera.y*scale);
    }

    /**
     * Draws all standard samples in reverse order
     * because when a sample is on top of a stack of several samples
     * it is more logical that it has to be moved first
     */
    void drawSamples() {
        for(int i = samples.size()-1; i >= 0; i--) {
            samples.get(i).draw(camera, scale, g2);
        }
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

        // iterates over all points and checks if some samples are under the mouse
        for(int i = 0; i < samples.size(); i++) {
            if(samples.get(i).hasInside(x,y)) {
                return i;
            }
        }
        // if no sample is under the mouse returns -1
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
        Point mouse = new Point(panel.prevMouseX, panel.prevMouseY);
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
        DrawUtils.line(screenX(0), screenY(0), screenX(x), screenY(y));
    }

    /**
     * Adds new sample when right mouse button was pressed.
     * Initial place of this new sample is place of mouse position
     * @param x - Initial x coordinate (in cartesian plane simulation)
     * @param y - Initial y coordinate (in cartesian plane simulation)
     */
    void addNewSample(double x, double y) {
        Sample sample = new Sample(x, y);
        samples.add(sample);
        menu.addSampleLabel(sample, STANDARD_BUTTON_HEIGHT, true);
    }

    /**
     * Removes the sample with the given index. Also removes the button
     * corresponding with this sample.
     * @param index - index of sample that has to be deleted.
     */
    void removeSample(int index) {
        menu.removeSampleLabel(samples.get(index));
        samples.remove(index);
    }

    /**
     * move the sample that was under the mouse when the mouse button was clicked.
     * Now when the mouse is still pressed and this sample returns true from isMoving() method
     * it will follow the mouse. When no sample moves then nothing happens and function
     * returns false meaning that no refresh is needed.
     * @param mouseX - current x coordinate of the mouse (in pixels)
     * @param mouseY - current y coordinate of the mouse (in pixels)
     * @return true if some sample was moved otherwise false
     */
    boolean moveSamples(double mouseX, double mouseY) {
        for (Sample sample : samples) {
            if (sample.isMoving()) {
                sample.instantMove(simulationX(mouseX), simulationY(mouseY));
                return true;
            }
        }
        return false;
    }

    /**
     * moves the camera when the mouse is dragged. Tries to keep the mouse position
     * in the same place in simulated coordinate system.
     * @param mouseX - current x coordinate of the mouse (in pixels)
     * @param mouseY - current y coordinate of the mouse (in pixels)
     * @param prevMouseX - previous x coordinate of the mouse (in pixels)
     * @param prevMouseY - previous y coordinate of the mouse (in pixels)
     */
    void moveCamera(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        camera.move((prevMouseX - mouseX)/scale, (mouseY - prevMouseY)/scale);
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
            removeSample(toRemove);
        } else {
            addNewSample(simulationX(mouseX), simulationY(mouseY));
        }
    }

    /**
     * If mouse is inside the messageWindow or menu then runs its onLeftClick method
     * else checks if some sample is under the mouse and if so then
     * set its moving variable to true (that sample will follow the mouse until left button will be released)
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @return true if something was moved
     */
    @Override
    public boolean onLeftClick(double mouseX, double mouseY) {
        menu.focusingInputs(mouseX, mouseY);

        if(messageWindow.hasInside(mouseX, mouseY)) {
            messageWindow.onLeftClick(mouseX, mouseY);
            return false;
        } else if(menu.hasInside(mouseX, mouseY)) {
            menu.onLeftClick(mouseX, mouseY);
            return false;
        } else {
            int index = select(mouseX, mouseY);
            if (index != -1) {
                samples.get(index).setMoving(true);
                return false;
            }
            return true;
        }
    }

    /**
     * if mouse is inside the menu then performs actions related to clicked button
     * else sets all samples moving variable to false (won't follow the mouse)
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        if(messageWindow.hasInside(mouseX, mouseY)) {
            messageWindow.onMouseReleased(mouseX, mouseY);
        } else if(menu.hasInside(mouseX, mouseY)) {
            menuOptions(menu.onReleased(mouseX, mouseY));
        } else {
            for(Sample sample: samples) {
                sample.setMoving(false);
            }
        }
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
            case "Grid": linesVisibility = !linesVisibility; break;
            case "About": messageWindow.toggleVisibility(); break;
            case "Menu": panel.changeGraphics("", menuName);
        }
    }

    /**
     * Drags the message window if it's selected
     * If mouse is inside the menu then executes {@code menu.onMouseDragged}
     * else checks if some sample has moving variable sets to true
     * if so then moves that sample else move camera.
     * Returns true if mouse was inside the menu or some sample was moved.
     * (it might be a good reason to refresh simulation)
     * if only camera was moved then no refreshing is needed and function returns false.
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     * @return true if mouse was inside menu or some sample was moved, false if only camera was moved
     */
    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(messageWindow.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY)) {
            return false;
        }

        if(menu.hasInside(mouseX, mouseY)) {
            menu.onMouseDragged(mouseX, mouseY, prevMouseX, prevMouseY);
            return true;
        }

        if(moveSamples(mouseX, mouseY)) return true;

        moveCamera(mouseX, mouseY, prevMouseX, prevMouseY);


        return false;
    }

    /**
     * Since cartesian plane simulation doesn't check if mouse was moved (unless the left button was pressed)
     * it runs {@code menu.onMouseMoved} because menu might have some buttons which support mouse hovering.
     * and messageWindow for the same reason
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     */
    @Override
    public void onMouseMoved(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        menu.onMouseMoved(mouseX, mouseY, prevMouseX, prevMouseY, simulationX(mouseX), simulationY(mouseY));

        messageWindow.onMouseMoved(mouseX, mouseY);
    }

    /**
     * If mouse is inside the menu then runs @code{menu.onMouseScrolled} else
     * changes current scale a little bit and moves camera toward the mouse.
     * @param rotation direction of scrolling. Can be either 1 [down] or -1 [up]
     */
    @Override
    public void onMouseScrolled(int rotation) {
        Point mouse = new Point(panel.prevMouseX, panel.prevMouseY);

        if(messageWindow.hasInside(mouse.x, mouse.y)) {
            messageWindow.onMouseScrolled(rotation);
        } else if(menu.hasInside(mouse.x, mouse.y)) {
            menu.onMouseScrolled(rotation);
        }  else {
            changeScale(rotation == 1 ? 0.95 : 1.05, mouse.x, mouse.y);
        }
    }

    /**
     * For now it supports only painting samples.
     * Updates the simulation if mouse was inside the menu or some sample was coloured.
     * @param event - all information about pressed button
     * @return always true if mouse was inside menu or some sample was coloured otherwise false
     */
    @Override
    public boolean onKeyPressed(KeyEvent event) {
        if(menu.onKeyPressed(event)) {
            update();
            return true;
        }

        char key = event.getKeyChar();
        if (key >= '0' && key <= '6') {
            if(colorSelectedSample(DrawUtils.sampleColors[Integer.parseInt(String.valueOf(key))],
                    Character.getNumericValue(key))) {
                update();
                return true;
            }
        }
        return false;
    }

    public void update() {

    }
}
