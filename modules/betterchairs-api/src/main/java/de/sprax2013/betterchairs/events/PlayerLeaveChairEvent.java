package de.sprax2013.betterchairs.events;

import de.sprax2013.betterchairs.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveChairEvent extends Event {
    public static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Chair chair;

    public PlayerLeaveChairEvent(Player player, Chair chair) {
        this.player = player;
        this.chair = chair;
    }

    public Player getPlayer() {
        return this.player;
    }

    @SuppressWarnings("unused")
    public Chair getChair() {
        return this.chair;
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
