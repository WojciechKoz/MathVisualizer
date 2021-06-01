import java.awt.*;
import java.util.ArrayList;

/**
 * Class that prints out some important information about current coordinate system simulation in message window.
 */
public class MessageWindow {
    // current x and y position of the window and its width and height
    private int x;
    private int y;
    private final int width;
    private int height;
    // every message window has the same height of top bar
    private static final int heightOfTopBar = 35;
    // height of all lines of text
    private int heightOfAllText;
    // max and min y of visibility lines
    private int lowerBound, upperBound;
    private Scrollbar scrollbar;
    private final ArrayList<TextLine> text;
    private String title;
    private final int FONT_SIZE = 24;
    private final int HEADER_FONT_SIZE = 40;
    private final ArrayList<Button> buttons = new ArrayList<>();
    // visibility of message window. selected tells if window is dragged currently by the mouse
    private boolean visibility, selected;
    private final CoordinateSystem simulation;

    MessageWindow(CoordinateSystem sim, String filename) {
        simulation = sim;
        int screenWidth = sim.width;
        int screenHeight = sim.height;

        // initial position and size of message window
        x = (int) (0.58*screenWidth);
        y = (int) (0.05*screenHeight);
        width = (int) (0.4*screenWidth);
        height = (int) (0.65*screenHeight);
        upperBound = (int) (height*0.15 + 48);
        lowerBound = (int) (height*0.95);

        DrawUtils.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        ArrayList<String> lines = TextManager.readMessageContent(width, filename);
        title = lines.remove(0);

        text = TextManager.transformStringsToLines(lines, FONT_SIZE, (int)(x + 0.025*width), y + upperBound);

        buttons.add(new ClickableButton((int)(x+0.8*width), y,
                (int)(0.2*width), heightOfTopBar, StringsResources.close(), (int) (FONT_SIZE *1.2)));

        visibility = false;
        selected = false;

        heightOfAllText = calculateHeightOfText();

        if(upperBound + heightOfAllText < lowerBound) {
            lowerBound = upperBound+heightOfAllText;
            height = upperBound+heightOfAllText+(int)(0.1*height);
        }

        scrollbar = new Scrollbar(y + upperBound, y + lowerBound, heightOfAllText, x + width - 23, y + upperBound, 20, lowerBound - upperBound);
    }

    MessageWindow(int x, int y, int width, int height, CoordinateSystem sim) {
        simulation = sim;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        text = new ArrayList<>();
        upperBound = 0;
        lowerBound = height;

        visibility = false;
        selected = false;
        title = "";

        DrawUtils.setFont(new Font(DrawUtils.regularFontName, Font.PLAIN, (int) (FONT_SIZE *1.7)));
        buttons.add(new ClickableButton((int)(x+0.8*width), y,
                (int)(0.2*width), heightOfTopBar, StringsResources.close(), (int) (FONT_SIZE *1.2)));
    }

    int calculateHeightOfText() {
        int output = 0;
        for(TextLine line: text) {
            output += line.getHeight();
        }
        return output;
    }

    /**
     * Draws a rectangular window and prints text inside it.
     */
    void draw() {
        if(!visibility) {
            return;
        }

        DrawUtils.g2.setColor(DrawUtils.transparentBlack);
        DrawUtils.g2.fillRect(x, y, width, height);

        DrawUtils.g2.setColor(DrawUtils.primaryColor);
        DrawUtils.g2.setStroke(new BasicStroke(3));
        DrawUtils.g2.drawRect(x, y, width, height);

        DrawUtils.g2.setColor(DrawUtils.secondaryColor);
        DrawUtils.g2.fillRect(x, y, width, heightOfTopBar);
        DrawUtils.g2.drawRect(x, y, width, heightOfTopBar);

        for(Button b: buttons) {
            b.draw();
        }

        DrawUtils.g2.setColor(DrawUtils.white);
        DrawUtils.setFont(new Font(DrawUtils.regularFontName, Font.PLAIN, (int) (FONT_SIZE *1.7)));
        DrawUtils.drawCenteredString(title, x+width/2, (int) (y+1.5*heightOfTopBar + DrawUtils.stringHeight(title)));


        DrawUtils.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        for (TextLine line: text) {
            line.draw(y + upperBound, y + lowerBound);
        }

        if(scrollbar != null) {
            scrollbar.draw();
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

        if(scrollbar != null) {
            scrollbar.setSelected(scrollbar.hasInside(mouseX, mouseY));
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

        if(scrollbar != null && scrollbar.getSelected()) {
            scrollbar.onMouseDragged(text, mouseY, prevMouseY);
            return true;
        }

        if(selected) {
            x += mouseX - prevMouseX;
            y += mouseY - prevMouseY;

            for(Button b: buttons) {
                b.move( mouseX - prevMouseX, mouseY - prevMouseY);
            }
            for(TextLine line: text) {
                line.move((int)(mouseX - prevMouseX), (int)(mouseY - prevMouseY));
            }

            if(scrollbar != null) {
                scrollbar.shiftEverything((int)(mouseX - prevMouseX), (int)(mouseY - prevMouseY));
            }
            return true;
        }
        return hasInside(mouseX, mouseY);
    }

    /**
     * moves the text toward the direction of scrolling.
     * @param rotation direction of scrolling. Can be either 1 [down] or -1 [up]
     */
    void onMouseScrolled(int rotation) {
        if(!visibility || heightOfAllText < lowerBound-upperBound || scrollbar == null) return;

        scrollbar.onMouseWheelMoved(text, rotation);
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
        if(label.equals(StringsResources.close())) {
            toggleVisibility();
        } else {
            simulation.menuOptions(title+" "+label);
        }
    }

    boolean hasInside(double mouseX, double mouseY) {
        return visibility && x < mouseX && mouseX < x+width && y < mouseY && mouseY < y+height;
    }

    boolean hasInsideTheTitleBar(double mouseX, double mouseY) {
        return x < mouseX && mouseX < x+0.8*width && y < mouseY && mouseY < y+heightOfTopBar;
    }

    void toggleVisibility() {
        visibility = !visibility;
    }

    void addButton(int x, int y, int width, int height, String text) {
        buttons.add(new ClickableButton(x, y, width, height, text, (int)(FONT_SIZE *1.2)));
    }

    void setTitle(String line) {
        this.title = line;
    }

    public void disableScrollbar() {
        if(scrollbar != null) {
            scrollbar.setSelected(false);
        }
    }
}
