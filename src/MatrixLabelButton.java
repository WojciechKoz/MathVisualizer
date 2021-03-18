import java.awt.*;

/**
 * Button that shows components of 2x2 matrix. Used only in sideMenus in coordinate systems
 * where there is some matrix.
 * Doesn't support hovering.
 */
class MatrixLabelButton extends Button {
    Matrix2x2 matrix;

    MatrixLabelButton(int x, int y, int width, int height, Matrix2x2 mat, int fontSize) {
        super(x, y, width, height, "", fontSize);
        matrix = mat;
    }

    /**
     * Draws button with matrix components.
     * @param g2 - object for drawing on the screen and responsible for the graphics
     */
    @Override
    void draw(Graphics2D g2) {
        super.draw(g2);

        g2.setColor(textCol);

        // value to accumulate all labels widths to avoid writing multiple strings in the same place
        // the initial offset is 4% of button's width
        int labelWidth = width/25;

        // label to be drawn
        String text;

        // first, prints matrix name and equal sign (and update labelWidth at the end)
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));
        text = matrix.getName() + " = ";
        DrawUtils.drawStringWithLeftAlignment(text, x+labelWidth, y+height/2);
        labelWidth += DrawUtils.stringWidth(text);

        // prints big square bracket representing matrix
        DrawUtils.setFont(new Font("TimesRoman Plain", Font.PLAIN, fontSize*5));
        text = "[";
        DrawUtils.drawStringWithLeftAlignment(text, x+labelWidth, (int)(y+height*0.4));
        labelWidth += DrawUtils.stringWidth(text);

        // sets color to color of matrix's x-axis
        g2.setColor(DrawUtils.lightRed);

        // prints first column of matrix
        DrawUtils.setFont(new Font("David bold", Font.PLAIN, fontSize));
        text = Double.toString(MathUtils.round(matrix.a, 2));
        DrawUtils.drawStringWithLeftAlignment(text, x+labelWidth, (int)(y+height*0.3));
        text = Double.toString(MathUtils.round(matrix.c, 2));
        DrawUtils.drawStringWithLeftAlignment(text, x+labelWidth, (int)(y+height*0.7));

        // sets color to color of matrix's y-axis
        g2.setColor(DrawUtils.lightGreen);

        // prints second column of the matrix (always starts with the same X) and
        // labelWidth is no longer necessary)
        text = Double.toString(MathUtils.round(matrix.b, 2));
        DrawUtils.drawStringWithLeftAlignment(text, (int) (x+width*0.65), (int)(y+height*0.3));
        text = Double.toString(MathUtils.round(matrix.d, 2));
        DrawUtils.drawStringWithLeftAlignment(text, (int)(x+width*0.65), (int)(y+height*0.7));

        // prints right bracket of the matrix
        g2.setColor(textCol);
        DrawUtils.setFont(new Font("TimesRoman Plain", Font.PLAIN, fontSize*5));
        DrawUtils.drawStringWithRightAlignment("]", (int)(x+width*0.95), (int)(y+height*0.4));
    }

    /**
     * Empty method - this class doesn't support hovering
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @param pressed - true when left mouse button is pressed otherwise false
     */
    @Override
    public void setHover(double mouseX, double mouseY, boolean pressed) {

    }

    /**
     * Always returns false
     * @param mouseX - x position of mouse (in pixels)
     * @param mouseY - y position of mouse (in pixels)
     * @return false
     */
    @Override
    boolean hasInside(double mouseX, double mouseY) {
        return false;
    }

    /**
     * Since this class is only for displaying matrix values, returns an empty string when is clicked
     * meaning that the upper layer doesn't perform any action after this button was pressed
     * @param mouseX - current x position of mouse
     * @param mouseY - current y position of mouse
     * @return - an empty string
     */
    @Override
    public String onClicked(double mouseX, double mouseY) {
        return "";
    }
}
