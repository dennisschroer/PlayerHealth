package denniss17.playerHealth;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;


public class PlayerHealth extends JavaPlugin {
	
	public void onEnable(){
		// Register PlayerListener
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		if(getConfig().getBoolean("check_updates")){
			new VersionChecker(this).activate(getConfig().getInt("check_updates_interval")*60*20);
		}		
		
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	// Makes the health of other players visible for this player
	public void showHealth(Player player){
		Objective objective = player.getScoreboard().registerNewObjective("playerhealth", Criterias.HEALTH);
		objective.setDisplayName(getConfig().getString("display_name"));
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		for(Player other : getServer().getOnlinePlayers()){
			other.setHealth(other.getHealth());
		}
	}
	
	// Makes the health of other players invisible for this player
	public void removeHealth(Player player){
		Objective objective = player.getScoreboard().getObjective("playerhealth");
		if(objective!=null) objective.unregister();
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
