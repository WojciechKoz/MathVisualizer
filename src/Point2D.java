import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

class Point2D {
    protected double x, y;

    Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    void instantMove(double new_x, double new_y) {
        x = new_x;
        y = new_y;
    }

    double getX() { return x; }
    double getY() { return y; }

    ArrayList<Double> values() { return new ArrayList<Double>(Arrays.asList(x,y)); }
}

class Sample extends Point2D {
    private static final double radius = 0.18f;
    private Color col = new Color(100, 100, 255);
    private Color predictedColor = new Color(130, 130, 130);
    private boolean isMoving, selected;
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

    void draw(Point2D camera, double scale, Graphics2D g2) {
        g2.setColor(col);
        double screenX = (x - camera.x)*scale;
        double screenY = -(y - camera.y)*scale;

        DrawUtils.circle(screenX, screenY, radius*scale, g2);
        if(category == 0) {
            g2.setColor(predictedColor);
            DrawUtils.circle(screenX, screenY, radius*scale/2, g2);
        }

        if(selected) {
            g2.setColor(new Color(251, 139, 36));
            g2.setStroke(new BasicStroke(2));
            DrawUtils.ring(screenX, screenY, radius*scale*1.7, g2);
        }
    }

    boolean hasInside(double px, double py) {
        return (px - x)*(px - x) + (py - y)*(py - y) <= radius*radius;
    }

    Color getColor() { return col; }

    void setColor(Color col) { this.col = col; }

    void setPredictedColor(Color newPredictedColor) {
        predictedColor = newPredictedColor;
    }

    void setMoving(boolean value) { isMoving = value; }

    boolean isMoving() { return isMoving; }

    int category() { return category; }

    void setCategory(int value) { category = value; }

    public void select(boolean value) {
        selected = value;
    }
}