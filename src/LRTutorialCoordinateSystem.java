import java.awt.*;
import java.util.ArrayList;

public class LRTutorialCoordinateSystem extends LRCoordinateSystem {
    //  number of current step of the tutorial
    private int state = 1;
    private ArrayList<Shape> shapes = new ArrayList<>();

    LRTutorialCoordinateSystem(Graphics2D g2, int width, int height, Panel mainPanel) {
        super(g2, width, height, mainPanel);
        menuName = "Menu";

        errorVisibility = false;
        menu.toggleCheckBoxButton("Errors");
        menu.toggleCheckBoxButton("Visible");
        messageWindow.toggleVisibility();

        shapes.add(new BlinkingRectangle(0, 1, 1, 1, DrawUtils.yellow));
    }

    /**
     * Initializes the message window
     */
    @Override
    void initComponents() {
        messageWindow = new MessageWindow(width, height, "data/Tutorial-Part-1-About");
    }

    /**
     * draws coordinate system lines, samples,
     * and, if there are at least two samples, best straight line
     * at the end menu and message window are drawn
     */
    @Override
    public void draw() {
        for(Shape s: shapes) s.draw(g2, this);
        super.draw();
    }

    /**
     * Adds new sample when right mouse button was pressed.
     * Checks if it is right moment and place to add it in the tutorial
     * @param x - Initial x coordinate (in cartesian plane simulation)
     * @param y - Initial y coordinate (in cartesian plane simulation)
     */
    void addNewSample(double x, double y) {
        if(state == 1 && 0 < x && x < 1 && 0 < y && y < 1) {
            super.addNewSample(x, y);
            changeState();
            shapes.add(new BlinkingRectangle(-2, 2, 1, 1, DrawUtils.yellow));
        } else if(state == 2 && -2 < x && x < -1 && 1 < y && y < 2) {
            super.addNewSample(x, y);
            changeState();
            shapes.add(new BlinkingRectangle(-1, 3, 1, 1, DrawUtils.yellow));
        } else if(state == 3 && -1 < x && x < 0 && 2 < y && y < 3) {
            super.addNewSample(x, y);
            changeState();
            shapes.add(new BlinkingRectangle(-1, 12, 1, 1, DrawUtils.yellow));

        }
    }

    void changeState() {
        state += 1;
        shapes.clear();
        if(state < 5) {
            messageWindow = new MessageWindow(width, height, "data/Tutorial-Part-" + state + "-About");
            messageWindow.toggleVisibility();
        }
    }
}
