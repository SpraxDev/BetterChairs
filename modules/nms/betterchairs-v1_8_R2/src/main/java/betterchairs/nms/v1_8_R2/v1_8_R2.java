package betterchairs.nms.v1_8_R2;

import de.sprax2013.betterchairs.ChairManager;
import de.sprax2013.betterchairs.ChairNMS;
import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import de.sprax2013.betterchairs.Messages;
import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class v1_8_R2 extends ChairNMS {
    @Override
    public @NotNull org.bukkit.entity.Entity spawnChairEntity(@NotNull Location loc, int regenerationAmplifier, boolean useArmorStand) {
        WorldServer nmsWorld = ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle();

        Entity nmsEntity;

        if (useArmorStand) {
            nmsEntity = new CustomArmorStand(nmsWorld, loc.getX(), loc.getY(), loc.getZ(), regenerationAmplifier);
        } else {
            nmsEntity = new CustomArrow(nmsWorld, loc.getX(), loc.getY() + .3, loc.getZ() + .25, regenerationAmplifier);
        }

        org.bukkit.entity.Entity bukkitEntity = nmsEntity.getBukkitEntity();

        ChairUtils.applyChairProtections(bukkitEntity);

        if (!nmsWorld.addEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            ChairManager.getLogger().warning(Messages.ERR_ANOTHER_PLUGIN_PREVENTING_SPAWN);
        }

        return bukkitEntity;
    }

    @Override
    public void killChairEntity(@NotNull org.bukkit.entity.Entity entity) {
        Entity nmsEntity = ((CraftEntity) entity).getHandle();

        if (!(nmsEntity instanceof CustomChairEntity)) {
            throw new IllegalArgumentException(String.format(Messages.ERR_NOT_CUSTOM_ENTITY, CustomChairEntity.class.getName()));
        }

        ((CustomChairEntity) nmsEntity).markAsRemoved();
        entity.remove();
    }

    @Override
    public boolean isStair(@NotNull Block block) {
        return block.getState().getData() instanceof Stairs;
    }

    @Override
    public boolean isStairUpsideDown(@NotNull Block block) {
        return ((Stairs) block.getState().getData()).isInverted();
    }

    @Override
    public @NotNull BlockFace getBlockRotation(@NotNull Block block) {
        return ChairUtils.getBlockRotationLegacy(block);
    }

    @Override
    public boolean isSlab(@NotNull Block block) {
        return (block.getState().getData() instanceof Step ||
                block.getState().getData() instanceof WoodenStep) &&
                block.getType() != Material.DOUBLE_STEP &&
                block.getType() != Material.WOOD_DOUBLE_STEP;
    }

    @Override
    public boolean isSlabTop(@NotNull Block block) {
        if (block.getState().getData() instanceof Step) {
            return ((Step) block.getState().getData()).isInverted();
        }

        return ((WoodenStep) block.getState().getData()).isInverted();
    }

    @Override
    public boolean hasEmptyMainHand(@NotNull Player player) {
        return player.getInventory().getItemInHand().getType() == Material.AIR;
    }

    @Override
    public boolean isChair(@NotNull org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle() instanceof CustomChairEntity;
    }
}
