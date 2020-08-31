import java.awt.*;
import java.util.ArrayList;

/**
 * Class that prints out some important information about current cartesian plane simulation in message window.
 */
public class MessageWindow {
    // current x and y position of the window and its width and height
    private int x;
    private int y;
    private final int width;
    private final int height;
    // height of the single line
    private int lineHeight;
    // y coordinate of first line of text - lowerBound
    private int scrollOffset;
    // height of all lines of text
    private int heightOfAllText;
    // max and min y of visibility lines
    private final int lowerBound, upperBound;
    private final ArrayList<String> text;
    private final String title;
    private final int fontSize;
    private final ArrayList<Button> buttons = new ArrayList<>();
    // visibility of message window. selected tells if window is dragged currently by the mouse
    private boolean visibility, selected;

    MessageWindow(double screenWidth, double screenHeight, String filename) {
        // initial position and size of message window
        x = (int) (0.58*screenWidth);
        y = (int) (0.05*screenHeight);
        width = (int) (0.4*screenWidth);
        height = (int) (0.65*screenHeight);
        fontSize = (int) (screenWidth/70.0);
        scrollOffset = 0;

        DrawUtils.setFont(new Font("Arial", Font.PLAIN, fontSize));
        text = TextManager.readMessageContent(width, filename);
        title = text.remove(0);

        DrawUtils.setFont(new Font("David bold", Font.PLAIN, (int) (fontSize*1.7)));
        upperBound = (int) (height*0.15 + DrawUtils.stringHeight(text.get(0))*1.2);
        lowerBound = (int) (height*0.95);

        buttons.add(new ClickableButton((int)(x+0.8*width), y,
                (int)(0.2*width), (int)(0.05*height), "Close", (int) (fontSize*1.2)));

        visibility = false;
        selected = false;

        findAverageLineHeight();
    }

    void findAverageLineHeight() {
        ArrayList<Double> heights = new ArrayList<>();
        DrawUtils.setFont(new Font("Arial", Font.PLAIN, fontSize));

        for(String s: text) {
            if(s.equals("")) continue;
            heights.add((double) DrawUtils.stringHeight(s));
        }
        lineHeight = (int) (1.2*MathUtils.mean(heights));
        heightOfAllText = lineHeight*text.size();
    }

    /**
     * Draws a rectangular window and prints text inside it.
     * @param g2 - graphics engine
     */
    void draw(Graphics2D g2) {
        if(!visibility) {
            return;
        }

        g2.setColor(DrawUtils.transparentBlack);
        g2.fillRect(x, y, width, height);

        g2.setColor(DrawUtils.orange);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(x, y, width, height);

        g2.setColor(DrawUtils.darkGray);
        g2.fillRect(x, y, width, (int)(0.05*height));
        g2.drawRect(x, y, width, (int)(0.05*height));

        for(Button b: buttons) {
            b.draw(g2);
        }

        g2.setColor(DrawUtils.white);
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, (int) (fontSize*1.7)));
        DrawUtils.drawCenteredString(title, x+width/2, (int) (y+0.15*height));


        double yOffset = y + upperBound + scrollOffset;
        DrawUtils.setFont(new Font("Arial", Font.PLAIN, fontSize));

        for (String s : text) {
            if(yOffset < y+upperBound) {
                yOffset += lineHeight;
                continue;
            }
            if(yOffset > y+lowerBound) {
                break;
            }

            if (s.equals("")) {
                yOffset += lineHeight;
                continue;
            }
            DrawUtils.drawStringWithLeftAlignment(s, (int) (x + width * 0.03), (int) (yOffset));
            yOffset += lineHeight;
        }
    }

    /**
     * checks if some button is under the mouse and changes its color.
     * Also if mouse is above the top bar of the window the selected variable is assign to true
     * and the window will start follow the mouse
     * @param mouseX - x coordinate of the mouse in pixels
     * @param mouseY - y coordinate of the mouse in pixels
     */
    void onLeftClick(double mouseX, double mouseY) {
        if(!visibility) return;

        for(Button b: buttons) {
            b.setHover(mouseX, mouseY, true);
        }

        if(hasInsideTheTitleBar(mouseX, mouseY)) {
            selected = true;
        }
    }

    /**
     * checks (only if visibility is on) whether some button is under the mouse and sets its color.
     * @param mouseX - x coordinate of the mouse in pixels
     * @param mouseY - y coordinate of the mouse in pixels
     */
    void onMouseMoved(double mouseX, double mouseY) {
        if(!visibility) return;

        for(Button b: buttons) {
            b.setHover(mouseX, mouseY, false);
        }
    }

    /**
     * Sets colors of buttons if they are under the mouse or moves the message window
     * @param mouseX - x coordinate of the mouse in pixels
     * @param mouseY - y coordinate of the mouse in pixels
     */
    boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        if(!visibility) return false;

        for(Button b: buttons) {
            b.setHover(mouseX, mouseY, true);
        }

        if(selected) {
            x += mouseX - prevMouseX;
            y += mouseY - prevMouseY;

            for(Button b: buttons) {
                b.move( mouseX - prevMouseX, mouseY - prevMouseY);
            }
            return true;
        }
        return false;
    }

    /**
     * moves the text toward the direction of scrolling.
     * @param rotation direction of scrolling. Can be either 1 [down] or -1 [up]
     */
    void onMouseScrolled(int rotation) {
        if(!visibility || heightOfAllText < lowerBound-upperBound) return;

        double factor = -20*rotation;

        // aligns the text to lower and upper bound
        if(scrollOffset+factor > 0) {
            factor = -scrollOffset;
        } else if(scrollOffset+heightOfAllText+upperBound+factor < lowerBound) {
            factor = lowerBound-scrollOffset-heightOfAllText-upperBound;
        }

        scrollOffset += factor;
    }

    /**
     * Checks if some button is under the mouse and if so performs corresponding action.
     * Only if visibility is on.
     * @param mouseX - x coordinate of the mouse in pixels
     * @param mouseY - y coordinate of the mouse in pixels
     */
    void onMouseReleased(double mouseX, double mouseY) {
        if(!visibility) return;

        for(Button b: buttons) {
            if(b.hasInside(mouseX, mouseY)) {
                buttonsOptions(b.getLabel());
                break;
            }
        }
        selected = false;
    }

    /**
     * Performs an action related with pressed button with given label
     * @param label - label of the pressed button
     */
    void buttonsOptions(String label) {
        switch(label) {
            case "Close": toggleVisibility(); break;
            case "Next": break; // TODO
        }
    }

    boolean hasInside(double mouseX, double mouseY) {
        return visibility && x < mouseX && mouseX < x+width && y < mouseY && mouseY < y+height;
    }

    boolean hasInsideTheTitleBar(double mouseX, double mouseY) {
        return x < mouseX && mouseX < x+0.8*width && y < mouseY && mouseY < y+0.05*height;
    }

    void toggleVisibility() {
        visibility = !visibility;
    }
}
