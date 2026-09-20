package dansplugins.cellphones.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Owns the current {@link PluginConfig} and {@link Messages} and knows how to (re)load them.
 *
 * <p>On every load the bundled {@code config.yml} is written if absent, missing keys are copied in
 * from the bundled defaults and saved back, and the operator's own keys are never removed.
 */
public final class ConfigService {

    private final JavaPlugin plugin;
    private volatile PluginConfig config;
    private volatile Messages messages;

    public ConfigService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration file = plugin.getConfig();
        file.options().copyDefaults(true);
        plugin.saveConfig();
        config = PluginConfig.from(file, plugin.getLogger()::warning);
        messages = Messages.fromRoot(file);
    }

    public PluginConfig config() {
        return config;
    }

    public Messages messages() {
        return messages;
    }
}
