package dansplugins.cellphones.commands;

import dansplugins.cellphones.config.ConfigService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The {@code /cellphones} command: parses the first argument and dispatches to a {@link Subcommand}.
 * No arguments, or an unknown subcommand, shows help.
 */
public final class CellphonesCommand implements TabExecutor {

    private final ConfigService configService;
    private final Map<String, Subcommand> subcommands = new LinkedHashMap<>();

    public CellphonesCommand(ConfigService configService) {
        this.configService = configService;
    }

    public CellphonesCommand register(Subcommand subcommand) {
        subcommands.put(subcommand.name().toLowerCase(Locale.ROOT), subcommand);
        return this;
    }

    /** Registered subcommands in registration order; used by help. */
    public List<Subcommand> subcommands() {
        return new ArrayList<>(subcommands.values());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Subcommand target = args.length == 0 ? subcommands.get("help")
                : subcommands.get(args[0].toLowerCase(Locale.ROOT));
        if (target == null) {
            sender.sendMessage(configService.messages().get("unknown-subcommand",
                    Collections.singletonMap("label", label)));
            return true;
        }
        if (!sender.hasPermission(target.permission())) {
            sender.sendMessage(configService.messages().get("no-permission"));
            return true;
        }
        String[] rest = args.length == 0 ? args : Arrays.copyOfRange(args, 1, args.length);
        target.execute(sender, label, rest);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            String prefix = args[0].toLowerCase(Locale.ROOT);
            for (Subcommand subcommand : subcommands.values()) {
                if (subcommand.name().startsWith(prefix) && sender.hasPermission(subcommand.permission())) {
                    names.add(subcommand.name());
                }
            }
            return names;
        }
        Subcommand target = subcommands.get(args[0].toLowerCase(Locale.ROOT));
        if (target == null || !sender.hasPermission(target.permission())) {
            return Collections.emptyList();
        }
        return target.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
