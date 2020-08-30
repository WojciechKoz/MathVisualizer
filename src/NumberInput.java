import java.awt.*;
import java.awt.event.KeyEvent;
import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class NumberInput {
    String text;
    SampleLabelButton parent;
    int x, y, width, height;
    Color foreground, background, backgroundActive;
    boolean selected, available;
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

    boolean hasInside(double mouseX, double mouseY) {
        return x < mouseX && mouseX < x+width && y < mouseY && mouseY < y+height;
    }

    void onLeftClick(double mouseX, double mouseY) {
        selected = available && hasInside(mouseX, mouseY);
    }

    void onKeyPressed(KeyEvent event) {
        if(!selected) return;

        int code = event.getKeyCode();

        if(code == 8) {
            text = text.substring(0, max(0,caret-1))+text.substring(caret);
            caret = max(0, caret-1);
        } else if(code == 37) {
            caret = max(0, caret - 1);
        } else if(code == 39) {
            caret = min(text.length(), caret + 1);
        } else if(code == 10) {
            selected = false;
            caret = text.length();
        } else if(45 <= code && code <= 57 && code != 47){
            text = text.substring(0, caret) + event.getKeyChar() + text.substring(caret);
            caret += 1;
        }

        update();
    }

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

    void updateFromButton(double newValue) {
        if(!selected) {
            value = newValue;
            text = Double.toString(value);
            caret = text.length();
        }
    }

    void setColors(Color textCol, Color background) {
        foreground = textCol;
        this.background = background;
    }
}