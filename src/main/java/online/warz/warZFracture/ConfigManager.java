package online.warz.warZFracture;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    public static void loadConfig() {
        WarZFracture plugin = WarZFracture.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void reload() {
        WarZFracture plugin = WarZFracture.getInstance();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public static int getInt(String path) {
        return config.getInt(path);
    }

    public static boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public static String getString(String path) {
        return config.getString(path);
    }
}
