package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility to hash passwords using the SHA-256 cryptographic algorithm.
 */
public class PasswordHasher {

    /**
     * Hashes a plain password into a SHA-256 hexadecimal string.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 hash algorithm not available.", e);
        }
    }

    /**
     * Validates a password against a stored hash.
     */
    public static boolean checkPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        return hashPassword(plainPassword).equals(storedHash);
    }
}
