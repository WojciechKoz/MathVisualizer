import java.awt.event.KeyEvent;

/**
 * Interface of main menu and all cartesian plane simulations
 * has methods for drawing components on the screen and capture mouse and keyboard actions
 */
public interface GraphicsInterface {
    /**
     * drawing components of menus or simulation on the screen
     */
    void draw();

    /**
     * this method runs when right mouse button was pressed
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    void onRightClick(double mouseX, double mouseY);

    /**
     * this method runs when left mouse button was pressed
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    boolean onLeftClick(double mouseX, double mouseY);

    /**
     * this method runs when left mouse button was released
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     */
    void onLeftMouseButtonReleased(double mouseX, double mouseY);

    /**
     * This method runs when LEFT mouse button is pressed and mouse is moving.
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     * @return true/false depending which scenario happened
     */
    boolean onMouseDragged(double mouseX, double mouseY, double prevMouseX, double prevMouseY);

    /**
     * This method runs when no mouse button is pressed and mouse is moving
     * @param mouseX - current mouse x position (in pixels)
     * @param mouseY - current mouse y position (in pixels)
     * @param prevMouseX - mouse x position in previous frame (in pixels)
     * @param prevMouseY - mouse y position in previous frame (in pixels)
     */
    void onMouseMoved(double mouseX, double mouseY, double prevMouseX, double prevMouseY);

    /**
     * This method runs when mouse wheel is moving
     * @param rotation direction of scrolling. Can be either 1 [down] or -1 [up]
     */
    void onMouseScrolled(int rotation);

    /**
     * This method runs when some button is pressed
     * @param event - all information of pressed button
     */
    boolean onKeyPressed(KeyEvent event);
}
