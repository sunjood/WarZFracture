package online.warz.warZFracture;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FractureTabCompleter implements TabCompleter {

    private final List<String> mainCommands = Arrays.asList("help", "status", "heal", "give");
    private final List<String> bodyParts = Arrays.asList("head", "arm", "leg", "all");
    private final List<String> itemTypes = Arrays.asList("painkiller", "firstaidkit");
    private final List<String> firstAidKitTypes = Arrays.asList("basic", "advanced", "professional");
    private final WarZFracture plugin;

    public FractureTabCompleter(WarZFracture plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            // 第一个参数的补全
            String partialCommand = args[0].toLowerCase();
            completions.addAll(mainCommands.stream()
                    .filter(cmd -> cmd.startsWith(partialCommand))
                    .collect(Collectors.toList()));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "heal":
                    String partialBodyPart = args[1].toLowerCase();
                    completions.addAll(bodyParts.stream()
                            .filter(part -> part.startsWith(partialBodyPart))
                            .collect(Collectors.toList()));
                    break;
                case "give":
                    String partialItemType = args[1].toLowerCase();
                    completions.addAll(itemTypes.stream()
                            .filter(type -> type.startsWith(partialItemType))
                            .collect(Collectors.toList()));
                    break;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                if (args[1].equalsIgnoreCase("firstaidkit")) {
                    String partialType = args[2].toLowerCase();
                    completions.addAll(firstAidKitTypes.stream()
                            .filter(type -> type.startsWith(partialType))
                            .collect(Collectors.toList()));
                } else if (args[1].equalsIgnoreCase("painkiller")) {
                    // 获取所有止痛药类型
                    ConfigurationSection section = plugin.getConfig().getConfigurationSection("items.painkillers");
                    if (section != null) {
                        completions.addAll(section.getKeys(false).stream()
                                .filter(key -> !key.equals("effect_duration") && !key.equals("lore"))
                                .collect(Collectors.toList()));
                    }
                }
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("give")) {
                // 数量补全，建议1-64
                completions.addAll(Arrays.asList("1", "32", "64"));
            }
        }

        return completions;
    }
}