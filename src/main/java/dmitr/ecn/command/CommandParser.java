package dmitr.ecn.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandParser implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		String cmd = command.getName();

		if (cmd.equalsIgnoreCase("search")) {
			
			Player searched = (Player) Bukkit.getServer().getPlayer(args[0]);
			
			if (args.length != 1)
				player.sendMessage("Используйте: /search <player>");
			else if (searched == null)
				player.sendMessage("Данный игрок отсутствует!");
			else if (searched == (Player) sender)
				player.sendMessage("Вы не можете обыскать самого себя!");
			else if (!playerHasNearbyEntity(player, searched)) {
				player.sendMessage("Игрок находится слишком далеко!");
			}
			else
				player.openInventory(searched.getInventory());
		}

		return true;
	}
	
	private static boolean playerHasNearbyEntity(Player player, Entity entity) {
        for (Entity ent : player.getNearbyEntities(1, 1, 1))
        	if (ent.equals(entity)) 
        		return true;
		return false;
	}

}
