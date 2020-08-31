import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Basic point class in 2D space. Can be moved.
 */
class Point2D {
    protected double x, y;

    Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * moves the point by the vector [dx, dy]
     * @param dx - difference of x
     * @param dy - difference of y
     */
    void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    /**
     * moves the point to the specific place.
     * @param new_x - new x of the point
     * @param new_y - new y of the point
     */
    void instantMove(double new_x, double new_y) {
        x = new_x;
        y = new_y;
    }

    double getX() { return x; }
    double getY() { return y; }

    ArrayList<Double> values() { return new ArrayList<>(Arrays.asList(x, y)); }
}

/**
 * Point class which can be drawn in cartesian plane simulations.
 */
class Sample extends Point2D {
    private static final double radius = 0.18;
    private Color col = DrawUtils.lightBlue;
    private Color predictedColor = DrawUtils.gray;
    // if point is moving then it follows the mouse,
    // if point is selected then around him an orange ring is drawn
    private boolean isMoving, selected;
    // used in prediction algorithms
    private int category;

    Sample(double x, double y) {
        super(x,y);
        isMoving = false;
        category = 1;
    }

    Sample(double x, double y, Color col) {
        super(x,y);
        this.col = col;
        isMoving = false;
        selected = false;
        category = 1;
    }

    /**
     * Draws the point as a circle. Its color corresponds the category which it has.
     * if point is selected then there is an orange ring round him
     * @param camera - left top corner of the screen position in the cartesian plane simulation.
     * @param scale - current scale of cartesian plane simulation
     * @param g2 - graphics engine
     */
    void draw(Point2D camera, double scale, Graphics2D g2) {
        g2.setColor(col);
        double screenX = (x - camera.x)*scale;
        double screenY = -(y - camera.y)*scale;

        DrawUtils.circle(screenX, screenY, radius*scale);
        if(category == 0) {
            g2.setColor(predictedColor);
            DrawUtils.circle(screenX, screenY, radius*scale/2);
        }

        if(selected) {
            g2.setColor(DrawUtils.orange);
            g2.setStroke(new BasicStroke(2));
            DrawUtils.ring(screenX, screenY, radius*scale*1.7);
        }
    }

    /**
     * checks whether the point (px, py) is inside the circle that represents the sample.
     * @param px - x coordinate of point
     * @param py - y coordinate of point
     * @return - true if point (px, py) is inside the sample otherwise false.
     */
    boolean hasInside(double px, double py) {
        return (px - x)*(px - x) + (py - y)*(py - y) <= radius*radius;
    }

    Color getColor() { return col; }

    void setColor(Color col) { this.col = col; }

    void setPredictedColor(Color newPredictedColor) {
        predictedColor = newPredictedColor;
    }

    Color getPredictedColor() {
        return predictedColor;
    }

    void setMoving(boolean value) { isMoving = value; }

    boolean isMoving() { return isMoving; }

    int category() { return category; }

    void setCategory(int value) { category = value; }

    public void select(boolean value) {
        selected = value;
    }

    Point2D position() { return new Point2D(x, y); }
}