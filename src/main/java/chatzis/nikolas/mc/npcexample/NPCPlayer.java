package chatzis.nikolas.mc.npcexample;

/*
LICENSE:
Copyright (C) 2024 Apache License

FOR FURTHER INFORMATION, READ THE LICENSE FILE
 */

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * The NPCPlayer class represents an instance of a NPC
 *
 * @author Nikolas Chatzis
 */
public class NPCPlayer extends ServerPlayer {

    protected final CustomMob mob;

    public NPCPlayer(MinecraftServer minecraftserver, ServerLevel world, GameProfile gameprofile, ClientInformation clientinformation, Location location) {
        super(minecraftserver, world, gameprofile, clientinformation);
        setPos(location.getX(), location.getY(), location.getZ());
        setXRot(Mth.clamp(location.getPitch(), -90.0F, 90.0F) % 360.0F);
        setYRot(location.getYaw() % 360);
        setYHeadRot(location.getYaw() % 360);

        mob = new CustomMob(this);
    }

    private boolean moved = false;

    @Override
    public void absMoveTo(double d0, double d1, double d2, float f, float f1) {
        this.moved = true;
        super.absMoveTo(d0, d1, d2, f, f1);

        ClientboundMoveEntityPacket packet = new ClientboundMoveEntityPacket.PosRot(
                getId(),
                (short) (d0 - this.xo),
                (short) (d1 - this.yo),
                (short) (d2 - this.zo),
                (byte) ((int) (f * 256F / 360.0F)),
                (byte) ((int) (f1 * 256F / 360.0F)), true);

        Bukkit.getOnlinePlayers().forEach(player -> Utils.sendPackets(player, packet));
    }

    @Override
    public void moveTo(double x, double y, double z) {
        mob.tellToMoveTo(x, y, z);
    }

    @Override
    public void die(DamageSource damageSource) {
        remove(RemovalReason.KILLED);
    }

    @Override
    public void remove(RemovalReason entity_removalreason) {
        super.remove(entity_removalreason);
        mob.remove(entity_removalreason);
    }

    /**
     * Checks for knockback and if it was moved.
     * If the NPC was moved, and it was not caused by it's pathfinder mob it will move the mob to the new location.
     */
    @Override
    public void tick() {
        super.tick();
        doTick();
        // move mob if npc moved by itself
        if (!this.moved && (this.xo != this.getX() || this.yo != this.getY() || this.zo != this.getZ())) {
            this.mob.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
        this.moved = false;
    }

    /**
     * Will actually send every needed spawn packet to the given player.
     * This method gets called as soon as the given player is capable of displaying the NPC.
     * E.g., chunk loading, joining, viewing distance
     *
     * @param serverPlayer ServerPlayer - the player to show the NPC
     */
    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        CraftPlayer player = serverPlayer.getBukkitEntity();
        // spawn packets
        Utils.sendPackets(player, new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this),
                new ClientboundAddEntityPacket(this),
                new ClientboundRotateHeadPacket(this, (byte) ((this.getYRot() * 256f) / 360f)),
                new ClientboundPlayerInfoRemovePacket(List.of(uuid)));
    }


    @Override
    public void doTick() {
        super.baseTick();
        moveWithFallDamage();
    }

    private void moveWithFallDamage() {
        double x = getX();
        double y = getY();
        double z = getZ();
        travel(Vec3.ZERO);
        doCheckFallDamage(getX() - x, getY() - y, getZ() - z, onGround);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        boolean damaged = super.hurt(damageSource, f);
        if (damaged && this.hurtMarked) {
            this.hurtMarked = false;
            new BukkitRunnable() {
                @Override
                public void run() {
                    NPCPlayer.this.hurtMarked = true; // this runnable enables knockback
                }
            }.runTask(NPCExample.getInstance());
        }
        return damaged;
    }
}
