package dansplugins.cellphones.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Consumer;

/**
 * Typed, immutable view of {@code config.yml}. Built once per (re)load; nothing else reads the raw
 * configuration.
 *
 * <p>Values are read with the one-argument getters so that a key missing from the operator's file
 * falls through to the bundled defaults (the two-argument getters ignore jar defaults). Invalid
 * materials are reported through {@code warn} and replaced by the bundled default rather than
 * disabling the plugin.
 */
public final class PluginConfig {

    /** Layout version this build understands; {@code config-version} in the file. */
    public static final int CURRENT_VERSION = 1;

    static final Material DEFAULT_TOWER_MATERIAL = Material.LIGHTNING_ROD;
    static final Material DEFAULT_PHONE_MATERIAL = Material.RECOVERY_COMPASS;

    private final int configVersion;
    private final Material towerMaterial;
    private final int towerRadius;
    private final boolean towerIgnoreY;
    private final boolean towerCraftable;
    private final Material phoneMaterial;
    private final boolean phoneCraftable;
    private final String networkMode;

    private PluginConfig(int configVersion, Material towerMaterial, int towerRadius, boolean towerIgnoreY,
                         boolean towerCraftable, Material phoneMaterial, boolean phoneCraftable,
                         String networkMode) {
        this.configVersion = configVersion;
        this.towerMaterial = towerMaterial;
        this.towerRadius = towerRadius;
        this.towerIgnoreY = towerIgnoreY;
        this.towerCraftable = towerCraftable;
        this.phoneMaterial = phoneMaterial;
        this.phoneCraftable = phoneCraftable;
        this.networkMode = networkMode;
    }

    /**
     * Reads a configuration. {@code warn} receives one line per value that was invalid and replaced.
     */
    public static PluginConfig from(ConfigurationSection config, Consumer<String> warn) {
        int version = config.getInt("config-version");
        if (version > CURRENT_VERSION) {
            warn.accept("config.yml is version " + version + " but this build understands version "
                    + CURRENT_VERSION + "; keys it does not know are ignored.");
        }
        Material tower = material(config, "tower.material", DEFAULT_TOWER_MATERIAL, warn);
        Material phone = material(config, "phone.material", DEFAULT_PHONE_MATERIAL, warn);
        int radius = config.getInt("tower.radius");
        if (radius < 0) {
            warn.accept("tower.radius is " + radius + "; a negative radius covers nothing, using 0.");
            radius = 0;
        }
        String mode = config.getString("network.mode");
        if (!"single".equals(mode)) {
            warn.accept("network.mode '" + mode + "' is not supported by this build; using 'single'.");
            mode = "single";
        }
        return new PluginConfig(version, tower, radius, config.getBoolean("tower.ignore-y"),
                config.getBoolean("tower.craftable"), phone, config.getBoolean("phone.craftable"), mode);
    }

    private static Material material(ConfigurationSection config, String path, Material fallback,
                                     Consumer<String> warn) {
        String name = config.getString(path);
        Material material = name == null ? null : Material.matchMaterial(name);
        // Material#isItem/#isAir consult a running server's registry, so the check is by identity:
        // the three air constants cannot be items, and legacy names are not accepted.
        if (material == null || material == Material.AIR || material == Material.CAVE_AIR
                || material == Material.VOID_AIR || material.isLegacy()) {
            warn.accept(path + " '" + name + "' is not a usable item material; using " + fallback.name() + ".");
            return fallback;
        }
        return material;
    }

    public int configVersion() {
        return configVersion;
    }

    public Material towerMaterial() {
        return towerMaterial;
    }

    public int towerRadius() {
        return towerRadius;
    }

    public boolean towerIgnoreY() {
        return towerIgnoreY;
    }

    public boolean towerCraftable() {
        return towerCraftable;
    }

    public Material phoneMaterial() {
        return phoneMaterial;
    }

    public boolean phoneCraftable() {
        return phoneCraftable;
    }

    public String networkMode() {
        return networkMode;
    }
}
