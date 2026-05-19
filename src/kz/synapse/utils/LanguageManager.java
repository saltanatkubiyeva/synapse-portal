package kz.synapse.utils;

import kz.synapse.enums.Language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Properties;

public class LanguageManager implements Serializable {

    private static final String RESOURCE_PREFIX = "recources/";

    private static LanguageManager instance;

    private Language currentLanguage;
    private final Properties properties = new Properties();

    private LanguageManager() {
        this.currentLanguage = Language.EN;
        loadProperties();
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    /** Shortcut for {@link #getString(String)}. */
    public static String get(String key) {
        return getInstance().getString(key);
    }

    /** Shortcut for {@link #getString(String, Object...)}. */
    public static String get(String key, Object... args) {
        return getInstance().getString(key, args);
    }

    public String getString(String key) {
        return properties.getProperty(key, key);
    }

    public String getString(String key, Object... args) {
        String pattern = getString(key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, args);
    }

    private void loadProperties() {
        properties.clear();

        String fileName = switch (currentLanguage) {
            case KZ -> "messages_kz.properties";
            case RU -> "messages_ru.properties";
            default -> "messages_en.properties";
        };

        String resourcePath = RESOURCE_PREFIX + fileName;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                properties.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            } else {
                System.err.println("Language file not found: " + resourcePath);
            }
        } catch (IOException e) {
            System.err.println("Error loading language file: " + e.getMessage());
        }
    }

    public void switchLanguage(Language language) {
        this.currentLanguage = language;
        loadProperties();
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }
}