import java.awt.*;
import java.util.ArrayList;

public class Menu implements GraphicsInterface {
    protected ArrayList<Button> buttons = new ArrayList();
    protected Graphics2D g2;
    protected int width, height;
    protected String title;
    protected Panel panel;

    Menu(Graphics2D g2, int width, int height, Panel mainPanel, String title) {
        this.g2 = g2;
        this.width = width;
        this.height = height;
        this.title = title;
        this.panel = mainPanel;
    }

    void addButtons(String[] labels) {
        int i = 0;
        for(String label: labels) {
            ClickableButton button =
                    new ClickableButton(width/4, (int)(height*0.3) + i*height/8,
                            width/2, height/10, label, 30);
            buttons.add(button);

            button.setTransparency(height*0.3, height);
            i++;
        }
    }

    @Override
    public void draw() {
        g2.setColor(new Color(251, 139, 36));
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, 70), g2);
        DrawUtils.drawCenteredString(title, width/2, height/7, g2);

        for(Button button: buttons) button.draw(g2);
    }

    @Override
    public void onRightClick(double mouseX, double mouseY) {

    }

    @Override
    public void onLeftClick(double mouseX, double mouseY) {
        for(Button button: buttons) {
            button.setHover(mouseX, mouseY, true);
        }
    }

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
            String message = ((ClickableButton)buttons.get(index)).getLabel();
            if(message.equals("Back")) message = title+"-"+message;
            panel.changeGraphics(message);
        }
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        for(Button button: buttons) {
            if(button instanceof ClickableButton) button.setHover(mouseX, mouseY, true);
            else if(button instanceof Slider && button.hasInside(mouseX, mouseY)) ((Slider) button).setValue(mouseX);
        }
        return true;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY, double prevMouseX, double prevMouseY) {
        for(Button button: buttons) {
            button.setHover(mouseX, mouseY, false);
        }
    }

    @Override
    public void onMouseScrolled(int rotation) {
        if(buttons.size() <= 5) return;
        double factor = -20*rotation;
        Button last = buttons.get(buttons.size()-1);

        if(buttons.get(0).getY()+factor > height*0.3) {
            factor = height*0.3 - buttons.get(0).getY();
        } else if(last.getY()+last.getHeight()+factor < height*0.95) {
            factor = height*0.95 - last.getY()-last.getHeight();
        }

        for(Button button: buttons) {
            button.setY(button.getY()+factor);
            button.setTransparency(height*0.3, height);
        }
    }

    @Override
    public void onKeyPressed(char key) {

    }
}
