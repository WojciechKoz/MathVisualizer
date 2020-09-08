import java.awt.*;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

/**
 * Class that has a lot of utility functions for drawing on the screen.
 */
public class DrawUtils {
    static Font font;
    static Graphics2D g2;

    // All colors that are used in the program
    public static final Color orange = new Color(251, 139, 36);
    public static final Color yellow = new Color(220, 220, 20);
    public static final Color gold = new Color(255, 215, 0);
    public static final Color transparentYellow = new Color(255, 255, 100, 120);

    public static final Color transparentWhite = new Color(255, 255, 255, 180);
    public static final Color white = new Color(255,255,255);
    public static final Color lightGray = new Color(150, 150, 150);
    public static final Color gray = new Color(130, 130, 130);
    public static final Color darkGray = new Color(50, 50, 50);
    public static final Color transparentBlack = new Color(0, 0, 0, 170);
    public static final Color black = new Color(0,0,0);
    public static final Color transparent = new Color(0,0,0, 0);

    public static final Color red = new Color(255, 0, 0);
    public static final Color transparentRed = new Color(255, 0, 0, 130);
    public static final Color lightRed = new Color(255, 100, 100);
    public static final Color darkRed = new Color(100, 50, 50);
    public static final Color green = new Color(0, 255, 0);
    public static final Color transparentGreen = new Color(0, 255, 0, 130);
    public static final Color lightGreen = new Color(100, 255, 100);
    public static final Color darkGreen = new Color(50, 100, 50);
    public static final Color lightBlue = new Color(100, 100, 255);

    // colors of samples
    static Color[] sampleColors = new Color[] {
            gray,
            lightBlue,
            lightRed,
            lightGreen,
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

    /**
     * @param text - text to be drawn
     * @return height of the string written with current font on the screen
     */
    public static int stringHeight(String text) {
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, text);
        Rectangle rect = gv.getPixelBounds(null, 0,0);
        return (int) rect.getHeight();
    }
}
