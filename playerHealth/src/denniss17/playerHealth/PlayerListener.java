package denniss17.playerHealth;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
		HashSet<String> set = new HashSet<String>();
		set.add(event.getPlayer().getName());
		plugin.tags.put(event.getPlayer(), set);
		
		if(event.getPlayer().hasPermission("playerhealth.*")){
			if(PlayerHealth.versionChecker!=null && PlayerHealth.versionChecker.latestVersion!=null && !PlayerHealth.versionChecker.latestVersion.equals(plugin.getDescription().getVersion())){
				event.getPlayer().sendMessage("There is a new version of " + ChatColor.YELLOW + "PlayerHealth" + ChatColor.WHITE + " available!");
			}
		}
		if(event.getPlayer().hasPermission("playerhealth.show")){
			plugin.showHealth(event.getPlayer());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event){
		// Cleanup
		plugin.tags.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		plugin.sendHealthOfPlayer(event.getPlayer(), (int) (event.getPlayer()).getMaxHealth());
	}
    
    @EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity() instanceof Player){
			plugin.sendHealthOfPlayer((Player)event.getEntity(), (int) (((Player)event.getEntity()).getHealth()-event.getDamage()));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerHealthRegain(EntityRegainHealthEvent event){
		if(event.getEntity() instanceof Player){
			plugin.sendHealthOfPlayer((Player)event.getEntity(), (int) (((Player)event.getEntity()).getHealth()+event.getAmount()));
		}
	}
}
