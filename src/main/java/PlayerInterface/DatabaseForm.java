package PlayerInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseForm extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;

    public DatabaseForm() {
        super("Database Table Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 250);
        setLocationRelativeTo(null);

        // Создаем колонки таблицы
        String[] columns = {"ID", "Winner", "Loser", "Gamedate"};
        tableModel = new DefaultTableModel(columns, 0);

        // Создаем таблицу с моделью данных
        table = new JTable(tableModel);

        // Добавляем таблицу в скроллируемую панель
        JScrollPane scrollPane = new JScrollPane(table);

        // Добавляем скроллируемую панель на форму
        add(scrollPane, BorderLayout.CENTER);

        // Загружаем данные из базы данных
        loadDataFromDatabase();

        setVisible(true);
    }

    private void loadDataFromDatabase() {
        

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM battlesea";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                // Очищаем таблицу перед загрузкой новых данных
                tableModel.setRowCount(0);

                // Заполняем таблицу данными из базы данных
                while (resultSet.next()) {
                    Object[] rowData = {
                            resultSet.getInt("id"),
                            resultSet.getString("winner"),
                            resultSet.getString("loser"),
                            resultSet.getTimestamp("gamedate")
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
