import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class LogTutorialCoordinateSystem extends LogCoordinateSystem {
    //  number of current step of the tutorial
    private int state = 1;
    private final ArrayList<Shape> areasShapes = new ArrayList<>();
    private final ArrayList<Shape> arrows = new ArrayList<>();
    private final AutoMotion autoMotion = new AutoMotion(this);
    private boolean addLock, redAreaAvailable, blueAreaAvailable;
    private int indexOfMovedSample;

    LogTutorialCoordinateSystem(Graphics2D g2, int width, int height, Panel mainPanel) {
        super(g2, width, height, mainPanel);
        menuName = "Menu";

        messageWindow.toggleVisibility();
        autoMotion.setTargetPoint(new Point2D(-2, 7), 60);

        addNewSample(0.6, 1.6);
        addNewSample(1, 3);
        addNewSample(1.5, 2.4);
        addNewSample(1.6, 3.8);
        addLock = true;
        arrows.add(new Arrow(new Point2D(3,3), 2, "left", DrawUtils.transparentYellow));
    }

    /**
     * Initializes the message window
     */
    @Override
    void initComponents() {
        messageWindow = new MessageWindow(width, height, "data/Tutorial-Part-2-1-About");
    }


    /**
     * draws coordinate system lines, samples,
     * and, if there are at least two samples, best straight line
     * at the end menu and message window are drawn
     */
    @Override
    public void draw() {
        for(Shape s: areasShapes) s.draw(g2, this);
        super.draw();
        for(Shape a: arrows) a.draw(g2, this);

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
            if(state == 3) {
                if(0 < x && x < 1 && 2 < y && y < 3) redAreaAvailable = false;

                if(3 < x && x < 4 && 0 < y && y < 1) {
                    blueAreaAvailable = false;
                    indexOfMovedSample = samples.size()-1;
                }

                if(!(redAreaAvailable || blueAreaAvailable)) {
                    changeState();
                }
            }
        }
    }

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

    @Override
    public boolean onKeyPressed(KeyEvent event) {
        char key = event.getKeyChar();
        if (isValidMouseAction(0,0, "color", Integer.parseInt(String.valueOf(key)))) {
            if(colorSelectedSample(DrawUtils.sampleColors[Integer.parseInt(String.valueOf(key))],
                    Character.getNumericValue(key))) {
                update();

                if(state == 1) {
                    for(Sample sample: samples) {
                        if(sample.category() != 2) return true;
                    }
                    changeState();
                }
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

    void changeState() {
        state += 1;
        areasShapes.clear();
        arrows.clear();

        addInterface();

        if(state < 7) {
            messageWindow = new MessageWindow(width, height, "data/Tutorial-Part-2-" + state + "-About");
            messageWindow.toggleVisibility();
        }
    }

    void addInterface() {
        switch(state) {
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

    boolean isValidMouseAction(double x, double y, String action, int index) {
        return ( state == 1 && action.equals("color") && index == 2) ||
               ( state == 2 && action.equals("color") && (index == 2 || index == 1)) ||
               ( state == 3 && action.equals("add") &&
                       ((0 < x && x < 1 && 2 < y && y < 3 && redAreaAvailable) ||
                        (3 < x && x < 4 && 0 < y && y < 1 && blueAreaAvailable))) ||
               ( state == 4 && action.equals("sample move") && (index == indexOfMovedSample));
    }
}
