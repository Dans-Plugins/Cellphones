package dansplugins.cellphones.config;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginConfigTest {

    @Test
    void bundledDefaultsLoadWithoutWarnings() {
        List<String> warnings = new ArrayList<>();
        PluginConfig config = PluginConfig.from(TestConfigs.bundled(), warnings::add);

        assertTrue(warnings.isEmpty(), warnings.toString());
        assertEquals(PluginConfig.CURRENT_VERSION, config.configVersion());
        assertEquals(Material.LIGHTNING_ROD, config.towerMaterial());
        assertEquals(200, config.towerRadius());
        assertTrue(config.towerIgnoreY());
        assertTrue(config.towerCraftable());
        assertEquals(Material.RECOVERY_COMPASS, config.phoneMaterial());
        assertTrue(config.phoneCraftable());
        assertEquals("single", config.networkMode());
    }

    @Test
    void invalidValuesFallBackAndAreReported() {
        List<String> warnings = new ArrayList<>();
        PluginConfig config = PluginConfig.from(TestConfigs.withDefaults("/config-overrides.yml"), warnings::add);

        assertEquals(Material.LIGHTNING_ROD, config.towerMaterial(), "unknown material falls back");
        assertEquals(Material.RECOVERY_COMPASS, config.phoneMaterial(), "air cannot be an item and falls back");
        assertEquals(0, config.towerRadius(), "negative radius clamps to 0");
        assertEquals("single", config.networkMode(), "unsupported mode falls back");
        assertEquals(4, warnings.size(), warnings.toString());
        assertTrue(warnings.get(0).contains("tower.material"));
        assertTrue(warnings.get(1).contains("phone.material"));
        assertTrue(warnings.get(2).contains("tower.radius"));
        assertTrue(warnings.get(3).contains("network.mode"));
    }

    @Test
    void operatorValuesWinOverDefaults() {
        PluginConfig config = PluginConfig.from(TestConfigs.withDefaults("/config-overrides.yml"), s -> { });

        assertFalse(config.towerIgnoreY());
        assertFalse(config.phoneCraftable());
        assertTrue(config.towerCraftable(), "a key missing from the operator file comes from the defaults");
    }

    @Test
    void newerFileVersionIsReportedNotRejected() {
        List<String> warnings = new ArrayList<>();
        var file = TestConfigs.bundled();
        file.set("config-version", PluginConfig.CURRENT_VERSION + 1);

        PluginConfig config = PluginConfig.from(file, warnings::add);

        assertEquals(PluginConfig.CURRENT_VERSION + 1, config.configVersion());
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("version"));
    }
}
