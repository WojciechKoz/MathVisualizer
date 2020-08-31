import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

/**
 * Class that has a lot of utility functions for drawing on the screen.
 */
public class DrawUtils {
    static Font font;
    static Graphics2D g2;

    static Color[] sampleColors = new Color[] {
            new Color(130, 130, 130),
            new Color(100, 100, 255),
            new Color(255, 100, 100),
            new Color(100, 255, 100),
            new Color(200, 200, 50),
            new Color(200, 50, 200),
            new Color(50, 200, 200)
    };

    /**
     * draws a full circle on the screen
     * @param x - x coordinate of the center of the circle
     * @param y - y coordinate of the center of the circle
     * @param r - radius of the circle
     */
    static void circle(double x, double y, double r) {
        g2.fillOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
    }

    /**
     * draws an empty circle on the screen
     * @param x - x coordinate of the center of the circle
     * @param y - y coordinate of the center of the circle
     * @param r - radius of the circle
     */
    static void ring(double x, double y, double r) {
        g2.drawOval((int)(x-r), (int)(y-r), (int)(2*r), (int)(2*r));
    }

    /**
     * Draws a straight line on the screen. Converts double to int.
     * @param x1 - x coordinate of first end of the line
     * @param y1 - y coordinate of first end of the line
     * @param x2 - x coordinate of second end of the line
     * @param y2 - y coordinate of second end of the line
     */
    static void line(double x1, double y1, double x2, double y2) {
        g2.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
    }

    /**
     * Draws centered string on the screen. it centers horizontal alignment as well as vertical.
     * @param text - text to be drawn
     * @param cenX - x coordinate of the point which should be exactly in the middle of the drawn text.
     * @param cenY - y coordinate of the point which should be exactly in the middle of the drawn text.
     */
    static void drawCenteredString(String text, int cenX, int cenY) {
        // Get the FontMetrics
        FontMetrics metrics = g2.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = cenX - metrics.stringWidth(text)/2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = cenY - metrics.getHeight()/2 + metrics.getAscent();

        // Draw the String
        g2.drawString(text, x, y);
    }

    /**
     * It centers vertical alignment and the text should end in certain place.
     * @param text - text to be drawn
     * @param rightSide - x coordinate of the place where string should end
     * @param cenY - y coordinate of the point which should be exactly in the middle of the drawn text.
     */
    static void drawStringWithRightAlignment(String text, int rightSide, int cenY) {
        // Get the FontMetrics
        FontMetrics metrics = g2.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rightSide - metrics.stringWidth(text);
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = cenY - metrics.getHeight()/2 + metrics.getAscent();

        // Draw the String
        g2.drawString(text, x, y);
    }

    /**
     * it centers only the vertical alignment. Text starts in the specific place.
     * @param text - text to be drawn
     * @param leftSide - x coordinate of the place where string should start
     * @param cenY - y coordinate of the point which should be exactly in the middle of the drawn text.
     */
    static void drawStringWithLeftAlignment(String text, int leftSide, int cenY) {
        // Get the FontMetrics
        FontMetrics metrics = g2.getFontMetrics(font);
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = cenY - metrics.getHeight()/2 + metrics.getAscent();

        // Draw the String
        g2.drawString(text, leftSide, y);
    }

    static void setFont(Font newFont) {
        font = newFont;
        g2.setFont(font);
    }

    static void setGraphicsContext(Graphics2D passedG2) {
        g2 = passedG2;
    }

    /**
     * @param text - text to be drawn
     * @return the width of the string written with current font on the screen.
     */
    public static int stringWidth(String text) {
        FontMetrics metrics = g2.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    public static int stringHeight(String text) {
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, text);
        Rectangle rect = gv.getPixelBounds(null, 0,0);
        return (int) rect.getHeight();
    }
}
