import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * This is the first part of the tutorial.
 * It has many switches depended on the current state of the tutorial
 * so my apology if the code has less quality than the other classes.
 */
public class LRTutorialCoordinateSystem extends LRCoordinateSystem {
    //  number of current step of the tutorial
    private int state = 1;
    private final ArrayList<Shape> areasShapes = new ArrayList<>();
    private final ArrayList<Shape> arrows = new ArrayList<>();
    private final AutoMotion autoMotion = new AutoMotion(this);

    LRTutorialCoordinateSystem(int width, int height, Panel mainPanel) {
        super(width, height, mainPanel);
        menuName = "Menu";

        // side menu is hide at the beginning but the message window is visible all the time
        errorVisibility = false;
        menu.toggleCheckBoxButton("Errors");
        menu.toggleCheckBoxButton("Visible");
        messageWindow.toggleVisibility();

        addInterface();
    }

    /**
     * Initializes the message window. However the messageWindow will be constantly changed
     * when the user has taken some step.
     */
    @Override
    void initComponents() {
        super.initComponents();
        messageWindow = new MessageWindow(this, "data/"+StringsResources.languageShortcut()+"/Tutorial-Part-1-1-About");
    }

    /**
     * at the bottom draws shapes (squares) to highlight some area
     * then draws standard linear regression simulation (with menu and message box)
     * on the very top draws some arrows.
     * if autoMotion is running it also moves camera and changes scale.
     */
    @Override
    public void draw() {
        for(Shape s: areasShapes) s.draw(this);
        super.draw();
        for(Shape a: arrows) a.draw(this);

        autoMotion.proceed();
    }

    /**
     * Adds new sample when right mouse button was pressed.
     * Checks if it is right moment and place to add it in the tutorial
     * If so it changes the state because in this part of the tutorial
     * every adding new samples is a step.
     * @param x - Initial x coordinate (in cartesian plane simulation)
     * @param y - Initial y coordinate (in cartesian plane simulation)
     */
    void addNewSample(double x, double y) {
        if(isValidMouseAction(x, y, "add", 0)) {
            super.addNewSample(x, y);
            changeState();
        }
    }

    /**
     * removes sample if it is right moment and sample to be removed.
     * @param index - index of sample that has to be deleted.
     */
    void removeSample(int index) {
        if(isValidMouseAction(0, 0, "remove", index)) {
            super.removeSample(index);
            if(state == 6) {
                changeState();
            } else if(state == 9 && samples.size() == 0) {
                changeState();
            }
        }
    }

    /**
     * move the sample that was under the mouse when the mouse button was clicked.
     * Now when the mouse is still pressed and this sample returns true from isMoving() method
     * it will follow the mouse. When no sample moves then nothing happens and function
     * returns false meaning that no refresh is needed. In tutorial it checks if it is allowed
     * to move a sample.
     * @param mouseX - current x coordinate of the mouse (in pixels)
     * @param mouseY - current y coordinate of the mouse (in pixels)
     * @return true if some sample should be moved otherwise false
     */
    boolean moveSamples(double mouseX, double mouseY) {
        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);
            if (sample.isMoving()) {
                if(isValidMouseAction(0, 0, "sample move", i)) {
                    sample.instantMove(simulationX(mouseX), simulationY(mouseY));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * moves the camera if it is allowed to do that
     * @param mouseX - current x coordinate of the mouse (in pixels)
     * @param mouseY - current y coordinate of the mouse (in pixels)
     * @param prevMouseX - previous x coordinate of the mouse (in pixels)
     * @param prevMouseY - previous y coordinate of the mouse (in pixels)
     */
    void moveCamera(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(isValidMouseAction(0, 0, "camera move", 0)) {
            super.moveCamera(mouseX, mouseY, prevMouseX, prevMouseY);
        }
    }

    /**
     * changes scale of cartesian plane simulation and move camera towards mouse position
     * Effect is similar to zooming in google maps. Checks if it is allowed to zoom in/out
     * you can change the scale if you are allowed to do that (in some states only)
     * @param amount - factor of scales change (can be either 0.95 [zoom out] or 1.05 [zoom in])
     * @param mouseX - current mouse x position on the screen (in pixels)
     * @param mouseY - current mouse y position on the screen (in pixels)
     */
    void changeScale(double amount, double mouseX, double mouseY) {
        if(isValidMouseAction(mouseX, mouseY, amount > 1 ? "zoom in" : "zoom out", 0)) {
            super.changeScale(amount, mouseX, mouseY);

            // in state number 5 it makes sure that all samples are visible
            // when you scroll to much but still can't spot all points
            // it will slightly move the camera.
            if(state == 5 && ( camera.y - height/scale < -1 || scale < 45)) {
                if(scale < 45) {
                    autoMotion.setTargetPoint(new Point2D(camera.x, height/scale-1), 20);
                }
                changeState();
            }
        }
    }

    /**
     * if mouse is inside the menu then performs actions related to clicked button
     * else sets all samples moving variable to false (won't follow the mouse)
     * In tutorial checks if the sample was moved in the good spot and it blocks side
     * menu at the beginning
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        if(samples.size() > 1) {
            // checks if the point was moved to the good position.
            Sample fst = samples.get(0);
            if(state == 7 && fst.isMoving() && MathUtils.isBetween(0, 1, fst.x)
                    && MathUtils.isBetween(3, 4, fst.y)) {
                changeState();
            }
        }

        // blocks the menu at the beginning
        if(state < 8 && menu.hasInside(mouseX, mouseY)) return;

        super.onLeftMouseButtonReleased(mouseX, mouseY);
    }

    /**
     * moves the samples using side menu text inputs
     * @param event - all information about pressed button
     * @return - true if some sample was moved otherwise false
     */
    @Override
    public boolean onKeyPressed(KeyEvent event) {
        if(menu.onKeyPressed(event) && state == 8) {
            update();

            // changes state when there are three samples with certain coordinates
            boolean fst=false, snd=false, trd=false;
            for(Sample sample: samples) {
                if(sample.x == -1.5 && sample.y == 1.5) fst = true;
                if(sample.x == -0.5 && sample.y == 2.5) snd = true;
                if(sample.x == 0.5 && sample.y == 3.5) trd = true;
            }
            if(fst && snd && trd) {
                changeState();
            }

            return true;
        }
        return false;
    }

    /**
     * move to the next step of the tutorial. Clears all interface related with
     * the previous step and adds new. Changes the content of the message box
     */
    void changeState() {
        state += 1;
        areasShapes.clear();
        arrows.clear();

        addInterface();

        if(state < 10) {
            messageWindow = new MessageWindow(this, "data/"+StringsResources.languageShortcut()+"/Tutorial-Part-1-" + state + "-About");
            messageWindow.toggleVisibility();
        }
    }

    /**
     * Adds some squares and arrows in the simulation corresponding to current state number.
     * Sometimes it moves the camera using autoMotion
     */
    void addInterface() {
        switch(state) {
            case 1: {
                areasShapes.add(new BlinkingRectangle(0, 1, 1, 1, DrawUtils.yellow));
            } break;
            case 2: {
                areasShapes.add(new BlinkingRectangle(-2, 2, 1, 1, DrawUtils.yellow));
            } break;
            case 3: {
                areasShapes.add(new BlinkingRectangle(-1, 3, 1, 1, DrawUtils.yellow));
            } break;
            case 4: {
                areasShapes.add(new BlinkingRectangle(-1, 12, 1, 1, DrawUtils.yellow));
                arrows.add(new Arrow(new Point2D(-2, 2), 2.5, "up", DrawUtils.transparentYellow));
                arrows.add(new Arrow(new Point2D(1, 2), 2.5, "up", DrawUtils.transparentYellow));
                arrows.add(new Arrow(new Point2D(-2, 8), 2.5, "up", DrawUtils.transparentYellow));
                arrows.add(new Arrow(new Point2D(1, 8), 2.5, "up", DrawUtils.transparentYellow));
                arrows.add(new Arrow(new Point2D(-0.5, 15), 2.5, "down", DrawUtils.transparentYellow));
                arrows.add(new Arrow(new Point2D(-4, 11.5), 2.5, "right", DrawUtils.transparentYellow));
                arrows.add(new Arrow(new Point2D(3, 11.5), 2.5, "left", DrawUtils.transparentYellow));
            } break;
            case 6: {
                arrows.add(new Arrow(new Point2D(-0.5, 8), 2.5, "up", DrawUtils.transparentRed));
                arrows.add(new Arrow(new Point2D(-0.5, 15), 2.5, "down", DrawUtils.transparentRed));
                arrows.add(new Arrow(new Point2D(-4, 11.5), 2.5, "right", DrawUtils.transparentRed));
                arrows.add(new Arrow(new Point2D(3, 11.5), 2.5, "left", DrawUtils.transparentRed));
            } break;
            case 7: {
                autoMotion.setTargetPoint(new Point2D(-6, 6), 40);
                autoMotion.setTargetScale(height/10.0, 40);

                arrows.add(new Arrow(new Point2D(0.5, 2), 1, "up", DrawUtils.transparentYellow));
                areasShapes.add(new BlinkingRectangle(0, 4, 1, 1, DrawUtils.yellow));
            } break;
            case 9: {
                autoMotion.setTargetPoint(new Point2D(-5, 5), 30);
                autoMotion.setTargetScale(100, 30);
            } break;
            case 10: {
                panel.changeGraphics("", "tutorial-part-2");
            }
        }
    }

    /**
     * Checks if the action is allowed in current state of the tutorial.
     * List of possible action. It could be one return statement but it is much less clear.
     * @param x - x coordinate of the mouse ( in simulation units)
     * @param y - y coordinate of the mouse ( in simulation units)
     * @param action - type of performing action ("add", "remove", "camera move" etc)
     * @param index - index of selected sample ( while removing and moving)
     * @return true if the action is allowed
     */
    boolean isValidMouseAction(double x, double y, String action, int index) {
        switch(state) {
            case 1: {
                return MathUtils.isBetween(0, 1, x) &&
                       MathUtils.isBetween(0, 1, y) && action.equals("add");
            }
            case 2: {
                return MathUtils.isBetween(-2, -1, x) &&
                        MathUtils.isBetween(1, 2, y) && action.equals("add");
            }
            case 3: {
                return MathUtils.isBetween(-1, 0, x) &&
                        MathUtils.isBetween(2, 3, y) && action.equals("add");
            }
            case 4: {
                return action.equals("camera move") ||
                        (action.equals("add") && MathUtils.isBetween(-1, 0, x) &&
                                MathUtils.isBetween(11, 12, y));
            }
            case 5: {
                return action.equals("zoom out");
            }
            case 6: {
                return index == 3 && action.equals("remove");
            }
            case 7: {
                return index == 0 && action.equals("sample move");
            }
            case 9: {
                return action.equals("remove");
            }
            default: {
                return false;
            }
        }
    }
}
