package denniss17.playerHealth;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
	
	private PlayerHealth plugin;

	public PlayerListener(PlayerHealth plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoinBefore(PlayerJoinEvent event){
		// Set a new player specific scoreboard
		event.getPlayer().setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoinAfter(PlayerJoinEvent event){
		if(event.getPlayer().hasPermission("playerhealth.*")){
			if(PlayerHealth.versionChecker!=null && !PlayerHealth.versionChecker.latestVersion.equals(plugin.getDescription().getVersion())){
				event.getPlayer().sendMessage("There is a new version of " + ChatColor.YELLOW + "PlayerHealth" + ChatColor.WHITE + " available!");
			}
		}
		if(event.getPlayer().hasPermission("playerhealth.show")){
			plugin.showHealth(event.getPlayer());
		}
	}
}
