package net.blackscarx.betterchairs.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.blackscarx.betterchairs.ChairType;

/**
 * This event is called when a player is entering a chair.<br>
 * When canceled the player won't sit on the chair
 */
public class PlayerEnteringChairEvent extends Event implements Cancellable {
	public static HandlerList handlers = new HandlerList();
	private boolean canceled = false;

	private Player p;

	private ChairType type;
	private Block chair;

	public PlayerEnteringChairEvent(Player p, ChairType type, Block chair) {
		this.p = p;

		this.type = type;
		this.chair = chair;
	}

	/**
	 * @return The player that is trying to sit on a chair
	 */
	public Player getPlayer() {
		return p;
	}

	/**
	 * @return The ChairType
	 */
	public ChairType getType() {
		return type;
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
