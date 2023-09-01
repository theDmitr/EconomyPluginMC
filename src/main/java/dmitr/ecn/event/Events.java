package dmitr.ecn.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import dmitr.ecn.SaltGenerator;
import dmitr.ecn.db.LockedDoor;

public class Events implements Listener {
	
	private JavaPlugin plugin;
	
	public Events(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		Block block = event.getBlock();

		if (isDoor(block) && LockedDoor.isLock(block)) {
			LockedDoor.remove(block);
		}

	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block against = event.getBlockAgainst();
		
		if (isDoor(against))
			event.setCancelled(true);
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {

		Action action = event.getAction();
		Block clicked = event.getClickedBlock();
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		ItemStack handItem = player.getItemInHand();
		
		if (action == Action.RIGHT_CLICK_BLOCK && isDoor(block)) {
			
			event.setCancelled(true);

			if (event.getHand() == EquipmentSlot.OFF_HAND || player.hasCooldown(Material.IRON_NUGGET))
				return;
			
			if (LockedDoor.isLock(block)) {
				
				if (isKey(handItem)) {
					String salt = LockedDoor.getKeySalt(handItem); 
					if (salt.equals(LockedDoor.get(block).salt)) {
						if (player.isSneaking()) {
							LockedDoor.remove(block);
							handItem.setAmount(handItem.getAmount() - 1);
							givePlayerItem(player, getNamedItemStack(new ItemStack(Material.IRON_NUGGET), "Замок"));
						} else
							changeCurrentDoor(block);
					} else {
					}
					player.setCooldown(Material.IRON_NUGGET, 20);
				} else {
				}
			}
			
			else if (isDoorLock(handItem) && player.isSneaking() && !player.hasCooldown(Material.IRON_NUGGET)) {
				
				String salt = SaltGenerator.getString(5);
				new LockedDoor(salt, clicked);
				handItem.setAmount(handItem.getAmount() - 1);
				givePlayerItem(player, getNamedItemStack(new ItemStack(Material.IRON_NUGGET), "Ключ " + salt));
				
				player.setCooldown(Material.IRON_NUGGET, 20);
			}
			
			else {
				changeCurrentDoor(block);
				player.playSound(player.getLocation(), !((Door) block.getBlockData()).isOpen() ? Sound.BLOCK_WOODEN_DOOR_OPEN : Sound.BLOCK_WOODEN_DOOR_CLOSE, 3f, 3f);
				event.setCancelled(false);
			}
		}

	}
	
	@EventHandler
	public void onRedstoneCurrent(BlockRedstoneEvent event) {
		
		Block block = event.getBlock();
		
		if (isDoor(block))
			event.setNewCurrent(((Door) block.getBlockData()).isOpen() ? 1 : 0);
			
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void anvilCost(PrepareAnvilEvent e){
		Player player = (Player) e.getView().getPlayer();
		AnvilInventory inv = e.getInventory();
		Bukkit.getScheduler().runTask(plugin, () -> inv.setRepairCost(0));
		inv.setRepairCost(0);
		inv.setMaximumRepairCost(0);
		//if (player.getLevel() < 1)
			//player.setLevel(player.getLevel() + inv.getRepairCost());
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void InventoryClick (InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            if (e.getInventory().getType() == InventoryType.ANVIL) {
                AnvilInventory anvil = (AnvilInventory) e.getInventory();
                InventoryType.SlotType slotType = e.getSlotType();
                if (slotType == InventoryType.SlotType.RESULT) {
                	player.setLevel(player.getLevel() + anvil.getRepairCost());
                }
                
            }
        }
    }
	
	private static void changeCurrentDoor(Block block) {
		Door door = (Door) block.getBlockData();
		door.setOpen(!door.isOpen());
		block.setBlockData((BlockData) door);
	}
	
	private static boolean isDoor(Block clickedBlock) {
		return clickedBlock.getBlockData() instanceof Door;
	}
	
	private static boolean isKey(ItemStack stack) {
		return stack.getType() == Material.IRON_NUGGET && stack.getItemMeta().getDisplayName().substring(0, 5).equals("Ключ ");
	}
	
	private static boolean isDoorLock(ItemStack stack) {
		return stack.getType() == Material.IRON_NUGGET && stack.getItemMeta().getDisplayName().equals("Замок");
	}

	private static ItemStack getNamedItemStack(ItemStack stack, String name) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private static void givePlayerItem(Player player, ItemStack stack) {
		Inventory inventory = player.getInventory();
		if (inventory.firstEmpty() != -1)
			inventory.addItem(stack);
		else
			player.getWorld().dropItem(player.getLocation(), stack);
	}

}