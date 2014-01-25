package denniss17.playerHealth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.kitteh.tag.TagAPI;


public class PlayerHealth extends JavaPlugin {
	public static VersionChecker versionChecker;
	
	public static final String OBJECTIVE_NAME = "playerhealth";
	public static final int PROJECT_ID = 55658;
	
	/**
	 * A mapping from player to a set of tags this player has
	 * By default, this set only contains player.getName(), but as tags are send
	 * via TagAPI, they are added too
	 */
	public Map<Player, Set<String>> tags;
	
	public void onEnable(){
		tags = new HashMap<Player, Set<String>>();
		
		// Fill mapping with online players
		for(Player player : getServer().getOnlinePlayers()){
			HashSet<String> set = new HashSet<String>();
			set.add(player.getName());
			tags.put(player, set);
		}
		
		// Register PlayerListener
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		// Check if TagAPI is installed
		if(getServer().getPluginManager().getPlugin("TagAPI")!=null){
			this.getServer().getPluginManager().registerEvents(new TagAPIListener(this), this);
			for(Player player : getServer().getOnlinePlayers()){
				// Resent tags for TagAPIListener to update tags
				TagAPI.refreshPlayer(player);
			}
			getLogger().info("TagAPI found, hooked into it...");
		}else{
			getLogger().info("TagAPI not found, continuing without...");
		}
		
		// Check if versionChecked should be enabled
		if(getConfig().getBoolean("check_updates")){
			versionChecker = new VersionChecker(this, PROJECT_ID);
			versionChecker.activate(getConfig().getInt("check_updates_interval")*60*20);
		}		
		
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	// Makes the health of other players visible for this player
	public void showHealth(Player player){
		Objective objective = player.getScoreboard().registerNewObjective(OBJECTIVE_NAME, "dummy");
		objective.setDisplayName(getConfig().getString("display_name"));
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		
		// Send initial healths
		for(Player other : getServer().getOnlinePlayers()){
			for(String tag : tags.get(other)){
				objective.getScore(getServer().getOfflinePlayer(tag)).setScore((int) other.getHealth());
			}
		}
	}
	
	// Makes the health of other players invisible for this player
	public void removeHealth(Player player){
		Objective objective = player.getScoreboard().getObjective(OBJECTIVE_NAME);
		if(objective!=null) objective.unregister();
	}

	public void sendHealthOfPlayer(Player player, int health){
		Set<String> playerTags = tags.get(player);
		Objective objective;
		
		// For each tag send the score
		for(String tag : playerTags){
			for(Player other : getServer().getOnlinePlayers()){
				objective = other.getScoreboard().getObjective(OBJECTIVE_NAME);
				if(objective!=null) objective.getScore(getServer().getOfflinePlayer(tag)).setScore(health);
			}
		}
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equals("playerhealth")){
    		if(args.length>0){
    			if(args[0].equals("reload")){
    				// Reload
    				if(sender.hasPermission("playerhealth.reload")){
    					reloadConfig();
    					for(Player player : getServer().getOnlinePlayers()){
    						if(player.getScoreboard().getObjective("playerhealth")!=null){
    							player.getScoreboard().getObjective("playerhealth").setDisplayName(getConfig().getString("display_name"));
    							if(!player.hasPermission("playerhealth.show"))
    								removeHealth(player);
    						}else{
    							if(player.hasPermission("playerhealth.show"))
    								showHealth(player);
    						}
    					}
    				}else{
    					sender.sendMessage(ChatColor.RED + "You don't have permission to do this!");
    				}
    				return true;
    			}
    			return false;
    		}else{
    			sender.sendMessage(
    					ChatColor.YELLOW + "PlayerHealth" + 
    					ChatColor.WHITE + " version " + 
    					ChatColor.YELLOW + getDescription().getVersion()
    				);
    			
    			sender.sendMessage(getDescription().getDescription());
    			sender.sendMessage("Website: " + ChatColor.YELLOW + getDescription().getWebsite());
    			if(sender.hasPermission("playerhealth.reload")){
    				sender.sendMessage("Use " + ChatColor.YELLOW + "/playerhealth reload" + ChatColor.WHITE + " to reload the config and displays");
    			}
    			return true;
    		}
    	}
    	return false; 
    }
}
