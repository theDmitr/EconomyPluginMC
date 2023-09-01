package dmitr.ecn;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import dmitr.ecn.command.CommandParser;
import dmitr.ecn.db.LockedDoor;
import dmitr.ecn.event.Events;

public class EconomyPlugin extends JavaPlugin implements CommandExecutor {

	@Override
	public void onEnable() {
		getCommand("search").setExecutor(new CommandParser());
		getServer().getPluginManager().registerEvents(new Events(this), this);
		LockedDoor.loadLockedDoors();
	}
	
	@Override
	public void onDisable() {
		LockedDoor.saveLockedDoors();
	}

}
