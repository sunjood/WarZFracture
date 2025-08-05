package online.warz.warZFracture;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.attribute.Attribute;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // 清除所有骨折数据
        FractureDataManager.setHeadFractures(player, 0);
        FractureDataManager.setArmFractures(player, 0);
        FractureDataManager.setLegFractures(player, 0);
        
        // 清除所有药水效果
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // 恢复最大生命值
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        
        // 恢复默认移动速度
        player.setWalkSpeed(0.2f);
    }
}