package chatzis.nikolas.mc.npcexample;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Utils {

    /**
     * Sends the given packet to the player.
     *
     * @param player  Player - the player to send the packets.
     * @param packets Object[] - the packets.
     */
    public static void sendPackets(Player player, Packet<?>... packets) {
        ServerGamePacketListenerImpl playerConnection = getPlayerConnection(player);
        for (Packet<?> packet : packets) {
            playerConnection.send(packet);
        }
    }

    /**
     * Gets the PlayerConnection of a given player.
     *
     * @param player Player - the player to get the connection from.
     * @return PlayerConnection - the connection.
     */
    public static ServerGamePacketListenerImpl getPlayerConnection(Player player) {
        return getEntityPlayer(player).connection;
    }

    public static ServerPlayer getEntityPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
