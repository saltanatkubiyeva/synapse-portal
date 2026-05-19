package kz.synapse.utils;

import kz.synapse.enums.Language;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

public class LanguageManager implements Serializable {

    // singleton
    private static LanguageManager instance;
    private Language currentLanguage;
    private Properties properties = new Properties();

    private LanguageManager() {
        this.currentLanguage = Language.EN;
        loadProperties();
    }

    public static LanguageManager getInstance() {
        if (instance == null)
            instance = new LanguageManager();
        return instance;
    }

    // загрузка файла
    private void loadProperties() {
        String fileName = switch (currentLanguage) {
            case KZ -> "messages_kz.properties";
            case RU -> "messages_ru.properties";
            default -> "messages_en.properties";
        };

        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(fileName)) {
            if (is != null)
                properties.load(is);
            else
                System.out.println("Language file not found: " + fileName);
        } catch (IOException e) {
            System.out.println("Error loading language file: " + e.getMessage());
        }
    }

    // получить строку по ключу
    public String getString(String key) {
        return properties.getProperty(key, key);
    }

    // переключить язык
    public void switchLanguage(Language language) {
        this.currentLanguage = language;
        loadProperties();
    }

    public Language getCurrentLanguage() { return currentLanguage; }
}