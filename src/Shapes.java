import java.awt.*;

class Stretch {
    private final Point2D A;
    private final Point2D B;
    private final Color color;

    Stretch(Point2D A, Point2D B, Color col) {
        this.A = A;
        this.B = B;
        color = col;
    }

    void draw(Graphics2D g2, CoordinateSystem plane) {
        g2.setStroke(new BasicStroke(3));
        g2.setColor(color);
        DrawUtils.line(plane.screenX(A.x), plane.screenY(A.y), plane.screenX(B.x), plane.screenY(B.y));
    }
}

class Ring {
    private final Point2D center;
    private final double radius;
    private final Color color;

    Ring(Point2D center, double r, Color col) {
        this.center = center;
        radius = r;
        color = col;
    }

    void draw(Graphics2D g2, CoordinateSystem plane) {
        g2.setStroke(new BasicStroke(3));
        g2.setColor(color);
        DrawUtils.ring(plane.screenX(center.x), plane.screenY(center.y), radius*plane.scale);
    }
}