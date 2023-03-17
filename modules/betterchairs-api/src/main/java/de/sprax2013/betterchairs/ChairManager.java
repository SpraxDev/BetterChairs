package de.sprax2013.betterchairs;

import de.sprax2013.betterchairs.events.PlayerEnterChairEvent;
import de.sprax2013.betterchairs.events.PlayerLeaveChairEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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
            Path path = new File(this.disabledForDir, uuid.toString()).toPath();

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
     * This method is a shortcut for {@link #create(Player, Block, double)} with {@code yOffset = ChairUtils#getSitOffset}
     *
     * @see #create(Player, Block, double)
     */
    public boolean create(Player player, Block block) {
        return create(player, block, ChairUtils.getSitOffset(block, !Settings.SIT_ON_ARROWS.getValueAsBoolean(), this.chairNMS));
    }

    /**
     * Check if a chair can be spawned and call an Event.<br>
     * If the event doesn't get cancelled,
     * the player should then be able to sit.<br>
     * <b>Ignores {@link #hasChairsDisabled(OfflinePlayer)}</b>
     *
     * @param player The player that should sit
     * @param block  The block the player should sit on
     * @return true if player is now sitting on a chair, false otherwise
     */
    public boolean create(Player player, Block block, double yOffset) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);
        if (isOccupied(block)) return false;

        Entity chairEntity = instance.chairNMS.spawnChairEntity(block.getLocation().add(0.5, yOffset, 0.5),
                ChairNMS.getRegenerationAmplifier(player),
                !Settings.SIT_ON_ARROWS.getValueAsBoolean());

        Chair chair = new Chair(block, chairEntity, player);

        PlayerEnterChairEvent event = new PlayerEnterChairEvent(player, chair);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            instance.chairNMS.killChairEntity(chairEntity);
            return false;
        }

        if (Settings.AUTO_ROTATE_PLAYER.getValueAsBoolean() && chair.getType() == ChairType.STAIR) {
            Location loc = player.getLocation();
            loc.setPitch(0);

            switch (this.chairNMS.getBlockRotation(block)) {
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

        this.chairs.add(chair);
        chairEntity.setPassenger(player);

        return true;
    }

    /**
     * Calls {@link #destroy(Chair, boolean, boolean)} with {@code sameTickTeleport = false}
     *
     * @param chair          The {@link Chair} that should be destroyed
     * @param teleportPlayer true, when called without an {@link org.bukkit.event.player.PlayerTeleportEvent}
     *                       being fired afterwards (e.g. {@link org.spigotmc.event.entity.EntityDismountEvent} does)
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

        boolean hasPassenger = chair.chairEntity.getPassenger() != null;

        if (hasPassenger) {
            Bukkit.getPluginManager().callEvent(new PlayerLeaveChairEvent(chair.player, chair));
        }

        this.chairNMS.killChairEntity(chair.chairEntity);
        this.chairs.remove(chair);

        if (hasPassenger && teleportPlayer) {
            Runnable teleportTask = () -> chair.player.teleport(chair.getPlayerLeavingLocation());

            if (sameTickTeleport) {
                teleportTask.run();
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), teleportTask);
            }
        }
    }

    public int destroyAll(boolean teleportPlayer) {
        return destroyAll(teleportPlayer, false);
    }

    public int destroyAll(boolean teleportPlayer, boolean sameTickTeleport) {
        int i = 0;

        for (Chair c : this.chairs.toArray(new Chair[0])) {
            destroy(c, teleportPlayer, sameTickTeleport);
            i++;
        }

        this.chairs.clear(); // Just to make sure

        return i;
    }

    /**
     * @param b The block to check
     * @return true if a player is sitting on it, false otherwise
     */
    public boolean isOccupied(@NotNull Block b) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        for (Chair c : this.chairs.toArray(new Chair[0])) {
            if (b.equals(c.block)) {
                return !c.destroyOnNoPassenger();
            }
        }

        return false;
    }

    @Nullable
    public Chair getChair(@NotNull Player p) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        for (Chair c : this.chairs.toArray(new Chair[0])) {
            if (p == c.player && !c.destroyOnNoPassenger()) {
                return c;
            }
        }

        return null;
    }

    @Nullable
    public Chair getChair(@NotNull Block b) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        for (Chair c : this.chairs.toArray(new Chair[0])) {
            if (b == c.block && !c.destroyOnNoPassenger()) {
                return c;
            }
        }

        return null;
    }

    @Nullable
    public Chair getChair(@NotNull Entity entity) {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(Messages.ERR_ASYNC_API_CALL);

        for (Chair c : this.chairs.toArray(new Chair[0])) {
            if (entity == c.chairEntity && !c.destroyOnNoPassenger()) {
                return c;
            }
        }

        return null;
    }

    /**
     * This does not yet guarantee that {@link #getChair(Entity)} is not {@code null}<br>
     * This may return true for Entities not yet spawned and thus not yet a {@link Chair} that is ready
     *
     * @param entity The {@link Entity} to check
     * @return true if the {@link Entity} is used or may be used as {@link Chair}
     */
    public boolean isChair(@NotNull Entity entity) {
        return getChair(entity) != null || this.chairNMS.isChair(entity);
    }

    public boolean hasChairsDisabled(OfflinePlayer player) {
        return hasChairsDisabled(player.getUniqueId());
    }

    public boolean hasChairsDisabled(UUID uuid) {
        Boolean value = this.disabled.get(uuid);

        if (value == null) {
            value = Settings.REMEMBER_IF_PLAYER_DISABLED_CHAIRS.getValueAsBoolean() && new File(this.disabledForDir, uuid.toString()).exists();

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
