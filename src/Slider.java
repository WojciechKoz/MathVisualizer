import java.awt.*;

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