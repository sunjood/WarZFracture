package online.warz.warZFracture;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.HandlerList;

public class FractureEffectListener implements Listener {

    private final WarZFractureConfig config;

    public FractureEffectListener(WarZFractureConfig config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // 检查玩家是否有止痛药效果，如果有则不应用骨折效果
        if (FractureDataManager.hasFractureData(player) &&
                FractureDataManager.getFractureData(player).hasPainkillerEffect()) {
            return;
        }

        // 头部骨折效果
        if (FractureDataManager.getHeadFractures(player) > 0) {
            applyHeadFractureEffect(player);
        }

        // 手臂骨折效果
        if (FractureDataManager.getArmFractures(player) > 0) {
            applyArmFractureEffect(player);
        }

        // 腿部骨折效果
        if (FractureDataManager.getLegFractures(player) > 0) {
            applyLegFractureEffect(player);
        }
    }

    // 在 onPlayerDamage 方法中添加保存数据的调用
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // 头部骨折额外伤害处理
            if (FractureDataManager.getHeadFractures(player) > 0) {
                // 增加受到的伤害
                double extraDamage = event.getDamage() * 1.2; // 20%额外伤害
                event.setDamage(extraDamage);
            }

            // 保存玩家数据
            FractureDataManager.savePlayerData();
        }
    }

    // 头部骨折效果
    private void applyHeadFractureEffect(Player player) {
        // 视觉模糊效果
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.CONFUSION,
                100, // 持续时间（tick）
                1 // 效果等级
        ));

        // 降低最大生命值
        double currentMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double reducedHealth = currentMaxHealth * 0.8; // 减少20%最大生命值
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(reducedHealth);

        // 虚弱效果
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.WEAKNESS,
                200, // 持续时间（tick）
                1 // 效果等级
        ));
    }

    // 手臂骨折效果
    private void applyArmFractureEffect(Player player) {
        // 降低攻击伤害
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.WEAKNESS,
                300, // 持续时间（tick）
                2 // 效果等级
        ));

        // 挖掘疲劳
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_DIGGING,
                300, // 持续时间（tick）
                1 // 效果等级
        ));
    }

    // 腿部骨折效果
    private void applyLegFractureEffect(Player player) {
        // 显著降低移动速度
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW,
                400, // 持续时间（tick）
                3 // 效果等级
        ));

        // 限制跳跃能力
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.JUMP,
                400, // 持续时间（tick）
                -2 // 负数表示降低跳跃能力
        ));
    }
}
