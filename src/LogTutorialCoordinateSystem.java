import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * This is the second part of the tutorial.
 * It has many switches depended on the current state of the tutorial
 * so my apology if the code has less quality than the other classes.
 */
public class LogTutorialCoordinateSystem extends LogCoordinateSystem {
    //  number of current step of the tutorial
    private int state = 1;
    private final ArrayList<Shape> areasShapes = new ArrayList<>();
    private final ArrayList<Shape> arrows = new ArrayList<>();
    private final AutoMotion autoMotion = new AutoMotion(this);
    // addLock - if true you can add sample wherever and whenevery you want if not
    // you can do this only if you are allowed to do that. Used when 4 neutral samples appear.
    private boolean addLock;
    // in one step you need to add one sample in one square and one in the other, so these
    // variables tell which square hasn't got the sample inside yet
    private boolean redAreaAvailable, blueAreaAvailable;
    // sample in the blue area will be moved. but order of adding these samples
    // are unknown so it stores this index
    private int indexOfMovedSample;

    LogTutorialCoordinateSystem(int width, int height, Panel mainPanel) {
        super(width, height, mainPanel);
        menuName = "Menu";

        // side menu is hide at the beginning but the message window is visible all the time
        messageWindow.toggleVisibility();
        autoMotion.setTargetPoint(new Point2D(-2, 7), 60);

        addInterface();
    }

    /**
     * Initializes the message window
     */
    @Override
    void initComponents() {
        super.initComponents();
        messageWindow = new MessageWindow(this, "data/"+StringsResources.languageShortcut()+"/Tutorial-Part-2-1-About");
    }


    /**
     * at the bottom draws shapes (squares) to highlight some area
     * then draws standard logistic regression simulation (with menu and message box)
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
     * @param x - Initial x coordinate (in cartesian plane simulation)
     * @param y - Initial y coordinate (in cartesian plane simulation)
     */
    void addNewSample(double x, double y) {
        if(isValidMouseAction(x, y, "add", 0) || !addLock) {
            super.addNewSample(x, y);

            // only in state 3 user can add samples but in state 1 and 2
            // samples are also added (not by user)
            if(state == 3) {
                if(MathUtils.isBetween(0, 1, x) && MathUtils.isBetween(2, 3, y)) {
                    redAreaAvailable = false;
                }

                if(MathUtils.isBetween(3, 4, x) && MathUtils.isBetween(0, 1, y)) {
                    blueAreaAvailable = false;
                    // remembers the index of sample that will be moved in the next state
                    indexOfMovedSample = samples.size()-1;
                }

                // if both samples are added goes to the next step
                if(!(redAreaAvailable || blueAreaAvailable)) {
                    changeState();
                }
            }
        }
    }

    /**
     * There is no removes in this part of the tutorial
     * @param index - index of sample that has to be deleted (not used)
     */
    void removeSample(int index) {

    }

    /**
     * moves the sample that is under the mouse.
     * since it is a tutorial it works only if you are allowed to move it.
     * @param mouseX - current x coordinate of the mouse (in pixels)
     * @param mouseY - current y coordinate of the mouse (in pixels)
     * @return true if sample was moved - simulation has to be refreshed
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
     * if mouse is inside the menu then performs actions related to clicked button
     * else sets all samples moving variable to false (won't follow the mouse)
     * In tutorial checks if the sample was moved in the good spot and checks
     * if user selects right values of eta and epochs.
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        if(samples.size() > 1) {
            Sample moved = samples.get(indexOfMovedSample);
            if(state == 4 && moved.getPredictedColor() == DrawUtils.lightRed) {
                changeState();
            }
        }

        super.onLeftMouseButtonReleased(mouseX, mouseY);

        if(state == 5) {
            if(menu.readValueFromSlider("Epochs") == 1 && menu.readValueFromSlider("ETA") >= 1.2) {
                changeState();
            }
        }
    }

    /**
     * Colors samples red or blue. Doesn't menu to take the input
     * from the keyboard
     * @param event - all information about pressed button
     * @return true if some sample was colored.
     */
    @Override
    public boolean onKeyPressed(KeyEvent event) {
        char key = event.getKeyChar();
        if (isValidMouseAction(0,0, "color", Integer.parseInt(String.valueOf(key)))) {
            if(colorSelectedSample(DrawUtils.sampleColors[Integer.parseInt(String.valueOf(key))],
                    Character.getNumericValue(key))) {
                update();

                // in first state all samples have to have red color in order to
                // change the state
                if(state == 1) {
                    for(Sample sample: samples) {
                        if(sample.category() != 2) return true;
                    }
                    changeState();
                }

                // in second state first 4 samples should have red color
                // but next 4 samples should be blue. Then the state will change.
                if(state == 2) {
                    for(int i = 0; i < samples.size(); i++) {
                        if((i < 4 && samples.get(i).category() != 2) ||
                                (i >= 4 && samples.get(i).category() != 1)) {
                            return true;
                        }
                    }
                    changeState();
                }
                return true;
            }
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

        if(state < 7) {
            messageWindow = new MessageWindow(this, "data/"+StringsResources.languageShortcut()+"/Tutorial-Part-2-" + state + "-About");
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
                addNewSample(0.6, 1.6);
                addNewSample(1, 3);
                addNewSample(1.5, 2.4);
                addNewSample(1.6, 3.8);
                addLock = true;
                arrows.add(new Arrow(new Point2D(3,3), 2, "left", DrawUtils.transparentYellow));
            } break;
            case 2: {
                arrows.add(new Arrow(new Point2D(6,2), 2, "left", DrawUtils.transparentYellow));
                addLock = false;
                addNewSample(1.4, 0.6);
                addNewSample(2.2, 1.1);
                addNewSample(2.9, 1.3);
                addNewSample(3.5, 2.7);
                addLock = true;
            } break;
            case 3: {
                areasShapes.add(new BlinkingRectangle(0, 3, 1, 1, DrawUtils.yellow));
                areasShapes.add(new BlinkingRectangle(3, 1, 1, 1, DrawUtils.yellow));
                redAreaAvailable = true;
                blueAreaAvailable = true;
            } break;
            case 4: {
                arrows.add(new Arrow(new Point2D(3.5, 3), 2, "up", DrawUtils.transparentYellow));
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
                return action.equals("color") && index == 2;
            }
            case 2: {
                return action.equals("color") && (index == 1 || index ==2);
            }
            case 3: {
                return action.equals("add") &&
                    ((MathUtils.isBetween(0, 1, x) && MathUtils.isBetween(2, 3, y) && redAreaAvailable) ||
                     (MathUtils.isBetween(3, 4, x) && MathUtils.isBetween(0, 1, y) && blueAreaAvailable));
            }
            case 4: {
                return action.equals("sample move") && index == indexOfMovedSample;
            }
            default: {
                return false;
            }
        }
    }
}
