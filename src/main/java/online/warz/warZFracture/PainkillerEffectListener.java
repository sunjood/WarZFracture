package online.warz.warZFracture;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.block.Action;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PainkillerEffectListener implements Listener {
    private final Map<UUID, BukkitTask> usingPainkiller = new HashMap<>();
    private final Map<UUID, Integer> painkillerEffects = new HashMap<>();
    private final Map<UUID, Listener> painkillerListeners = new HashMap<>();

    public static Integer getPainkillerTimeLeft(Player player) {
        return WarZFracture.getInstance().getPainkillerEffectListener().painkillerEffects.get(player.getUniqueId());
    }

    public PainkillerEffectListener() {
        // Empty constructor
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        String type = ItemUtils.getPainkillerType(item);

        if (type == null)
            return;

        event.setCancelled(true);

        if (usingPainkiller.containsKey(player.getUniqueId())) {
            player.sendMessage("§c你正在使用止痛药！");
            return;
        }

        FileConfiguration config = WarZFracture.getInstance().getConfig();
        String path = "items.painkillers." + type + ".";
        int usageTime = config.getInt(path + "usage_time", 60);
        int effectDuration = config.getInt(path + "effect_duration", 600);
        String sound = config.getString(path + "sound", "ENTITY_PLAYER_LEVELUP");

        // 创建监听器来检测玩家切换物品和死亡事件
        Listener listener = new Listener() {
            @EventHandler
            public void onPlayerItemHeld(PlayerItemHeldEvent e) {
                if (e.getPlayer().equals(player)) {
                    BukkitTask task = usingPainkiller.get(player.getUniqueId());
                    if (task != null) {
                        task.cancel();
                        usingPainkiller.remove(player.getUniqueId());
                        painkillerListeners.remove(player.getUniqueId());
                        HandlerList.unregisterAll(this);
                        player.sendMessage("§c止痛药使用已取消！");
                    }
                }
            }

            @EventHandler
            public void onPlayerDeath(PlayerDeathEvent e) {
                if (e.getEntity().equals(player)) {
                    BukkitTask task = usingPainkiller.get(player.getUniqueId());
                    if (task != null) {
                        task.cancel();
                        usingPainkiller.remove(player.getUniqueId());
                        painkillerListeners.remove(player.getUniqueId());
                        HandlerList.unregisterAll(this);
                    }
                }
            }
        };

        // 注册监听器
        Bukkit.getPluginManager().registerEvents(listener, WarZFracture.getInstance());
        painkillerListeners.put(player.getUniqueId(), listener);

        // 开始使用计时
        AtomicInteger timeLeft = new AtomicInteger(usageTime);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || timeLeft.get() <= 0) {
                    cancel();
                    usingPainkiller.remove(player.getUniqueId());
                    if (player.isOnline()) {
                        applyPainkillerEffect(player, type);
                        if (item.getAmount() > 1) {
                            item.setAmount(item.getAmount() - 1);
                        } else {
                            player.getInventory().setItemInMainHand(null);
                        }
                    }
                    return;
                }

                if (timeLeft.get() % 20 == 0) {
                    player.sendMessage("§e止痛药使用中... §7剩余 " + (timeLeft.get() / 20) + " 秒");
                    player.playSound(player.getLocation(), Sound.valueOf(sound), 1.0f, 1.0f);
                }

                timeLeft.decrementAndGet();
            }
        }.runTaskTimer(WarZFracture.getInstance(), 0L, 1L);

        usingPainkiller.put(player.getUniqueId(), task);
    }

    private void applyPainkillerEffect(Player player, String type) {
        FileConfiguration config = WarZFracture.getInstance().getConfig();
        String path = "items.painkillers." + type + ".";
        int effectDuration = config.getInt(path + "effect_duration", 600);

        // 立即设置止痛药效果标记，使玩家暂时免疫骨折效果
        FractureDataManager.setPainkillerEffect(player, true);

        // 移除现有的负面效果
        player.removePotionEffect(PotionEffectType.CONFUSION);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        player.removePotionEffect(PotionEffectType.JUMP); // 移除腿部骨折导致的跳跃提升效果
        player.setWalkSpeed(0.2f); // 恢复默认行走速度

        player.sendMessage("§a止痛药立即生效了！效果将持续 " + (effectDuration / 20) + " 秒");
        painkillerEffects.put(player.getUniqueId(), effectDuration);

        // 设置定时任务，在效果结束后移除免疫
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    FractureDataManager.setPainkillerEffect(player, false);
                    player.sendMessage("§c止痛药效果已结束！");
                }
                painkillerEffects.remove(player.getUniqueId());

                // 确保监听器被注销
                Listener listener = painkillerListeners.remove(player.getUniqueId());
                if (listener != null) {
                    HandlerList.unregisterAll(listener);
                }
            }
        }.runTaskLater(WarZFracture.getInstance(), effectDuration);
    }
}
