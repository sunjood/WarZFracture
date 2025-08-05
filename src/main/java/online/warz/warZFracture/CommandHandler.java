package online.warz.warZFracture;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;

public class CommandHandler implements CommandExecutor {

    private final WarZFracture plugin;

    public CommandHandler(WarZFracture plugin) {
        this.plugin = plugin;
    }

    // 在现有的onCommand方法中添加help命令处理
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LangManager.getMessage("command.player_only"));
            return true;
        }

        Player player = (Player) sender;

        // 首先检查是否是help命令
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            // 显示帮助信息
            showHelp(player);
            return true;
        }

        // 添加新命令处理
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("warzfracture.reload")) {
                player.sendMessage("§c你没有权限使用此命令！");
                return true;
            }
            plugin.reloadConfig();
            LangManager.loadLangFile(plugin.getConfig().getString("language", "zh_CN"));
            player.sendMessage(LangManager.getMessage("fracture.reload_config"));
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
            if (!player.hasPermission("warzfracture.give")) {
                player.sendMessage("§c你没有权限使用此命令！");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage("§c用法: /fz give <painkiller|firstaidkit> [类型] [数量]");
                return true;
            }

            ItemStack item;
            if (args[1].equalsIgnoreCase("painkiller")) {
                String type = args.length >= 3 ? args[2] : "default";
                item = ItemUtils.createPainkiller(type);
                if (args.length >= 4) {
                    try {
                        int amount = Integer.parseInt(args[3]);
                        item.setAmount(amount);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§c数量参数无效！");
                        return true;
                    }
                }
            } else if (args[1].equalsIgnoreCase("firstaidkit")) {
                String type = args.length >= 3 ? args[2] : "basic";
                item = ItemUtils.createFirstAidKit(type);
                if (args.length >= 4) {
                    try {
                        int amount = Integer.parseInt(args[3]);
                        item.setAmount(amount);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§c数量参数无效！");
                        return true;
                    }
                }
            } else {
                player.sendMessage("§c无效的物品类型！");
                return true;
            }
            player.getInventory().addItem(item);
            return true;
        }

        // 处理status命令
        if (args.length == 1 && args[0].equalsIgnoreCase("status")) {
            showFractureStatus(player);
            return true;
        }

        // 处理heal命令
        if (args.length == 1 && args[0].equalsIgnoreCase("heal")) {
            healAllFractures(player);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("heal")) {
            String bodyPart = args[1].toLowerCase();
            healSpecificFracture(player, bodyPart);
            return true;
        }

        // 如果命令不匹配任何已知格式，显示未知命令消息
        player.sendMessage(LangManager.getMessage("command.unknown"));
        return true;
    }

    // 显示骨折状态
    // 修改显示骨折状态的方法
    private void showFractureStatus(Player player) {
        int head = FractureDataManager.getHeadFractures(player);
        int arm = FractureDataManager.getArmFractures(player);
        int leg = FractureDataManager.getLegFractures(player);

        // 直接使用硬编码消息，不再使用语言文件
        player.sendMessage("§7头部骨折：" + (head > 0 ? head + "处" : "无"));
        player.sendMessage("§7手臂骨折：" + (arm > 0 ? arm + "处" : "无"));
        player.sendMessage("§7腿部骨折：" + (leg > 0 ? leg + "处" : "无"));
    }

    // 治愈所有骨折
    private void healAllFractures(Player player) {
        // 检查玩家是否有任何骨折
        boolean hasAnyFracture = FractureDataManager.getHeadFractures(player) > 0 ||
                FractureDataManager.getArmFractures(player) > 0 ||
                FractureDataManager.getLegFractures(player) > 0;

        if (!hasAnyFracture) {
            player.sendMessage(LangManager.getMessage("fracture.no_fracture"));
            return;
        }

        // 清除所有骨折数据
        FractureDataManager.setHeadFractures(player, 0);
        FractureDataManager.setArmFractures(player, 0);
        FractureDataManager.setLegFractures(player, 0);

        // 移除所有骨折相关的药水效果
        removeAllFractureEffects(player);

        // 恢复最大生命值
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);

        // 恢复默认移动速度
        player.setWalkSpeed(0.2f);

        player.sendMessage(LangManager.getMessage("fracture.healed_all"));
        // 添加保存数据的调用
        FractureDataManager.savePlayerData();
    }

    // 添加一个新方法来移除所有骨折效果
    private void removeAllFractureEffects(Player player) {
        // 移除所有骨折相关的药水效果
        player.removePotionEffect(PotionEffectType.CONFUSION);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    // 添加显示帮助的方法
    private void showHelp(Player player) {
        player.sendMessage(LangManager.getMessage("command.help.header"));
        player.sendMessage(LangManager.getMessage("command.help.status"));
        player.sendMessage(LangManager.getMessage("command.help.heal_all"));
        player.sendMessage(LangManager.getMessage("command.help.heal_part"));
        player.sendMessage(LangManager.getMessage("command.help.reload"));
        player.sendMessage(LangManager.getMessage("command.help.give_painkiller"));
        player.sendMessage(LangManager.getMessage("command.help.give_firstaid"));
        player.sendMessage(LangManager.getMessage("command.help.help"));
        player.sendMessage(LangManager.getMessage("command.help.footer"));
    }

    // 治愈特定部位的骨折
    private void healSpecificFracture(Player player, String bodyPart) {
        switch (bodyPart) {
            case "head":
                if (FractureDataManager.getHeadFractures(player) == 0) {
                    player.sendMessage(LangManager.getMessage("fracture.no_fracture"));
                    return;
                }
                FractureDataManager.setHeadFractures(player, 0);
                // 移除头部骨折效果
                player.removePotionEffect(PotionEffectType.CONFUSION);
                player.removePotionEffect(PotionEffectType.WEAKNESS);
                // 恢复最大生命值
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                player.sendMessage(LangManager.getMessage("fracture.healed_head"));
                break;
            case "arm":
                if (FractureDataManager.getArmFractures(player) == 0) {
                    player.sendMessage(LangManager.getMessage("fracture.no_fracture"));
                    return;
                }
                FractureDataManager.setArmFractures(player, 0);
                // 移除手臂骨折效果
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                player.removePotionEffect(PotionEffectType.WEAKNESS);
                player.sendMessage(LangManager.getMessage("fracture.healed_arm"));
                break;
            case "leg":
                if (FractureDataManager.getLegFractures(player) == 0) {
                    player.sendMessage(LangManager.getMessage("fracture.no_fracture"));
                    return;
                }
                FractureDataManager.setLegFractures(player, 0);
                // 移除腿部骨折效果
                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.JUMP);
                // 恢复默认移动速度
                player.setWalkSpeed(0.2f);
                player.sendMessage(LangManager.getMessage("fracture.healed_leg"));
                break;
            case "all":
                healAllFractures(player);
                break;
            default:
                player.sendMessage(LangManager.getMessage("fracture.invalid_body_part"));
                break;
        }

        // 保存玩家数据
        FractureDataManager.savePlayerData();
    }

    // 添加给予止痛药的方法
    // Remove these unused methods
    /*
     * private void givePainkiller(Player player) {
     * ItemStack painkiller = ItemUtils.createPainkiller();
     * player.getInventory().addItem(painkiller);
     * player.sendMessage(LangManager.getMessage("command.item.painkiller_received")
     * );
     * }
     * 
     * private void giveFirstAidKit(Player player) {
     * ItemStack firstAidKit = ItemUtils.createFirstAidKit();
     * player.getInventory().addItem(firstAidKit);
     * player.sendMessage(LangManager.getMessage("command.item.firstaidkit_received"
     * ));
     * }
     */
}
