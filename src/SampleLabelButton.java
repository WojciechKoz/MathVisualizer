import java.awt.*;
import java.awt.event.KeyEvent;

import static java.lang.StrictMath.min;

/**
 * TODO docs of this class are outdated
 * Class used only in sideMenus in CartesianPlane. Shows current coordinate of sample and its color.
 */
class SampleLabelButton extends Button {
    Sample sample;
    NumberInput inputForX, inputForY;

    SampleLabelButton(int x, int y, int width, int height, Sample sample, int fontSize, boolean available) {
        super(x, y, width, height, "", fontSize);
        this.sample = sample;

        inputForX = new NumberInput((int) (x+width*0.3), y, (int)(width*0.3), height,
                MathUtils.round(sample.x,3), this, available,
                textCol, new Color(0,0,0,0), new Color(100, 50, 50));

        inputForY = new NumberInput((int)(x+width*0.7), y, (int)(width*0.3), height,
                MathUtils.round(sample.y, 3), this, available,
                textCol, new Color(0,0,0,0), new Color(50, 100, 50));
    }

    /**
     * Draws rectangle symbolizing the button, small circle inside (using color of corresponding sample)
     * and label with coordinates of sample
     * @param g2 - object for drawing on the screen and responsible for the graphics
     */
    @Override
    void draw(Graphics2D g2) {
        // draws button background
        super.draw(g2);

        // draws small circle symbolizing chosen sample
        g2.setColor(sample.getColor());
        DrawUtils.circle(x+width/10.0, y+height/2.0, min(width/12, height/4));

        inputForX.updateFromButton(MathUtils.round(sample.x, 3));
        inputForY.updateFromButton(MathUtils.round(sample.y, 3));

        // prints labels
        g2.setColor(textCol);
        DrawUtils.setFont(new Font("David Bold", Font.PLAIN, fontSize));
        DrawUtils.drawStringWithLeftAlignment("X:", x+width/5, y+height/2);
        inputForX.draw(g2);
        g2.setColor(textCol);
        DrawUtils.drawStringWithLeftAlignment("Y:", (int)(x+width*0.6), y+height/2);
        inputForY.draw(g2);
    }

    /**
     * If mouse is over the sample then background and text colors swap
     * @param mouseX - x coordinate in CartesianPlane pointed by the mouse (not real mouse x position)
     * @param mouseY - y coordinate in CartesianPlane pointed by the mouse (not real mouse y position)
     */
    public void hoverFromSample(double mouseX, double mouseY) {
        if(sample.hasInside(mouseX, mouseY)) {
            backgroundCol = new Color(251, 139, 36);
            textCol = new Color(50, 50, 50);
        } else {
            backgroundCol = new Color(50, 50, 50);
            textCol = new Color(251, 139, 36);
        }
        inputForX.setColors(textCol, backgroundCol);
        inputForY.setColors(textCol, backgroundCol);
    }

    /**
     * Sets {@code Sample#selected} value to true if mouse is over the button.
     * If {@code Sample#selected} is true then an orange ring is drawn around the sample.
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @param pressed - true when left mouse button is pressed otherwise false, unused
     */
    @Override
    public void setHover(double mouseX, double mouseY, boolean pressed) {
        sample.select(hasInside(mouseX, mouseY));
        if(pressed) {
            inputForX.onLeftClick(mouseX, mouseY);
            inputForY.onLeftClick(mouseX, mouseY);
        }
    }

    /**
     * Checks if mouse is inside this button.
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @return true if mouse is inside this button.
     */
    @Override
    boolean hasInside(double mouseX, double mouseY) {
        return mouseX > x && mouseX < x+width && mouseY > y && mouseY < y+height;
    }

    boolean onKeyPressed(KeyEvent event) {
        inputForX.onKeyPressed(event);
        inputForY.onKeyPressed(event);
        return inputForX.selected || inputForY.selected;
    }

    void onLeftClick(double mouseX, double mouseY) {
        inputForX.onLeftClick(mouseX, mouseY);
        inputForY.onLeftClick(mouseX, mouseY);
    }

    void updateFromInput() {
        sample.instantMove(inputForX.value, inputForY.value);
    }

    public Sample getSample() {
        return sample;
    }

    public void setY(double newY) {
        y = (int)newY;
        inputForX.y = y;
        inputForY.y = y;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;

        inputForX.x = x;
        inputForY.x = x;

        inputForX.y = y;
        inputForY.y = y;
    }

    @Override
    public String onClicked(double mouseX, double mouseY) {
        return "";
    }
}