package denniss17.playerHealth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

public class TagAPIListener implements Listener {

	private PlayerHealth plugin;

	public TagAPIListener(PlayerHealth plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onNameTag(PlayerReceiveNameTagEvent event) {
    	// A new tag is send -> add it to list
    	plugin.tags.get(event.getNamedPlayer()).add(event.getTag());
    }
}
