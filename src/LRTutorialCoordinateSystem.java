import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class LRTutorialCoordinateSystem extends LRCoordinateSystem {
    //  number of current step of the tutorial
    private int state = 1;
    private final ArrayList<Shape> areasShapes = new ArrayList<>();
    private final ArrayList<Shape> arrows = new ArrayList<>();
    private final AutoMotion autoMotion = new AutoMotion(this);

    LRTutorialCoordinateSystem(Graphics2D g2, int width, int height, Panel mainPanel) {
        super(g2, width, height, mainPanel);
        menuName = "Menu";

        errorVisibility = false;
        menu.toggleCheckBoxButton("Errors");
        menu.toggleCheckBoxButton("Visible");
        messageWindow.toggleVisibility();

        areasShapes.add(new BlinkingRectangle(0, 1, 1, 1, DrawUtils.yellow));
    }

    /**
     * Initializes the message window
     */
    @Override
    void initComponents() {
        messageWindow = new MessageWindow(width, height, "data/Tutorial-Part-1-1-About");
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
        if(isValidMouseAction(x, y, "add", 0)) {
            super.addNewSample(x, y);
            changeState();
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

    void moveCamera(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(isValidMouseAction(0, 0, "camera move", 0)) {
            super.moveCamera(mouseX, mouseY, prevMouseX, prevMouseY);
        }
    }

    /**
     * changes scale of cartesian plane simulation and move camera towards mouse position
     * Effect is similar to zooming in google maps. Checks if it is allowed to zoom in/out
     * @param amount - factor of scales change (can be either 0.95 [zoom out] or 1.05 [zoom in])
     * @param mouseX - current mouse x position on the screen (in pixels)
     * @param mouseY - current mouse y position on the screen (in pixels)
     */
    void changeScale(double amount, double mouseX, double mouseY) {
        if(isValidMouseAction(mouseX, mouseY, amount > 1 ? "zoom in" : "zoom out", 0)) {
            super.changeScale(amount, mouseX, mouseY);
            if(state == 5 && ( camera.y - height/scale < -1 || scale < 45)) {
                if(scale < 45) {
                    autoMotion.setTargetPoint(new Point2D(camera.x, height/scale-1), 20);
                }
                changeState();
            }
        }
    }

    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        if(samples.size() > 1) {
            Sample fst = samples.get(0);
            if(state == 7 && fst.isMoving() && 0 < fst.x && fst.x < 1 && 3 < fst.y && fst.y < 4) {
                changeState();
            }
        }

        // blocks the menu at the beginning
        if(state < 8 && menu.hasInside(mouseX, mouseY)) return;

        super.onLeftMouseButtonReleased(mouseX, mouseY);
    }

    @Override
    public boolean onKeyPressed(KeyEvent event) {
        if(menu.onKeyPressed(event) && state == 8) {
            update();

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

    void changeState() {
        state += 1;
        areasShapes.clear();
        arrows.clear();

        addInterface();

        if(state < 10) {
            messageWindow = new MessageWindow(width, height, "data/Tutorial-Part-1-" + state + "-About");
            messageWindow.toggleVisibility();
        }
    }

    void addInterface() {
        switch(state) {
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
                autoMotion.setTargetPoint(new Point2D(-width/(2*scale) + 0.5, height/(2*scale) + 0.5), 30);
                autoMotion.setTargetScale(100, 30);
            } break;
            case 10: {
                panel.changeGraphics("", "tutorial-part-2");
            }
        }
    }

    boolean isValidMouseAction(double x, double y, String action, int index) {
        return ( state == 1 && 0 < x && x < 1 && 0 < y && y < 1 && action.equals("add") )  ||
               ( state == 2 && -2 < x && x < -1 && 1 < y && y < 2 && action.equals("add")) ||
               ( state == 3 && -1 < x && x < 0 && 2 < y && y < 3 && action.equals("add"))  ||
               ( state == 4 && -1 < x && x < 0 && 11 < y && y < 12 && action.equals("add")) ||
               ( state == 4 && action.equals("camera move")) ||
               ( state == 5 && action.equals("zoom out")) ||
               ( state == 6 && index == 3 && action.equals("remove")) ||
               ( state == 7 && index == 0 && action.equals("sample move")) ||
               ( state == 9 && action.equals("remove")) ;
    }
}
