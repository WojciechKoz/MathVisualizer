import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int WIDTH = (int)screenSize.getWidth();
    private final int HEIGHT = (int)screenSize.getHeight();

    public Window() {
        setTitle("MV v0.4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        Panel panel = new Panel(WIDTH, HEIGHT, this);
        add(panel);
        setVisible(true);

        GraphicsConfiguration config = this.getGraphicsConfiguration();
        Rectangle bounds = config.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

        int y = insets.top - this.getHeight();
        System.out.println(y);
    }
}