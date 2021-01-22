package de.sprax2013.betterchairs;

import de.sprax2013.betterchairs.events.PlayerEnterChairEvent;
import de.sprax2013.betterchairs.events.PlayerLeaveChairEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class should be instantiated as soon as possible inside
 * {@link JavaPlugin#onEnable()} to ensure that other classes relying on it work as intended
 */
public class ChairManager {
    protected static JavaPlugin plugin;
    protected static ChairManager instance;

    protected final ChairNMS chairNMS;
    protected final List<Chair> chairs = new ArrayList<>();

    protected final HashMap<UUID, Boolean> disabled = new HashMap<>();
    protected final File disabledForDir;

    protected ChairManager(@NotNull JavaPlugin plugin, @NotNull ChairNMS chairNMS) {
        this.chairNMS = Objects.requireNonNull(chairNMS);

        this.disabledForDir = new File(plugin.getDataFolder(), "disabled_for");

        ChairManager.plugin = Objects.requireNonNull(plugin);
        ChairManager.instance = this;
    }

    protected void onQuit(UUID uuid) {
        Boolean value = this.disabled.remove(uuid);

        if (value != null && Settings.REMEMBER_IF_PLAYER_DISABLED_CHAIRS.getValueAsBoolean()) {
            Path path = new File(disabledForDir, uuid.toString()).toPath();

            try {
                if (value) {
                    Files.createDirectories(path.getParent());
                    Files.createFile(path);
                } else {
                    Files.deleteIfExists(path);
                }
            } catch (FileAlreadyExistsException ignore) {
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        this.disabled.remove(uuid);
    }

    /**
     * Check if a chair can be spawned and call an Event.<br>
     * If the event doesn't get cancelled,
     * the player should then be able to sit.<br>
     * <b>Ignores {@link #hasChairsDisabled(Player)}</b>
     *
     * @param player The player that should sit
     * @param block  The block the player should sit on
     *
     * @return true if player is now sitting on a chair, false otherwise
     */
    public boolean create(Player player, Block block) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);
        if (isOccupied(block)) return false;

        // Normal blocks and slabs that are placed in the upper half of an block need the player to sit 0.5 blocks higher
        double yOffset = (!chairNMS.isStair(block) && !chairNMS.isSlab(block)) ||
                (chairNMS.isSlab(block) && chairNMS.isSlabTop(block)) ? 0.5 : 0;

        ArmorStand armorStand = instance.chairNMS.spawnChairArmorStand(
                block.getLocation().add(0.5, -1.2 + yOffset, 0.5), ChairNMS.getRegenerationAmplifier(player));

        Chair chair = new Chair(block, armorStand, player);

        PlayerEnterChairEvent event = new PlayerEnterChairEvent(player, chair);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            instance.chairNMS.killChairArmorStand(armorStand);
            return false;
        }

        if (Settings.AUTO_ROTATE_PLAYER.getValueAsBoolean() && chair.getType() == ChairType.STAIR) {
            Location loc = player.getLocation();
            loc.setPitch(0);

            switch (chairNMS.getBlockRotation(block)) {
                case NORTH:
                    loc.setYaw(0);
                    break;
                case EAST:
                    loc.setYaw(90);
                    break;
                case SOUTH:
                    loc.setYaw(180);
                    break;
                case WEST:
                    loc.setYaw(-90);
                    break;
                default:
                    break;
            }

            player.teleport(loc);
        }

        chairs.add(chair);
        armorStand.setPassenger(player);

        return true;
    }

    /**
     * Calls {@link #destroy(Chair, boolean, boolean)} with {@code sameTickTeleport = false}
     *
     * @param chair          The {@link Chair} that should be destroyed
     * @param teleportPlayer true, when called without an {@link org.bukkit.event.player.PlayerTeleportEvent}
     *                       being fired afterwards (e.g. {@link org.spigotmc.event.entity.EntityDismountEvent} does)
     *
     * @see #destroy(Chair, boolean, boolean)
     */
    public void destroy(Chair chair, boolean teleportPlayer) {
        destroy(chair, teleportPlayer, false);
    }

    /**
     * @param chair            The {@link Chair} that should be destroyed
     * @param teleportPlayer   true, when called without an {@link org.bukkit.event.player.PlayerTeleportEvent}
     *                         being fired afterwards (e.g. {@link org.spigotmc.event.entity.EntityDismountEvent} does)
     * @param sameTickTeleport For compatibility reasons the player is teleported on the next server tick.
     *                         This may not be possible in some situations
     */
    public void destroy(Chair chair, boolean teleportPlayer, boolean sameTickTeleport) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        boolean hasPassenger = chair.armorStand.getPassenger() != null;

        if (hasPassenger)
            Bukkit.getPluginManager().callEvent(new PlayerLeaveChairEvent(chair.player, chair));

        chairNMS.killChairArmorStand(chair.armorStand);
        chairs.remove(chair);

        if (hasPassenger && teleportPlayer && Settings.LEAVING_CHAIR_TELEPORT_TO_OLD_LOCATION.getValueAsBoolean()) {
            Runnable task = () -> chair.player.teleport(chair.getPlayerLeavingLocation());

            if (sameTickTeleport) {
                task.run();
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), task);
            }
        }
    }

    public int destroyAll(boolean teleportPlayer) {
        return destroyAll(teleportPlayer, false);
    }

    public int destroyAll(boolean teleportPlayer, boolean sameTickTeleport) {
        int i = 0;

        for (Chair c : new ArrayList<>(chairs)) {
            destroy(c, teleportPlayer, sameTickTeleport);
            i++;
        }

        chairs.clear(); // Just to make sure

        return i;
    }

    /**
     * @param b The block to check
     *
     * @return true if a player is sitting on it, false otherwise
     */
    public boolean isOccupied(@NotNull Block b) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        for (Chair c : new ArrayList<>(chairs)) {
            if (b.equals(c.block)) {
                return !c.destroyOnNoPassenger();
            }
        }

        return false;
    }

    @Nullable
    public Chair getChair(@NotNull Player p) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        for (Chair c : new ArrayList<>(chairs)) {
            if (p == c.player && !c.destroyOnNoPassenger()) {
                return c;
            }
        }

        return null;
    }

    @Nullable
    public Chair getChair(@NotNull Block b) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        for (Chair c : new ArrayList<>(chairs)) {
            if (b == c.block && !c.destroyOnNoPassenger()) {
                return c;
            }
        }

        return null;
    }

    @Nullable
    public Chair getChair(@NotNull ArmorStand armorStand) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        for (Chair c : new ArrayList<>(chairs)) {
            if (armorStand == c.armorStand && !c.destroyOnNoPassenger()) {
                return c;
            }
        }

        return null;
    }

    /**
     * This does not yet guarantee that {@link #getChair(ArmorStand)} is not {@code null}<br>
     * This may return true for ArmorStand not yet spawned and thus not yet a {@link Chair} that is ready
     *
     * @param armorStand The {@link ArmorStand} to check
     *
     * @return true if the {@link ArmorStand} is used or may be used as {@link Chair}
     */
    public boolean isChair(@NotNull ArmorStand armorStand) {
        return getChair(armorStand) != null || chairNMS.isChair(armorStand);
    }

    public boolean hasChairsDisabled(Player player) {
        return hasChairsDisabled(player.getUniqueId());
    }

    public boolean hasChairsDisabled(UUID uuid) {
        Boolean value = disabled.get(uuid);

        if (value == null) {
            if (Settings.REMEMBER_IF_PLAYER_DISABLED_CHAIRS.getValueAsBoolean()) {
                value = new File(disabledForDir, uuid.toString()).exists();
            } else {
                value = false;
            }

            if (Bukkit.getPlayer(uuid).isOnline()) {
                this.disabled.put(uuid, value);
            }
        }

        return value;
    }

    public void setChairsDisabled(Player player, boolean areDisabled) {
        setChairsDisabled(player.getUniqueId(), areDisabled);
    }

    public void setChairsDisabled(UUID uuid, boolean areDisabled) {
        boolean isOnline = Bukkit.getOfflinePlayer(uuid).isOnline();
        boolean directlyWriteToFile = !isOnline && Settings.REMEMBER_IF_PLAYER_DISABLED_CHAIRS.getValueAsBoolean();

        if (isOnline || directlyWriteToFile) {
            this.disabled.put(uuid, areDisabled);
        }

        if (directlyWriteToFile) {
            onQuit(uuid);   // Write changes to file
        }
    }

    /**
     * Returns BetterChair's logger or the global one,
     * when {@link #getPlugin()} is {@code null}.
     *
     * @return The plugin's {@link Logger} or the global one, never null
     */
    public static @NotNull Logger getLogger() {
        return plugin != null ? plugin.getLogger() : Logger.getGlobal();
    }

    /**
     * May be null if BetterChairs is not enabled
     *
     * @return The {@link ChairManager} instance created by BetterChairs, or null
     */
    @Nullable
    public static ChairManager getInstance() {
        return instance;
    }

    /**
     * May be null if BetterChairs is not enabled
     *
     * @return The {@link JavaPlugin} instance representing BetterChairs, or null
     */
    @Nullable
    public static JavaPlugin getPlugin() {
        return plugin;
    }
}