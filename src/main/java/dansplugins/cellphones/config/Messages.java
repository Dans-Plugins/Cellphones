package dansplugins.cellphones.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.Map;

/**
 * Every user-facing string, read from the {@code messages} section of {@code config.yml}.
 *
 * <p>{@code &} colour codes are translated and {@code {placeholder}} tokens are substituted. A key that
 * is missing from both the operator's file and the bundled defaults renders as a visible marker rather
 * than an empty line, so documentation drift is noticed instead of hidden.
 */
public final class Messages {

    private final ConfigurationSection section;

    public Messages(ConfigurationSection messagesSection) {
        this.section = messagesSection;
    }

    /** Builds from the root of the configuration; the {@code messages} section may be absent. */
    public static Messages fromRoot(ConfigurationSection root) {
        ConfigurationSection messages = root.getConfigurationSection("messages");
        return new Messages(messages == null ? root.createSection("messages") : messages);
    }

    public String get(String key) {
        return get(key, Collections.emptyMap());
    }

    public String get(String key, Map<String, ?> placeholders) {
        String raw = section.getString(key);
        if (raw == null) {
            return ChatColor.RED + "<missing message: " + key + ">";
        }
        String text = raw;
        for (Map.Entry<String, ?> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /** Whether the key exists; used by tests and by help to skip subcommands with no description. */
    public boolean has(String key) {
        return section.isString(key);
    }
}
