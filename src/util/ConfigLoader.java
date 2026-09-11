package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility to load application configurations from the config.properties file.
 */
public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        // Search in working directory
        File configFile = new File("config.properties");
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error reading config.properties. Fallback to defaults will be used.");
            }
        } else {
            System.out.println("config.properties not found. Using default internal configurations.");
        }
    }

    /**
     * Gets a property by key, returning a default value if not found.
     */
    public static String getProperty(String key, String defaultValue) {
        String val = properties.getProperty(key);
        return (val != null) ? val.trim() : defaultValue;
    }
}
