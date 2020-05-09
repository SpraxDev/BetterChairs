/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public interface NMS {

    String getVersion();

    ArmorStand spawn(Location loc, Player p);

    void kill(ArmorStand armorStand);

    void sendUpdate(Player p);

    boolean check(ArmorStand armorStand);

}
