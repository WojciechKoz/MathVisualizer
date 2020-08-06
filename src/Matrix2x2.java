import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.StrictMath.*;

public class Matrix2x2 {
    protected double a,b,c,d;
    // [v1x, v1y, v2x, v2y, l1, l2]
    protected double[] eigenvectorsAndValues = new double[6];

    Matrix2x2(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

        computeEigenvectorsAndValues();
    }

    Matrix2x2(ArrayList<Double> values) {
        if(values.size() == 4) {
            this.a = values.get(0);
            this.b = values.get(1);
            this.c = values.get(2);
            this.d = values.get(3);
        } else {
            a = 0; b = 0; c = 0; d = 0;
        }

        computeEigenvectorsAndValues();
    }

    double det() {
        return a*d - b*c;
    }

    double trace() {
        return a+d;
    }

    Sample project(Sample v) {
        return new Sample(a*v.getX() + b*v.getY(), c*v.getX() + d*v.getY());
    }

    Matrix2x2 dot(Matrix2x2 other) {
        return new Matrix2x2(a*other.a + b*other.c, a*other.b + b*other.d,
                c*other.a + d*other.c, c*other.b + d*other.d);
    }

    Matrix2x2 transpose() {
        return new Matrix2x2(a, c, b, d);
    }

    Matrix2x2 inverse() {
        double det = det();
        if(det == 0) {
            return new Matrix2x2(0,0,0,0);
        }
        return new Matrix2x2(d/det, -c/det, -b/det, a/det);
    }

    void computeEigenvectorsAndValues() {
        if(trace()*trace() < 4*det() || singular()) {
            eigenvectorsAndValues = new double[] {0,0,0,0,0,0};
            return;
        }

        double delta = sqrt(trace()*trace() - 4*det());

        double lambda_1 = (trace() - delta)/2;
        double lambda_2 = (trace() + delta)/2;

        double len_v1 = sqrt(1+((lambda_1-a)/b)*((lambda_1-a)/b));
        double len_v2 = sqrt(1+((lambda_2-a)/b)*((lambda_2-a)/b));

        // compute eigenvectors divided by their lengths and multiply by lambda values to achieve lengths = lambda
        double v1x = lambda_1/len_v1;
        double v1y = lambda_1*(lambda_1-a)/(b*len_v1);
        double v2x = lambda_2/len_v2;
        double v2y = lambda_2*(lambda_2-a)/(b*len_v2);

        eigenvectorsAndValues = new double[] {v1x, v1y, v2x, v2y, lambda_1, lambda_2};
    }

    double[] realEig() {
        return eigenvectorsAndValues;
    }

    ArrayList<Double> greaterEigenvector() {
        if(eigenvectorsAndValues[4] > eigenvectorsAndValues[5]) {
            new ArrayList<>(Arrays.asList(eigenvectorsAndValues[0], eigenvectorsAndValues[1]));
        }
        return new ArrayList<>(Arrays.asList(eigenvectorsAndValues[2], eigenvectorsAndValues[3]));
    }

    boolean almostSingular() {
        return abs(det()) < 0.03 || abs((a*b+c*d)/(sqrt(a*a+c*c)*sqrt(b*b+d*d))) > 0.999 || a*a+c*c < 0.01 || b*b + d*d < 0.01 ;
    }

    boolean singular() {
        return det() == 0.0;
    }

    String getName() { return "A"; }

    public void setValues(Matrix2x2 covarianceMatrix) {
        a = covarianceMatrix.a;
        b = covarianceMatrix.b;
        c = covarianceMatrix.c;
        d = covarianceMatrix.d;
        computeEigenvectorsAndValues();
    }
}


class GraphicsMatrix2x2 extends Matrix2x2 {
    private final double radius = 0.1;
    private boolean xBasisSelected, yBasisSelected;

    GraphicsMatrix2x2(double a, double b, double c, double d) {
        super(a, b, c, d);
        deselect();
    }

    GraphicsMatrix2x2(ArrayList<Double> values) {
        super(values);
        deselect();
    }

    GraphicsMatrix2x2(Matrix2x2 matrix) {
        super(matrix.a, matrix.b, matrix.c, matrix.d);
        deselect();
    }

    boolean moveXBasis(double mx, double my) { // mx and my should be converted to cartesian values
        return (mx - a)*(mx - a) + (my - c)*(my - c) <= radius*radius;
    }

    boolean moveYBasis(double mx, double my) { // mx and my should be converted to cartesian values
        return (mx - b)*(mx - b) + (my - d)*(my - d) <= radius*radius;
    }

    boolean isSelected() { return xBasisSelected || yBasisSelected; }

    void deselect() {
        xBasisSelected = false;
        yBasisSelected = false;
    }

    void selectBasis(double x, double y) {
        if(MathUtils.dist(x, y, a, c) < radius) {
            xBasisSelected = true;
        } else if(MathUtils.dist(x, y, b, d) < radius) {
            yBasisSelected = true;
        }
    }

    void moveBasis(double x, double y) {
        if(xBasisSelected) {
            a = x;
            c = y;
            computeEigenvectorsAndValues();
        } else if(yBasisSelected) {
            b = x;
            d = y;
            computeEigenvectorsAndValues();
        }
    }

    void drawBasis(Graphics2D g2, double scale, Point2D camera, boolean active) {
        g2.setColor(new Color(255, 0, 0));
        DrawUtils.line(-camera.x * scale, camera.y * scale, (a - camera.x)*scale, -(c - camera.y)*scale, g2);
        if(active) DrawUtils.circle((a - camera.x)*scale, -(c - camera.y)*scale, scale*radius, g2);

        g2.setColor(new Color(0, 255, 0));
        DrawUtils.line(-camera.x * scale, camera.y * scale, (b - camera.x)*scale, -(d - camera.y)*scale, g2);
        if(active) DrawUtils.circle((b - camera.x)*scale, -(d - camera.y)*scale, scale*radius, g2);
    }

    void drawEigenvectors(Graphics2D g2, double scale, Point2D camera) {
        g2.setColor(new Color(255,255,255));
        g2.setStroke(new BasicStroke(2));

        DrawUtils.line(-camera.x*scale, camera.y*scale,
                (eigenvectorsAndValues[0] - camera.x)*scale,
                -(eigenvectorsAndValues[1] - camera.y)*scale, g2);

        DrawUtils.line(-camera.x*scale, camera.y*scale,
                (eigenvectorsAndValues[2] - camera.x)*scale,
                -(eigenvectorsAndValues[3] - camera.y)*scale, g2);
    }

    void drawAxes(Graphics2D g2, double scale, Point2D camera, CartesianPlane plane) {
        g2.setStroke(new BasicStroke(3));
        g2.setColor(new Color(255, 0, 0));
        plane.drawStraightLine(c/a, 0);
        g2.setColor(new Color(0, 255, 0));
        plane.drawStraightLine(d/b, 0);
    }

    void drawTranspose(Graphics2D g2, CartesianPlane plane) {
        g2.setColor(new Color(100, 100, 255));
        g2.setStroke(new BasicStroke(2));
        drawOtherMatrixBasis(transpose(), g2, plane);
    }

    void drawInverse(Graphics2D g2, CartesianPlane plane) {
        g2.setColor(new Color(230, 180, 0));
        g2.setStroke(new BasicStroke(2));
        drawOtherMatrixBasis(inverse(), g2, plane);
    }

    void drawOtherMatrixBasis(Matrix2x2 matrix, Graphics2D g2, CartesianPlane plane) {
        g2.drawLine((int)plane.screenX(0), (int)plane.screenY(0), (int)plane.screenX(matrix.a), (int)plane.screenY(matrix.c));
        g2.drawLine((int)plane.screenX(0), (int)plane.screenY(0), (int)plane.screenX(matrix.b), (int)plane.screenY(matrix.d));

        g2.setColor(new Color(255, 0, 0));
        DrawUtils.circle(plane.screenX(matrix.a), plane.screenY(matrix.c), radius*plane.scale, g2);
        g2.setColor(new Color(0, 255, 0));
        DrawUtils.circle(plane.screenX(matrix.b), plane.screenY(matrix.d), radius*plane.scale, g2);
    }

    void drawEigenlines(Graphics2D g2, CartesianPlane plane) {
        if(eigenvectorsAndValues[0] == 0 || eigenvectorsAndValues[2] == 0) {
            return; // matrix is singular or has imaginary eigenvalues
        }
        g2.setColor(new Color(255, 255, 255, 180));
        g2.setStroke(new BasicStroke(2));
        plane.drawStraightLine(eigenvectorsAndValues[1]/eigenvectorsAndValues[0], 0);
        plane.drawStraightLine(eigenvectorsAndValues[3]/eigenvectorsAndValues[2], 0);
    }

    void drawDeterminant(Graphics2D g2, CartesianPlane plane) {
        g2.setColor(new Color(255, 255, 100, 120));
        g2.fillPolygon(
                new int[] {(int)plane.screenX(0), (int)plane.screenX(a), (int)plane.screenX(a+b), (int)plane.screenX(b)},
                new int[]{(int)plane.screenY(0), (int)plane.screenY(c), (int)plane.screenY(c+d), (int)plane.screenY(d)}, 4);
    }

    void drawGrid(Graphics2D g2, double scale, Point2D camera, CartesianPlane plane) {
        if(almostSingular()) {
            return;
        }
        g2.setStroke(new BasicStroke(1));

        g2.setColor(new Color(255, 0, 0, 130));
        double direction = c/a;
        double freeCoefficient = abs(b*c/a-d);
        drawParallelLines(direction, freeCoefficient, camera, scale, plane, g2);

        g2.setColor(new Color(0, 255, 0, 130));
        direction = d/b;
        freeCoefficient = abs(c-a*d/b);
        drawParallelLines(direction, freeCoefficient, camera, scale, plane, g2);
    }

    private void drawParallelLines(double direction, double freeCoefficient, Point2D camera, double scale,
                                   CartesianPlane plane, Graphics2D g2) {
        if(freeCoefficient < 0.001) return;

        if(Double.isInfinite(direction)) {
            for (int i = (int) ceil(camera.x); i < ceil(camera.x) + floor(plane.width / scale) + 1; i++) {
                DrawUtils.line((i - camera.x) * scale, 0, (i - camera.x) * scale, plane.height, g2);
            }
        } else if(direction >= 0) {
            int i = 1;
            while (direction * camera.getX() + i * freeCoefficient < camera.getY()) {
                plane.drawStraightLine(direction, i * freeCoefficient);
                i++;
            }

            i = -1;
            while (direction * (camera.getX()+plane.width/scale) + i * freeCoefficient > camera.getY() - plane.height/scale) {
                plane.drawStraightLine(direction, i * freeCoefficient);
                i--;
            }
        } else {
            int i = -1;
            while (direction * camera.getX() + i * freeCoefficient > camera.getY() - plane.height/scale) {
                plane.drawStraightLine(direction, i * freeCoefficient);
                i--;
            }

            i = 1;
            while (direction * (camera.getX()+plane.width/scale) + i * freeCoefficient < camera.getY()) {
                plane.drawStraightLine(direction, i * freeCoefficient);
                i++;
            }
        }
    }
}
