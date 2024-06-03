package chatzis.nikolas.mc.npcexample;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Wolf;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

/**
 * This class represents the PathfindingMob for the {@link NPCPlayer} class.
 * It's needed because the NPCPlayer does not have a Navigator inside.
 * <br>
 * As soon as this entity is moved, the NPCPlayer copies its movement.
 * <br>
 * It will spawn invisible, not-persistent and not collidable.
 *
 * @author Nikolas Chatzis
 */
public class CustomMob extends Wolf {

    private final NPCPlayer npcPlayer;

    /**
     * Spawns an invisible, invulnerable, not colidable, not persistable mob at the position of the given NPCPlayer.
     *
     * @param npcPlayer NPCPlayer - the NPC the Pathfinding mob is for
     */
    public CustomMob(NPCPlayer npcPlayer) {
        super(EntityType.WOLF, npcPlayer.serverLevel());
        this.npcPlayer = npcPlayer;

        setInvulnerable(true);
        setPersistenceRequired(false); // this mob should not be safed
        setPos(npcPlayer.getX(), npcPlayer.getY(), npcPlayer.getZ());
        setXRot(npcPlayer.getXRot());
        setYRot(npcPlayer.getYRot());
        setYHeadRot(npcPlayer.getYHeadRot());

        LivingEntity entity = (LivingEntity) getBukkitEntity();
        entity.setCollidable(false);
        entity.setPersistent(false); // this mob should not be safed

        this.goalSelector.getAvailableGoals().clear();
        // add your custom pathfinder here

        npcPlayer.serverLevel().getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        new BukkitRunnable() {
            @Override
            public void run() {
                setInvisible(true); // Needs to be set delayed. Would not affect otherwise
            }
        }.runTaskLater(NPCExample.getInstance(), 3);
    }

    /**
     * Will try to move to the given position, which leads the NPC to move as well.
     * This method only works in a radius of 30 blocks. Use a custom Pathfinder instead.
     *
     * @param x double - the x position
     * @param y double - the y position
     * @param z double - the z position
     */
    public void tellToMoveTo(double x, double y, double z) {
        this.navigation.moveTo(x, y, z, 1D);
    }

    /**
     * Checks if the mob moved and if so, moves tells the NPC to move.
     */
    @Override
    public void tick() {
        super.tick();
        if (this.xo != this.getX() || this.yo != this.getY() || this.zo != this.getZ()) {
            npcPlayer.absMoveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (npcPlayer.isAlive())
           npcPlayer.die(damagesource);
    }

    @Override
    public void remove(RemovalReason entity_removalreason) {
        super.remove(entity_removalreason);
        if (!npcPlayer.isRemoved())
            npcPlayer.remove(entity_removalreason);
    }
}
