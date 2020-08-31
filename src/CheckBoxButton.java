import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static java.lang.Integer.min;

public class CheckBoxButton extends ClickableButton {
    private boolean value;
    private static Image tick;

    CheckBoxButton(int x, int y, int width, int height, String label, int fontSize, boolean value) {
        super(x, y, width, height, label, fontSize);
        this.value = value;

        if(tick == null) {
            try {
                tick = ImageIO.read(new File("data/tick.png"));
            } catch (IOException e) {
                System.out.println("load failed");
            }
        }
    }

    @Override
    void draw(Graphics2D g2) {
        g2.setColor(backgroundCol);
        g2.fillRect(x, y, width, height);

        g2.setColor(textCol);
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));
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

    @Override
    public String onClicked(double mouseX, double mouseY) {
        toggleValue();
        return super.onClicked(mouseX, mouseY);
    }
}
