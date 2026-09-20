package dansplugins.cellphones;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin entry point.
 *
 * <p>This is the scaffold: it loads the default configuration and answers {@code /cellphones help}.
 * The behaviour described in MVP.md is built on top of it, in the packages laid out in ARCHITECTURE.md.
 */
public final class Cellphones extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Cellphones " + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cellphones disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Cellphones " + getDescription().getVersion());
        sender.sendMessage("/" + label + " help - show this message");
        sender.sendMessage("See https://github.com/Dans-Plugins/Cellphones for the roadmap.");
        return true;
    }
}
