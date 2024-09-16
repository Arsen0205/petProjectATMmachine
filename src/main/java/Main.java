import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        boolean exit = true;
        while (exit) {
            System.out.println("Выберите операцию:");
            System.out.println("1. Авторизоваться");
            System.out.println("2. Зарегистрироваться");
            System.out.println("3. Выйти");

            Scanner scanner = new Scanner(System.in);

            int choice = scanner.nextInt();
            switch (choice) {
                case 1: /// Авторизация
                    System.out.println("Введите номер: ");
                    String number = scanner.next();
                    System.out.println("Введите пароль: ");
                    String password = scanner.next();
                    /// Эти данные идут в метод авторизации
                    boolean log = SQLqueries.login(number, password);
                    if (log) {
                        boolean leave = true;
                        while (leave) {
                            System.out.println("Выберите услугу:");
                            System.out.println("1. Пополнить счет");
                            System.out.println("2. Проверить баланс");
                            System.out.println("3. Перевести деньги по номеру");
                            System.out.println("4. Выйти");

                            int selection = scanner.nextInt();
                            switch (selection) {
                                case 1:
                                    System.out.println("Введите сумму: ");
                                    double sum = scanner.nextDouble();
                                    SQLqueries.topUpYourBalance(number, sum);
                                    break;
                                case 2:
                                    SQLqueries.checkTheBalance(number);
                                    break;
                                case 3:
                                    System.out.println("Введите сумму для перевода");
                                    double amount = scanner.nextDouble();
                                    System.out.println("Введите номер");
                                    String numberTransfer = scanner.next();
                                    SQLqueries.transfer(number, numberTransfer, amount);
                                    break;
                                case 4:
                                    leave = false;
                                    break;
                            }
                        }
                    }
                    break;

                case 2: /// Регистрация
                    System.out.println("Введите Имя и Фамилию: ");
                    String a = scanner.nextLine();
                    String newNameSurname = scanner.nextLine();
                    System.out.println("Введите номер: ");
                    String newNumber = scanner.next();
                    System.out.println("Введите пароль: ");
                    String newPassword = scanner.next();
                    String hashPassword = PasswordUtils.hashPassword(newPassword);
                    /// Эти данные идут в метод регистрации
                    SQLqueries.register(newNameSurname, newNumber, hashPassword);
                    break;

                case 3:
                    System.out.println("До свидания!");
                    exit = false;
            }
        }
    }
}