import java.awt.*;

/**
 * This class is only to print some text between other buttons in the menu.
 * Doesn't support Hovering and doesn't check whether mouse is inside this button.
 * Used for separate some groups of buttons from the others in the menu.
 */
class LabelButton extends Button {
    LabelButton(int x, int y, int width, int height, String label, int fontSize) {
        super(x, y, width, height, label, fontSize);
    }

    /**
     * Draws the rectangle using parent's draw method and in the middle of the button prints label
     * @param g2 - object for drawing on the screen and responsible for the graphics
     */
    @Override
    void draw(Graphics2D g2) {
        super.draw(g2);

        g2.setColor(textCol);
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));
        DrawUtils.drawCenteredString(label, x+width/2, y+height/2);
    }

    /**
     * Empty method - this class doesn't support hovering
     * @param mouseX - x position of mouse (in pixels) unused
     * @param mouseY - y position of mouse (in pixels) unused
     * @param pressed - true when left mouse button is pressed otherwise false, unused
     */
    @Override
    void setHover(double mouseX, double mouseY, boolean pressed) {

    }

    /**
     * Always returns false, since button doesn't change anytime
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @return {@code false}
     */
    @Override
    boolean hasInside(double mouseX, double mouseY) {
        return false;
    }

    /**
     * Since this class is only a label then returns an empty string when is clicked
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