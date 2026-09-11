package util;

import java.util.regex.Pattern;

/**
 * Utility to validate various user inputs.
 */
public class InputValidator {
    
    // Simple email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * Check if a string is null or whitespace-only.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates username requirements (3-50 characters, alphanumeric and underscore).
     */
    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) {
            return false;
        }
        String trimmed = username.trim();
        return trimmed.length() >= 3 && trimmed.length() <= 50 && trimmed.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Validates password requirements (minimum 6 characters).
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validates display name (not empty, up to 100 characters).
     */
    public static boolean isValidName(String name) {
        if (isEmpty(name)) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() <= 100 && trimmed.matches("^[a-zA-Z\\s.]+$");
    }
}
