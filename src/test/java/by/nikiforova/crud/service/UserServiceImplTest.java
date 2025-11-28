package by.nikiforova.crud.service;

import by.nikiforova.crud.dao.UserDao;
import by.nikiforova.crud.entity.User;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Kate", "kate_mail@gmail.com", 32);
        user.setId(1);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createUserTest() {
        doNothing().when(userDao).save(any(User.class));

        User testUser = userService.createUser("Kate", "kate_mail@gmail.com", 32);

        assertNotNull(testUser);
        assertEquals("Kate", testUser.getName());
        assertEquals("kate_mail@gmail.com", testUser.getEmail());
        assertEquals(32, testUser.getAge());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void getUserByIdTest() {
        when(userDao.findById(1)).thenReturn(Optional.ofNullable(user));
        Optional<User> optionalTestUser = userService.getUserById(1);

        assertNotNull(optionalTestUser.get());
        assertEquals(1, optionalTestUser.get().getId());
        verify(userDao, times(1)).findById(1);
    }

    @Test
    void getUserByIdTestNegative() {
        when(userDao.findById(1000)).thenReturn(null);
        Optional<User> optionalTestUser = userService.getUserById(1000);

        assertNull(optionalTestUser);
        verify(userDao, times(1)).findById(1000);
    }

    @Test
    void getAllUsersTest() {
        when(userDao.findAll()).thenReturn(Arrays.asList(user));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void updateUserTest() {
        when(userDao.findById(1)).thenReturn(Optional.ofNullable(user));

        Optional<User> optionalUserToUpdate = userDao.findById(1);

        if (optionalUserToUpdate.isPresent()) {
            User userToUpdate = optionalUserToUpdate.get();
            userToUpdate.setName("Updated Name");
            userToUpdate.setEmail("Updated_email@mail.com");
            userToUpdate.setAge(33);

            userService.updateUser(user);

            assertEquals("Updated Name", user.getName());
            assertEquals("Updated_email@mail.com", user.getEmail());
            assertEquals(33, user.getAge());
        }
    }

    @Test
    void deleteUserTest() {
        when(userDao.findById(1)).thenReturn(Optional.ofNullable(user));

        userService.deleteUser(1);

        verify(userDao, times(1)).findById(1);
    }
}