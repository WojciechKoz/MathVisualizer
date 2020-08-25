import java.awt.*;

import static java.lang.StrictMath.min;

/**
 *  class used in menus and sideMenus (in CartesianPlane)
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

        // standard colors for background and foreground (it might be changed while the program is running)
        backgroundCol = new Color(50, 50, 50);
        textCol = new Color(251, 139, 36);

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
}


/**
 * The most default version of the button. Used in menus and sideMenus.
 * It doesn't have any onClicked method, however when it is clicked, upper layer (menu, sideMenu) gets
 * its title value and performs some action depending of that value.
 * That class supports hover colors.
 */
class ClickableButton extends Button {
    protected final Color backgroundBase, backgroundHover, backgroundOnClicked, textBase, textHover, textOnClicked;

    ClickableButton(int x, int y, int width, int height, String label, int fontSize) {
        super(x, y, width, height, label, fontSize);

        // sets all default colors
        backgroundBase = new Color(50,50,50);
        backgroundHover = new Color(251, 139, 36);
        backgroundOnClicked = new Color(50, 50, 50);

        textBase = new Color(251, 139, 36);
        textHover = new Color(0,0,0);
        textOnClicked = new Color(150, 150, 150);
    }

    /**
     * Draws the rectangle using parent's draw method and in the middle of the button prints label
     * @param g2 - object for drawing on the screen and responsible for the graphics
     */
    void draw(Graphics2D g2) {
        super.draw(g2);

        g2.setColor(textCol);
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));
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
}


/**
 * Button with slider inside. Slider is moved by the mouse.
 * Can be used for real or integer values (although @value is always a real number, @discrete variable
 * tells us if casting to an integer when displaying is necessary)
 * If @value = @lowerBound then slider is on the left hand side,
 * and if @value = @upperBound then slider is on the right side
 * All other @value values place slider somewhere in the middle of the bar
 */
class Slider extends Button {
    private final double lowerBound, upperBound;
    private double value;
    private final boolean discrete;

    Slider(int x, int y, int width, int height, String label, int fontSize, double lowerBound, double upperBound, boolean discrete) {
        super(x, y, width, height, label, fontSize);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.discrete = discrete;

        // by default value is set to 50%
        value = (upperBound - lowerBound)/2;
    }

    /**
     * draws a rectangle using parent's draw method. @see Button#draw(Graphics2D g2)
     * In the upper half of the button, draws label with its value and in the lower half draws bar
     * @param g2 - object for drawing on the screen and responsible for the graphics
     */
    @Override
    void draw(Graphics2D g2) {
        super.draw(g2);

        g2.setColor(textCol);
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));

        // prints label+": "+roundedValue where roundedValue has two decimal places if discrete variable is false
        // or int(value) if discrete is true
        String fullLabel = label+": " + (discrete ? Integer.toString((int)(value)) : MathUtils.round(value, 2));
        DrawUtils.drawCenteredString(fullLabel, x+width/2, y+height/4);

        // draws a bar
        int sliderWidth = width/20;
        int position = (int)((value-lowerBound)/(upperBound-lowerBound)*0.8*width);
        g2.setColor(new Color(0,0,0));
        g2.drawLine(x+width/10, (int)(y+height*0.6), (int)(x+width*0.9), (int)(y+height*0.6));
        g2.setColor(textCol);
        g2.fillRect(x+width/10+position-sliderWidth/2, (int)(y+height*0.54), sliderWidth, (int)(0.15*height));
    }

    /**
     * Empty method - sliderButton doesn't support hovering
     * TODO fill this function like in ClickableButton (maybe another parent class HoveredButton)
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @param pressed - true when left mouse button is pressed otherwise false
     */
    @Override
    void setHover(double mouseX, double mouseY, boolean pressed) {

    }

    /**
     * Checks whether mouse is close to the bar.
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @return true if mouse is close to bar
     */
    @Override
    boolean hasInside(double mouseX, double mouseY) {
        return mouseX > x && mouseX < x+width && mouseY > y+height/3.0 && mouseY < y+height;
    }

    /**
     * sets {@code value} to a number between {@code lowerBound} and {@code upperBound}
     * depending on current mouse x position
     * @param mouseX - x position of mouse (in pixels)
     */
    void setValue(double mouseX) {
        double beginOfBar = x+0.1*width;
        double endOfBar = x+0.9*width;
        double position = MathUtils.clamp(mouseX, beginOfBar, endOfBar);
        double percentValue = (position-beginOfBar)/(endOfBar-beginOfBar);
        value = lowerBound + percentValue*(upperBound-lowerBound);
    }

    double getValue() { return value; }
}


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
}


/**
 * Class used only in sideMenus in CartesianPlane. Shows current coordinate of sample and its color.
 */
class SampleLabelButton extends Button {
    Sample sample;

    SampleLabelButton(int x, int y, int width, int height, Sample sample, int fontSize) {
        super(x, y, width, height, "", fontSize);
        this.sample = sample;
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
        DrawUtils.circle(x+width/8.0, y+height/2.0, min(width/10, height/4));

        // prints label with its
        g2.setColor(textCol);
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));
        label = "X: "+MathUtils.round(sample.getX(),2)+", Y: "+MathUtils.round(sample.getY(),2);
        DrawUtils.drawStringWithRightAlignment(label, (int)(x+width*0.95), y+height/2);
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

    public Sample getSample() {
        return sample;
    }
}


/**
 * Button that shows components of 2x2 matrix. Used only in sideMenus in cartesianPlane where there is some matrix.
 * Doesn't support hovering.
 */
class MatrixLabelButton extends Button {
    Matrix2x2 matrix;

    MatrixLabelButton(int x, int y, int width, int height, Matrix2x2 mat, int fontSize) {
        super(x, y, width, height, "", fontSize);
        matrix = mat;
    }

    /**
     * Draws button with matrix components.
     * @param g2 - object for drawing on the screen and responsible for the graphics
     */
    @Override
    void draw(Graphics2D g2) {
        super.draw(g2);

        g2.setColor(textCol);

        // value to accumulate all labels widths to avoid writing multiple strings in the same place
        // the initial offset is 4% of button's width
        int labelWidth = width/25;

        // label to be drawn
        String text;

        // first, prints matrix name and equal sign (and update labelWidth at the end)
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));
        text = matrix.getName() + " = ";
        DrawUtils.drawStringWithLeftAlignment(text, x+labelWidth, y+height/2);
        labelWidth += DrawUtils.stringWidth(text);

        // prints big square bracket representing matrix
        DrawUtils.setFont(new Font("TimesRoman Plain", Font.PLAIN, fontSize*4));
        text = "[";
        DrawUtils.drawStringWithLeftAlignment(text, x+labelWidth, (int)(y+height*0.4));
        labelWidth += DrawUtils.stringWidth(text);

        // sets color to color of matrix's x-axis
        g2.setColor(new Color(255, 100, 100));

        // prints first column of matrix
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));
        text = Double.toString(MathUtils.round(matrix.a, 2));
        DrawUtils.drawStringWithLeftAlignment(text, x+labelWidth, (int)(y+height*0.3));
        text = Double.toString(MathUtils.round(matrix.c, 2));
        DrawUtils.drawStringWithLeftAlignment(text, x+labelWidth, (int)(y+height*0.7));

        // sets color to color of matrix's y-axis
        g2.setColor(new Color(100, 255, 100));

        // prints second column of the matrix (always starts with the same X) and
        // labelWidth is no longer necessary)
        text = Double.toString(MathUtils.round(matrix.b, 2));
        DrawUtils.drawStringWithLeftAlignment(text, (int)(x+width*0.6), (int)(y+height*0.3));
        text = Double.toString(MathUtils.round(matrix.d, 2));
        DrawUtils.drawStringWithLeftAlignment(text, (int)(x+width*0.6), (int)(y+height*0.7));

        // prints right bracket of the matrix
        g2.setColor(textCol);
        DrawUtils.setFont(new Font("TimesRoman Plain", Font.PLAIN, fontSize*4));
        DrawUtils.drawStringWithRightAlignment("]", (int)(x+width*0.95), (int)(y+height*0.4));
    }

    /**
     * Empty method - this class doesn't support hovering
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @param pressed - true when left mouse button is pressed otherwise false
     */
    @Override
    public void setHover(double mouseX, double mouseY, boolean pressed) {

    }

    /**
     * Always returns false
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @return false
     */
    @Override
    boolean hasInside(double mouseX, double mouseY) {
        return false;
    }
}

/**
 * LabelButton with pair of title and value
 * title is a constant string to identify this button and to be drawn in button
 * to explain what the value is. Value can be any String (usually number convert to be a string) and it can be updated
 * This class inherit most method from LabelButton (doesn't support hovering either)
 */
class ValueLabelButton extends LabelButton {
    private final String title;
    private String value;

    ValueLabelButton(int x, int y, int width, int height, String title, String value, int fontSize) {
        super(x, y, width, height, title+": "+value, fontSize);
        this.title = title;
        this.value = value;
    }

    /**
     * Updates values of {@code value} and {@code label} variables
     * @param newValue - it's a new value of {@code value} variable
     */
    void updateValue(String newValue) {
        value = newValue;
        label = title+": "+value;
    }

    public String getTitle() { return title; }
}