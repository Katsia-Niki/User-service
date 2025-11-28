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

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>("postgres:17.5")
                    .withDatabaseName("test_db")
                    .withUsername("postgres")
                    .withPassword("password");

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
    void saveAndGetUser() {
        User user = new User("Polina", "test1@gmail.com", 15);
        userDao.save(user);

        Optional<User> optionalFromDb = userDao.findById(user.getId());
        optionalFromDb.ifPresent(value -> Assertions.assertEquals(user.getName(), value.getName()));
    }


    @Test
    void findAllTest() {
        User anna = new User("Anna", "test2@gmail.com", 5);
        User polina = new User("Polina", "test1@gmail.com", 15);
        List<User> listForTest = List.of(anna, polina);
        UserDaoImplTest.userDao.save(anna);
        userDao.save(polina);

        List<User> result = userDao.findAll();

        Assertions.assertArrayEquals(listForTest.stream().map(User::getId).toArray(),
                result.stream().map(User::getId).toArray());
    }

    @Test
    void updateTest() {
        User polina = new User("Polina", "test1@gmail.com", 15);
        userDao.save(polina);
        userDao.update(polina);

        Optional<User> optionalFoundUser = userDao.findById(polina.getId());
        optionalFoundUser.ifPresent(user -> Assertions.assertEquals(polina.getEmail(), user.getEmail()));
    }

    @Test
    void deleteTest() {
        User anna = new User("Anna", "test2@gmail.com", 5);
        User polina = new User("Polina", "test1@gmail.com", 15);
        userDao.save(anna);
        userDao.save(polina);

        userDao.delete(anna);

        Assertions.assertThrows(DataAccessException.class,() -> userDao.findById(anna.getId()));
    }
}