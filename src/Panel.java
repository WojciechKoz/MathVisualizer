import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Panel extends JPanel implements ActionListener, MouseWheelListener, KeyListener, MouseListener, MouseMotionListener {
    private final Image context;
    private final Graphics2D g2;
    private final int width;
    private final int height;
    private GraphicsInterface graphics;
    int prevMouseX= -1;
    int prevMouseY= -1;
    Window window;

    public Panel(int width, int height, Window win) {
        this.width = width;
        this.height = height;
        this.window = win;
        context = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        g2 = (Graphics2D) context.getGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        changeGraphics("Menu");

        Timer t = new Timer(15, this);
        t.start();
    }

    void changeGraphics(String name) {
        switch (name) {
            case "Menu": case "Visualizations-Back": case "Theory-Back": {
                graphics = new Menu(g2, width, height, this, "Math Visualizer V0.4a");
                ((Menu)graphics).addButtons(new String[] {"First Steps", "Visualizations", "Theory", "Settings", "Exit"});
            } break;
            case "Visualizations": {
                graphics = new Menu(g2, width, height, this, "Visualizations");
                ((Menu)graphics).addButtons(new String[] {
                        "Matrix Simulation",
                        "Linear Regression",
                        "Logistic Regression",
                        "PCA Algorithm",
                        "Back"});
            } break;
            case "Theory": case "Linear Algebra-Back": {
                graphics = new Menu(g2, width, height, this, "Theory");
                ((Menu)graphics).addButtons(new String[] {
                        "Linear Algebra",
                        "Calculus",
                        "Sets & Graphs Theory",
                        "Probabilistic & Statistic",
                        "Machine Learning",
                        "Back"});
            } break;
            case "Linear Algebra": {
                graphics = new Menu(g2, width, height, this, "Linear Algebra");
                ((Menu)graphics).addButtons(new String[] {
                        "Topic 1",
                        "Topic 2",
                        "Topic 3",
                        "Topic 4",
                        "Topic 5",
                        "Topic 6",
                        "Topic 7",
                        "Topic 8",
                        "Topic 9",
                        "Topic 10",
                        "Topic 11",
                        "Back"});
            } break;
            case "First Steps": graphics = new CartesianPlane(g2, width, height, this); break;
            case "PCA Algorithm": graphics = new PCACartesianPlane(g2, width, height, this); break;
            case "Linear Regression": graphics = new LRCartesianPlane(g2, width, height, this); break;
            case "Matrix Simulation": graphics = new MatrixCartesianPlane(g2, width, height, this); break;
            case "Logistic Regression": graphics = new LogCartesianPlane(g2, width, height, this); break;
            case "Exit": {window.dispose(); System.exit(0);} break;
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(context, 0, 0, null);
        g2.setColor(new Color(0,0,0));
        g2.fillRect(0,0, width, height);

        graphics.draw();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        graphics.onMouseScrolled(e.getWheelRotation());
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        graphics.onKeyPressed(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

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

    @Override
    public void mouseReleased(MouseEvent me) {
        final int LEFT = 1;
        if(me.getButton() == LEFT) {
            graphics.onLeftMouseButtonReleased(me.getX(), me.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent me) {
        int b1 = MouseEvent.BUTTON1_DOWN_MASK;
        int b2 = MouseEvent.BUTTON2_DOWN_MASK;
        if ((me.getModifiersEx() & (b1 | b2)) != b1) { // checks whether left button is clicked
            return;
        }

        graphics.onMouseDragged(me.getX(), me.getY(), prevMouseX, prevMouseY);
        prevMouseX = me.getX();
        prevMouseY = me.getY();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        graphics.onMouseMoved(me.getX(), me.getY(), prevMouseX, prevMouseY);
        prevMouseX = me.getX();
        prevMouseY = me.getY();
    }
}