package online.warz.warZFracture;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class WarZFractureConfig {

    private final FileConfiguration config;

    // 构造函数，接收插件实例并加载配置
    public WarZFractureConfig(WarZFracture plugin) {
        this.config = plugin.getConfig();
        loadConfig();
    }

    // 加载配置文件内容
    private void loadConfig() {
        // 读取骨折效果的配置
        int legFractureEffectAmplifier = config.getInt("fracture.legFracture.effectAmplifier", 1); // 默认值1
        int armFractureEffectAmplifier = config.getInt("fracture.armFracture.effectAmplifier", 1); // 默认值1
        double headFractureMaxHealthPenalty = config.getDouble("fracture.headFracture.maxHealthPenalty", 2.0); // 默认值2.0

        // 你可以在这里使用这些值进行进一步的配置加载
    }

    // 获取配置项的方法
    public int getLegFractureEffectAmplifier() {
        return config.getInt("fracture.legFracture.effectAmplifier", 1); // 默认值1
    }

    public int getArmFractureEffectAmplifier() {
        return config.getInt("fracture.armFracture.effectAmplifier", 1); // 默认值1
    }

    public double getHeadFractureMaxHealthPenalty() {
        return config.getDouble("fracture.headFracture.maxHealthPenalty", 2.0); // 默认值2.0
    }

    // 其他现有配置方法
    public double getHeadFractureProbability() {
        return config.getDouble("fracture.headFracture.probability", 0.05);
    }

    public double getArmFractureProbability() {
        return config.getDouble("fracture.armFracture.probability", 0.15);
    }

    public double getLegFractureProbability() {
        return config.getDouble("fracture.legFracture.probability", 0.20);
    }

    public String getHeadFractureEffect() {
        return config.getString("fracture.headFracture.effect", "reduce max health by 1");
    }

    public int getHeadFractureDuration() {
        return config.getInt("fracture.headFracture.duration", 300);
    }

    public String getArmFractureEffect() {
        return config.getString("fracture.armFracture.effect", "slow down player");
    }

    public int getArmFractureDuration() {
        return config.getInt("fracture.armFracture.duration", 600);
    }

    public String getLegFractureEffect() {
        return config.getString("fracture.legFracture.effect", "reduce running speed");
    }

    public int getLegFractureDuration() {
        return config.getInt("fracture.legFracture.duration", 600);
    }

    public int getMinFallHeight() {
        return config.getInt("fall.minHeight", 10);
    }

    // 其他物品配置方法
    public String getPainkillerId() {
        return config.getString("items.painkillers.id", "minecraft:potion");
    }

    public int getPainkillerCustomModelData() {
        return config.getInt("items.painkillers.customModelData", 12345);
    }

    public String getPainkillerLore() {
        return config.getString("items.painkillers.lore", "Painkiller");
    }

    public boolean isPainkillerIdEnabled() {
        return config.getBoolean("items.painkillers.idEnabled", true);
    }

    public boolean isPainkillerCustomModelDataEnabled() {
        return config.getBoolean("items.painkillers.customModelDataEnabled", true);
    }

    // 获取头部骨折最大数量
    public int getHeadFractureMaxCount() {
        return config.getInt("fracture.headFracture.maxFractures", 1); // 默认值1
    }

    // 获取手臂骨折最大数量
    public int getArmFractureMaxCount() {
        return config.getInt("fracture.armFracture.maxFractures", 2); // 默认值2
    }

    // 获取腿部骨折最大数量
    public int getLegFractureMaxCount() {
        return config.getInt("fracture.legFracture.maxFractures", 2); // 默认值2
    }

    // 保留这个 isPainkillerLoreEnabled 方法
    public boolean isPainkillerLoreEnabled() {
        return config.getBoolean("items.painkillers.loreEnabled", true);
    }

    public int getPainkillerDuration() {
        return config.getInt("items.painkillers.default.duration", 200);
    }

    public int getPainkillerEffectDuration() {
        return config.getInt("items.painkillers.effect_duration", 600);
    }

    public int getPainkillerEffectDuration(String type) {
        return config.getInt("items.painkillers." + type + ".effect_duration",
                config.getInt("items.painkillers.default.effect_duration", 600));
    }

    public int getPainkillerUsageTime(String type) {
        return config.getInt("items.painkillers." + type + ".usage_time",
                config.getInt("items.painkillers.default.usage_time", 60));
    }

    public String getFirstAidKitId() {
        return config.getString("items.firstAidKit.id", "minecraft:bandage");
    }

    public int getFirstAidKitCustomModelData() {
        return config.getInt("items.firstAidKit.customModelData", 67890);
    }

    public String getFirstAidKitLore() {
        return config.getString("items.firstAidKit.lore", "First Aid Kit");
    }

    public boolean isFirstAidKitIdEnabled() {
        return config.getBoolean("items.firstAidKit.idEnabled", true);
    }

    public boolean isFirstAidKitCustomModelDataEnabled() {
        return config.getBoolean("items.firstAidKit.customModelDataEnabled", true);
    }

    public boolean isFirstAidKitLoreEnabled() {
        return config.getBoolean("items.firstAidKit.loreEnabled", true);
    }

    public int getFirstAidKitDurability() {
        return config.getInt("items.firstAidKit.durability", 5);
    }

    public int getFirstAidKitDurability(String type) {
        return config.getInt("items.firstAidKit." + type + ".durability",
                config.getInt("items.firstAidKit.default.durability", 5));
    }

    public int getFirstAidKitHealTime() {
        return config.getInt("items.firstAidKit.healTime", 100);
    }

    public int getFirstAidKitUsageTime(String type) {
        return config.getInt("items.firstAidKit." + type + ".usage_time",
                config.getInt("items.firstAidKit.default.usage_time", 60));
    }

    public String getFirstAidKitSound(String type) {
        return config.getString("items.firstAidKit." + type + ".sound", "ENTITY_PLAYER_LEVELUP");
    }

    // 获取不同类型止痛药的ID
    public String getPainkillerId(String type) {
        return config.getString("items.painkillers." + type + ".id",
                config.getString("items.painkillers.default.id", "minecraft:potion"));
    }

    // 获取不同类型止痛药的CustomModelData
    public int getPainkillerCustomModelData(String type) {
        return config.getInt("items.painkillers." + type + ".customModelData",
                config.getInt("items.painkillers.default.customModelData", 12345));
    }

    // 获取不同类型止痛药的Lore
    public String getPainkillerLore(String type) {
        return config.getString("items.painkillers." + type + ".lore",
                config.getString("items.painkillers.default.lore", "Painkiller"));
    }

    // 获取不同类型手术包的ID
    public String getFirstAidKitId(String type) {
        return config.getString("items.firstAidKit." + type + ".id",
                config.getString("items.firstAidKit.default.id", "minecraft:bandage"));
    }

    // 获取不同类型手术包的CustomModelData
    public int getFirstAidKitCustomModelData(String type) {
        return config.getInt("items.firstAidKit." + type + ".customModelData",
                config.getInt("items.firstAidKit.default.customModelData", 67890));
    }

    // 获取不同类型手术包的Lore
    public String getFirstAidKitLore(String type) {
        return config.getString("items.firstAidKit." + type + ".lore",
                config.getString("items.firstAidKit.default.lore", "First Aid Kit"));
    }

    // 检查是否启用特定类型止痛药的ID检查
    public boolean isPainkillerIdEnabled(String type) {
        return config.getBoolean("items.painkillers." + type + ".idEnabled",
                config.getBoolean("items.painkillers.default.idEnabled", true));
    }

    // 检查是否启用特定类型止痛药的CustomModelData检查
    public boolean isPainkillerCustomModelDataEnabled(String type) {
        return config.getBoolean("items.painkillers." + type + ".customModelDataEnabled",
                config.getBoolean("items.painkillers.default.customModelDataEnabled", true));
    }

    // 检查是否启用特定类型止痛药的Lore检查
    public boolean isPainkillerLoreEnabled(String type) {
        return config.getBoolean("items.painkillers." + type + ".loreEnabled",
                config.getBoolean("items.painkillers.default.loreEnabled", true));
    }

    // 检查是否启用特定类型手术包的ID检查
    public boolean isFirstAidKitIdEnabled(String type) {
        return config.getBoolean("items.firstAidKit." + type + ".idEnabled",
                config.getBoolean("items.firstAidKit.default.idEnabled", true));
    }

    // 检查是否启用特定类型手术包的CustomModelData检查
    public boolean isFirstAidKitCustomModelDataEnabled(String type) {
        return config.getBoolean("items.firstAidKit." + type + ".customModelDataEnabled",
                config.getBoolean("items.firstAidKit.default.customModelDataEnabled", true));
    }

    // 检查是否启用特定类型手术包的Lore检查
    public boolean isFirstAidKitLoreEnabled(String type) {
        return config.getBoolean("items.firstAidKit." + type + ".loreEnabled",
                config.getBoolean("items.firstAidKit.default.loreEnabled", true));
    }
}
