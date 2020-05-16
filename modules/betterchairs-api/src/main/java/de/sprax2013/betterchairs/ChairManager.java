package de.sprax2013.betterchairs;

import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChairManager {
    protected static ChairManager instance;

    protected final ChairNMS chairNMS;
    protected final List<ArmorStand> armorStands = new ArrayList<>();

    protected ChairManager(@NotNull ChairNMS chairNMS) {
        this.chairNMS = Objects.requireNonNull(chairNMS);

        instance = this;
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
}