import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KNNInterface {
    Sample predicted;
    ArrayList<Stretch> distances = new ArrayList<>();
    Ring ring;
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

    void drawDistances(Graphics2D g2, CartesianPlane plane, boolean drawAll) {
        if(!(active || drawAll)) return;

        for(Stretch stretch: distances) {
            stretch.draw(g2, plane);
        }
    }

    void drawRing(Graphics2D g2, CartesianPlane plane, boolean drawAll) {
        if(!(active || drawAll)) return;

        ring.draw(g2, plane);
    }

    void onMouseMoved(double mouseX, double mouseY, CartesianPlane plane) {
        active = predicted.hasInside(plane.simulationX(mouseX), plane.simulationY(mouseY));
    }
}

class DistComparator implements Comparator<Sample> {
    private final Sample neutral;

    DistComparator(Sample neutral) {
        this.neutral = neutral;
    }

    @Override
    public int compare(Sample sample1, Sample sample2) {
        if(MathUtils.dist(sample1, neutral) > MathUtils.dist(sample2, neutral)){
            return 1;
        } else {
            return -1;
        }
    }
}