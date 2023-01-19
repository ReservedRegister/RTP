import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RTP extends JavaPlugin
{
	private List<String> player_delay;
	private Map<String, Long> delay_time = new HashMap<>();
	
	public void onEnable()
	{
		new RTPCommand(this);
		saveDefaultConfig();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getDescription().getName() + " is enabled!");
		initFields();
	}
	
	public void initFields()
	{
		player_delay = new ArrayList<>();
	}
	
	public Long getTime(String player_id)
	{
		Long time_taken = (System.currentTimeMillis() - delay_time.get(player_id)) / 1000L;
		return time_taken;
	}
	
	public boolean hasPlayerRanCommand(String player_id)
	{
		if(player_delay.contains(player_id) && delay_time.containsKey(player_id))
			return true;
		
		return false;
	}
	
	public void addPlayerToDelay(String player_id)
	{
		delay_time.put(player_id, System.currentTimeMillis());
		player_delay.add(player_id);
	}
	
	public void removePlayerFromDelay(String player_id)
	{
		for(int i = 0; i < player_delay.size(); i++)
		{
			if(player_id.equals(player_delay.get(i)))
			{
				player_delay.remove(i);
				delay_time.remove(player_id);
			}
		}
	}
	
	public int getSurface(int x, int z, String world_name)
	{
		World player_world = getServer().getWorld(world_name);
		int air_counter = 0;
		
		for(int i = 60; i < player_world.getMaxHeight(); i++)
		{
			if(air_counter == 10)
			{
				return i - 9;
			}
			Location location = new Location(getServer().getWorld(world_name), x, i, z);
			
			if(player_world.getBlockAt(location).getType().equals(Material.AIR))
			{
				air_counter++;
			}
			else
				air_counter = 0;
		}
		
		return 100;
	}
	
	public boolean hasPlayerTPedInWater(int x, int y, int z, String world_name)
	{
		World player_world = getServer().getWorld(world_name);
		Location location = new Location(getServer().getWorld(world_name), x, y, z);
		
		for(int i = 0; i < 10; i++)
		{
			location.setY(y);
			if(player_world.getBlockAt(location).getType().equals(Material.WATER))
				return true;
			
			y--;
		}
		
		return false;
	}
	
	public void teleportPlayer(Player player)
	{
		int x,y,z;
		World player_world = player.getWorld();
		WorldBorder world_border = player_world.getWorldBorder();
		double limit = world_border.getSize() / 2 - 1;
		List<String> allowed_worlds = getConfig().getStringList("allowed_worlds");
		Random random = new Random();
		
		if(!allowed_worlds.contains(player_world.getName()))
		{
			player.sendMessage(ChatColor.RED + "You are not allowed to random teleport here");
			return;
		}
		
		x = random.nextInt((int) limit);
		z = random.nextInt((int) limit);
		y = getSurface(x, z, player_world.getName());
		
		while(hasPlayerTPedInWater(x, y ,z , player_world.getName()))
		{
			x = random.nextInt((int) limit);
			z = random.nextInt((int) limit);
			y = getSurface(x, z, player_world.getName());
		}
		player.teleport(new Location(player_world, x, y, z));
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("rtp-message")));
	}
}
