import java.awt.*;
import java.awt.event.KeyEvent;
import static java.lang.StrictMath.min;

/**
 * Class used only in sideMenus in CartesianPlane. Shows current coordinate of sample and its color.
 * This button has two inputField that support moving the sample by typing its exact coordinates from the keyboard.
 */
class SampleLabelButton extends Button {
    Sample sample;
    NumberInput inputForX, inputForY;

    SampleLabelButton(int x, int y, int width, int height, Sample sample, int fontSize, boolean available) {
        super(x, y, width, height, "", fontSize);
        this.sample = sample;

        // initializes two inputs. First is for x coordinate and second is for y coordinate
        inputForX = new NumberInput((int) (x+width*0.3), y, (int)(width*0.3), height,
                MathUtils.round(sample.x,3), this, available,
                textCol, DrawUtils.transparent, DrawUtils.darkRed);

        inputForY = new NumberInput((int)(x+width*0.7), y, (int)(width*0.3), height,
                MathUtils.round(sample.y, 3), this, available,
                textCol, DrawUtils.transparent, DrawUtils.darkGreen);
    }

    /**
     * Draws rectangle symbolizing the button, small circle inside (using color of corresponding sample)
     * and two input fields
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

        // prints labels with input fields
        g2.setColor(textCol);
        DrawUtils.setFont(new Font("David Bold", Font.PLAIN, fontSize));
        DrawUtils.drawStringWithLeftAlignment("X:", x+width/5, y+height/2);
        inputForX.draw(g2);
        g2.setColor(textCol);
        DrawUtils.drawStringWithLeftAlignment("Y:", (int)(x+width*0.6), y+height/2);
        inputForY.draw(g2);
    }

    /**
     * If mouse is over the sample then background and text colors swap.
     * IT changes the colors of input fields as well
     * @param mouseX - x coordinate in CartesianPlane pointed by the mouse (not real mouse x position)
     * @param mouseY - y coordinate in CartesianPlane pointed by the mouse (not real mouse y position)
     */
    public void hoverFromSample(double mouseX, double mouseY) {
        if(sample.hasInside(mouseX, mouseY)) {
            backgroundCol = DrawUtils.orange;
            textCol = DrawUtils.darkGray;
        } else {
            backgroundCol = DrawUtils.darkGray;
            textCol = DrawUtils.orange;
        }
        inputForX.setColors(textCol, backgroundCol);
        inputForY.setColors(textCol, backgroundCol);
    }

    /**
     * Sets {@code Sample#selected} value to true if mouse is over the button.
     * If {@code Sample#selected} is true then an orange ring is drawn around the sample.
     * If the mouse button is down, it sets the accessibility of the
     * input fields by checking if the mouse is inside them.
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

    /**
     * Function that runs when some key was pressed. It passes the information about this pressing
     * to input fields and return true if at least one of them (actually it's not possible that both) is selected.
     * That means that the key pressed event might changed the coordinates and the simulation has to be refreshed.
     * @param event - event that carries the information of the key pressing
     * @return - true if one of the input is selected (simulation has to be refreshed because this key pressed event
     *           might changed the coordiantes of some sample)
     */
    boolean onKeyPressed(KeyEvent event) {
        inputForX.onKeyPressed(event);
        inputForY.onKeyPressed(event);
        return inputForX.selected || inputForY.selected;
    }

    /**
     * Runs when left mouse button is down. Passes the information about it to input fields.
     * @param mouseX - current x coordinate of the mouse (in pixels)
     * @param mouseY - current y coordinate of the mouse (in pixels)
     */
    void onLeftClick(double mouseX, double mouseY) {
        inputForX.onLeftClick(mouseX, mouseY);
        inputForY.onLeftClick(mouseX, mouseY);
    }

    /**
     * If text in the input field is valid and the value is different than the real sample position
     * it updates the position of the sample. Runs from input fields.
     * TODO when more buttons will have their inputs it might be not the best name of update function.
     */
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

    /**
     * Since this class is only for displaying sample values, returns an empty string when is clicked
     * meaning that the upper layer doesn't perform any action after this button was pressed
     * @param mouseX - current x position of mouse
     * @param mouseY - current y position of mouse
     * @return - an empty string
     */
    @Override
    public String onClicked(double mouseX, double mouseY) {
        return "";
    }
}