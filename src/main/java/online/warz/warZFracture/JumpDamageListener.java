package online.warz.warZFracture;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class JumpDamageListener implements Listener {

    private final WarZFracture plugin;

    public JumpDamageListener(WarZFracture plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // 检查玩家是否有腿部骨折且不在止痛药效果中
        if (FractureDataManager.getLegFractures(player) > 0
                && !FractureDataManager.getFractureData(player).hasPainkillerEffect()) {
            Location from = event.getFrom();
            Location to = event.getTo();

            // 检查玩家是否在跳跃（Y坐标增加且玩家在地面上）
            if (to != null && to.getY() > from.getY() && from.getBlock().getType().isSolid() && !player.isFlying()) {
                // 确保Y坐标增加不是因为走上楼梯或其他方块
                if (to.getY() - from.getY() > 0.4) { // 跳跃通常Y坐标增加约0.42
                    // 减少玩家当前血量，但不低于1点
                    double currentHealth = player.getHealth();
                    if (currentHealth > 1.0) {
                        player.setHealth(Math.max(1.0, currentHealth - 1.0));
                        player.sendMessage("§c你的腿部骨折了，跳跃会造成疼痛！");
                    }
                }
            }
        }
    }
}