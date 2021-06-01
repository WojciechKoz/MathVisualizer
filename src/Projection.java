public class Projection {
    private Sample point;
    private double a, b;
    private Point2D projection;

    Projection(Sample P, double a, double b) {
        update(P, a, b);
    }

    void draw(CoordinateSystem plane) {
        DrawUtils.g2.setColor(point.getColor());
        plane.drawSection(point.getX(), point.getY(), projection.getX(), projection.getY());
    }

    void update(Sample P, double a, double b) {
        point = P;
        this.a = a;
        this.b = b;

        calculate();
    }

    private void calculate() {
        double perpendicularA = -1.0 / a;
        double perpendicularB = point.getY() + point.getX() / a;

        double projectionX = (perpendicularB - b) / (a - perpendicularA);
        double projectionY = a*projectionX + b;

        projection = new Point2D(projectionX, projectionY);
    }
}
