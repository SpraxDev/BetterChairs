package de.sprax2013.betterchairs.events;

import de.sprax2013.betterchairs.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEnterChairEvent extends Event implements Cancellable {
    public static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Player player;
    private final Chair chair;

    public PlayerEnterChairEvent(Player player, Chair chair) {
        this.player = player;
        this.chair = chair;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Chair getChair() {
        return this.chair;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
