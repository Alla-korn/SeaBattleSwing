package Game;


import Server.GameMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GameBoard {
    private JFrame mainFrame;
    private JPanel gamePanel;
    private JPanel playerBoard;
    private JPanel enemyBoard;

    private JLabel phaseLabel;

    private JButton[][] playerBoardButtons;
    private JButton[][] enemyBoardButtons;

    private int[] shipsSizeCount = new int[4];
    private ArrayList<Ship> ships;

    private Coordinate startCoordinate;
    private Coordinate endCoordinate;

    public enum Phase
    {
        YOUR_PLANT_SHIPS, YOUR_SHOT, ENEMY_SHOT, WIN, LOOSE, WAIT
    }

    public Phase current_phase;

    public boolean isFirstTurn;

    Socket playerSocket;

    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public int playerID;

    public GameBoard(Socket socket, ObjectOutputStream obj_out, ObjectInputStream obj_in)
    {
        initializeUI();
        playerSocket = socket;

        objectInputStream = obj_in;
        objectOutputStream = obj_out;

        shipsSizeCount[0] = 1;
        shipsSizeCount[1] = 2;
        shipsSizeCount[2] = 3;
        shipsSizeCount[3] = 4;

        ships = new ArrayList<Ship>();
    }

    private void initializeUI() {
        // Инициализируем основное окно
        mainFrame = new JFrame("Battleship");
        mainFrame.setSize(600, 400);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создаем панель игры
        gamePanel = new JPanel(new GridLayout(1, 2));

        // Создаем сетки клеток для игровых полей
        playerBoard = new JPanel(new GridLayout(10, 10));
        enemyBoard = new JPanel(new GridLayout(10, 10));

        // Инициализируем двумерные массивы для кнопок игровых полей
        playerBoardButtons = new JButton[10][10];
        enemyBoardButtons = new JButton[10][10];

        // Добавляем кнопки в каждую клетку поля
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                // Создаем кнопки для поля игрока
                JButton playerButton = new JButton();
                playerButton.setPreferredSize(new Dimension(20, 20));
                playerButton.addActionListener(new CellListener(i, j, true));
                playerBoard.add(playerButton);
                playerBoardButtons[i][j] = playerButton;

                // Создаем кнопки для поля противника
                JButton enemyButton = new JButton();
                enemyButton.setPreferredSize(new Dimension(20, 20));
                enemyButton.addActionListener(new CellListener(i, j, false));
                enemyBoard.add(enemyButton);
                enemyBoardButtons[i][j] = enemyButton;
            }
        }

        JPanel playerBoardContainer = new JPanel();
        playerBoardContainer.setLayout(new BorderLayout());
        playerBoardContainer.add(playerBoard, BorderLayout.CENTER);

        JPanel enemyBoardContainer = new JPanel();
        enemyBoardContainer.setLayout(new BorderLayout());
        enemyBoardContainer.add(enemyBoard, BorderLayout.CENTER);

        // Вставляем надпись "Ваше поле"
        JLabel playerBoardLabel = new JLabel("Your field", SwingConstants.CENTER);
        playerBoardLabel.setVerticalAlignment(SwingConstants.TOP);
        playerBoardContainer.add(playerBoardLabel, BorderLayout.NORTH);

        // Вставляем надпись "Поле противника"
        JLabel enemyBoardLabel = new JLabel("Your enemy field", SwingConstants.CENTER);
        enemyBoardLabel.setVerticalAlignment(SwingConstants.TOP);
        enemyBoardContainer.add(enemyBoardLabel, BorderLayout.NORTH);

        // Добавляем панели игроков на панель игры
        gamePanel.add(playerBoardContainer);
        gamePanel.add(enemyBoardContainer);

        // Создаем верхнюю панель
        JPanel topPanel = new JPanel(new BorderLayout());

        // Создаем метку для отображения фазы игры
        phaseLabel = new JLabel("Put ships", SwingConstants.CENTER);

        // Добавляем метку в верхнюю панель
        topPanel.add(phaseLabel, BorderLayout.CENTER);

        // Добавляем верхнюю панель в основное окно
        mainFrame.add(topPanel, BorderLayout.NORTH);

        // Добавляем панель игры в основное окно
        mainFrame.getContentPane().add(gamePanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public void ShiftPhase(Phase ph)
    {
        if(ph.equals(Phase.YOUR_PLANT_SHIPS))
        {
            phaseLabel.setText("Your ships:");
            UpdateButtons(playerBoardButtons, true);
            UpdateButtons(enemyBoardButtons, false);
        }
        else if(ph.equals(Phase.YOUR_SHOT))
        {
            phaseLabel.setText("Your turn:");
            UpdateButtons(playerBoardButtons, false);
            UpdateButtons(enemyBoardButtons, true);
        }
        else if(ph.equals(Phase.WAIT) || ph.equals(Phase.ENEMY_SHOT))
        {
            phaseLabel.setText("Your enemy turn");
            UpdateButtons(playerBoardButtons, false);
            UpdateButtons(enemyBoardButtons, false);
        }
        else if(ph.equals(Phase.WIN))
        {
            UpdateButtons(playerBoardButtons, false);
            UpdateButtons(enemyBoardButtons, false);
            JOptionPane.showMessageDialog(null, "You win!!!");
        }
        else if(ph.equals(Phase.LOOSE))
        {
            UpdateButtons(playerBoardButtons, false);
            UpdateButtons(enemyBoardButtons, false);
            JOptionPane.showMessageDialog(null, "You failed...");
        }

        current_phase = ph;

        System.out.println("СОСТОЯНИЕ:1 " + ph.toString());
    }

    private class CellListener implements ActionListener
    {
        private final int x;
        private final int y;
        private final boolean isPlayerBoard;

        public CellListener(int x, int y, boolean isPlayerBoard) {
            this.x = x;
            this.y = y;
            this.isPlayerBoard = isPlayerBoard;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.out.println(x + " " + y);

            // Обработка кликов на клетки поля
            if (isPlayerBoard)
            {
                if(current_phase == Phase.YOUR_PLANT_SHIPS)
                {
                    // пользователь нажал на поле игрока
                    System.out.println("Ваше поле");

                    int currentShipSize = 0;

                    if(shipsSizeCount[0] > 0)
                    {
                        currentShipSize = 4;
                    }
                    else if(shipsSizeCount[1] > 0 && currentShipSize == 0)
                    {
                        currentShipSize = 3;
                    }
                    else if(shipsSizeCount[2] > 0 && currentShipSize == 0) {
                        currentShipSize = 2;
                    }
                    else if(shipsSizeCount[3] > 0 && currentShipSize == 0)
                    {
                        currentShipSize = 1;
                    }

                    phaseLabel.setText("Put " + currentShipSize + "ship");

                    ArrayList<JButton> buttons = new ArrayList<>();

                    Coordinate clickedCoord = new Coordinate(x,y);
                    if(startCoordinate == null)
                    {
                        if(isValidShipPlacement(clickedCoord))
                        {
                            startCoordinate = clickedCoord;
                            playerBoardButtons[x][y].setBackground(Color.GRAY);
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "Wrong coordinate. Be careful");
                        }
                    }
                    else
                    {
                        int proverksSize = 0;

                        if(isValidShipPlacement(clickedCoord))
                        {
                            if(startCoordinate.equals(clickedCoord))
                            {
                                endCoordinate = startCoordinate;
                                proverksSize = 1;
                                buttons.add(playerBoardButtons[startCoordinate.getX()][startCoordinate.getY()]);
                            }
                            else if(startCoordinate.getX() == clickedCoord.getX())
                            {
                                if(startCoordinate.getY() > clickedCoord.getY())
                                {
                                    endCoordinate = new Coordinate(startCoordinate.getX(), startCoordinate.getY());
                                    startCoordinate = clickedCoord;
                                }
                                else
                                {
                                    endCoordinate = clickedCoord;
                                }

                                proverksSize = (endCoordinate.getY() - startCoordinate.getY()) + 1;

                                for(int i = startCoordinate.getY(); i<=endCoordinate.getY(); i++)
                                {
                                    buttons.add(playerBoardButtons[startCoordinate.getX()][i]);
                                }
                            }
                            else if(startCoordinate.getY() == clickedCoord.getY())
                            {
                                if(startCoordinate.getX() > clickedCoord.getX())
                                {
                                    endCoordinate =  new Coordinate(startCoordinate.getX(), startCoordinate.getY());
                                    startCoordinate = clickedCoord;
                                }
                                else
                                {
                                    endCoordinate = clickedCoord;
                                }

                                proverksSize = (endCoordinate.getX() - startCoordinate.getX()) + 1;

                                for(int i = startCoordinate.getX(); i<=endCoordinate.getX(); i++)
                                {
                                    buttons.add(playerBoardButtons[i][startCoordinate.getY()]);
                                }
                            }

                            if(startCoordinate.getX() != endCoordinate.getX() && startCoordinate.getY() != endCoordinate.getY())
                            {
                                JOptionPane.showMessageDialog(null, "Wrong place");
                            }
                            else
                            {
                                if(proverksSize != currentShipSize)
                                {
                                    playerBoardButtons[startCoordinate.getX()][startCoordinate.getY()].setBackground(null);
                                    playerBoardButtons[endCoordinate.getX()][endCoordinate.getY()].setBackground(null);

                                    startCoordinate = null;
                                    endCoordinate = null;

                                    JOptionPane.showMessageDialog(null, "Impossible ship lenght");
                                }
                                else
                                {
                                    System.out.println(currentShipSize + "-ый корабль размещен");
                                    ships.add(new Ship(startCoordinate, endCoordinate, currentShipSize));
                                    shipsSizeCount[4 - currentShipSize]--;

                                    startCoordinate = null;
                                    endCoordinate = null;

                                    for (int i = 0; i<buttons.size(); i++)
                                    {
                                        buttons.get(i).setBackground(Color.GREEN);
                                    }

                                    if(ships.size() == 10)
                                    {
                                        ShiftPhase(Phase.WAIT);
                                        try {
                                            objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.READY, "Ready"));
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "Wrong place");
                        }
                    }
                }
            }
            else
            {
                if(current_phase == Phase.YOUR_SHOT && (enemyBoardButtons[x][y].getBackground() != Color.yellow
                        && enemyBoardButtons[x][y].getBackground() != Color.RED
                        && enemyBoardButtons[x][y].getBackground() != Color.BLACK))
                {
                    // пользователь нажал на поле противника
                    System.out.println(x + " " + y);

                    try {
                        objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.SHOT, "Shot", new Coordinate(x,y), playerID));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    public void HitHandling(Coordinate coord, boolean hit)
    {
        if(hit)
        {
            enemyBoardButtons[coord.getX()][coord.getY()].setBackground(Color.RED);
        }
        else
        {
            enemyBoardButtons[coord.getX()][coord.getY()].setBackground(Color.yellow);
        }
    }

    public void DestroyShip(Ship ship)
    {
        Coordinate start = ship.getCoordinate();
        Coordinate end = ship.getEndCoordinate();

        if(start.getX() == end.getX() && start.getY() == end.getY())
        {
            enemyBoardButtons[start.getX()][start.getY()].setBackground(Color.BLACK);
        }
        else if(start.getX() == end.getX())
        {
            for(int i = start.getY(); i<=end.getY(); i++)
            {
                enemyBoardButtons[start.getX()][i].setBackground(Color.BLACK);
            }
        }
        else if(start.getY() == end.getY())
        {
            for(int i = start.getX(); i<=end.getX(); i++)
            {
                enemyBoardButtons[i][start.getY()].setBackground(Color.BLACK);
            }
        }
    }

    public void HitHandlingEnemy(Coordinate coord) throws IOException {
        for(int i = 0; i<ships.size(); i++)
        {
            Coordinate start = ships.get(i).getCoordinate();
            Coordinate end = ships.get(i).getEndCoordinate();

            if(start.equals(end) && start.equals(coord))
            {
                objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.DESTROY_SHIP, "killed", ships.get(i), playerID));
                playerBoardButtons[start.getX()][start.getY()].setBackground(Color.BLACK);
                ships.remove(i);

                if(ships.size() == 0)
                {
                    objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.LOOSE, "Player failed"));
                    ShiftPhase(Phase.LOOSE);
                }

                return;
            }
            else if(start.getX() == end.getX())
            {
                for(int j = start.getY(); j<= end.getY(); j++)
                {
                    Coordinate compare = new Coordinate(start.getX(), j);
                    if(compare.equals(coord))
                    {
                        if(!ships.get(i).isDestroy())
                        {
                            objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.RAN, "hurt", coord, playerID));
                            playerBoardButtons[start.getX()][j].setBackground(Color.red);
                        }
                        else
                        {
                            objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.DESTROY_SHIP, "killed", ships.get(i), playerID));

                            for(int z = ships.get(i).getCoordinate().getY(); z<=ships.get(i).getEndCoordinate().getY(); z++)
                            {
                                playerBoardButtons[ships.get(i).getCoordinate().getX()][z].setBackground(Color.BLACK);
                            }

                            ships.remove(i);
                            if(ships.size() == 0)
                            {
                                objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.LOOSE, "Player failed "));
                                ShiftPhase(Phase.LOOSE);
                            }
                        }

                        return;
                    }
                }
            }
            else if(start.getY() == end.getY())
            {
                for(int j = start.getX(); j<=end.getX(); j++)
                {
                    Coordinate compare = new Coordinate(j, start.getY());
                    if(compare.equals(coord))
                    {
                        if(!ships.get(i).isDestroy())
                        {
                            objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.RAN, "hurt", coord, playerID));
                            playerBoardButtons[j][start.getY()].setBackground(Color.red);
                        }
                        else
                        {
                            objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.DESTROY_SHIP, "killed", ships.get(i), playerID));

                            for(int z = ships.get(i).getCoordinate().getX(); z<=ships.get(i).getEndCoordinate().getX(); z++)
                            {
                                playerBoardButtons[z][start.getY()].setBackground(Color.BLACK);
                            }

                            ships.remove(i);

                            if(ships.size() == 0)
                            {
                                objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.LOOSE, "Player failed"));
                                ShiftPhase(Phase.LOOSE);
                            }
                        }

                        return;
                    }
                }
            }
        }

        objectOutputStream.writeObject(new GameMessage(GameMessage.MessageType.MISS, "miss", coord, playerID));
        ShiftPhase(Phase.YOUR_SHOT);
    }

    public boolean isValidShipPlacement(Coordinate checkCoordinate)
    {
        for(int i = 0; i < ships.size(); i++)
        {
            Coordinate startCoordinate = ships.get(i).getCoordinate();
            Coordinate endCoordinate = ships.get(i).getEndCoordinate();

            if(startCoordinate.getX() == endCoordinate.getX() &&
                    startCoordinate.getY() == endCoordinate.getY())
            {
                if(checkCoordinate.equals(startCoordinate))
                {
                    return false;
                }
            }
            else if(startCoordinate.getX() == endCoordinate.getX())
            {
                for(int j = startCoordinate.getY(); j<=endCoordinate.getY(); j++)
                {
                    Coordinate compare;
// Проверка клеток слева от текущей линии корабля
                    if(startCoordinate.getX() > 0)
                    {
                        compare = new Coordinate(startCoordinate.getX() - 1, j);
                        if(compare.equals(checkCoordinate))
                        {
                            return false;
                        }
// Проверка клеток по диагонали слева и сверху
                        if(j == startCoordinate.getY() && j > 0)
                        {
                            compare = new Coordinate(startCoordinate.getX() - 1, j - 1);
                            if(compare.equals(checkCoordinate))
                            {
                                return false;
                            }
                        }
// Проверка клеток по диагонали слева и снизу
                        if(j == endCoordinate.getY() && j < 10)
                        {
                            compare = new Coordinate(startCoordinate.getX() - 1, j+1);
                            if(compare.equals(checkCoordinate))
                            {
                                return false;
                            }
                        }
                    }
// Проверка клетки текущей линии корабля
                    compare = new Coordinate(startCoordinate.getX(), j);
                    if(compare.equals(checkCoordinate))
                    {
                        return false;
                    }
// Проверка клеток по диагонали справа и сверху
                    if(j == startCoordinate.getY() && j > 0)
                    {
                        compare = new Coordinate(startCoordinate.getX(), j-1);
                        if(compare.equals(checkCoordinate))
                        {
                            return false;
                        }
                    }
                    // Проверка клеток по диагонали справа и снизу
                    if(j == endCoordinate.getY() && j < 10)
                    {
                        compare = new Coordinate(startCoordinate.getX(), j+1);
                        if(compare.equals(checkCoordinate))
                        {
                            return false;
                        }
                    }
// Проверка клеток справа от текущей линии корабля
                    if(startCoordinate.getX() < 10)
                    {
                        compare = new Coordinate(startCoordinate.getX() + 1, j);
                        if(compare.equals(checkCoordinate))
                        {
                            return false;
                        }
// Проверка клеток по диагонали справа и сверху
                        if(j == startCoordinate.getY() && j > 0)
                        {
                            compare = new Coordinate(startCoordinate.getX() + 1, j - 1);
                            if(compare.equals(checkCoordinate))
                            {
                                return false;
                            }
                        }
                        // Проверка клеток по диагонали справа и снизу
                        if(j == endCoordinate.getY() && j < 10)
                        {
                            compare = new Coordinate(startCoordinate.getX() + 1, j+1);
                            if(compare.equals(checkCoordinate))
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            else if(startCoordinate.getY() == endCoordinate.getY())
            {
                for(int j = startCoordinate.getX(); j<=endCoordinate.getX(); j++)
                {
                    Coordinate compare = new Coordinate();

                    if(startCoordinate.getY() > 0)
                    {
                        compare = new Coordinate(j, startCoordinate.getY() - 1);
                        if(compare.equals(checkCoordinate))
                        {
                            return false;
                        }

                        if(j == startCoordinate.getX() && j > 0)
                        {
                            compare = new Coordinate(j - 1, startCoordinate.getY() - 1);
                            if(compare.equals(checkCoordinate))
                            {
                                return false;
                            }
                        }

                        if(j == endCoordinate.getX() && j < 10)
                        {
                            compare = new Coordinate(j + 1, startCoordinate.getY() - 1);
                            if(compare.equals(checkCoordinate))
                            {
                                return false;
                            }
                        }
                    }

                    compare = new Coordinate(j, startCoordinate.getY());
                    if(compare.equals(checkCoordinate))
                    {
                        return false;
                    }

                    if(j == startCoordinate.getX() && j > 0)
                    {
                        compare = new Coordinate(j - 1, startCoordinate.getY());
                        if(compare.equals(checkCoordinate))
                        {
                            return false;
                        }
                    }

                    if(j == endCoordinate.getX() && j < 10)
                    {
                        compare = new Coordinate(j + 1, startCoordinate.getY());
                        if(compare.equals(checkCoordinate))
                        {
                            return false;
                        }
                    }

                    if(startCoordinate.getY() < 10)
                    {
                        compare = new Coordinate(j, startCoordinate.getY() + 1);
                        if(compare.equals(checkCoordinate))
                        {
                            return false;
                        }

                        if(j == startCoordinate.getX() && j > 0)
                        {
                            compare = new Coordinate(j - 1, startCoordinate.getY() + 1);
                            if(compare.equals(checkCoordinate))
                            {
                                return false;
                            }
                        }

                        if(j == endCoordinate.getX() && j < 10)
                        {
                            compare = new Coordinate(j + 1, startCoordinate.getY() + 1);
                            if(compare.equals(checkCoordinate))
                            {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public void UpdateButtons(JButton[][] buttons, boolean open)
    {
        for (int i = 0; i<buttons.length; i++)
        {
            for (int j = 0; j<buttons[i].length; j++)
            {
                if(open)
                {
                    buttons[i][j].setEnabled(true);
                }
                else
                {
                    buttons[i][j].setEnabled(false);
                }
            }
        }
    }
}
