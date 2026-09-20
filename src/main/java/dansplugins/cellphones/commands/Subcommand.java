package dansplugins.cellphones.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * One {@code /cellphones <name>} subcommand. The dispatcher checks {@link #permission()} before
 * calling {@link #execute}; the description shown by {@code help} is the message key
 * {@code help.<name>}.
 */
public interface Subcommand {

    String name();

    String permission();

    /** @param args the arguments after the subcommand name */
    void execute(CommandSender sender, String label, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
