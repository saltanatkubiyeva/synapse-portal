package kz.synapse.ui;

import kz.synapse.utils.LanguageManager;
public class UIStrings {

    private UIStrings() {}

    public static String get(String key) {
        return LanguageManager.get(key);
    }

    public static String get(String key, Object... args) {
        return LanguageManager.get(key, args);
    }
}
