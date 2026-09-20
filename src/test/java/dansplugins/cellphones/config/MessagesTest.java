package dansplugins.cellphones.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesTest {

    @Test
    void translatesColourCodesAndPlaceholders() {
        Messages messages = Messages.fromRoot(TestConfigs.bundled());
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("sender", "Alice");
        placeholders.put("message", "hello");

        String text = messages.get("phone-format", placeholders);

        assertEquals(ChatColor.translateAlternateColorCodes('&', "&7[&bPhone&7] &fAlice&7: &fhello"), text);
        assertFalse(text.contains("&"));
    }

    @Test
    void operatorTextOverridesTheDefault() {
        Messages messages = Messages.fromRoot(TestConfigs.withDefaults("/config-overrides.yml"));

        assertEquals(ChatColor.RED + "No bars.", messages.get("no-signal"));
        assertTrue(messages.has("no-phone"), "keys absent from the operator file come from the defaults");
    }

    @Test
    void missingKeyRendersAVisibleMarker() {
        Messages messages = Messages.fromRoot(new YamlConfiguration());

        assertFalse(messages.has("no-such-key"));
        assertTrue(messages.get("no-such-key").contains("<missing message: no-such-key>"));
    }

    @Test
    void everyDocumentedHelpEntryExistsForEachSubcommandMessage() {
        Messages messages = Messages.fromRoot(TestConfigs.bundled());
        for (String key : new String[] {"help-header", "help-entry", "help.help", "help.reload",
                "unknown-subcommand", "reloaded", "no-permission", "no-signal", "signal", "no-phone",
                "tower-placed", "tower-removed", "phone-format"}) {
            assertTrue(messages.has(key), key);
        }
    }
}
