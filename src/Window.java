import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public Window() {
        setTitle("MV v0.4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        int WIDTH = (int) screenSize.getWidth();
        int HEIGHT = (int) screenSize.getHeight();
        Panel panel = new Panel(WIDTH, HEIGHT, this);
        add(panel);
        setVisible(true);
    }
}