package by.nikiforova.crud.service;

import by.nikiforova.crud.dao.UserDao;
import by.nikiforova.crud.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final Integer USER_ID = 1;
    private static final Integer UNEXISTING_USER_ID = 1000;
    private static final String USERNAME = "Kate";
    private static final String EMAIL = "kate_mail@gmail.com";
    private static final Integer USER_AGE = 32;
    private static final String UPDATED_USERNAME = "Anna";
    private static final String UPDATED_EMAIL = "anna_mail@gmail.com";
    private static final Integer UPDATED_USER_AGE = 33;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USERNAME, EMAIL, USER_AGE);
        user.setId(USER_ID);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("createUserTest() - success")
    void createUserTest() {
        doNothing().when(userDao).save(any(User.class));

        User testUser = userService.createUser(USERNAME, EMAIL, USER_AGE);

        assertNotNull(testUser);
        assertEquals(USERNAME, testUser.getName());
        assertEquals(EMAIL, testUser.getEmail());
        assertEquals(USER_AGE, testUser.getAge());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("getUserByIdTest() - success")
    void getUserByIdTest() {
        when(userDao.findById(USER_ID)).thenReturn(Optional.ofNullable(user));
        Optional<User> optionalTestUser = userService.getUserById(USER_ID);

        assertNotNull(optionalTestUser.get());
        assertEquals(USER_ID, optionalTestUser.get().getId());
        verify(userDao, times(1)).findById(USER_ID);
    }

    @Test
    @DisplayName("getUserByIdTestNegative() - null")
    void getUserByIdTestNegative() {
        when(userDao.findById(UNEXISTING_USER_ID)).thenReturn(null);
        Optional<User> optionalTestUser = userService.getUserById(UNEXISTING_USER_ID);

        assertNull(optionalTestUser);
        verify(userDao, times(1)).findById(UNEXISTING_USER_ID);
    }

    @Test
    @DisplayName("getAllUsersTest() - success")
    void getAllUsersTest() {
        when(userDao.findAll()).thenReturn(Arrays.asList(user));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    @DisplayName("updateUserTest() - success")
    void updateUserTest() {
        when(userDao.findById(USER_ID)).thenReturn(Optional.ofNullable(user));

        Optional<User> optionalUserToUpdate = userDao.findById(USER_ID);

        if (optionalUserToUpdate.isPresent()) {
            User userToUpdate = optionalUserToUpdate.get();
            userToUpdate.setName(UPDATED_USERNAME);
            userToUpdate.setEmail(UPDATED_EMAIL);
            userToUpdate.setAge(UPDATED_USER_AGE);

            userService.updateUser(user);

            assertEquals(UPDATED_USERNAME, user.getName());
            assertEquals(UPDATED_EMAIL, user.getEmail());
            assertEquals(UPDATED_USER_AGE, user.getAge());
        }
    }

    @Test
    @DisplayName("deleteUserTest() - success")
    void deleteUserTest() {
        when(userDao.findById(USER_ID)).thenReturn(Optional.ofNullable(user));

        userService.deleteUser(USER_ID);

        verify(userDao, times(1)).findById(USER_ID);
    }
}