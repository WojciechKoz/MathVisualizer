import java.awt.*;

import static java.lang.StrictMath.abs;

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

    void move(double dx, double dy) {
        x += dx;
        y += dy;
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
    private Color color;

    Triangle(Point2D A, Point2D B, Point2D C, Color col) {
        this.A = A;
        this.B = B;
        this.C = C;
        color = col;
    }

    @Override
    public void draw(Graphics2D g2, CoordinateSystem plane) {
        g2.setColor(color);
        g2.fillPolygon(
                new int[] {
                    (int) plane.screenX(A.x),
                    (int) plane.screenX(B.x),
                    (int) plane.screenX(C.x)
                },
                new int[] {
                    (int) plane.screenY(A.y),
                    (int) plane.screenY(B.y),
                    (int) plane.screenY(C.y)
                }, 3);
    }

    void move(double dx, double dy) {
        A.move(dx, dy);
        B.move(dx, dy);
        C.move(dx, dy);
    }
}

class Arrow implements Shape {
    Triangle dart;
    Rectangle body;
    private double positionShift = 0;
    private double positionChanges;
    private String direction;

    Arrow(Point2D center, double length, String direction, Color color) {
        this.direction = direction;

        switch(direction) {
            case "up": {
                body = new Rectangle(center.x - 0.125*length, center.y + 0.15*length,
                        0.25*length, 0.8*length, color);

                dart = new Triangle(new Point2D(center.x-0.3*length, center.y+0.15*length),
                        new Point2D(center.x+0.3*length, center.y+0.15*length),
                        new Point2D(center.x, center.y+0.45*length), color);
            } break;
            case "down": {
                body = new Rectangle(center.x - 0.125*length, center.y + 0.65*length,
                        0.25*length, 0.8*length, color);

                dart = new Triangle(new Point2D(center.x-0.3*length, center.y-0.15*length),
                        new Point2D(center.x+0.3*length, center.y-0.15*length),
                        new Point2D(center.x, center.y-0.45*length), color);
            } break;
            case "right": {
                body = new Rectangle(center.x - 0.65*length, center.y + 0.125*length,
                         0.8*length, 0.25*length, color);

                dart = new Triangle(new Point2D(center.x+0.15*length, center.y-0.3*length),
                        new Point2D(center.x+0.15*length, center.y+0.3*length),
                        new Point2D(center.x+0.45*length, center.y), color);
            } break;
            case "left": {
                body = new Rectangle(center.x - 0.15*length, center.y + 0.125*length,
                        0.8*length, 0.25*length, color);

                dart = new Triangle(new Point2D(center.x-0.15*length, center.y-0.3*length),
                        new Point2D(center.x-0.15*length, center.y+0.3*length),
                        new Point2D(center.x-0.45*length, center.y), color);
            } break;
        }
        positionChanges = length/100 * (direction.equals("right") || direction.equals("up") ? 1 : -1);
    }

    @Override
    public void draw(Graphics2D g2, CoordinateSystem plane) {
        dart.draw(g2, plane);
        body.draw(g2, plane);

        if(direction.equals("up") || direction.equals("down")) {
            dart.move(0, positionChanges);
            body.move(0, positionChanges);
        } else {
            dart.move(positionChanges, 0);
            body.move(positionChanges, 0);
        }

        positionShift += positionChanges;
        if(abs(positionShift) > abs(positionChanges)*25) {
            positionChanges = -positionChanges;
            positionShift += positionChanges;
        }
    }

}