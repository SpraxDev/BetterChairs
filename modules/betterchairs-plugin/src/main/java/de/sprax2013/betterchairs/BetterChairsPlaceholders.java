package de.sprax2013.betterchairs;

import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class BetterChairsPlaceholders extends PlaceholderExpansion {
	
	private static BetterChairsPlugin plugin;
	
	public BetterChairsPlaceholders(BetterChairsPlugin betterChairs) {
		plugin = betterChairs;
	}
	
    @Override
    public boolean persist(){
        return true;
    }  

   @Override
   public boolean canRegister(){
       return true;
   }

   @Override
   public String getAuthor(){
       return plugin.getDescription().getAuthors().toString();
   }

	@Override
	public String getIdentifier(){
		return plugin.getDescription().getName();
	}

	@Override
	public String getVersion(){
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier){
		if(identifier.equals("chairs_disabled")){
			if(BetterChairsPlugin.getManager().hasChairsDisabled(player))
				return "true";
			else
				return "false";
		}
		return "";
	}
	
}