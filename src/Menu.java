import java.awt.*;
import java.util.ArrayList;

/**
 * Class that is responsible for the graphics of main menu which appears when program starts.
 * If button is clicked that class sends title of menu + label on that button to Panel
 * where right action is performed.
 * That class is also extensive (@code{SideMenu})
 * to a smaller class - side menu inside the cartesian plane simulations.
 */
public class Menu implements GraphicsInterface {
    // lists of buttons that this menu contains
    protected ArrayList<Button> buttons = new ArrayList<>();
    // graphical context
    protected Graphics2D g2;
    // width and height of the menu (usually the entire screen)
    protected int width, height;
    // label that stands at the top of the menu
    protected String title;
    // pointer to parent class which performs actions related to pressed buttons
    protected Panel panel;
    private final double upperTransparencyBound, lowerTransparencyBound;

    Menu(Graphics2D g2, int width, int height, Panel mainPanel, String title) {
        this.g2 = g2;
        this.width = width;
        this.height = height;
        this.title = title;
        this.panel = mainPanel;

        upperTransparencyBound = height*0.3;
        lowerTransparencyBound = height*0.85;
    }

    /**
     * Adds list of buttons. Their place is set automatically.
     * That function should be executed only one time (in contrast to SideMenu)
     * because starting position of the buttons os always the same!!
     * Buttons that don't lay between upperTransparencyBound and lowerTransparencyBound have bigger transparency
     * @param labels - list of labels which will be on the buttons
     */
    void addButtons(String[] labels) {
        int i = 0;
        for(String label: labels) {
            ClickableButton button =
                    new ClickableButton(width/4, (int)(height*0.3) + i*height/8,
                            width/2, height/10, label, 30);
            buttons.add(button);

            button.setTransparency(upperTransparencyBound, lowerTransparencyBound);
            i++;
        }
    }

    /**
     * Draws title of the menu and all its buttons.
     */
    @Override
    public void draw() {
        g2.setColor(new Color(251, 139, 36));
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, 70));
        DrawUtils.drawCenteredString(title, width/2, height/7);

        for(Button button: buttons) button.draw(g2);
    }

    /**
     * Menu doesn't support the right click method
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onRightClick(double mouseX, double mouseY) {

    }

    /**
     * If left mouse button is pressed then color of the button is changed to clicked hover theme.
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onLeftClick(double mouseX, double mouseY) {
        for(Button button: buttons) {
            button.setHover(mouseX, mouseY, true);
        }
    }

    /**
     * for all buttons sets clicked hover to false and checks whether some button was pressed
     * If so then sends title of the menu + label on the button to panel using @code{Panel#changeGraphics}
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    @Override
    public void onLeftMouseButtonReleased(double mouseX, double mouseY) {
        int index = -1;
        for(int i = 0; i < buttons.size(); i++) {
            if(buttons.get(i).hasInside(mouseX, mouseY)) {
                index = i;
            }
            buttons.get(i).setHover(mouseX, mouseY, false);
        }
        if(index != -1) {
            String buttonLabel = buttons.get(index).getLabel();
            panel.changeGraphics(title, buttonLabel);
        }
    }

    /**
     * Checks if some button is under the mouse. If so then sets its color to pressed hover theme.
     * If its a slider changes its value to the mouse position.
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     * @return always true
     */
    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        for(Button button: buttons) {
            if(button instanceof ClickableButton) button.setHover(mouseX, mouseY, true);
            else if(button instanceof Slider && button.hasInside(mouseX, mouseY)) ((Slider) button).setValue(mouseX);
        }
        return true;
    }

    /**
     * sets normal hover color for each button.
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     */
    @Override
    public void onMouseMoved(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        for(Button button: buttons) {
            button.setHover(mouseX, mouseY, false);
        }
    }

    /**
     * Moves every button towards the direction of scrolling.
     * If Buttons are out of range (lowerTransparencyBound, upperTransparencyBound)
     * then its transparency increases until the button is invisible.
     * @param rotation direction of scrolling. Can be either 1 [down] or -1 [up]
     */
    @Override
    public void onMouseScrolled(int rotation) {
        if(buttons.size() < 5) return;
        double factor = -20*rotation;
        Button last = buttons.get(buttons.size()-1);

        // aligns the outermost buttons to lower and upper bound
        if(buttons.get(0).getY()+factor > upperTransparencyBound) {
            factor = upperTransparencyBound - buttons.get(0).getY();
        } else if(last.getY()+last.getHeight()+factor < lowerTransparencyBound) {
            factor = lowerTransparencyBound - last.getY()-last.getHeight();
        }

        for(Button button: buttons) {
            button.setY(button.getY()+factor);
            button.setTransparency(upperTransparencyBound, lowerTransparencyBound);
        }
    }

    /**
     * Menu doesn't support keyboard
     * @param key - char value of pressed button
     */
    @Override
    public void onKeyPressed(char key) {

    }
}
