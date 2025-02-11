package Player;

import PlayerInterface.JoinGame;

import javax.swing.*;

public class Player extends JFrame
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JoinGame joinGame = new JoinGame();
            }
        });
    }
}
