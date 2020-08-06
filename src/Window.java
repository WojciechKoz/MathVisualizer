import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int WIDTH = (int)screenSize.getWidth();
    private final int HEIGHT = (int)screenSize.getHeight();

    public Window() {
        setTitle("MLV v0.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        Panel panel = new Panel(WIDTH, HEIGHT, this);
        add(panel);
        setVisible(true);
    }
}