package by.nikiforova.crud.dao;

import by.nikiforova.crud.entity.User;
import by.nikiforova.crud.exception.DataAccessException;
import by.nikiforova.crud.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;


@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoImplTest {

    private static final String DATABASE_NAME = "test_db";
    private static final String POSTGRE_SQL_CONTAINER = "postgres:17.5";
    private static final String USERNAME_DB = "postgres";
    private static final String PASSWORD_DB = "password";

    private static final String USER_POLINA_NAME = "Polina";
    private static final String USER_POLINA_EMAIL = "test1@gmail.com";
    private static final Integer USER_POLINA_AGE = 15;

    private static final String USER_ANNA_NAME = "Anna";
    private static final String USER_ANNA_EMAIL = "test2@gmail.com";
    private static final Integer USER_ANNA_AGE = 5;

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>(POSTGRE_SQL_CONTAINER)
                    .withDatabaseName(DATABASE_NAME)
                    .withUsername(USERNAME_DB)
                    .withPassword(PASSWORD_DB);

    private static UserDao userDao;
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setup() {
        System.out.println("JDBC URL: " + postgresqlContainer.getJdbcUrl());

        try (Connection conn = DriverManager.getConnection(
                postgresqlContainer.getJdbcUrl(),
                postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword()
        )) {
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }

        userDao = new UserDaoImpl();
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    @DisplayName("saveAndGetUser() - success")
    void saveAndGetUser() {
        User user = new User(USER_POLINA_NAME, USER_POLINA_EMAIL, USER_POLINA_AGE);
        userDao.save(user);

        Optional<User> optionalFromDb = userDao.findById(user.getId());
        optionalFromDb.ifPresent(value -> Assertions.assertEquals(user.getName(), value.getName()));
    }


    @Test
    @DisplayName("findAllTest() - success")
    void findAllTest() {
        User anna = new User(USER_ANNA_NAME, USER_ANNA_EMAIL, USER_ANNA_AGE);
        User polina = new User(USER_POLINA_NAME, USER_POLINA_EMAIL, USER_POLINA_AGE);
        List<User> listForTest = List.of(anna, polina);
        UserDaoImplTest.userDao.save(anna);
        userDao.save(polina);

        List<User> result = userDao.findAll();

        Assertions.assertArrayEquals(listForTest.stream().map(User::getId).toArray(),
                result.stream().map(User::getId).toArray());
    }

    @Test
    @DisplayName("updateTest() - success")
    void updateTest() {
        User polina = new User(USER_POLINA_NAME, USER_POLINA_EMAIL, USER_POLINA_AGE);
        userDao.save(polina);
        userDao.update(polina);

        Optional<User> optionalFoundUser = userDao.findById(polina.getId());
        optionalFoundUser.ifPresent(user -> Assertions.assertEquals(polina.getEmail(), user.getEmail()));
    }

    @Test
    @DisplayName("deleteTest() - exception")
    void deleteTest() {
        User anna = new User(USER_ANNA_NAME, USER_ANNA_EMAIL, USER_ANNA_AGE);
        User polina = new User(USER_POLINA_NAME, USER_POLINA_EMAIL, USER_POLINA_AGE);
        userDao.save(anna);
        userDao.save(polina);

        userDao.delete(anna);

        Assertions.assertThrows(DataAccessException.class,() -> userDao.findById(anna.getId()));
    }
}