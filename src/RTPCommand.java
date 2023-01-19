import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPCommand implements CommandExecutor
{
	private RTP pl;
	
	public RTPCommand(RTP plugin)
	{
		pl = plugin;
		pl.getCommand("rtp").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("rtp"))
		{
			if(args.length == 0)
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage(ChatColor.RED + "Command only runs from ingame");
					return false;
				}
				
				Player player = (Player) sender;
				String player_id = player.getUniqueId().toString();
				
				if(!player.hasPermission("rtp.use"))
				{
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("no-permission-msg")));
					return false;
				}
				
				if(!pl.hasPlayerRanCommand(player_id) && !player.hasPermission("rtp.bypass"))
				{
					pl.addPlayerToDelay(player_id);
				}
				else if(pl.hasPlayerRanCommand(player_id) && !player.hasPermission("rtp.bypass"))
				{
					Integer time_must_wait = pl.getConfig().getInt("rtp-delay");
					Long time_taken = pl.getTime(player_id);
					Integer time_left = time_must_wait - time_taken.intValue();
					
					if(time_left == 1)
					{
						player.sendMessage(ChatColor.RED + "Please wait " + time_left + " second");
						return true;
					}
					else if(time_left == 0)
					{
						player.sendMessage(ChatColor.RED + "Try now!");
						pl.removePlayerFromDelay(player_id);
						return true;
					}
					else if(time_left < 0)
					{
						pl.removePlayerFromDelay(player_id);
						pl.teleportPlayer(player);
						pl.addPlayerToDelay(player_id);
						return true;
					}
					
					player.sendMessage(ChatColor.RED + "Please wait " + time_left + " seconds");
					return false;
				}
				
				pl.teleportPlayer(player);
				return true;
			}
			else if(args.length == 1)
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage(ChatColor.RED + "Command only runs from ingame");
					return false;
				}
				
				Player player = (Player) sender;
				
				if(!player.hasPermission("rtp.reload"))
				{
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("no-permission-msg")));
					return false;
				}
				
				if(args[0].equalsIgnoreCase("reload"))
				{
					pl.reloadConfig();
					player.sendMessage(ChatColor.GREEN + "RTP Reloaded!");
					return true;
				}
			}
		}
		
		return false;
	}
	
}
