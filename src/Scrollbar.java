import java.awt.*;
import java.util.ArrayList;

/**
 * Class used in a menu, side menu and text windows. keeps track on bounds of available space and
 * current shift. Can handle transparency effect of buttons outside the box.
 * Changes state on mouse scrolled and scrollbar moved events.
 */
public class Scrollbar {
    private int highBound, lowBound;
    private int currentShift, totalHeight;
    private int x, y;
    private final int width, height;
    private boolean selected;

    Scrollbar(int hb, int lb, int total, int x, int y, int width, int height) {
        highBound = hb;
        lowBound = lb;
        totalHeight = total;
        currentShift = highBound;
        selected = false;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    void onMouseWheelMoved(ArrayList<? extends Container> containers, int rotation) {
        // scrolls 40 pixel per event
        double factor = -40*rotation;
        changeState(containers, (int)factor);
    }

    void onMouseDragged(ArrayList<? extends Container> containers, double mouseY, double prevMouseY) {
        selected = true;
        double factor = (prevMouseY - mouseY)*(double)totalHeight/height;
        changeState(containers, (int)factor);
    }

    private void changeState(ArrayList<? extends Container> containers, int factor) {
        Container last = containers.get(containers.size()-1);

        // checks if top of first container is under the screen top
        // or if bottom of last container is above the screen bottom
        // if so then aligns all containers to that bounds.
        if(containers.get(0).getY()+factor > highBound) {
            factor = highBound-containers.get(0).getY();
        } else if(last.getY()+last.getHeight()+factor < lowBound) {
            factor = lowBound - last.getY() - last.getHeight();
        }

        // moves each container by that factor
        for(Container container: containers) {
            container.setY(container.getY()+factor);
        }

        // update the offset
        currentShift = containers.get(0).getY();
    }

    void draw() {
        int availableHeight = lowBound - highBound;
        double drawnPart = (double)availableHeight/totalHeight;

        if(drawnPart >= 1) { return; }

        DrawUtils.g2.setColor(DrawUtils.secondaryColor);
        DrawUtils.g2.fillRect(x, y, width, height);

        double shiftOnBar = height*((double)(highBound - currentShift)/totalHeight);

        DrawUtils.g2.setColor(DrawUtils.primaryColor);
        DrawUtils.g2.fillRect(x, y + (int)shiftOnBar, width, (int)(drawnPart*height));
    }

    boolean hasInside(double mouseX, double mouseY) {
        return x < mouseX && mouseX < x+width && y < mouseY && mouseY < y+height;
    }

    void incrementTotalHeight(int value) {
        totalHeight += value;
    }

    void decrementTotalHeight(int value, ArrayList<? extends Container> containers) {
        totalHeight -= value;

        if(highBound + currentShift + totalHeight < lowBound)  {
            int newShift = 0;
            int factor = newShift - currentShift;
            currentShift = newShift;

            // moves each button by that factor
            for(Container container: containers) {
                container.setY(container.getY()+factor);
            }
        }
    }

    public void shiftEverything(int dx, int dy) {
        x += dx;
        y += dy;
        highBound += dy;
        lowBound += dy;
        currentShift += dy;
    }

    int getCurrentShift() {
        return currentShift;
    }

    int getTotalHeight() {
        return totalHeight;
    }

    boolean scrollable() {
        return (lowBound - highBound) < totalHeight;
    }

    int getWidth() {
        return width;
    }

    boolean getSelected() {
        return selected;
    }

    void setSelected(boolean value) {
        selected = value;
    }

}
