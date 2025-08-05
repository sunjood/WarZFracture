package online.warz.warZFracture;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class FracturePlaceholders extends PlaceholderExpansion {

    private final WarZFracture plugin;

    public FracturePlaceholders(WarZFracture plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "fracture";
    }

    @Override
    public String getAuthor() {
        return "Crazy_Jky";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; // 插件重载时不需要重新注册
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        // 获取头部骨折数量
        if (identifier.equals("head_count")) {
            return String.valueOf(FractureDataManager.getHeadFractures(player));
        }

        // 获取手臂骨折数量
        if (identifier.equals("arm_count")) {
            return String.valueOf(FractureDataManager.getArmFractures(player));
        }

        // 获取腿部骨折数量
        if (identifier.equals("leg_count")) {
            return String.valueOf(FractureDataManager.getLegFractures(player));
        }

        // 获取总骨折数量
        if (identifier.equals("total_count")) {
            int total = FractureDataManager.getHeadFractures(player) +
                    FractureDataManager.getArmFractures(player) +
                    FractureDataManager.getLegFractures(player);
            return String.valueOf(total);
        }

        // 获取头部骨折状态（有/无）
        if (identifier.equals("head_status")) {
            return FractureDataManager.getHeadFractures(player) > 0 ? "§c有" : "§a无";
        }

        // 获取手臂骨折状态（有/无）
        if (identifier.equals("arm_status")) {
            return FractureDataManager.getArmFractures(player) > 0 ? "§c有" : "§a无";
        }

        // 获取腿部骨折状态（有/无）
        if (identifier.equals("leg_status")) {
            return FractureDataManager.getLegFractures(player) > 0 ? "§c有" : "§a无";
        }

        // 获取止痛药剩余时间
        if (identifier.equals("painkiller_time_left")) {
            Integer timeLeft = PainkillerEffectListener.getPainkillerTimeLeft(player);
            return timeLeft != null ? String.valueOf(timeLeft / 20) : "0";
        }

        return null; // 未知占位符
    }
}