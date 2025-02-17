import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * Class (with only one instance) that has a loop function for updating and drawing the whole program.
 * This class also supports all kind of mouse and keyboard events and passes them to more specific classes.
 * It manages menus and simulations changes using MenuScenarios.
 */
public class Panel extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseListener, MouseMotionListener {
    private Image context;
    private Graphics2D g2;
    // dimensions of the program window
    private int width;
    private int height;
    // could be a menu or a simulation
    private GraphicsInterface graphics;
    int prevMouseX= -1;
    int prevMouseY= -1;
    Window window;

    public Panel(Window win) {
        this.window = win;
    }

    void start(int width, int height) {
        this.width = width;
        this.height = height;
        context = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

        // Adds all listeners ( mouse and keyboard )
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        g2 = (Graphics2D) context.getGraphics();

        // antialiasing for better graphics quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // initializes important pointers in static classes
        DrawUtils.setGraphicsContext(g2);
        DrawUtils.registerFont("DAVIDBD.ttf");
        DrawUtils.registerFont("AbrilFatface-Regular.otf");
        MenuScenarios.setPanel(this);

        // program starts with main menu
        changeGraphics("", "Menu");

        // timer to frames changes
        Timer t = new Timer(15, this);
        t.start();
    }

    /**
     * Function that changes current menu or simulation to new one.
     * @param title - title of the current menu
     * @param buttonLabel - label on pressed button
     */
    void changeGraphics(String title, String buttonLabel) {
        // find out from which menu the button was pressed
        if(title.equals("")) {
            graphics = MenuScenarios.blankOptions(buttonLabel);
        } else if(title.equals(StringsResources.title())) {
            graphics = MenuScenarios.mainMenuOptions(buttonLabel);
        } else if(title.equals(StringsResources.visualizations())) {
            graphics = MenuScenarios.visualizationsMenuOptions(buttonLabel);
        } else if(title.equals(StringsResources.theory())) {
            graphics = MenuScenarios.theoryMenuOptions(buttonLabel);
        } else if(title.equals(StringsResources.linearAlgebra())) {
            graphics = MenuScenarios.LinearAlgebraMenuOptions(buttonLabel);
        }
    }

    /**
     * Draws black rectangle over the entire screen and performs draw method for graphics which
     * can be either a menu or a simulation.
     * @param g - graphics engine
     */
    public void paintComponent(Graphics g) {
        if(window.getSize().width != width || window.getSize().height != height) {
            width = window.getSize().width;
            height = window.getSize().height;
            context = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2 = (Graphics2D) context.getGraphics();

            // antialiasing for better graphics quality
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            DrawUtils.setGraphicsContext(g2);
            changeGraphics("", "Menu");
        }

        g.drawImage(context, 0, 0, null);

        if (g2 != null) {
            g2.setColor(DrawUtils.background);
            g2.fillRect(0,0, width, height);

            graphics.draw();
        }
    }

    /**
     * performs this.paintComponent() whenever timer starts an event (every 15 milliseconds)
     * @param e - action event (not used)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    /**
     * runs when mouse is scrolling and pass that information to the graphics interface
     * @param e - mouse rotation event
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        graphics.onMouseScrolled(e.getWheelRotation());
    }

    /**
     * when key is down (could runs many times with one key press)
     * @param e - key event
     */
    @Override
    public void keyTyped(KeyEvent e) { }

    /**
     * when key is down (runs only one per key press)
     * @param e - key event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        graphics.onKeyPressed(e);
    }

    /**
     * runs when key is released
     * @param e - key event
     */
    @Override
    public void keyReleased(KeyEvent e) { }

    /**
     * when mouse button is down (could runs many times with one button press)
     * @param me - mouse event
     */
    @Override
    public void mouseClicked(MouseEvent me) { }

    /**
     * runs when button is down (only one per button press)
     * @param me - mouse event
     */
    @Override
    public void mousePressed(MouseEvent me) {
        final int LEFT = 1;
        final int RIGHT = 3;
        if(me.getButton() == LEFT) {
            graphics.onLeftClick(me.getX(), me.getY());
        } else if(me.getButton() == RIGHT) {
            graphics.onRightClick(me.getX(), me.getY());
        }
    }

    /**
     * runs when mouse button is released
     * @param me - mouse event
     */
    @Override
    public void mouseReleased(MouseEvent me) {
        final int LEFT = 1;
        if(me.getButton() == LEFT) {
            graphics.onLeftMouseButtonReleased(me.getX(), me.getY());
        }
    }

    /**
     * No idea what is does but it is not used
     * @param e - mouse event
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * No idea what is does but it is not used
     * @param e - mouse event
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Runs when mouse is moved while some mouse button is pressed.
     * However information is passed only if the left mouse button is pressed.
     * Saves current mouse position for the next frame.
     * @param me - mouse event
     */
    @Override
    public void mouseDragged(MouseEvent me) {
        int b1 = MouseEvent.BUTTON1_DOWN_MASK;
        int b2 = MouseEvent.BUTTON2_DOWN_MASK;
        if ((me.getModifiersEx() & (b1 | b2)) != b1) { // checks whether left button is not clicked
            return;
        }

        graphics.onMouseDragged(me.getX(), me.getY(), prevMouseX, prevMouseY);
        prevMouseX = me.getX();
        prevMouseY = me.getY();
    }

    /**
     * When mouse is moving but no button is pressed. Saves current mouse position for the next frame.
     * @param me - mouse event
     */
    @Override
    public void mouseMoved(MouseEvent me) {
        if(graphics != null) {
            graphics.onMouseMoved(me.getX(), me.getY(), prevMouseX, prevMouseY);
        }
        prevMouseX = me.getX();
        prevMouseY = me.getY();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    Window getWindow() {
        return window;
    }

    GraphicsInterface getCurrentGraphics() {
        return graphics;
    }
}