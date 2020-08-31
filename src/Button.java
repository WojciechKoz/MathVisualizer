import java.awt.*;

/**
 *  class used in menus and sideMenus (in CoordinateSystem)
 *  Its children can be used like a normal buttons, check buttons,
 *  sliders or even as a label between other buttons in menu
 *  It's drawn as a rectangle with given x, y, width, height and color
 *  Things drawn inside rectangle are different for various subclasses
 */
public abstract class Button {
    protected int x, y, width, height, fontSize;
    protected String label;
    protected Color backgroundCol, textCol; // current background and text colors (could be change in some subclasses)
    protected int visibility; // integer value between 0 and 255 represents alpha value in RGBA

    Button(int x, int y, int width, int height, String label, int fontSize) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;

        this.fontSize = fontSize;

        // standard colors for background and foreground (they might be changed while the program is running)
        backgroundCol = DrawUtils.darkGray;
        textCol = DrawUtils.orange;

        // by default visible has maximum value (it changes while scrolling in menu)
        visibility = 255;
    }

    /**
     * checks (using hasInside method) if mouse is inside the button and if so,
     * does various things like change background and foreground colors
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @param pressed - true when left mouse button is pressed otherwise false
     */
    abstract void setHover(double mouseX, double mouseY, boolean pressed);

    /**
     * checks if mouse is inside the button (or inside the active part of that button)
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @return true if mouse is inside some specific area (for normal buttons it's a whole button but
     * e.g. in sliders case it's a slider bar)
     */
    abstract boolean hasInside(double mouseX, double mouseY);

    /**
     * draws a rectangle (symbolizing a button) coloured in a current background color
     * @param g2 - object for drawing on the screen and responsible for the graphics
     */
    void draw(Graphics2D g2) {
        g2.setColor(backgroundCol);
        g2.fillRect(x, y, width, height);
    }

    /**
     * method used in menus to change visibility of the button if it's outside the center of the screen
     * if y < min which means that button is above the central area its visibility is a percent value of the
     * button which is still under that line
     * if y+height > max which means that button is under the central area, its visibility is a percent value of the
     * button which is still above that line
     *
     * @param min - value represents top border of the central area of the screen ( if button is partially above that
     *            line then its visibility decreasing )
     * @param max - value represents bottom border of the central area of the screen ( if the button is partially
     *            under that line then its visibility decreasing )
     */
    public void setTransparency(double min, double max) {
        visibility = 255;
        if(y < min) {
            visibility = (int)(255*(1-MathUtils.clamp((min-y)/height, 0, 1)));
        } else if(y+height > max) {
            visibility = (int)(255*(1-MathUtils.clamp((y+height-max)/height, 0, 1)));
        }
        updateVisibility();
    }

    /**
     * Method that runs when button is clicked.
     * For buttons that have corresponding action in CoordinateSystem that method returns label of pressed button
     * so CoordinateSystem is able to distinguish which button was pressed. For other buttons like
     * for example sliders it does some changes (change slider value in sliders case) and returns an empty string
     * @param mouseX - current x position of mouse
     * @param mouseY - current y position of mouse
     * @return - label or empty string
     */
    abstract String onClicked(double mouseX, double mouseY);

    /**
     * method used after changes of visibility or colors
     * RGB values stays the same, but backgroundCol and textCol are updated by visibility
     */
    protected void updateVisibility() {
        backgroundCol = new Color(backgroundCol.getRed(), backgroundCol.getGreen(), backgroundCol.getBlue(), visibility);
        textCol = new Color(textCol.getRed(), textCol.getGreen(), textCol.getBlue(), visibility);
    }

    String getLabel() { return label; }

    public double getHeight() { return height; }

    public void setY(double newY) { y = (int)newY; }

    public double getY() { return y; }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }
}




