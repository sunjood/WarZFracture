package online.warz.warZFracture;

import org.bukkit.entity.Player;

public class FractureStatus {

    // 显示玩家的骨折状态（数量）
    public static void showFractureStatus(Player player) {
        int headFractureCount = FractureDataManager.getHeadFractures(player);  // 获取头部骨折数
        int armFractureCount = FractureDataManager.getArmFractures(player);    // 获取手臂骨折数
        int legFractureCount = FractureDataManager.getLegFractures(player);    // 获取腿部骨折数

        // 显示骨折数量，若为0则显示“无”
        player.sendMessage(LangManager.getMessageWithPlaceholder("fracture.status_head", headFractureCount > 0 ? headFractureCount + "处" : "无"));
        player.sendMessage(LangManager.getMessageWithPlaceholder("fracture.status_arm", armFractureCount > 0 ? armFractureCount + "处" : "无"));
        player.sendMessage(LangManager.getMessageWithPlaceholder("fracture.status_leg", legFractureCount > 0 ? legFractureCount + "处" : "无"));
    }
}
