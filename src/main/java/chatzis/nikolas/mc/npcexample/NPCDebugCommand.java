package chatzis.nikolas.mc.npcexample;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCDebugCommand implements CommandExecutor {

    private NPCPlayer npcPlayer;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player player && player.isOp()) {
            if (args.length == 0) {
                return false;
            }
            switch (args[0].toLowerCase()) {
                case "spawn" ->
                        npcPlayer = NPCExample.getInstance().createNPC(player.getLocation(), NPCExample.DEFAULT_SKIN);
                case "teleport" -> {
                    if (npcPlayer != null) {
                        Location location = player.getLocation();
                        npcPlayer.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                    }
                }
                case "here" -> {
                    if (npcPlayer != null) {
                        Location location = player.getLocation();
                        npcPlayer.mob.tellToMoveTo(location.getX(), location.getY(), location.getZ());
                    }
                }
            }
        }
        return true;
    }
}
