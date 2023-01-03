package betterchairs.nms.v1_19_0;

import de.sprax2013.betterchairs.ChairManager;
import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import de.sprax2013.betterchairs.Messages;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Between Minecraft 1.19 and 1.19.1 the mappings changed but not the NMS version.
 * <br>
 * This is a compatibility class that doesn't fully comfort to {@link de.sprax2013.betterchairs.ChairNMS}
 * and is used by the implementation for v1_19_R1
 */
public class v1_19_0 {
    public @NotNull org.bukkit.entity.Entity spawnChairEntity(@NotNull Location loc, int regenerationAmplifier, boolean useArmorStand) {
        ServerLevel nmsWorld = ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle();

        Entity nmsEntity;

        if (useArmorStand) {
            nmsEntity = new CustomArmorStand(nmsWorld, loc.getX(), loc.getY(), loc.getZ(), regenerationAmplifier);
        } else {
            nmsEntity = new CustomArrow(nmsWorld, loc.getX(), loc.getY() + .3, loc.getZ() + .25, regenerationAmplifier);
        }

        org.bukkit.entity.Entity bukkitEntity = nmsEntity.getBukkitEntity();

        ChairUtils.applyChairProtections(bukkitEntity);

        if (!nmsWorld.addFreshEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            ChairManager.getLogger().warning(Messages.ERR_ANOTHER_PLUGIN_PREVENTING_SPAWN);
        }

        return bukkitEntity;
    }

    public void killChairEntity(@NotNull org.bukkit.entity.Entity entity) {
        Entity nmsEntity = ((CraftEntity) entity).getHandle();

        if (!(nmsEntity instanceof CustomChairEntity)) {
            throw new IllegalArgumentException(String.format(Messages.ERR_NOT_CUSTOM_ENTITY, CustomChairEntity.class.getName()));
        }

        ((CustomChairEntity) nmsEntity).markAsRemoved();
        entity.remove();
    }
}
