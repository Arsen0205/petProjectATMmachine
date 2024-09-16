import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/bank";
    private static final String DB_USER = "postgres"; // Имя пользователя PostgreSQL
    private static final String DB_PASSWORD = "batman05"; // Пароль пользователя PostgreSQL

    // Метод для получения соединения с базой данных
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
