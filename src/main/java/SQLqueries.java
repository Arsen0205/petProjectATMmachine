import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLqueries {


    ///Метод авторизации
    public static boolean login(String number, String password) {
        String selectUserSQL = "SELECT * FROM users WHERE number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {

            preparedStatement.setString(1, number);

            ResultSet resultSet = preparedStatement.executeQuery();


            if (resultSet.next()) {
                if (PasswordUtils.checkPassword(password, resultSet.getString("password"))) {
                    System.out.println("Здравствуйте, " + resultSet.getString("name_surname") + "!");
                    return true;
                } else {
                    System.out.println("Неверный номер или пароль!");
                    return false;
                }
            } else {
                System.out.println("Пользователь не найден");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка базы данных при входе: " + e.getMessage());
            return false;
        }
    }


    ///Метод регистрации
    public static boolean register(String name, String number, String password) {
        String insertUserSQL = "INSERT INTO users (name_surname, number, password) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {

            // Установка параметров запроса
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, number);
            preparedStatement.setString(3, password);

            // Выполнение запроса на добавление
            int rowsAffected = preparedStatement.executeUpdate();

            // Проверка успешности вставки
            if (rowsAffected > 0) {
                System.out.println("Пользователь успешно зарегистрирован.");
                return true;
            } else {
                System.out.println("Ошибка при регистрации пользователя.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Ошибка базы данных при регистрации: " + e.getMessage());
            return false;
        }
    }


    ///Метод пополнения баланса
    public static boolean topUpYourBalance(String number, double sum) {
        String updateUsersSQL = "UPDATE users SET balance = balance + ? WHERE number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateUsersSQL)) {

            preparedStatement.setDouble(1, sum);
            preparedStatement.setString(2, number);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("Ваш баланс был успешно поплнен на " + sum);
                return true;
            } else {
                System.out.println("Не получилось пополнить баланс!");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка базы данных при входе: " + e.getMessage());
            return false;
        }
    }


    ///Метод проверки баланса
    public static boolean checkTheBalance(String number) {
        String selectUsersSQL = "SELECT * FROM users WHERE number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectUsersSQL)) {

            preparedStatement.setString(1, number);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Ваш баланс: " + resultSet.getDouble("balance"));
                return true;
            } else {
                System.out.println("Не получилось проверить ваш баланс");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка базы данных при входе: " + e.getMessage());
            return false;
        }
    }


    ///Метод перевода
    public static boolean transfer(String number, String numberTransfer, double amount) {
        // Проверка на недопустимые суммы
        if (amount <= 0) {
            System.out.println("Сумма перевода должна быть больше 0.");
            return false;
        }

        String withdrawSQL = "UPDATE users SET balance = balance - ? WHERE number = ?";
        String depositSQL = "UPDATE users SET balance = balance + ? WHERE number = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Отключаем автокоммит для начала транзакции
            connection.setAutoCommit(false);

            try (PreparedStatement withdrawStmt = connection.prepareStatement(withdrawSQL);
                 PreparedStatement depositStmt = connection.prepareStatement(depositSQL)) {

                // Снять деньги с баланса отправителя
                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setString(2, number);
                int rowsWithdrawn = withdrawStmt.executeUpdate();

                // Проверить успешность снятия денег
                if (rowsWithdrawn == 0) {
                    System.out.println("Ошибка: пользователь-отправитель не найден или недостаточно средств.");
                    connection.rollback(); // Откатить транзакцию при ошибке
                    return false;
                }

                // Пополнить баланс получателя
                depositStmt.setDouble(1, amount);
                depositStmt.setString(2, numberTransfer);
                int rowsDeposited = depositStmt.executeUpdate();

                // Проверить успешность пополнения
                if (rowsDeposited == 0) {
                    System.out.println("Ошибка: пользователь-получатель не найден.");
                    connection.rollback(); // Откатить транзакцию при ошибке
                    return false;
                }

                // Если обе операции прошли успешно, зафиксировать изменения
                connection.commit();
                System.out.println("Перевод выполнен успешно.");
                return true;

            } catch (SQLException e) {
                // В случае ошибки откатить транзакцию
                connection.rollback();
                System.out.println("Ошибка при выполнении перевода: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
            return false;
        }
    }
}
