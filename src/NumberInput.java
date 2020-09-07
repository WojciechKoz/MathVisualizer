import java.awt.*;
import java.awt.event.KeyEvent;
import static java.lang.Integer.max;
import static java.lang.Integer.min;


/**
 * Class that is my implementation of TextField but it only has functions that are
 * useful in this program. As an input it takes only digits and minus and comma signs.
 * Has different background color when it is selected. Value that is connected with this
 * TextField is refreshing after every pressing of a valid button.
 *
 */
public class NumberInput {
    String text;
    // TODO matrix label buttons will be able to have numberInput as well
    SampleLabelButton parent;
    int x, y, width, height;
    Color foreground, background, backgroundActive;
    boolean selected, available;
    // position of the caret
    int caret;
    double value;

    NumberInput(int x, int y, int width, int height, double value, SampleLabelButton parent, boolean available,
                Color foreground, Color background, Color backgroundActive) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.foreground = foreground;
        this.background = background;
        this.backgroundActive = backgroundActive;
        this.value = value;
        this.available = available;

        text = Double.toString(value);
        selected = false;
        caret = text.length();
    }

    /**
     * Draws text field with value inside. Depending on selected value it paints
     * various background color. Cannot use parent method because the
     * alignment is to the left - not centered.
     * @param g2 - graphics engine
     */
    void draw(Graphics2D g2) {
        g2.setColor(selected ? backgroundActive : background);
        g2.fillRect(x, y, width, height);
        g2.setColor(foreground);
        DrawUtils.drawStringWithLeftAlignment(text, x+width/15, y+height/2);

        if(selected) {
            int caretX = x + width/15 + DrawUtils.stringWidth(text.substring(0,caret));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(caretX, y+height/4, caretX, y+3*height/4);
        }
    }

    /**
     * Checks if mouse is inside the NumberInput field.
     * @param mouseX - current x position of the mouse (in pixels)
     * @param mouseY - current y position of the mouse (in pixels)
     * @return true if mouse is inside NumberInput field, otherwise false
     */
    boolean hasInside(double mouseX, double mouseY) {
        return x < mouseX && mouseX < x+width && y < mouseY && mouseY < y+height;
    }

    /**
     * If the NumberInput is available (value can be changed through this input field)
     * and if the mouse is inside this field, selected variable will be equal to true.
     * @param mouseX - current x position of the mouse (in pixels)
     * @param mouseY - current y position of the mouse (in pixels)
     */
    void onLeftClick(double mouseX, double mouseY) {
        selected = available && hasInside(mouseX, mouseY);
    }

    /**
     * Checks which key was pressed and performs right action.
     * Some special characters like backspace and enter are also used.
     * Updates value of variable that is linked to this Input if value of input is correct.
     * @param event - object with all information about key event
     */
    void onKeyPressed(KeyEvent event) {
        if(!selected) return;

        final int BACKSPACE = 8;
        final int LEFT_ARROW = 37;
        final int RIGHT_ARROW = 39;
        final int ENTER = 10;
        // numeric char values are 0-9 "." and "-"
        final int FIRST_NUMERIC_CHAR_VALUE = 45;
        final int LAST_NUMERIC_CHAR_VALUE = 57;
        final int WRONG_NUMERIC_CHAR_VALUE = 47; // "/" character (not used in this input)

        int code = event.getKeyCode();

        if(code == BACKSPACE) {
            text = text.substring(0, max(0,caret-1))+text.substring(caret);
            caret = max(0, caret-1);
        } else if(code == LEFT_ARROW) {
            caret = max(0, caret - 1);
        } else if(code == RIGHT_ARROW) {
            caret = min(text.length(), caret + 1);
        } else if(code == ENTER) {
            selected = false;
            caret = text.length();
        } else if(FIRST_NUMERIC_CHAR_VALUE <= code && code <= LAST_NUMERIC_CHAR_VALUE &&
                code != WRONG_NUMERIC_CHAR_VALUE){
            text = text.substring(0, caret) + event.getKeyChar() + text.substring(caret);
            caret += 1;
        }

        update();
    }

    /**
     * Checks if it is possible to parse text value of input to real number.
     * If it is possible then updates variable that is connected to this input field.
     */
    void update() {
        try {
            value = Double.parseDouble(text);
        } catch(NumberFormatException nfe) {
            if(!selected) {
                text = Double.toString(value);
            }
        }
        parent.updateFromInput();
    }

    /**
     * If varaible connected to this input field was changed in other way then
     * the text value in this input field will also be changed.
     * @param newValue - new value of connected variable
     */
    void updateFromButton(double newValue) {
        if(!selected) {
            value = newValue;
            text = Double.toString(value);
            caret = text.length();
        }
    }

    /**
     * Sets colors 
     * @param textCol
     * @param background
     */
    void setColors(Color textCol, Color background) {
        foreground = textCol;
        this.background = background;
    }
}