import java.awt.*;

/**
 * The most default version of the button. Used in menus and sideMenus.
 * when it is clicked, upper layer (menu, sideMenu) gets
 * its title value and performs some action depending of that value.
 * That class supports hover colors.
 */
class ClickableButton extends Button {
    protected final Color backgroundBase, backgroundHover, backgroundOnClicked, textBase, textHover, textOnClicked;

    ClickableButton(int x, int y, int width, int height, String label, int fontSize) {
        super(x, y, width, height, label, fontSize);

        // sets all default colors
        backgroundBase = backgroundCol;
        backgroundHover = textCol;
        backgroundOnClicked = backgroundCol;

        textBase = textCol;
        textHover = DrawUtils.black;
        textOnClicked = DrawUtils.lightGray;
    }

    /**
     * Draws the rectangle using parent's draw method and in the middle of the button prints centered label
     */
    void draw() {
        super.draw();

        DrawUtils.g2.setColor(textCol);
        DrawUtils.setFont(new Font(DrawUtils.regularFontName, Font.PLAIN, fontSize));
        DrawUtils.drawCenteredString(label, x+width/2, y+height/2);
    }

    /**
     * changes current background and text colors
     * if mouse is inside the button and left mouse button is not pressed
     * then {@code backgroundCol = backgroundHover; textCol = textHover}
     * if mouse is inside the button and left mouse button is pressed
     * then {@code backgroundCol = backgroundOnClicked; textCol = textOnClicked}
     * if mouse is not inside the button
     * then {@code backgroundCol = backgroundBase; textCol = textBase}
     * At the end of the function current colors are updated by visibility value
     *
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @param pressed - true when left mouse button is pressed otherwise false
     */
    void setHover(double mouseX, double mouseY, boolean pressed) {
        if(hasInside(mouseX, mouseY)) {
            if(pressed) {
                backgroundCol = backgroundOnClicked;
                textCol = textOnClicked;
            } else {
                backgroundCol = backgroundHover;
                textCol = textHover;
            }
        } else {
            backgroundCol = backgroundBase;
            textCol = textBase;
        }
        updateVisibility();
    }

    /**
     * checks if mouse is inside the button
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @return true if mouse is inside the button otherwise false
     */
    boolean hasInside(double mouseX, double mouseY) {
        return mouseX > x && mouseX < x+width && mouseY > y && mouseY < y+height;
    }

    /**
     * When button is pressed, it sends its label to the upper layer.
     * @param mouseX - current x position of mouse
     * @param mouseY - current y position of mouse
     * @return label of this button
     */
    @Override
    public String onClicked(double mouseX, double mouseY) {
        return getLabel();
    }
}