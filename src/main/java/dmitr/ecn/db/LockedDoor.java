package dmitr.ecn.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class LockedDoor {

	public static List<LockedDoor> doors = new ArrayList<>();

	public final String salt;
	public final Block door;

	public LockedDoor(String salt, Block door) {
		this.salt = salt;
		this.door = door;
		doors.add(this);
	}

	public static LockedDoor get(String salt) {
		for (LockedDoor d : doors) {
			if (d.salt == salt)
				return d;
		}
		return null;
	}

	public static LockedDoor get(Block door) {
		for (LockedDoor d : doors) {
			if ((equalsDoors(d.door, door)))
				return d;
		}
		return null;
	}

	public static boolean remove(String salt) {
		for (LockedDoor d : doors) {
			if (d.salt.equals(salt)) {
				doors.remove(d);
				return true;
			}
		}
		return false;
	}

	public static boolean remove(Block door) {
		for (LockedDoor d : doors) {
			if (equalsDoors(d.door, door)) {
				doors.remove(d);
				return true;
			}
		}
		return false;
	}

	public static boolean isLock(Block door) {
		for (LockedDoor d : doors) {
			if (equalsDoors(d.door, door))
				return true;
		}
		return false;
	}

	private static boolean equalsDoors(Block door1, Block door2) {
		return (door1.getX() == door2.getX() && door1.getY() == door2.getY() && door1.getZ() == door2.getZ())
				|| (door1.getX() == door2.getX() && door1.getY() + 1 == door2.getY() && door1.getZ() == door2.getZ())
				|| (door1.getX() == door2.getX() && door1.getY() - 1 == door2.getY() && door1.getZ() == door2.getZ());
	}
	
	public static String getKeySalt(ItemStack stack) {
		return stack.getItemMeta().getDisplayName().substring(5, stack.getItemMeta().getDisplayName().length());
	}

	public static void loadLockedDoors() {
		try (BufferedReader reader = new BufferedReader(new FileReader("plugins/EconomyPlugin/lockedDoors.txt"))) {
			String line;
			String[] splt;
			while ((line = reader.readLine()) != null) {
				splt = line.split("#");
				new LockedDoor(splt[0], Bukkit.getWorld("world").getBlockAt(Integer.parseInt(splt[1]),
						Integer.parseInt(splt[2]), Integer.parseInt(splt[3])));
			}
		} catch (FileNotFoundException e) {
			new File("plugins/EconomyPlugin").mkdirs();
			try {
				new File("plugins/EconomyPlugin/lockedDoors.txt").createNewFile();
			} catch (IOException e1) {
			}
			loadLockedDoors();
		} catch (IOException e) {
		}
	}

	public static void saveLockedDoors() {
		try (BufferedWriter writter = new BufferedWriter(new FileWriter("plugins/EconomyPlugin/lockedDoors.txt"))) {
			for (LockedDoor door : LockedDoor.doors) {
				writter.write(
						String.format("%s#%d#%d#%d", door.salt, door.door.getX(), door.door.getY(), door.door.getZ())
								+ "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
