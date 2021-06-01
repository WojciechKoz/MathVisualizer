import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.StrictMath.*;

/**
 * This class contains all abstract things you can do with 2x2 matrix
 */
public class Matrix2x2 {
    protected double a,b,c,d;
    // eigenvectors and values table looks like this [v1x, v1y, v2x, v2y, l1, l2]
    protected double[] eigenvectorsAndValues = new double[6];
    protected String name;

    Matrix2x2(double a, double b, double c, double d) {
        this(a, b, c, d,"A");
    }

    Matrix2x2(double a, double b, double c, double d, String name) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.name = name;

        computeEigenvectorsAndValues();
    }

    /* not used
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
     */

    /**
     * @return determinant of this matrix
     */
    double det() {
        return a*d - b*c;
    }

    /**
     * @return trace of this matrix
     */
    double trace() {
        return a+d;
    }

    /**
     * projects vector v onto this matrix space.
     * @param v - projected vector
     * @return A * v where A is this matrix
     */
    Sample project(Sample v) {
        return new Sample(a*v.getX() + b*v.getY(), c*v.getX() + d*v.getY());
    }

    /* not used
    Matrix2x2 dot(Matrix2x2 other) {
        return new Matrix2x2(a*other.a + b*other.c, a*other.b + b*other.d,
                c*other.a + d*other.c, c*other.b + d*other.d);
    }
    */


    /**
     * @return transpose matrix (in 2x2 case just swap b and c)
     */
    Matrix2x2 transpose() {
        return new Matrix2x2(a, c, b, d);
    }

    /**
     * @return inverse matrix of this one. If matrix is singular returns 0 matrix.
     */
    Matrix2x2 inverse() {
        if(singular()) {
            return new Matrix2x2(0,0,0,0);
        }

        double det = det();
        return new Matrix2x2(d/det, -c/det, -b/det, a/det);
    }

    /**
     * computes real eigenvectors and eigenvalues.
     * If eigenvalues are supposed to be complex
     * then all eigenvectors and eigenvalues are equal to 0.
     * Lengths of eigenvectors are equal to corresponding eigenvalues (NOT 1 !!)
     * Results are saved in {@code realEigenvectorsAndValues}.
     */
    protected void computeEigenvectorsAndValues() {
        if(trace()*trace() < 4*det() || zeroMatrix()) {
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

    /**
     * @return previously computed eigenvectors and eigenvalues.
     */
    double[] realEig() {
        return eigenvectorsAndValues;
    }

    /**
     * @return returns eigenvectors which has bigger corresponding eigenvalue.
     */
    ArrayList<Double> greaterEigenvector() {
        if(eigenvectorsAndValues[4] > eigenvectorsAndValues[5]) {
            new ArrayList<>(Arrays.asList(eigenvectorsAndValues[0], eigenvectorsAndValues[1]));
        }
        return new ArrayList<>(Arrays.asList(eigenvectorsAndValues[2], eigenvectorsAndValues[3]));
    }

    double slopeOfGreaterEigenvector() {
        if(eigenvectorsAndValues[4] > eigenvectorsAndValues[5]) {
            return eigenvectorsAndValues[1] / eigenvectorsAndValues[0];
        }
        return eigenvectorsAndValues[3] / eigenvectorsAndValues[2];
    }

    /**
     * Checks if matrix is almost singular to avoid drawing numerous lines in matrix grid.
     * @return true if matrix is almost singular
     * (determinant is small, one of basis is almost 0 vector or two basis are almost parallel)
     */
    boolean almostSingular() {
        return abs(det()) < 0.03 || abs((a*b+c*d)/(sqrt(a*a+c*c)*sqrt(b*b+d*d))) > 0.999 || a*a+c*c < 0.01 || b*b + d*d < 0.01 ;
    }

    /**
     * checks whether matrix is singular.
     * @return true if matrix is singular otherwise false.
     */
    boolean singular() {
        return det() == 0.0;
    }

    /**
     * @return true if matrix is a 0 matrix meaning that all components are 0
     */
    boolean zeroMatrix() {
        return a==0 && b==0 && c==0 && d==0;
    }

    String getName() { return name; }

    void setName(String newName) { name = newName; }

    /**
     * sets new values to avoid creating a new matrix.
     * Creating a new matrix started to become a problem when buttons have pointer to its matrix.
     * This way is easier.
     * @param other - other matrix whose values this matrix should copy
     */
    public void setValues(Matrix2x2 other) {
        a = other.a;
        b = other.b;
        c = other.c;
        d = other.d;
        computeEigenvectorsAndValues();
    }
}

/**
 * Class which inherits all math standing behind 2x2 matrix and adds graphical methods to
 * draw this matrix and all its traits
 */
class GraphicsMatrix2x2 extends Matrix2x2 {
    // radius of circle which ends matrix base and that matrix base can be moved dragging this circle.
    private final double radius = 0.1;
    // variables that control matrix shift
    private boolean xBasisSelected, yBasisSelected;

    GraphicsMatrix2x2(double a, double b, double c, double d) {
        super(a, b, c, d);
        deselect();
    }

    /*
    GraphicsMatrix2x2(double a, double b, double c, double d, String name) {
        super(a, b, c, d, name);
        deselect();
    }
    */
    /* not used
    GraphicsMatrix2x2(ArrayList<Double> values) {
        super(values);
        deselect();
    }
     */

    GraphicsMatrix2x2(Matrix2x2 matrix) {
        super(matrix.a, matrix.b, matrix.c, matrix.d);
        deselect();
    }

    /* not used anymore
    boolean moveXBasis(double mx, double my) { // mx and my should be converted to cartesian values
        return (mx - a)*(mx - a) + (my - c)*(my - c) <= radius*radius;
    }

    boolean moveYBasis(double mx, double my) { // mx and my should be converted to cartesian values
        return (mx - b)*(mx - b) + (my - d)*(my - d) <= radius*radius;
    }
    */

    /**
     * checks if some of basis is moved by the mouse.
     * @return true if one of the basis are moving.
     */
    boolean isSelected() { return xBasisSelected || yBasisSelected; }

    /**
     * sets all variables that control matrix shift to false. Means that no base is moving.
     */
    void deselect() {
        xBasisSelected = false;
        yBasisSelected = false;
    }

    /**
     * checks if one of the basis is under the mouse. If it is then sets its selected to true
     * @param mouseX - x coordinate of point where lays the mouse (in simulation units not real pixel values)
     * @param mouseY - y coordinate of point where lays the mouse (in simulation units not real pixel values)
     */
    void selectBasis(double mouseX, double mouseY) {
        if(MathUtils.dist(mouseX, mouseY, a, c) < radius) {
            xBasisSelected = true;
        } else if(MathUtils.dist(mouseX, mouseY, b, d) < radius) {
            yBasisSelected = true;
        }
    }

    /**
     * moves base which is selected to current mouse position.
     * @param mouseX - x coordinate of point where lays the mouse (in simulation units not real pixel values)
     * @param mouseY - y coordinate of point where lays the mouse (in simulation units not real pixel values)
     */
    void moveBase(double mouseX, double mouseY) {
        if(xBasisSelected) {
            a = mouseX;
            c = mouseY;
            computeEigenvectorsAndValues();
        } else if(yBasisSelected) {
            b = mouseX;
            d = mouseY;
            computeEigenvectorsAndValues();
        }
    }

    /**
     * draws two vectors representing the basis of this matrix.
     * @param plane - cartesian plane
     * @param circleAtTheEnd - if true then at the end of vectors small circles are drawn
     */
    void drawBasis(CoordinateSystem plane, boolean circleAtTheEnd) {
        DrawUtils.g2.setColor(DrawUtils.red);
        plane.drawVector(a, c);
        if(circleAtTheEnd) DrawUtils.circle(plane.screenX(a), plane.screenY(c), plane.scale*radius);

        DrawUtils.g2.setColor(DrawUtils.green);
        plane.drawVector(b, d);
        if(circleAtTheEnd) DrawUtils.circle(plane.screenX(b), plane.screenY(d), plane.scale*radius);
    }

    /**
     * Draws eigenvectors as a vector scaled by its eigenvalue.
     * @param plane - current cartesian plane
     */
    void drawEigenvectors(CoordinateSystem plane) {
        DrawUtils.g2.setColor(DrawUtils.fontColor);
        DrawUtils.g2.setStroke(new BasicStroke(2));

        plane.drawVector(eigenvectorsAndValues[0], eigenvectorsAndValues[1]);
        plane.drawVector(eigenvectorsAndValues[2], eigenvectorsAndValues[3]);
    }

    /**
     * Draws x and y axis as lines. X-axis is red and Y-axis is blue.
     * @param plane - current cartesian plane
     */
    void drawAxes(CoordinateSystem plane) {
        DrawUtils.g2.setStroke(new BasicStroke(3));
        DrawUtils.g2.setColor(DrawUtils.red);
        plane.drawStraightLine(c/a, 0);
        DrawUtils.g2.setColor(DrawUtils.green);
        plane.drawStraightLine(d/b, 0);
    }

    /**
     * Draws basis of transposition of this matrix using @code{drawOtherMatrixBasis}.
     * color of these basis is blue and at the end of each base there are small circles.
     * red circle ends the x-base and blue ends the y-base.
     * @param plane - current cartesian plane
     */
    void drawTranspose(CoordinateSystem plane) {
        DrawUtils.g2.setColor(DrawUtils.lightBlue);
        DrawUtils.g2.setStroke(new BasicStroke(2));
        drawOtherMatrixBasis(transpose(), plane);
    }

    /**
     * Draws basis of inversion of this matrix using @code{drawOtherMatrixBasis}.
     * color of these basis is yellow and at the end of each base there are small circles.
     * red circle ends the x-base and blue ends the y-base.
     * @param plane - current cartesian plane
     */
    void drawInverse(CoordinateSystem plane) {
        DrawUtils.g2.setColor(DrawUtils.gold);
        DrawUtils.g2.setStroke(new BasicStroke(2));
        drawOtherMatrixBasis(inverse(), plane);
    }

    /**
     * Draws vectors representing the basis of some matrix. At the end of these basis two circles are drawn
     * to distinguish x-base from y-base. x-base has red circle and y-base has blue one.
     * @param matrix - matrix to be drawn
     * @param plane - current cartesian plane
     */
    private void drawOtherMatrixBasis(Matrix2x2 matrix, CoordinateSystem plane) {
        plane.drawVector(matrix.a, matrix.c);
        plane.drawVector(matrix.b, matrix.d);

        DrawUtils.g2.setColor(DrawUtils.red);
        DrawUtils.circle(plane.screenX(matrix.a), plane.screenY(matrix.c), radius*plane.scale);
        DrawUtils.g2.setColor(DrawUtils.green);
        DrawUtils.circle(plane.screenX(matrix.b), plane.screenY(matrix.d), radius*plane.scale);
    }

    /**
     * draws every vector (line) that satisfy Ax = lx. If eigenvalues are imaginary or matrix is singular
     * then draws nothing
     * @param plane - current cartesian plane
     */
    void drawEigenvectorsLines(CoordinateSystem plane) {
        if(eigenvectorsAndValues[0] == 0 || eigenvectorsAndValues[2] == 0) {
            return; // matrix is singular or has imaginary eigenvalues
        }
        DrawUtils.g2.setColor(DrawUtils.fontColor);
        DrawUtils.g2.setStroke(new BasicStroke(2));
        plane.drawStraightLine(eigenvectorsAndValues[1]/eigenvectorsAndValues[0], 0);
        plane.drawStraightLine(eigenvectorsAndValues[3]/eigenvectorsAndValues[2], 0);
    }

    /**
     * draws every vector (line) that satisfy Ax = lx. If eigenvalues are imaginary or matrix is singular
     * then draws nothing
     * @param plane - current cartesian plane
     */
    void drawEigenvectorLineWithBiggerEigenvalue(CoordinateSystem plane) {
        if(eigenvectorsAndValues[0] == 0 && eigenvectorsAndValues[2] == 0) {
            return; // matrix is singular or has imaginary eigenvalues
        }
        DrawUtils.g2.setColor(DrawUtils.fontColor);
        DrawUtils.g2.setStroke(new BasicStroke(2));
        if(eigenvectorsAndValues[4] > eigenvectorsAndValues[5]) {
            plane.drawStraightLine(eigenvectorsAndValues[1]/eigenvectorsAndValues[0], 0);
        } else {
            plane.drawStraightLine(eigenvectorsAndValues[3]/eigenvectorsAndValues[2], 0);
        }
    }

    /**
     * draws a parallelogram which two of its sides are basis of matrix and the other two are parallel to them.
     * Area of this quadrangle equals to absolute value of determinant of this matrix.
     * @param plane - current cartesian plane
     */
    void drawDeterminant(CoordinateSystem plane) {
        DrawUtils.g2.setColor(DrawUtils.transparentYellow);
        DrawUtils.g2.fillPolygon(
                new int[] {(int)plane.screenX(0), (int)plane.screenX(a), (int)plane.screenX(a+b), (int)plane.screenX(b)},
                new int[]{(int)plane.screenY(0), (int)plane.screenY(c), (int)plane.screenY(c+d), (int)plane.screenY(d)}, 4);
    }

    /**
     * Draws (using drawParallelLines function) a bunch of lines parallel to the axis of this matrix and
     * separated from each other by length of the base of this matrix.
     * Draws only lines which are visible on the screen.
     * lines parallel to x-axis are reddish and ones which are parallel to y-axis are greenish
     * @param scale - scale in the cartesian plane simulation
     * @param camera - point where lies top left corner of the screen in cartesian plane simulation units.
     * @param plane - current cartesian plane
     */
    void drawGrid(double scale, Point2D camera, CoordinateSystem plane) {
        if(almostSingular()) {
            return;
        }
        DrawUtils.g2.setStroke(new BasicStroke(1));

        DrawUtils.g2.setColor(DrawUtils.transparentRed);
        double direction = c/a;
        double y_intercept = abs(b*c/a-d);
        drawParallelLines(direction, y_intercept, camera, scale, plane);

        DrawUtils.g2.setColor(DrawUtils.transparentGreen);
        direction = d/b;
        y_intercept = abs(c-a*d/b);
        drawParallelLines(direction, y_intercept, camera, scale, plane);
    }

    /**
     * Draws lines with given slope separated from each other by yInterceptIncrement.
     * Draws only these lines that appear on the screen.
     *
     * @param slope - slope of each drawn line (since all of them are parallel)
     * @param yInterceptIncrement - distance between closest lines
     * @param scale - scale in the cartesian plane simulation
     * @param camera - point where lies top left corner of the screen in cartesian plane simulation units.
     * @param plane - current cartesian plane
     */
    private void drawParallelLines(double slope, double yInterceptIncrement, Point2D camera, double scale,
                                   CoordinateSystem plane) {

        if(Double.isInfinite(slope)) {
            // is a is infinite draws parallel vertical lines
            for (int i = (int) ceil(camera.x); i < ceil(camera.x) + floor(plane.width / scale) + 1; i++) {
                DrawUtils.line((i - camera.x) * scale, 0, (i - camera.x) * scale, plane.height);
            }
        } else {
            double bAtStartingPoint, bAtEndingPoint;

            if(slope >= 0) { // when a>0 starting point is at the bottom right corner
                             // and ending point is at the top left corner of the screen
                bAtStartingPoint = camera.getY() - plane.height / scale - slope * (camera.getX() + plane.width / scale);
                bAtEndingPoint = camera.getY() - slope * camera.getX();
            } else { // when a<0 starting point is at the bottom right corner and ending point
                     // is at the top right corner of the screen
                bAtStartingPoint = camera.getY() - plane.height/scale - slope*camera.getX();
                bAtEndingPoint = camera.getY() - slope*(camera.getX() + plane.width/scale);
            }

            for(int i = (int)ceil(bAtStartingPoint/yInterceptIncrement);
                    i <= floor(bAtEndingPoint/yInterceptIncrement); i++) {
                plane.drawStraightLine(slope, i*yInterceptIncrement);
            }
        }
    }
}
