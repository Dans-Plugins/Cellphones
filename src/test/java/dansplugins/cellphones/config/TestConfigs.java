package dansplugins.cellphones.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/** Loads YAML from the classpath the way the plugin sees it, without a server. */
final class TestConfigs {

    private TestConfigs() {
    }

    /** The bundled default config.yml shipped in the jar. */
    static YamlConfiguration bundled() {
        return load("/config.yml");
    }

    static YamlConfiguration load(String resource) {
        try (Reader reader = new InputStreamReader(
                TestConfigs.class.getResourceAsStream(resource), StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (Exception e) {
            throw new IllegalStateException("cannot load " + resource, e);
        }
    }

    /** An operator file layered over the bundled defaults, as Bukkit does with copyDefaults. */
    static YamlConfiguration withDefaults(String resource) {
        YamlConfiguration file = load(resource);
        file.setDefaults(bundled());
        return file;
    }
}
