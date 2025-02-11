package PlayerInterface;


import javax.swing.*;
import java.awt.*;

public class WaitingRoom extends JFrame {
    private static final int width = 300;
    private static final int height = 150;
    private JLabel waitingLabel = new JLabel("Waiting for another player");
    public WaitingRoom()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 1));
        add(waitingLabel);
        setVisible(true);
    }
}
