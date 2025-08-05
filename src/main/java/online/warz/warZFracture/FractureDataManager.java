package online.warz.warZFracture;

import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerItemHeldEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FractureDataManager {

    // 使用 HashMap 存储玩家骨折数据
    private static final Map<UUID, FractureData> playerFractureData = new HashMap<>();
    private static File dataFile;
    private static YamlConfiguration dataConfig;

    // 初始化数据文件和配置
    public static void init(JavaPlugin plugin) {
        // 创建数据文件
        dataFile = new File(plugin.getDataFolder(), "player_fractures.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("player_fractures.yml", false); // 如果文件不存在则创建
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile); // 加载配置文件
    }

    // 在类的顶部添加一个标志变量和最后保存时间
    private static boolean pendingSave = false;
    private static long lastSaveTime = 0;

    // 修改savePlayerData方法
    public static void savePlayerData() {
        // 设置待保存标志
        pendingSave = true;

        // 如果距离上次保存时间不足30秒，则不立即保存
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSaveTime < 30000) { // 30秒
            return;
        }

        // 执行实际保存操作
        savePlayerDataNow();
    }

    // 添加一个立即保存的方法
    public static void savePlayerDataNow() {
        if (!pendingSave) {
            return;
        }

        // 实际保存逻辑
        YamlConfiguration config = new YamlConfiguration();

        // 创建players节点
        config.createSection("players");

        // 遍历所有玩家数据
        for (Map.Entry<UUID, FractureData> entry : playerFractureData.entrySet()) {
            UUID uuid = entry.getKey();
            FractureData data = entry.getValue();

            // 设置玩家数据
            String path = "players." + uuid.toString();
            config.set(path + ".head", data.getHeadFractures());
            config.set(path + ".arm", data.getArmFractures());
            config.set(path + ".leg", data.getLegFractures());
        }

        try {
            config.save(dataFile);
            // 减少日志输出频率
            // WarZFracture.getInstance().getLogger().info("已保存玩家骨折数据");
        } catch (IOException e) {
            e.printStackTrace();
            WarZFracture.getInstance().getLogger().severe("保存玩家骨折数据时出错: " + e.getMessage());
        }

        // 重置标志和时间
        pendingSave = false;
        lastSaveTime = System.currentTimeMillis();
    }

    // 从 YAML 文件加载玩家数据
    public static void loadPlayerData() {
        File file = new File(WarZFracture.getInstance().getDataFolder(), "player_fractures.yml");
        if (!file.exists()) {
            WarZFracture.getInstance().getLogger().info("玩家骨折数据文件不存在，将创建新文件");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains("players")) {
            WarZFracture.getInstance().getLogger().info("玩家骨折数据文件中没有players节点");
            return;
        }

        org.bukkit.configuration.ConfigurationSection playersSection = config.getConfigurationSection("players");
        if (playersSection == null) {
            WarZFracture.getInstance().getLogger().info("无法获取players节点");
            return;
        }

        for (String uuidString : playersSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                int head = config.getInt("players." + uuidString + ".head", 0);
                int arm = config.getInt("players." + uuidString + ".arm", 0);
                int leg = config.getInt("players." + uuidString + ".leg", 0);

                FractureData data = new FractureData(head, arm, leg);
                playerFractureData.put(uuid, data);
                WarZFracture.getInstance().getLogger().info("已加载玩家 " + uuidString + " 的骨折数据");
            } catch (IllegalArgumentException e) {
                WarZFracture.getInstance().getLogger().warning("无效的UUID: " + uuidString);
            }
        }
    }

    // 检查玩家是否已有骨折数据
    public static boolean hasFractureData(Player player) {
        return playerFractureData.containsKey(player.getUniqueId());
    }

    // 获取玩家的头部骨折数量
    public static int getHeadFractures(Player player) {
        if (!hasFractureData(player)) {
            return 0;
        }
        return playerFractureData.get(player.getUniqueId()).getHeadFractures();
    }

    // 获取玩家的手臂骨折数量
    public static int getArmFractures(Player player) {
        if (!hasFractureData(player)) {
            return 0;
        }
        return playerFractureData.get(player.getUniqueId()).getArmFractures();
    }

    // 获取玩家的腿部骨折数量
    public static int getLegFractures(Player player) {
        if (!hasFractureData(player)) {
            return 0;
        }
        return playerFractureData.get(player.getUniqueId()).getLegFractures();
    }

    // 设置玩家的头部骨折数量
    public static void setHeadFractures(Player player, int count) {
        // 获取配置的最大骨折数量
        int maxHeadFractures = WarZFracture.getInstance().getConfig().getInt("fracture.headFracture.maxFractures", 3);
        // 确保不超过最大值
        count = Math.min(count, maxHeadFractures);

        FractureData fractureData = getFractureData(player);
        fractureData.setHeadFractures(count);
    }

    // 设置玩家的手臂骨折数量
    public static void setArmFractures(Player player, int count) {
        // 获取配置的最大骨折数量
        int maxArmFractures = WarZFracture.getInstance().getConfig().getInt("fracture.armFracture.maxFractures", 2);
        // 确保不超过最大值
        count = Math.min(count, maxArmFractures);

        FractureData fractureData = getFractureData(player);
        fractureData.setArmFractures(count);
    }

    // 设置玩家的腿部骨折数量
    public static void setLegFractures(Player player, int count) {
        // 获取配置的最大骨折数量
        int maxLegFractures = WarZFracture.getInstance().getConfig().getInt("fracture.legFracture.maxFractures", 2);
        // 确保不超过最大值
        count = Math.min(count, maxLegFractures);

        FractureData fractureData = getFractureData(player);
        fractureData.setLegFractures(count);
    }

    public static void setPainkillerEffect(Player player, boolean hasEffect) {
        FractureData fractureData = getFractureData(player);
        fractureData.setPainkillerEffect(hasEffect);
    }

    // 获取或创建玩家的骨折数据
    public static FractureData getFractureData(Player player) {
        if (!hasFractureData(player)) {
            playerFractureData.put(player.getUniqueId(), new FractureData());
        }
        return playerFractureData.get(player.getUniqueId());
    }

    // 内部类存储每个玩家的骨折数据
    public static class FractureData {
        private int headFractures = 0;
        private int armFractures = 0;
        private int legFractures = 0;
        private boolean hasPainkillerEffect = false;

        public FractureData() {
        }

        public FractureData(int head, int arm, int leg) {
            this.headFractures = head;
            this.armFractures = arm;
            this.legFractures = leg;
        }

        public int getHeadFractures() {
            return headFractures;
        }

        public void setHeadFractures(int headFractures) {
            this.headFractures = headFractures;
        }

        public int getArmFractures() {
            return armFractures;
        }

        public void setArmFractures(int armFractures) {
            this.armFractures = armFractures;
        }

        public int getLegFractures() {
            return legFractures;
        }

        public void setLegFractures(int legFractures) {
            this.legFractures = legFractures;
        }

        public boolean hasPainkillerEffect() {
            return hasPainkillerEffect;
        }

        public void setPainkillerEffect(boolean hasEffect) {
            this.hasPainkillerEffect = hasEffect;
        }
    }
}
