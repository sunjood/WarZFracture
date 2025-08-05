package online.warz.warZFracture;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class WarZFracture extends JavaPlugin {

    private static WarZFracture instance;

    // 插件启动时的 onEnable 方法
    // 在onEnable方法中添加TabCompleter注册
    @Override
    public void onEnable() {
        instance = this;

        // 确保配置目录存在
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // 保存默认配置
        saveDefaultConfig();

        // 获取语言设置（只保留这一个定义）
        String language = getConfig().getString("language", "zh_CN"); // 默认使用中文

        // 加载语言文件
        LangManager.loadLangFile(language);

        // 初始化 FractureDataManager
        FractureDataManager.init(this);

        // 使用配置创建监听器
        WarZFractureConfig config = new WarZFractureConfig(this);
        getServer().getPluginManager().registerEvents(new FractureEffectListener(config), this);

        // Remove this duplicate line:
        // config = new WarZFractureConfig(this);

        // 注册带参数的事件监听器
        getServer().getPluginManager().registerEvents(new FractureEffectListener(config), this);

        // 使用 WarZFractureConfig 实例获取配置项
        double headFractureProbability = config.getHeadFractureProbability();
        double armFractureProbability = config.getArmFractureProbability();
        double legFractureProbability = config.getLegFractureProbability();
        String headFractureEffect = config.getHeadFractureEffect();
        int headFractureDuration = config.getHeadFractureDuration();

        // 只保留这两个简单的注册:
        getServer().getPluginManager().registerEvents(new PainkillerEffectListener(), this);
        getServer().getPluginManager().registerEvents(new FirstAidKitEffectListener(), this);

        // 美化后的启动信息
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§c▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("     §f[§c§lWarZ§f§lFracture§f] §a插件已启用");
        Bukkit.getConsoleSender().sendMessage("     §b版本: §fv1.0");
        Bukkit.getConsoleSender().sendMessage("     §b作者: §fCrazy_Jky");
        Bukkit.getConsoleSender().sendMessage("     §b联系: §fQQ 1285988665");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("     §a✔ §f骨折系统已启动");
        Bukkit.getConsoleSender().sendMessage("     §a✔ §f配置文件加载完成");
        Bukkit.getConsoleSender().sendMessage("     §a✔ §f数据存储初始化完成");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getConsoleSender().sendMessage("     §a✔ §f成功挂钩 PlaceholderAPI");
        }
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§c▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§4■■■■■■■■■■■■■■■■■■■■■■■■■");
        Bukkit.getConsoleSender().sendMessage("§f[ §cWarZFracture §f] §a插件已启用!");
        Bukkit.getConsoleSender().sendMessage("§f[ §cWarZFracture §f] §e骨折系统已启动");
        Bukkit.getConsoleSender().sendMessage("§f[ §cWarZFracture §f] §7By Crazy_Jky QQ: 1285988665");
        Bukkit.getConsoleSender().sendMessage("§4■■■■■■■■■■■■■■■■■■■■■■■■■");

        // 注册命令处理器
        this.getCommand("fracture").setExecutor(new CommandHandler(this));
        // 注册fz别名命令处理器
        this.getCommand("fz").setExecutor(new CommandHandler(this));
        // 注册Tab补全
        this.getCommand("fracture").setTabCompleter(new FractureTabCompleter(this)); // Pass plugin instance
        this.getCommand("fz").setTabCompleter(new FractureTabCompleter(this)); // Pass plugin instance

        // 加载配置文件
        ConfigManager.loadConfig();

        // 根据配置加载语言文件
        LangManager.loadLangFile(language);

        // 注册事件监听器，传入插件实例
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // 加载玩家骨折数据
        FractureDataManager.loadPlayerData();

        // 初始化玩家骨折数据（数量结构）
        initFractureData();

        // 注册跳跃伤害监听器
        getServer().getPluginManager().registerEvents(new JumpDamageListener(this), this);

        // 注册 PlaceholderAPI 扩展
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FracturePlaceholders(this).register();
            getLogger().info("成功注册 PlaceholderAPI 扩展！");
        } else {
            getLogger().warning("未找到 PlaceholderAPI，变量功能将不可用！");
        }

        // 添加定期保存任务
        Bukkit.getScheduler().runTaskTimer(this, FractureDataManager::savePlayerData, 6000L, 6000L); // 每5分钟保存一次
    }

    @Override
    public void onDisable() {
        // 确保关服时保存所有数据
        FractureDataManager.savePlayerDataNow();

        // 美化后的卸载信息
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§c▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("     §f[§c§lWarZ§f§lFracture§f] §c插件已卸载");
        Bukkit.getConsoleSender().sendMessage("     §b版本: §fv1.0");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("     §c✘ §f骨折系统已关闭");
        Bukkit.getConsoleSender().sendMessage("     §a✔ §f数据已保存完成");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§c▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§4■■■■■■■■■■■■■■■■■■■■■■■■■");
        Bukkit.getConsoleSender().sendMessage("§f[ §cWarZFracture §f] §a插件已卸载!");
        Bukkit.getConsoleSender().sendMessage("§f[ §cWarZFracture §f] §e骨折系统已关闭");
        Bukkit.getConsoleSender().sendMessage("§f[ §cWarZFracture §f] §7By Crazy_Jky QQ: 1285988665");
        Bukkit.getConsoleSender().sendMessage("§4■■■■■■■■■■■■■■■■■■■■■■■■■");
    }

    // 获取插件实例
    public static WarZFracture getInstance() {
        return instance;
    }

    private PainkillerEffectListener painkillerEffectListener;

    public PainkillerEffectListener getPainkillerEffectListener() {
        return painkillerEffectListener;
    }

    // 初始化玩家骨折数据
    private void initFractureData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!FractureDataManager.hasFractureData(player)) {
                FractureDataManager.setHeadFractures(player, 0); // 修改为 setHeadFractures
                FractureDataManager.setArmFractures(player, 0); // 修改为 setArmFractures
                FractureDataManager.setLegFractures(player, 0); // 修改为 setLegFractures
            }
        }
    }
}
