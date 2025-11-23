package by.nikiforova.crud.service;

import by.nikiforova.crud.dao.UserDao;
import by.nikiforova.crud.entity.User;
import by.nikiforova.crud.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserService {

    private static final Logger logger = LogManager.getLogger();
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, int age) {
        User user = new User(name, email, age);
        try{
            userDao.save(user);
            logger.info("User {} created", user.getName());

        } catch(UserPersistenceException e){
            logger.error("Error while creating user", e);
            throw new ServiceException("Error while creating user", e);

        } catch(DataAccessException e){
            logger.error("Unexpected error while creating user", e);
            throw new ServiceException("Unexpected error while creating user", e);
        }
        return user;
    }

    public Optional<User> getUserById(Integer id) {
        Optional<User> user;
        try{
            user = userDao.findById(id);

        }  catch (UserNotFoundException e){
            logger.error("User not found with id: {}", id);
            throw new ServiceException("User was not found", e);

        }  catch(DataAccessException e){
            logger.error("Unexpected error while creating user", e);
            throw new ServiceException("Unexpected error while creating user", e);
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users;
        try {
            users = userDao.findAll();
            logger.info("All users found");

        }  catch (DataAccessException e) {
            logger.error("Unexpected error while finding all users", e);
            throw new ServiceException("Unexpected error while finding all users", e);
        } catch(DatabaseException e){
            logger.error("Error while finding all users", e);
            throw new ServiceException("Error while finding all users", e);
        }
        return users;
    }

    public void updateUser(User user) {
        Optional<User> optionalUser = userDao.findById(user.getId());
        if (optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();
            if (user.getName() != null) userToUpdate.setName(user.getName());
            if (user.getEmail() != null) userToUpdate.setEmail(user.getEmail());
            if (user.getAge() != null) userToUpdate.setAge(user.getAge());
            try {
                userDao.update(user);
                logger.info("{} was updated", userToUpdate.getName());
            } catch (UserPersistenceException e) {
                logger.error("Error while updating user", e);
                throw new ServiceException("Error while updating user", e);
            } catch (DataAccessException e) {
                logger.error("Unexpected error while updating user", e);
                throw new ServiceException("Unexpected error while updating user", e);
            }
        }
    }

    public void deleteUser(Integer id) {
        try {
            Optional<User> optionalUser = userDao.findById(id);
            optionalUser.ifPresent(userDao::delete);
        } catch (UserPersistenceException e) {
            logger.error("Error while deleting user", e);
            throw new ServiceException("Error while deleting user", e);
            
        } catch (DataAccessException e) {
            logger.error("Unexpected error while deleting user", e);
            throw new ServiceException("Unexpected error while deleting user", e);
        }
    }
}
