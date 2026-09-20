package dansplugins.cellphones;

import org.bukkit.plugin.java.JavaPlugin;

public final class Cellphones extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Cellphones " + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cellphones disabled.");
    }
}
