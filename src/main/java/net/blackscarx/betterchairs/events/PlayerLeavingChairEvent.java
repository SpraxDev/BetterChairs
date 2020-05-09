package net.blackscarx.betterchairs.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a player is leaving it's chair.<br>
 * This Event can't be canceled in some cases!<b>For example when the player is
 * beeing disconnected.
 */
public class PlayerLeavingChairEvent extends Event implements Cancellable {
	public static HandlerList handlers = new HandlerList();
	private boolean canceled = false;

	private Player p;

	private Block chair;

	public PlayerLeavingChairEvent(Player p, Block chair) {
		this.p = p;

		this.chair = chair;
	}

	/**
	 * @return The player that is trying to sit on a chair
	 */
	public Player getPlayer() {
		return p;
	}

	/**
	 * @return The Block the player is trying to sit on (Stair or Step)
	 */
	public Block getChair() {
		return chair;
	}

	@Override
	public boolean isCancelled() {
		return canceled;
	}

	/*
	 * The event won't get cancelled when the player is for example disconnecting
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.Cancellable#setCancelled(boolean)
	 */
	@Override
	public void setCancelled(boolean cancel) {
		canceled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
