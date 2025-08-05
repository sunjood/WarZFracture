package online.warz.warZFracture;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class LangManager {

    private static FileConfiguration langConfig;

    // 加载语言文件
    // 确保语言文件正确加载
    public static void loadLangFile(String language) {
        // 确保语言文件夹存在
        File langFolder = new File(WarZFracture.getInstance().getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        File langFile = new File(langFolder, language + ".yml");
        
        // 如果文件不存在，尝试从插件jar中复制
        if (!langFile.exists()) {
            try {
                WarZFracture.getInstance().saveResource("lang/" + language + ".yml", false);
                WarZFracture.getInstance().getLogger().info("已创建默认语言文件: " + language + ".yml");
            } catch (IllegalArgumentException e) {
                // 如果指定语言文件不存在，使用英文作为后备
                if (!language.equals("en")) {
                    WarZFracture.getInstance().getLogger().warning("找不到语言文件: " + language + ".yml，将使用英文(en)");
                    language = "en";
                    langFile = new File(langFolder, "en.yml");
                }
            }
        }

        // 加载语言文件
        langConfig = YamlConfiguration.loadConfiguration(langFile);
        WarZFracture.getInstance().getLogger().info("已加载语言文件: " + language + ".yml");
    }

    // 获取带有占位符的语言文本
    // 确保 LangManager 类中有这个方法
    public static String getMessageWithPlaceholder(String key, String value) {
        String message = getMessage(key);
        if (message == null) {
            return "§c未找到指定文本: " + key;
        }
        return message.replace("%count%", value);
    }

    // 获取语言文本
    public static String getMessage(String path) {
        return langConfig.getString(path, "未找到指定文本");
    }
}
