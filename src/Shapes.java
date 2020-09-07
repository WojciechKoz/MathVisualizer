import java.awt.*;

interface Shape {
    public void draw(Graphics2D g2, CoordinateSystem plane);
}

/**
 * Graphical representation of the stretch in coordinate system.
 * Used in interfaces inside the coordinate systems visualizations ( such as KNNInterface)
 */
class Stretch implements Shape {
    private final Point2D A;
    private final Point2D B;
    private final Color color;

    Stretch(Point2D A, Point2D B, Color col) {
        this.A = A;
        this.B = B;
        color = col;
    }

    /**
     * Draws a line in the coordinate system simulation. Coordinates are consistent with the simulation.
     * @param g2 - graphics engine
     * @param plane - current coordinate system
     */
    @Override
    public void draw(Graphics2D g2, CoordinateSystem plane) {
        g2.setStroke(new BasicStroke(3));
        g2.setColor(color);
        DrawUtils.line(plane.screenX(A.x), plane.screenY(A.y), plane.screenX(B.x), plane.screenY(B.y));
    }
}

/**
 * Graphical representation of the ring in coordinate system.
 * Used in interfaces inside the coordinate systems visualizations ( such as KNNInterface)
 */
class Ring implements Shape {
    private final Point2D center;
    private final double radius;
    private final Color color;

    Ring(Point2D center, double r, Color col) {
        this.center = center;
        radius = r;
        color = col;
    }

    /**
     * Draws a ring in the coordinate system simulation. Coordinates are consistent with the simulation.
     * @param g2 - graphics engine
     * @param plane - current coordinate system
     */
    @Override
    public void draw(Graphics2D g2, CoordinateSystem plane) {
        g2.setStroke(new BasicStroke(3));
        g2.setColor(color);
        DrawUtils.ring(plane.screenX(center.x), plane.screenY(center.y), radius*plane.scale);
    }
}

class Rectangle implements Shape {
    protected double x, y, width, height;
    protected Color color;

    Rectangle(double x, double y, double width, double height, Color col) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        color = col;
    }

    /**
     * Draws a rectangle in the coordinate system simulation. Coordinates are consistent with the simulation.
     * @param g2 - graphics engine
     * @param plane - current coordinate system
     */
    @Override
    public void draw(Graphics2D g2, CoordinateSystem plane) {
        g2.setColor(color);
        g2.fillRect((int)plane.screenX(x), (int)plane.screenY(y), (int)(plane.scale*width), (int)(plane.scale*height));
    }
}


class BlinkingRectangle extends Rectangle {
    private int transparency = 255;
    private int transChanges = -5;

    BlinkingRectangle(double x, double y, double width, double height, Color col) {
        super(x, y, width, height, col);

    }

    /**
     * Draws a rectangle in the coordinate system simulation. Coordinates are consistent with the simulation.
     * @param g2 - graphics engine
     * @param plane - current coordinate system
     */
    @Override
    public void draw(Graphics2D g2, CoordinateSystem plane) {
        super.draw(g2, plane);

        transparency += transChanges;

        if(transparency >= 255) {
            transChanges = -5;
        } else if(transparency <= 0) {
            transChanges = 5;
        }

        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), transparency);
    }
}

class Triangle implements Shape {
    private Point2D A, B, C;

    Triangle(Point2D A, Point2D B, Point2D C) {
        this.A = A;
        this.B = B;
        this.C = C;
    }

    @Override
    public void draw(Graphics2D g2, CoordinateSystem plane) {
        g2.setColor(color);
        g2.fillTriangle();
    }
}

class Arrow implements Shape {
    Triangle dart;
    Rectangle body;


}