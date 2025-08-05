package online.warz.warZFracture;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FirstAidKitEffectListener implements Listener {
    private final Map<UUID, BukkitTask> usingFirstAidKit = new HashMap<>();

    private final Map<UUID, Listener> firstAidKitListeners = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        String type = ItemUtils.getFirstAidKitType(item);

        if (type == null)
            return;

        event.setCancelled(true);

        if (usingFirstAidKit.containsKey(player.getUniqueId())) {
            player.sendMessage("§c你正在使用手术包！");
            return;
        }

        // 检查是否有骨折需要治疗
        if (FractureDataManager.getHeadFractures(player) == 0 &&
                FractureDataManager.getArmFractures(player) == 0 &&
                FractureDataManager.getLegFractures(player) == 0) {
            player.sendMessage("§c你没有需要治疗的骨折！");
            return;
        }

        // 创建监听器来检测玩家切换物品和死亡事件
        Listener listener = new Listener() {
            @EventHandler
            public void onPlayerItemHeld(PlayerItemHeldEvent e) {
                if (e.getPlayer().equals(player)) {
                    BukkitTask task = usingFirstAidKit.get(player.getUniqueId());
                    if (task != null) {
                        task.cancel();
                        usingFirstAidKit.remove(player.getUniqueId());
                        firstAidKitListeners.remove(player.getUniqueId());
                        HandlerList.unregisterAll(this);
                        player.sendMessage("§c手术包使用已取消！");
                    }
                }
            }

            @EventHandler
            public void onPlayerDeath(PlayerDeathEvent e) {
                if (e.getEntity().equals(player)) {
                    BukkitTask task = usingFirstAidKit.get(player.getUniqueId());
                    if (task != null) {
                        task.cancel();
                        usingFirstAidKit.remove(player.getUniqueId());
                        firstAidKitListeners.remove(player.getUniqueId());
                        HandlerList.unregisterAll(this);
                    }
                }
            }
        };

        // 注册监听器
        Bukkit.getPluginManager().registerEvents(listener, WarZFracture.getInstance());
        firstAidKitListeners.put(player.getUniqueId(), listener);

        FileConfiguration config = WarZFracture.getInstance().getConfig();
        String path = "items.firstAidKit." + type + ".";
        int usageTime = config.getInt(path + "usage_time", 100);
        String sound = config.getString(path + "sound", "ENTITY_PLAYER_LEVELUP");

        // 开始使用计时
        AtomicInteger timeLeft = new AtomicInteger(usageTime);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || timeLeft.get() <= 0) {
                    cancel();
                    usingFirstAidKit.remove(player.getUniqueId());
                    if (player.isOnline()) {
                        healFractures(player, item);
                    }
                    return;
                }

                if (timeLeft.get() % 20 == 0) {
                    player.sendMessage("§e正在治疗骨折... §7剩余 " + (timeLeft.get() / 20) + " 秒");
                    player.playSound(player.getLocation(), Sound.valueOf(sound), 1.0f, 1.0f);
                }

                timeLeft.decrementAndGet();
            }
        }.runTaskTimer(WarZFracture.getInstance(), 0L, 1L);

        usingFirstAidKit.put(player.getUniqueId(), task);
    }

    private void healFractures(Player player, ItemStack item) {
        boolean healed = false;

        if (FractureDataManager.getHeadFractures(player) > 0) {
            FractureDataManager.setHeadFractures(player, 0);
            player.removePotionEffect(PotionEffectType.CONFUSION);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            player.sendMessage(LangManager.getMessage("fracture.healed_head"));
            healed = true;
        }

        if (FractureDataManager.getArmFractures(player) > 0) {
            FractureDataManager.setArmFractures(player, 0);
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
            player.sendMessage(LangManager.getMessage("fracture.healed_arm"));
            healed = true;
        }

        if (FractureDataManager.getLegFractures(player) > 0) {
            FractureDataManager.setLegFractures(player, 0);
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.setWalkSpeed(0.2f);
            player.sendMessage(LangManager.getMessage("fracture.healed_leg"));
            healed = true;
        }

        if (healed) {
            int usesLeft = ItemUtils.getFirstAidKitUsesLeft(item);
            if (usesLeft <= 1) {
                player.getInventory().setItemInMainHand(null);
                player.sendMessage("§c手术包已用尽！");
            } else {
                ItemUtils.setFirstAidKitUsesLeft(item, usesLeft - 1);
                ItemUtils.updateFirstAidKitLore(item);
                player.getInventory().setItemInMainHand(item);
                player.sendMessage("§a手术包剩余使用次数: " + (usesLeft - 1));
            }
            FractureDataManager.savePlayerData();
        }
    }
}
