package chatzis.nikolas.mc.npcsystem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Wolf;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomMob extends Wolf {

    private final NPCPlayer npcPlayer;

    public CustomMob(NPCPlayer npcPlayer) {
        super(EntityType.WOLF, npcPlayer.serverLevel());
        this.npcPlayer = npcPlayer;

        setInvulnerable(true);
        setPersistenceRequired(false);
        setPos(npcPlayer.getX(), npcPlayer.getY(), npcPlayer.getZ());
        setXRot(npcPlayer.getXRot());
        setYRot(npcPlayer.getYRot());
        setYHeadRot(npcPlayer.getYHeadRot());

        LivingEntity entity = (LivingEntity) getBukkitEntity();
        entity.setCollidable(false);
        entity.setPersistent(false);

        this.goalSelector.getAvailableGoals().clear();
        if (!npcPlayer.npc.shouldLookAtPlayer())
            this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));

        npcPlayer.serverLevel().getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

        new BukkitRunnable() {
            @Override
            public void run() {
                setInvisible(true);
            }
        }.runTaskLater(NPCSystem.getInstance(), 3);
    }

    public void tellToMoveTo(double x, double y, double z) {
        this.navigation.moveTo(x, y, z, 1D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.xo != this.getX() || this.yo != this.getY() || this.zo != this.getZ()) {
            npcPlayer.absMoveTo(this.getX(), this.getY(), this.getZ(), this.yRotO, this.xRotO);
        }
    }
}
