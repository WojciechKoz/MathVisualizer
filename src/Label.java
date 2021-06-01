import java.awt.*;

/**
 * Class that holds a string and draws it on the screen. Used in menus
 */
public class Label {
    private final String text;
    private final int cenX;
    private final int cenY;
    private final int fontSize;

    Label(String t, int x, int y, int fontSize) {
        text = t;
        cenX = x;
        cenY = y;
        this.fontSize = fontSize;
    }

    void draw() {
        DrawUtils.g2.setColor(DrawUtils.primaryColor);
        DrawUtils.setFont(new Font(DrawUtils.regularFontName, Font.PLAIN, fontSize));
        DrawUtils.drawCenteredString(text, cenX, cenY);
    }
}
