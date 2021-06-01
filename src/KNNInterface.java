import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Graphical interface that is related to one neutral sample.
 * It draws distances between that sample and its neighbours and ring whose
 * center is that neutral sample and radius equals to the biggest distance between it and
 * some sample from neighbours list
 */
public class KNNInterface {
    // sample that is the center of the ring and all distances starts from it
    Sample predicted;
    ArrayList<Stretch> distances = new ArrayList<>();
    Ring ring;
    // if mouse is above the predicted sample then ring and distances are drawn otherwise
    // these options have to be selected in the side menu
    boolean active;

    KNNInterface(Sample predicted, List<Sample> closest) {
        this.predicted = predicted;

        for(Sample neighbour: closest) {
            distances.add(new Stretch(this.predicted, neighbour, neighbour.getColor()));
        }
        Sample last = closest.get(closest.size()-1);
        ring = new Ring(predicted, MathUtils.dist(predicted, last), predicted.getPredictedColor());

        active = false;
    }

    /**
     * Draws all lines between this neutral sample and its neighbours.
     * Lines are drawn if the sample is under the mouse, is moving
     * or if that option was selected in the side menu.
     * @param plane - current working coordinate system
     * @param drawAll - option from side menu that allows drawing all distances from all neutral samples
     */
    void drawDistances(CoordinateSystem plane, boolean drawAll) {
        if(!(active || drawAll || predicted.isMoving())) return;

        for(Stretch stretch: distances) {
            stretch.draw(plane);
        }
    }

    /**
     * Draws a ring whose center is neutral sample and radius is the biggest distance between this sample
     * and some sample from neighbours list. Ring is drawn if this neutral sample is under the mouse,
     * is moving or is that option was selected in the side menu.
     * @param plane - current working coordinate system
     * @param drawAll - option from side menu that allows drawing all distances from all neutral samples
     */
    void drawRing(CoordinateSystem plane, boolean drawAll) {
        if(!(active || drawAll || predicted.isMoving())) return;

        ring.draw(plane);
    }

    /**
     * Checks whether the mouse is above the neutral sample and changes the active value
     * @param mouseX - current x coordinate of the mouse (in pixels)
     * @param mouseY - current y coordinate of the mouse (in pixels)
     * @param plane - current working Coordinate system
     */
    void onMouseMoved(double mouseX, double mouseY, CoordinateSystem plane) {
        active = predicted.hasInside(plane.simulationX(mouseX), plane.simulationY(mouseY));
    }
}

/**
 * Class that is used in KNN algorithm when training samples are sorted.
 */
class DistComparator implements Comparator<Sample> {
    private final Sample neutral;

    DistComparator(Sample neutral) {
        this.neutral = neutral;
    }

    /**
     * Compare two training samples which one is closest to the neutral sample.
     * @param sample1 - first training sample
     * @param sample2 - second training sample
     * @return 1 if second sample if closer or -1 of first is closer
     */
    @Override
    public int compare(Sample sample1, Sample sample2) {
        if(MathUtils.dist(sample1, neutral) > MathUtils.dist(sample2, neutral)){
            return 1;
        } else {
            return -1;
        }
    }
}