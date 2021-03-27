import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.lang.Integer.min;

/**
 * Class that does exactly the same thing as ClickableButton but has a tick image to distinguish
 * whether corresponding logical value is on or off. Using to switch on/off some visual parts of simulation.
 */
public class CheckBoxButton extends ClickableButton {
    private boolean value;
    private static Image tick;

    CheckBoxButton(int x, int y, int width, int height, String label, int fontSize, boolean value) {
        super(x, y, width, height, label, fontSize);
        this.value = value;

        // when first CheckBoxButton is created tick image is loaded and it's available for all CheckBox buttons
        if(tick == null) {
            try {
                tick = ImageIO.read(new File("data/tick.png"));
            } catch (IOException e) {
                System.out.println("load failed");
            }
        }
    }

    /**
     * Draws buttons with a small square on the left side.
     * The square is empty if corresponding logical value is equal to false
     * otherwise inside that square the tick image is drawn.
     * Text alignment is to the left, NOT centered like in ClickableButton.
     * @param g2 - object for drawing on the screen and responsible for the graphics
     */
    @Override
    void draw(Graphics2D g2) {
        g2.setColor(backgroundCol);
        g2.fillRect(x, y, width, height);

        g2.setColor(textCol);
        DrawUtils.setFont(new Font(DrawUtils.regularFontName, Font.PLAIN, fontSize));
        DrawUtils.drawStringWithLeftAlignment(label, x+width/4, y+height/2);

        int side = min(3*height/4, width/8);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x+width/15, y+(height-side)/2, side, side);

        if(value) {
            g2.drawImage(tick, x+width/15, y+(height-side)/2, side, side,null);
        }
    }

    void toggleValue() {
        value = !value;
    }

    /**
     * toggle value responsible for drawing the tick image and returns the label (using method from ClickableButton)
     * @param mouseX - x coordinate of mouse
     * @param mouseY - y coordiante of mouse
     * @return - label of this button
     */
    @Override
    public String onClicked(double mouseX, double mouseY) {
        toggleValue();
        return super.onClicked(mouseX, mouseY);
    }
}
