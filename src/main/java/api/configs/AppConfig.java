package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private final Properties PROPERTIES = new Properties();
    private static final String CONFIG_FILE_NAME = "app-config-dev.properties";

    private AppConfig() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {

            if (inputStream == null) {
                throw new IllegalStateException(CONFIG_FILE_NAME + " не найден в classpath");
            }

            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new IllegalStateException("Ошибка чтения " + CONFIG_FILE_NAME, e);
        }
    }

    public static String getProperty(String key) {
        return INSTANCE.PROPERTIES.getProperty(key);
    }
}
