package chatzis.nikolas.mc.npcexample;

import chatzis.nikolas.mc.npcexample.connection.NPCConnection;
import chatzis.nikolas.mc.npcexample.connection.NPCPacketListener;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.CraftServer;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class NPCExample extends JavaPlugin {

    public static final NPCSkin DEFAULT_SKIN = new NPCSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTYxMjM1NDc5NzYyMCwKICAicHJvZmlsZUlkIiA6ICIyYmRmODYwOGFjNmM0NDdiYTg1MzBiMTBjODQ5ZWUyNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJldENyYWZ0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M5Y2VhYmZmZTljZWE0ZjQzOTEzYzJjYzgwM2UwZmY2ODQ3ODFiZTQ2ZTlkNDRhZjI2NzE2MzIxZDRhNDM3NDgiCiAgICB9CiAgfQp9",
            "NpppNFiMehdYplRmNqOlGo2daKwsPdxi2KHXhimQQ4T/F2jqBRvFbN2qseL9svEjbYhumKEd+nIzYCxJSBg1/OGRqD0iY8L9rNlI+EvLB9JHmDfnq8IY2QOEDciOAAN5iw5zfzrOCtnfEBU82NkvUyH7BZsZtN7XshSK3lS3r4bvOdUdjM5Z2qw/cjjSnDQJ1g1sWOIjZjBGTrwIfzvHbkvd65K78uj6lIIHW4Y3nlCA1nqmwT2SdtSviQyYzEpYdFSHOFiuEahM2C3BGea3QtqsgYBn7h0G4inOM2XZ7sLtQMZXPoYNGm77N08dz2nXp/5eJUiXLVTgwVvMX1WY2ssi97jv4HNZpOeFByhkvrG5XUtHWCtr1Thb7EwdLdQLeodxpTrgi0qyMgkqVYdJLPUbwlUuvx8VljCZPNUSDZSV6IO5S8Y0ESSb2eS9PKnqzV1DhHlIgp79mmlgLuwGHPpMR+R6OFvcKXyCnOeW4qVnktFKy5qPFEClBze81UjgWnusLDqoUgAHc6Ko4FWMD1WPsUi0bnKii8t5ADzdsmh5L0mEDl829MRDrhMAz0w/OwsyUI5QJkxodg9Nvy8t7FDcJShvUrZITJy9iWukdlXYUVvJBi/y9u5KJZxzllICybWBQshaWo9jXzruWMh6rTA7pkrPTmO183TkPAtM1IY=",
            Optional.empty()
    );
    private static NPCExample instance;


    @Override
    public void onEnable() {
        instance = this;
        Objects.requireNonNull(getCommand("npctest")).setExecutor(new NPCDebugCommand());
    }

    public NPCPlayer createNPC(Location location, NPCSkin skin) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(location.getWorld());

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "I am just a NPC");
        if (skin != null) {
            gameProfile.getProperties().put("textures", new Property("textures", skin.value(), skin.signature()));
        }

        MinecraftServer server = ((CraftServer) getServer()).getServer();
        ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        ClientInformation info = new ClientInformation("en_us", 0, ChatVisiblity.FULL, false, 0, HumanoidArm.RIGHT, false, false);
        NPCPlayer npcPlayer = new NPCPlayer(server, nmsWorld, gameProfile, info, location);
        npcPlayer.connection = new NPCPacketListener(server, new NPCConnection(PacketFlow.CLIENTBOUND), npcPlayer,
                new CommonListenerCookie(gameProfile, 0, info, false));

        nmsWorld.addNewPlayer(npcPlayer);

        SynchedEntityData dataWatcher = npcPlayer.getEntityData();
        dataWatcher.set(net.minecraft.world.entity.player.Player.DATA_HEALTH_ID, 20F); // set max life

        EntityDataAccessor<Byte> skinAccessor;
        try {
            Field field = net.minecraft.world.entity.player.Player.class.getDeclaredField("bV");
            field.setAccessible(true);
            skinAccessor = (EntityDataAccessor<Byte>) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException | ClassCastException e) {
            skinAccessor = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE);
        }
        dataWatcher.set(skinAccessor, (byte) 0xFF); // set skin attribute

        getLogger().info("Spawned npc at " + location);
        return npcPlayer;
    }


    public static NPCExample getInstance() {
        return instance;
    }
}
