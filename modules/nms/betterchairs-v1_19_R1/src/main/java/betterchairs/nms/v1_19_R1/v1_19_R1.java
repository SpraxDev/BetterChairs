package betterchairs.nms.v1_19_R1;

import betterchairs.nms.v1_19_0.v1_19_0;
import de.sprax2013.betterchairs.ChairManager;
import de.sprax2013.betterchairs.ChairNMS;
import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import de.sprax2013.betterchairs.Messages;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class v1_19_R1 extends ChairNMS {
    protected final v1_19_0 implementationForMc1_19_0;

    public v1_19_R1() {
        if (((CraftMagicNumbers) CraftMagicNumbers.INSTANCE).getMappingsVersion().equals("7b9de0da1357e5b251eddde9aa762916")) {
            this.implementationForMc1_19_0 = new v1_19_0();
        } else {
            this.implementationForMc1_19_0 = null;
        }
    }

    @Override
    public @NotNull org.bukkit.entity.Entity spawnChairEntity(@NotNull Location loc, int regenerationAmplifier, boolean useArmorStand) {
        if (this.implementationForMc1_19_0 != null) {
            return this.implementationForMc1_19_0.spawnChairEntity(loc, regenerationAmplifier, useArmorStand);
        }

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

    @Override
    public void killChairEntity(@NotNull org.bukkit.entity.Entity entity) {
        if (this.implementationForMc1_19_0 != null) {
            this.implementationForMc1_19_0.killChairEntity(entity);
            return;
        }

        Entity nmsEntity = ((CraftEntity) entity).getHandle();

        if (!(nmsEntity instanceof CustomChairEntity)) {
            throw new IllegalArgumentException(String.format(Messages.ERR_NOT_CUSTOM_ENTITY, CustomChairEntity.class.getName()));
        }

        ((CustomChairEntity) nmsEntity).markAsRemoved();
        entity.remove();
    }

    @Override
    public boolean isStair(@NotNull Block block) {
        return block.getBlockData() instanceof Stairs;
    }

    @Override
    public boolean isStairUpsideDown(@NotNull Block block) {
        return ((Stairs) block.getBlockData()).getHalf() == Bisected.Half.TOP;
    }

    @Override
    public @NotNull
    BlockFace getBlockRotation(@NotNull Block block) {
        return ((Directional) block.getBlockData()).getFacing();
    }

    @Override
    public boolean isSlab(@NotNull Block block) {
        return block.getBlockData() instanceof Slab && ((Slab) block.getBlockData()).getType() != Slab.Type.DOUBLE;
    }

    @Override
    public boolean isSlabTop(@NotNull Block block) {
        return ((Slab) block.getBlockData()).getType() == Slab.Type.TOP;
    }

    @Override
    public boolean hasEmptyMainHand(@NotNull Player player) {
        return player.getInventory().getItemInMainHand().getType() == Material.AIR;
    }

    @Override
    public boolean isChair(@NotNull org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle() instanceof CustomChairEntity;
    }
}
