package online.warz.warZFracture;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    // 创建止痛药
    public static ItemStack createPainkiller(String type) {
        FileConfiguration config = WarZFracture.getInstance().getConfig();
        String path = "items.painkillers." + type + ".";

        if (!config.contains(path)) {
            type = "default";
            path = "items.painkillers." + type + ".";
        }

        String id = config.getString(path + "id", "minecraft:potion");
        String name = config.getString(path + "name", "§c止痛药");
        int customModelData = config.getInt(path + "customModelData", 12345);
        List<String> lore = config.getStringList(path + "lore");
        int effectDuration = config.getInt(path + "effect_duration", 600);

        Material material = Material.valueOf(id.replace("minecraft:", "").toUpperCase());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setCustomModelData(customModelData);

            // Add NBT tag
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(WarZFracture.getInstance(), "painkiller_type"),
                    PersistentDataType.STRING,
                    type);

            item.setItemMeta(meta);
        }

        return item;
    }

    // 创建手术包
    public static ItemStack createFirstAidKit(String type) {
        FileConfiguration config = WarZFracture.getInstance().getConfig();
        String path = "items.firstAidKit." + type + ".";

        if (!config.contains(path)) {
            type = "basic";
            path = "items.firstAidKit." + type + ".";
        }

        String id = config.getString(path + "id", "minecraft:book");
        String name = config.getString(path + "name", "§a医疗手术包");
        int customModelData = config.getInt(path + "customModelData", 12348);
        List<String> lore = config.getStringList(path + "lore");
        int durability = config.getInt(path + "durability", 3);

        Material material = Material.valueOf(id.replace("minecraft:", "").toUpperCase());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setCustomModelData(customModelData);

            // 处理lore中的占位符
            List<String> processedLore = new ArrayList<>();
            for (String line : lore) {
                processedLore.add(line.replace("%uses%", String.valueOf(durability)));
            }
            meta.setLore(processedLore);

            // 添加NBT标签
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(WarZFracture.getInstance(), "firstaidkit_type"),
                    PersistentDataType.STRING,
                    type);

            meta.getPersistentDataContainer().set(
                    new NamespacedKey(WarZFracture.getInstance(), "uses_left"),
                    PersistentDataType.INTEGER,
                    durability);

            item.setItemMeta(meta);
        }

        return item;
    }

    // 检查物品类型
    public static String getPainkillerType(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return null;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(
                new NamespacedKey(WarZFracture.getInstance(), "painkiller_type"),
                PersistentDataType.STRING);
    }

    public static String getFirstAidKitType(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return null;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(
                new NamespacedKey(WarZFracture.getInstance(), "firstaidkit_type"),
                PersistentDataType.STRING);
    }

    public static int getFirstAidKitUsesLeft(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return 0;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        Integer uses = container.get(
                new NamespacedKey(WarZFracture.getInstance(), "uses_left"),
                PersistentDataType.INTEGER);
        return uses != null ? uses : 0;
    }

    public static void setFirstAidKitUsesLeft(ItemStack item, int uses) {
        if (item == null || !item.hasItemMeta())
            return;

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
                new NamespacedKey(WarZFracture.getInstance(), "uses_left"),
                PersistentDataType.INTEGER,
                uses);

        // 更新lore中的使用次数
        updateFirstAidKitLore(item);

        // 应用更新后的ItemMeta到物品
        item.setItemMeta(meta);

        // 确保物品在玩家手中更新
        String type = getFirstAidKitType(item);
        if (type != null) {
            FileConfiguration config = WarZFracture.getInstance().getConfig();
            String path = "items.firstAidKit." + type + ".";
            String name = config.getString(path + "name", "§a医疗手术包");
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
    }

    public static void updateFirstAidKitLore(ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return;

        ItemMeta meta = item.getItemMeta();
        int uses = getFirstAidKitUsesLeft(item);

        // 更新lore中的使用次数
        List<String> lore = meta.getLore();
        if (lore != null) {
            List<String> newLore = new ArrayList<>();
            for (String line : lore) {
                newLore.add(line.replace("%uses%", String.valueOf(uses)));
            }
            meta.setLore(newLore);
        }

        item.setItemMeta(meta);
    }
}
