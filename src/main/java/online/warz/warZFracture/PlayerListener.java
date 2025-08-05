package online.warz.warZFracture;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Random;

public class PlayerListener implements Listener {

    private final WarZFracture plugin;

    // 通过插件实例初始化
    public PlayerListener(WarZFracture plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                double fallHeight = player.getFallDistance();
                double minHeight = plugin.getConfig().getDouble("fall.minHeight");

                // 如果掉落高度超过最小高度，必定造成腿部骨折
                if (fallHeight > minHeight) {
                    // 增加腿部骨折计数
                    FractureDataManager.setLegFractures(player, FractureDataManager.getLegFractures(player) + 1);
                    // 应用效果
                    applyFractureEffect(player, "legFracture");
                    player.sendMessage("§c你从高处掉落，造成了腿部骨折！");
                    // 保存数据
                    FractureDataManager.savePlayerData();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // 获取配置文件中的骨折概率
            double headProb = plugin.getConfig().getDouble("fracture.headFracture.probability");
            double armProb = plugin.getConfig().getDouble("fracture.armFracture.probability");
            double legProb = plugin.getConfig().getDouble("fracture.legFracture.probability");

            Random rand = new Random();
            double random = rand.nextDouble();  // 生成 0 到 1 之间的随机数

            // 根据概率判断造成哪个部位骨折
            if (random < headProb) {
                // 增加头部骨折计数
                FractureDataManager.setHeadFractures(player, FractureDataManager.getHeadFractures(player) + 1);
                // 应用效果
                applyFractureEffect(player, "headFracture");
                player.sendMessage("§c你受到了攻击，造成了头部骨折！");
                FractureDataManager.savePlayerData();
            } else if (random < headProb + armProb) {
                // 增加手臂骨折计数
                FractureDataManager.setArmFractures(player, FractureDataManager.getArmFractures(player) + 1);
                // 应用效果
                applyFractureEffect(player, "armFracture");
                player.sendMessage("§c你受到了攻击，造成了手臂骨折！");
                FractureDataManager.savePlayerData();
            } else if (random < headProb + armProb + legProb) {
                // 增加腿部骨折计数
                FractureDataManager.setLegFractures(player, FractureDataManager.getLegFractures(player) + 1);
                // 应用效果
                applyFractureEffect(player, "legFracture");
                player.sendMessage("§c你受到了攻击，造成了腿部骨折！");
                FractureDataManager.savePlayerData();
            }
        }
    }

    // 使用一个计数器来减少效果应用的频率
    private int moveCounter = 0;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // 每20次移动才应用一次效果，减少处理频率
        moveCounter++;
        if (moveCounter % 20 != 0) {
            return;
        }
        
        Player player = event.getPlayer();
    
        // 获取骨折类型并应用负面效果
        if (FractureDataManager.getHeadFractures(player) > 0) {
            applyFractureEffectWithoutSave(player, "headFracture");
        }
    
        if (FractureDataManager.getArmFractures(player) > 0) {
            applyFractureEffectWithoutSave(player, "armFracture");
        }
    
        if (FractureDataManager.getLegFractures(player) > 0) {
            applyFractureEffectWithoutSave(player, "legFracture");
        }
    }
    
    // 添加一个不保存数据的效果应用方法
    private void applyFractureEffectWithoutSave(Player player, String fractureType) {
        // 复制原方法的代码，但移除最后的savePlayerData调用
        // 根据配置文件应用骨折效果
        String effect = plugin.getConfig().getString("fracture." + fractureType + ".effect");
        // 获取配置文件中的持续时间
        int duration = plugin.getConfig().getInt("fracture." + fractureType + ".duration");
    
        // 只应用效果，不增加骨折计数
        switch (effect) {
            case "reduce max health by 1":
                double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth - 1);  // 头部骨折，减少 1 点最大生命值
                break;
    
            case "slow down player":
                player.setWalkSpeed(0.2f);  // 手臂骨折，减慢玩家的行走速度
                break;
    
            case "reduce running speed":
                player.setWalkSpeed(0.2f);  // 腿部骨折，减少跑步速度
                break;
        }
    
        // 设定效果持续时间（删除了重复的duration变量定义）
        new BukkitRunnable() {
            @Override
            public void run() {
                // 恢复玩家的最大生命值和行走速度
                if (fractureType.equals("headFracture")) {
                    double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth + 2);
                }
                if (fractureType.equals("armFracture") || fractureType.equals("legFracture")) {
                    player.setWalkSpeed(0.2f);
                }
            }
        }.runTaskLater(plugin, duration * 20L);
    
        // 添加保存数据的调用
        // 移除这一行
        // FractureDataManager.savePlayerData();
    }

    // 添加玩家死亡事件处理
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // 清除所有骨折数据
        FractureDataManager.setHeadFractures(player, 0);
        FractureDataManager.setArmFractures(player, 0);
        FractureDataManager.setLegFractures(player, 0);
        
        // 恢复默认属性
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        player.setWalkSpeed(0.2f);
        
        // 移除所有骨折相关的药水效果
        player.removePotionEffect(PotionEffectType.CONFUSION);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.JUMP);
        
        // 保存数据
        FractureDataManager.savePlayerData();
    }

    // 添加回原始的applyFractureEffect方法
    private void applyFractureEffect(Player player, String fractureType) {
        // 根据配置文件应用骨折效果
        String effect = plugin.getConfig().getString("fracture." + fractureType + ".effect");
        int duration = plugin.getConfig().getInt("fracture." + fractureType + ".duration");

        // 应用效果
        switch (effect) {
            case "reduce max health by 1":
                double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth - 1);
                break;

            case "slow down player":
                player.setWalkSpeed(0.2f);
                break;

            case "reduce running speed":
                player.setWalkSpeed(0.2f);
                break;
        }

        // 设定效果持续时间
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fractureType.equals("headFracture")) {
                    double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth + 2);
                }
                if (fractureType.equals("armFracture") || fractureType.equals("legFracture")) {
                    player.setWalkSpeed(0.2f);
                }
            }
        }.runTaskLater(plugin, duration * 20L);
    }
}
