import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class DrawUtils {
    static Font font;

    static void circle(double x, double y, double r, Graphics2D g2) {
        g2.fillOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
    }

    static void ring(double x, double y, double r, Graphics2D g2) {
        g2.drawOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
    }

    static void line(double x1, double y1, double x2, double y2, Graphics2D g2) {
        g2.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
    }

    static void drawCenteredString(String text, int cenX, int cenY, Graphics2D g2) {
        // Get the FontMetrics
        FontMetrics metrics = g2.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = cenX - metrics.stringWidth(text)/2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = cenY - metrics.getHeight()/2 + metrics.getAscent();

        // Draw the String
        g2.drawString(text, x, y);
    }

    static void drawStringWithRightAlignment(String text, int rightSide, int cenY, Graphics2D g2) {
        // Get the FontMetrics
        FontMetrics metrics = g2.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rightSide - metrics.stringWidth(text);
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = cenY - metrics.getHeight()/2 + metrics.getAscent();

        // Draw the String
        g2.drawString(text, x, y);
    }

    static void drawStringWithLeftAlignment(String text, int leftSide, int cenY, Graphics2D g2) {
        // Get the FontMetrics
        FontMetrics metrics = g2.getFontMetrics(font);
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = cenY - metrics.getHeight()/2 + metrics.getAscent();

        // Draw the String
        g2.drawString(text, leftSide, y);
    }

    static void setFont(Font newFont, Graphics2D g2) {
        font = newFont;
        g2.setFont(font);
    }

    public static int stringWidth(String s, Graphics2D g2) {
        FontMetrics metrics = g2.getFontMetrics(font);
        return metrics.stringWidth(s);
    }
}
