package service;

import dao.UserDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.User;
import util.InputValidator;
import util.PasswordHasher;

/**
 * Service layer coordinating validation logic and DB actions for Users.
 */
public class UserService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Handles user registration with validation checks and password hashing.
     */
    public boolean registerUser(String name, String email, String username, String password, String confirmPassword) 
            throws ValidationException, DatabaseException {
        
        if (!InputValidator.isValidName(name)) {
            throw new ValidationException("Full Name must contain only letters and spaces (max 100 characters).");
        }
        if (!InputValidator.isValidEmail(email)) {
            throw new ValidationException("Invalid email format.");
        }
        if (!InputValidator.isValidUsername(username)) {
            throw new ValidationException("Username must be 3-50 characters long and contain only letters, numbers, or underscores.");
        }
        if (!InputValidator.isValidPassword(password)) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match.");
        }

        // Duplicate checks
        if (userDAO.checkUsernameExists(username)) {
            throw new ValidationException("Username is already taken.");
        }
        if (userDAO.checkEmailExists(email)) {
            throw new ValidationException("Email is already registered.");
        }

        // Hash password and register
        String passwordHash = PasswordHasher.hashPassword(password);
        User user = new User(name, email, username, passwordHash);
        
        return userDAO.register(user);
    }

    /**
     * Authenticates a user login.
     */
    public User loginUser(String username, String password) throws ValidationException, DatabaseException {
        if (InputValidator.isEmpty(username) || InputValidator.isEmpty(password)) {
            throw new ValidationException("Username and Password fields cannot be empty.");
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        User user = userDAO.login(username.trim(), passwordHash);
        
        if (user == null) {
            throw new ValidationException("Invalid username or password.");
        }
        
        return user;
    }

    /**
     * Changes a user's password.
     */
    public boolean changePassword(User user, String currentPassword, String newPassword, String confirmNewPassword) 
            throws ValidationException, DatabaseException {
        
        if (InputValidator.isEmpty(currentPassword) || InputValidator.isEmpty(newPassword) || InputValidator.isEmpty(confirmNewPassword)) {
            throw new ValidationException("All password fields are required.");
        }
        if (!PasswordHasher.checkPassword(currentPassword, user.getPassword())) {
            throw new ValidationException("Incorrect current password.");
        }
        if (!InputValidator.isValidPassword(newPassword)) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }
        if (currentPassword.equals(newPassword)) {
            throw new ValidationException("New password cannot be the same as the current password.");
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new ValidationException("Passwords do not match.");
        }

        String newHash = PasswordHasher.hashPassword(newPassword);
        boolean success = userDAO.changePassword(user.getId(), newHash);
        
        if (success) {
            user.setPassword(newHash); // Update session model
        }
        
        return success;
    }
}
