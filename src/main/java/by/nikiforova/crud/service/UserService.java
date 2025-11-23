package by.nikiforova.crud.service;

import by.nikiforova.crud.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String email, int age);
    Optional<User> getUserById(Integer id);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(Integer id);
}
