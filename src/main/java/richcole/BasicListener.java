package richcole;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BasicListener implements Listener {
	
	private Logger log;
	private Plugin owner;
	
	private Map<Player, Sign> lastSignMap = Maps.newHashMap();
	private Map<Player, Block> lastBlockPlaced = Maps.newHashMap();
	private Map<Player, Location> markMap = Maps.newHashMap();
	private Server server;

	public BasicListener(Plugin owner, Logger log, Server server) {
		this.log = log;
		this.owner = owner;
		this.server = server;
	}
	
	public boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		log.info("Command received: " + cmd.getName() + " " + Joiner.on(" ").join(args));
		List<String> argList = Lists.newArrayList(args);
		if ( cmd.getName().equals("bp") ) {
			String subCommand = args[0];
			List<String> subArgList = argList.subList(1, argList.size());
			if ( args.length >= 1 ) {
				if (subCommand.equals("mark")) {
					return setMark(sender, subArgList);
				}
				if (subCommand.equals("fill")) {
					return fill(sender, subArgList);
				}
				if (subCommand.equals("stroke")) {
					return stroke(sender, subArgList);
				}
				if (subCommand.equals("clear")) {
					return clear(sender, subArgList);
				}
				if (subCommand.equals("set")) {
					return setMetaDataCommand(sender, subArgList);
				}
				if (subCommand.equals("teleport")) {
					return teleportCommand(sender, subArgList);
				}
				if (subCommand.equals("where")) {
					return whereCommand(sender, subArgList);
				}
				if (subCommand.equals("shoot")) {
					return shootCommand(sender, subArgList);
				}
				return false;
			}
			
		}
		return false;
	}
	
	private boolean shootCommand(CommandSender sender, List<String> subArgList) {
		if ( sender instanceof Player ) {
			Player player = (Player) sender;
			player.launchProjectile(SmallFireball.class);
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onInteraction(PlayerInteractEvent event) {
		ItemStack i = event.getItem();
		Player p = event.getPlayer();
		if (i != null && i.getType() != null && i.getType().equals(Material.BLAZE_ROD)) {
			Fireball fireball = p.launchProjectile(Fireball.class);
			fireball.setYield(0);
		}
	}
	
	private boolean fill(CommandSender sender, List<String> subArgList) {
		if ( sender instanceof Player ) {
			Player player = (Player) sender;
			Block lastBlock = lastBlockPlaced.get(player);
			Location ll = lastBlock.getLocation();
			Location tr = markMap.get(player);
			World world = ll.getWorld();
			int x1 = (int)Math.min(ll.getX(), tr.getX());
			int x2 = (int)Math.max(ll.getX(), tr.getX());
			int y1 = (int)Math.min(ll.getY(), tr.getY());
			int y2 = (int)Math.max(ll.getY(), tr.getY());
			int z1 = (int)Math.min(ll.getZ(), tr.getZ());
			int z2 = (int)Math.max(ll.getZ(), tr.getZ());
			
			player.sendMessage("Filling " + x1 + " -> " + x2 + ", " + y1 + " -> " + y2 + ", " + + z1 + " -> " + z2 + " with " + lastBlock.getType());
			for(int x=x1; x<=x2; x+=1) {
				for(int y=y1; y<=y2; y+=1) {
					for(int z=z1; z<=z2; z+=1) {
						 world.getBlockAt(x,y,z).setType(lastBlock.getType());
					}
				}
			}
			return true;
		}
		else {
			sender.sendMessage("You are not a player");
			return false;
		}
	}

	private boolean stroke(CommandSender sender, List<String> subArgList) {
		if ( sender instanceof Player ) {
			Player player = (Player) sender;
			Block lastBlock = lastBlockPlaced.get(player);
			Location ll = lastBlock.getLocation();
			Location tr = markMap.get(player);
			World world = ll.getWorld();
			int x1 = (int)Math.min(ll.getX(), tr.getX());
			int x2 = (int)Math.max(ll.getX(), tr.getX());
			int y1 = (int)Math.min(ll.getY(), tr.getY());
			int y2 = (int)Math.max(ll.getY(), tr.getY());
			int z1 = (int)Math.min(ll.getZ(), tr.getZ());
			int z2 = (int)Math.max(ll.getZ(), tr.getZ());
			
			player.sendMessage("Stroking " + x1 + " -> " + x2 + ", " + y1 + " -> " + y2 + ", " + + z1 + " -> " + z2 + " with " + lastBlock.getType());
			for(int x=x1; x<=x2; x+=1) {
				for(int y=y1; y<=y2; y+=1) {
					world.getBlockAt(x,y,z1).setType(lastBlock.getType());
					world.getBlockAt(x,y,z2).setType(lastBlock.getType());
				}
			}
			for(int x=x1; x<=x2; x+=1) {
				for(int z=z1; z<=z2; z+=1) {
					world.getBlockAt(x,y1,z).setType(lastBlock.getType());
					world.getBlockAt(x,y2,z).setType(lastBlock.getType());
				}
			}
			for(int y=y1; y<=y2; y+=1) {
				for(int z=z1; z<=z2; z+=1) {
					world.getBlockAt(x1,y,z).setType(lastBlock.getType());
					world.getBlockAt(x2,y,z).setType(lastBlock.getType());
				}
			}
			return true;
		}
		else {
			sender.sendMessage("You are not a player");
			return false;
		}
	}

	private boolean clear(CommandSender sender, List<String> subArgList) {
		if ( sender instanceof Player ) {
			Player player = (Player) sender;
			Block lastBlock = lastBlockPlaced.get(player);
			Location ll = lastBlock.getLocation();
			Location tr = markMap.get(player);
			World world = ll.getWorld();
			int x1 = (int)Math.min(ll.getX(), tr.getX());
			int x2 = (int)Math.max(ll.getX(), tr.getX());
			int y1 = (int)Math.min(ll.getY(), tr.getY());
			int y2 = (int)Math.max(ll.getY(), tr.getY());
			int z1 = (int)Math.min(ll.getZ(), tr.getZ());
			int z2 = (int)Math.max(ll.getZ(), tr.getZ());
			
			player.sendMessage("Clearing " + x1 + " -> " + x2 + ", " + y1 + " -> " + y2 + ", " + + z1 + " -> " + z2 + " with " + lastBlock.getType());
			player.sendMessage("Filling " + x1 + " -> " + x2 + ", " + y1 + " -> " + y2 + ", " + + z1 + " -> " + z2 + " with " + lastBlock.getType());
			for(int x=x1; x<=x2; x+=1) {
				for(int y=y1; y<=y2; y+=1) {
					for(int z=z1; z<=z2; z+=1) {
						 world.getBlockAt(x,y,z).setType(Material.AIR);
					}
				}
			}
			return true;
		}
		else {
			sender.sendMessage("You are not a player");
			return false;
		}
	}

	private boolean setMark(CommandSender sender, List<String> subArgList) {
		if ( sender instanceof Player ) {
			Player player = (Player) sender;
			Location location = lastBlockPlaced.get(player).getLocation();
			markMap.put(player, location);
			sender.sendMessage("Mark set to " + getStringFromLocation(location));
			return true;
		}
		else {
			sender.sendMessage("You are not a player");
			return false;
		}
	}

	private Location getLocationFromString(Server server, String s) {
		try {
			List<String> fields = Lists.newArrayList(Splitter.on(":").split(s));
			if ( fields.size() == 1 ) {
				String worldName = fields.get(0);
				return server.createWorld(new WorldCreator(worldName)).getSpawnLocation();
			}
			else {
				String worldName = fields.get(0);
				Integer xBlock = Integer.valueOf(fields.get(1));
				Integer yBlock = Integer.valueOf(fields.get(2));
				Integer zBlock = Integer.valueOf(fields.get(3));
				World world = server.createWorld(new WorldCreator(worldName));
				return new Location(world, xBlock, yBlock, zBlock);
			}
		}
		catch(Exception e) {
			throw new RuntimeException("Unable to parse " + s);
		}
	}
	
	private String getStringFromLocation(Location l) {
		return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
		
	}

	private boolean whereCommand(CommandSender sender, List<String> argList) {
		if ( sender instanceof Player ) {
			Player p = (Player) sender;
			p.sendMessage("You are " + getStringFromLocation(p.getLocation()));
			return true;
		}
		else {
			sender.sendMessage("You are not a player!");
			return false;
		}
	}

	private boolean teleportCommand(CommandSender sender, List<String> argList) {
		log.info("teleport command");
		if ( argList.size() != 2 ) {
			sender.sendMessage("Expected [player] [location]");
			return false;
		}
		try {
			String playerString = argList.get(0);
			String locationString = argList.get(1);
			Player player = server.getPlayer(playerString);
			Location location = getLocationFromString(server, locationString);
			log.info("Teleporting player " + player.getName() + " to " + getStringFromLocation(location));
			player.teleport(location);
			return true;
		}
		catch(Exception e) {
			log.info("Exception raised: " + e.getMessage());
			sender.sendMessage("Unable to process command " + e.getMessage());
			return false;
		}
	}

	private boolean setMetaDataCommand(CommandSender sender, List<String> argList) {
		String key = argList.get(0);
		String value = argList.get(1);
		if ( sender instanceof Player ) {
			Player p = (Player) sender;
			Sign sign = lastSignMap.get(p);
			if ( sign != null ) {
				sign.setMetadata(key, new FixedMetadataValue(owner, value));
				sender.sendMessage("set " + key + "=" + value);
				return true;
			} 
			else {
				sender.sendMessage("no sign selected");
			}
		}
		else {
			sender.sendMessage("You are not a player!");
		}
		return false;
	}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	event.getPlayer().setGameMode(GameMode.ADVENTURE);
    	PlayerInventory iv = event.getPlayer().getInventory();
    	iv.setItem(2, newSignWriter());
    	Bukkit.broadcastMessage("The server welcomes " + event.getPlayer().getDisplayName());
    }	
    
    private ItemStack newSignWriter() {
    	ItemStack item = new ItemStack(Material.BOOK_AND_QUILL);
    	ItemMeta im = item.getItemMeta();
    	im.setLore(Lists.newArrayList("Sign meta writer"));
    	item.setItemMeta(im);
    	return item;
	}

	@EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	if (isLobby(event.getPlayer()) && !isOp(event.getPlayer())) {
    		event.setCancelled(true);
    	}
    }
    
    private boolean isOp(Player player) {
		return player.getGameMode().equals(GameMode.CREATIVE);
	}

	@EventHandler
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
    	if (isLobby(event.getPlayer()) && !isOp(event.getPlayer())) {
    		event.setCancelled(true);
    	}
    }

    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event) {
    	if (isLobby(event.getPlayer()) && !isOp(event.getPlayer())) {
    		event.setCancelled(true);
    	}
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) { 
        Entity entity = event.getEntity(); 
        Location location = entity.getLocation();
        World world = entity.getWorld();

        if (event.getSpawnReason() == SpawnReason.EGG && event.getEntityType() == EntityType.CHICKEN) {
            entity.remove(); 
            for (int i = 0 ; i < 2 ; i++) {
                world.spawnEntity(location, EntityType.PIG);
            }
        }
    }
	
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
    	if (isLobby(event.getEntity()) ) {
	    	if (event.getEntityType() == EntityType.PLAYER) {
	    		event.setCancelled(true);
	    	}
    	}
    }
    
    @EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
    	if ( isLobby(event.getBlock()) ) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	Block b = e.getClickedBlock();
    	if( b != null ) { 
    		BlockState s = b.getState();
    		if ( s instanceof Sign ) {
    			List<MetadataValue> ms = s.getMetadata("teleport");
    			for(MetadataValue v: ms) {
    				teleportToWorld(e.getPlayer(), v.asString());
    				return;
    			}
    			e.getPlayer().sendMessage("sign selected");
    			lastSignMap.put(e.getPlayer(), (Sign)s);
    		}
    	}
    }
    
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
    	Block b = e.getBlockPlaced();
    	if( b != null ) { 
    		lastBlockPlaced.put(e.getPlayer(), b);
    	}
    }
    
	private void teleportToWorld(Player p, String name) {
		World world = server.createWorld(new WorldCreator(name));
		p.teleport(world.getSpawnLocation());
	}


	// @EventHandler
    public void fireGun(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() == Action.LEFT_CLICK_AIR ||
           e.getAction() == Action.LEFT_CLICK_BLOCK)
        {
    		if (p.getItemInHand().getType() != Material.APPLE) {
    			  return;
			}
    		
        	// createTower(p);
    		teleportToWorld(p, "world");
    		    		
    		e.setCancelled(true);
        }
    }

	private void createTower(Player p) {
		
		Vector d = p.getLocation().getDirection();
		
		int x = p.getLocation().getBlockX() + (int)(d.getX() * 5); 
		int y = p.getLocation().getBlockY() + (int)(d.getY() * 5);
		int z = p.getLocation().getBlockZ() + (int)(d.getZ() * 5);

		int[] tower = {
		    x + 1, y, z,
		    x - 1, y, z,
		    x, y, z + 1,
		    x, y, z - 1,
		    x + 1, y, z + 1,
		    x - 1, y, z - 1,
		    x + 1, y, z - 1,
		    x - 1, y, z + 1
		};

		World world = p.getWorld();

		int layer = 0;
		int height = 3;

		for (int i = 0; i < height; i++) { 
		    for (int j = 0; j < tower.length; j += 3) {
		        world.getBlockAt(
		    		tower[j],
		            tower[j + 1] + layer,
		            tower[j + 2]
				).setType(Material.BRICK);
		    }
		    layer++;
		}
	}

    private boolean isLobby(Block block) {
    	return isLobby(block.getWorld());
	}

	private boolean isLobby(World world) {
		return world.getName().equals("world");
	}

    private boolean isLobby(Entity entity) {
    	return isLobby(entity.getWorld());
	}


}
