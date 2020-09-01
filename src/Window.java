import javax.swing.*;

public class Window extends JFrame {

    public Window() {
        setTitle("MV v0.4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        Panel panel = new Panel(this);
        add(panel);
        pack();
        setVisible(true);
        panel.start(getSize().width, getSize().height);

    }
}