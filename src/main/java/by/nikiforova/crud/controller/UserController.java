package by.nikiforova.crud.controller;

import by.nikiforova.crud.entity.User;
import by.nikiforova.crud.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class UserController {

    private static final Logger logger = LogManager.getLogger();

    private final Scanner scanner;
    private final UserService userService;
    private final Validator validator;

    public UserController(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void runApplication() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1 -> addUser();
                case 2 -> getUser();
                case 3 -> listUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> running = false;
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void printMenu() {
        System.out.println("1 - Добавить User");
        System.out.println("2 - Найти User");
        System.out.println("3 - Показать всех User");
        System.out.println("4 - Обновить User");
        System.out.println("5 - Удалить User");
        System.out.println("0 - Выход");
    }

    private void addUser() {
        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            int age = getIntInput("Возраст: ");

            Set<ConstraintViolation<User>> violations = validator.validate(new User(name, email, age));

            if (!violations.isEmpty()) {
                for (ConstraintViolation<User> violation : violations) {
                    String propertyPath = violation.getPropertyPath().toString();
                    String message = violation.getMessage();

                    if (propertyPath.equals("email")) {
                        System.out.println("Некорректный email: " + message);
                    } else if (propertyPath.equals("age")) {
                        System.out.println("Некорректный возраст: " + message);
                    } else if (propertyPath.equals("name")) {
                        System.out.println("Некорректное имя: " + message);
                    }
                }
                return;
            }

            userService.createUser(name, email, age);
            System.out.println("Пользователь успешно создан!");

        } catch (Exception e) {
            logger.error("Ошибка при добавлении пользователя", e);
            System.out.println("Произошла неожиданная ошибка при создании пользователя");
        }
    }

    private void getUser() {
        try {
            Optional<User> optionalUser = userService.getUserById(getIntInput("User id:"));
            User user;
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                System.out.printf("Id: %d | %s | %s | %d лет%n",
                        user.getId(), user.getName(), user.getEmail(), user.getAge());
            } else {
                logger.info("Пользователя с таким id не существует.");
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении пользователя", e);
            System.out.println("Произошла неожиданная ошибка при получении пользователя");
        }
    }

    private void listUsers() {
        try {
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("Users не найдены");
                return;
            }

            System.out.println("\n--- Список User ---");
            users.forEach(user ->
                    System.out.printf("Id: %d | %s | %s | %d лет%n",
                            user.getId(), user.getName(), user.getEmail(), user.getAge())
            );

        } catch (Exception e) {
            logger.error("Ошибка при получении списка пользователей", e);
            System.out.println("Произошла неожиданная ошибка при получении списка пользователей");
        }
    }

    private void updateUser() {
        try {
            Integer id = getIntInput("User id для обновления: ");

            Optional<User> optionalUser = userService.getUserById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                System.out.printf("Текущие данные: id=%d, Имя='%s', Email='%s', Возраст=%d%n",
                        user.getId(), user.getName(), user.getEmail(), user.getAge());
                System.out.println("Оставьте поле пустым, чтобы не изменять его");

                System.out.print("Новое имя: ");
                String newName = scanner.nextLine().trim();
                if (!newName.isEmpty()) {
                    user.setName(newName);
                }

                System.out.print("Новый email: ");
                String newEmail = scanner.nextLine().trim();
                if (!newEmail.isEmpty()) {
                    user.setEmail(newEmail);
                }

                System.out.print("Новый возраст: ");
                String ageInput = scanner.nextLine().trim();
                if (!ageInput.isEmpty()) {
                    try {
                        int newAge = Integer.parseInt(ageInput);
                        if (newAge >= 0 && newAge <= 120) {
                            user.setAge(newAge);
                        } else {
                            System.out.println("Некорректный возраст, оставлен прежний");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный формат возраста, оставлен прежний");
                    }
                }
                userService.updateUser(user);
                System.out.println("Пользователь обновлен!");
            } else {
                logger.info("Пользователь с таким id не найден.");
            }
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя", e);
            System.out.println("Произошла неожиданная ошибка при обновлении пользователя");
        }
    }

    private void deleteUser() {
        try {
            Integer id = getIntInput("User id для удаления: ");
            Optional<User> optionalUser = userService.getUserById(id);

            if (optionalUser.isPresent()) {
                System.out.println("Удалить User: " + optionalUser.get() + " ? (Y/N)");
                String confirmation = scanner.nextLine().trim().toLowerCase();

                if ("y".equals(confirmation)) {
                    userService.deleteUser(optionalUser.get().getId());
                    System.out.println("User удален!");
                } else {
                    System.out.println("Отменено");
                }
            } else {
                logger.info("Введён некорректный id");
            }
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя", e);
            System.out.println("Произошла неожиданная ошибка при удалении пользователя");
        }
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число:");
            }
        }
    }

    public void close() {
        scanner.close();
    }
}
