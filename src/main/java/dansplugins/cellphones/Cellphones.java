package dansplugins.cellphones;

import dansplugins.cellphones.commands.CellphonesCommand;
import dansplugins.cellphones.commands.HelpSubcommand;
import dansplugins.cellphones.commands.ReloadSubcommand;
import dansplugins.cellphones.config.ConfigService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin entry point. Constructs and registers; the behaviour lives in the packages laid out in
 * ARCHITECTURE.md.
 */
public final class Cellphones extends JavaPlugin {

    private ConfigService configService;

    @Override
    public void onEnable() {
        configService = new ConfigService(this);
        configService.load();

        CellphonesCommand command = new CellphonesCommand(configService);
        command.register(new HelpSubcommand(configService, () -> command, getDescription().getVersion()))
                .register(new ReloadSubcommand(configService));
        PluginCommand pluginCommand = getCommand("cellphones");
        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);

        getLogger().info("Cellphones " + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cellphones disabled.");
    }
}
