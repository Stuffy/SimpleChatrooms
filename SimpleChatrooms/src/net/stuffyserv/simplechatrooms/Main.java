package net.stuffyserv.simplechatrooms;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {
	
	ArrayList<Object[]> rooms = new ArrayList<Object[]>();
	
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
	}
	
	public void onDisable() {
		// BlaBla
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("createroom")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("simplechatrooms.create")) {
					
					if (args.length > 2) {
						sender.sendMessage("Zu viele Argumente angegeben.");
						return false;
					}
					else if (args.length < 2) {
						return false;
					}
					
					Object[] roomprops = new Object[3];
					ArrayList<String> members = new ArrayList<String>();
					
					roomprops[0] = args[0];
					roomprops[1] = args[1];
					roomprops[2] = members;
					
					rooms.add(roomprops);
					
					sender.sendMessage("Raum mit dem Namen: " + ChatColor.GREEN + args[0] + ChatColor.WHITE + " und dem Passwort: " + ChatColor.GREEN + args[1] + ChatColor.WHITE + " hinzugefügt");
					
					return true;
					
				}
				else {
					return false;
				}
			}
			else {
				sender.sendMessage("Es können keine Chatrooms von der Konsole aus erstellt werden.");
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("deleteroom")) {
			if (sender instanceof Player) {
				for (int i = 0; i < rooms.size(); i++) {
					Object[] subarray = rooms.get(i);
					
					@SuppressWarnings("unchecked")
					ArrayList<Player> roommembers = (ArrayList<Player>) subarray[2];
					
					if (subarray[0].equals(args[0])) {
						rooms.remove(i);
						sender.sendMessage("Raum " + ChatColor.RED + ((String) subarray[0]) + ChatColor.WHITE + " gelöscht.");
						for (Player p : roommembers) {
							p.sendMessage("Dein Raum wurde gelöscht!");
						}
						return true;
					}
				}
				sender.sendMessage("Raum:" + ChatColor.RED + args[0] + ChatColor.WHITE + " wurde nicht gefunden.");
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("joinroom")) {
			if (sender instanceof Player) {
				if (rooms.size() > 0) {
					if (args.length == 2) {
						if (getRoom((Player) sender) == "NONE") {
							for (int i = 0; i < rooms.size(); i++) {
								Object[] subarray = rooms.get(i);
								String roomname = (String) subarray[0];
								String password = (String) subarray[1];
								if (args[0].equals(roomname) && args[1].equals(password)) {
									@SuppressWarnings("unchecked")
									ArrayList<Player> playerlist = (ArrayList<Player>) subarray[2];
									
									for (Player p : playerlist) {
										p.sendMessage("Player " + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " ist dem Raum beigetreten.");
									}
									playerlist.add((Player) sender);
									
									subarray[2] = (Object) playerlist;
									rooms.set(i, subarray);
									
									sender.sendMessage("Du bist dem Raum " + ChatColor.RED + roomname + ChatColor.WHITE + " beigetreten.");
									
									return true;
								}
							}
							sender.sendMessage("Falsches Passwort oder Raum nicht gefunden.");
							return true;
						}
						else {
							sender.sendMessage("Du bist bereits im Raum " + ChatColor.RED + getRoom((Player) sender) + ChatColor.WHITE + ".");
							return true;
						}
					}
					else {
						sender.sendMessage("Not all args");
						return false;
					}
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("showrooms")) {
			if (rooms.size() > 0) {
				for (Object[] subarray : rooms) {
					@SuppressWarnings("unchecked")
					int membercount = ((ArrayList<Player>) subarray[2]).size();
					sender.sendMessage("Raumname: " + ChatColor.RED + subarray[0].toString() + ChatColor.WHITE + " (" + ChatColor.GREEN + membercount + ChatColor.WHITE + ")");
				}
				return true;
			}
			else {
				sender.sendMessage("Keine Räume gefunden");
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("moveplayer")) {
			Player target = (Bukkit.getServer().getPlayer(args[0]));
			boolean check = false;
			boolean check2 = false;
			
			if (target != null) {
				if (rooms.size() > 0) {
					if (args.length == 2) {
						String roomname = "";
						if (args[1].equals("#") && getRoom(target) != "NONE") {
							roomname = getRoom(target);
						}
						else {
							roomname = args[1];
						}
						for (int i = 0; i < rooms.size(); i++) {
							Object[] subarray = rooms.get(i);
							
							@SuppressWarnings("unchecked")
							ArrayList<Player> playerlist = (ArrayList<Player>) subarray[2];
							
							if (getRoom(target) != "NONE") {
								if (playerlist.contains(target)) {
									playerlist.remove(target);
									
									subarray[2] = (Object) playerlist;
									rooms.set(i, subarray);
									
									for (Player p : playerlist) {
										p.sendMessage("Player " + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " wurde aus dem Raum bewegt.");
									}
									
									check = true;
								}
							}
							
							if (((String) subarray[0]).equals(roomname)) {
								
								for (Player p : playerlist) {
									p.sendMessage("Player " + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " wurde in den Raum bewegt.");
								}
								playerlist.add(target);
								
								subarray[2] = (Object) playerlist;
								rooms.set(i, subarray);
								check2 = true;
							}
						}
						
						if (check && check2) {
							sender.sendMessage("Player " + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + " wurde in den Raum " + ChatColor.RED + roomname + ChatColor.WHITE + " bewegt.");
							return true;
						}
						else {
							sender.sendMessage("Move failed");
							return false;
						}
					}
					else {
						return false;
					}
		        }
			}
		    else {
	        	sender.sendMessage("Spieler " + ChatColor.RED + args[0] + ChatColor.WHITE + " wurde nicht gefunden.");
	        	return true;
		    }
		}
		
		if (cmd.getName().equalsIgnoreCase("showmembers")) {
			if (args.length > 0) {
				String roomname = "";
				for (Object[] subarray : rooms) {
					if (args[0].equals("#") && getRoom((Player) sender) != "NONE") {
						roomname = getRoom((Player) sender);
					}
					else if (getRoom((Player) sender) != "NONE") {
						roomname = args[0];
					}
					else {
						sender.sendMessage("Du bist in keinem Raum.");
						return true;
					}
					
					if (((String) subarray[0]).equals(roomname)) {
						@SuppressWarnings("unchecked")
						int membercount = ((ArrayList<Player>) subarray[2]).size();
						@SuppressWarnings("unchecked")
						ArrayList<Player> playerlist = (ArrayList<Player>) subarray[2];
						sender.sendMessage(ChatColor.YELLOW + "---------------------------------------");
						sender.sendMessage("Member im Raum " + ChatColor.RED + roomname + ChatColor.WHITE + " (" + ChatColor.GREEN + membercount + ChatColor.WHITE + ")");
						sender.sendMessage(ChatColor.YELLOW + "---------------------------------------");
						for (Player p : playerlist) {
							sender.sendMessage(ChatColor.GREEN + p.getName());
						}
						return true;
					}
				}
				sender.sendMessage("Raum " + ChatColor.RED + args[0] + ChatColor.WHITE + " wurde nicht gefunden.");
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("leaveroom")) {
			if (sender instanceof Player) {
				if (rooms.size() > 0) {
					for (int i = 0; i < rooms.size(); i++) {
						Object[] subarray = rooms.get(i);
						
						@SuppressWarnings("unchecked")
						ArrayList<Player> playerlist = (ArrayList<Player>) subarray[2];
						
						if (playerlist.contains((Player) sender)) {
							playerlist.remove((Player) sender);
							
							subarray[2] = (Object) playerlist;
							rooms.set(i, subarray);
							
							sender.sendMessage("Du hast den Raum " + ChatColor.RED + ((String) subarray[0]) + ChatColor.WHITE + " verlassen.");
							
							return true;
						}
					}
				}
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("kickplayer")) {
			if (rooms.size() > 0) {
				if (args.length > 0) {
					for (int i = 0; i < rooms.size(); i++) {
						Object[] subarray = rooms.get(i);
						
						@SuppressWarnings("unchecked")
						ArrayList<Player> playerlist = (ArrayList<Player>) subarray[2];
						
						Player target = (Bukkit.getServer().getPlayer(args[0]));
				        if (target != null) {
							if (playerlist.contains(target)) {
								playerlist.remove(target);
								
								subarray[2] = (Object) playerlist;
								rooms.set(i, subarray);
								
								sender.sendMessage("Player " + ChatColor.GREEN + target.getName() + ChatColor.WHITE + " aus Raum " + ChatColor.RED + subarray[0].toString() + ChatColor.WHITE + " gekickt.");
								target.sendMessage("Du wurdest aus dem Raum " + ChatColor.RED + subarray[0].toString() + ChatColor.WHITE + " gekickt");
								
								return true;
							}
				        }
				        else {
				        	sender.sendMessage("Spieler " + ChatColor.RED + args[0] + ChatColor.WHITE + " wurde nicht gefunden.");
				        	return true;
				        }
					}
				}
				else {
					return false;
				}
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public String getRoom(Player pl) {
		if (rooms.size() >= 0) {
			for (Object obj : rooms) {
				Object[] subarray = (Object[]) obj;
				if (((ArrayList<Player>) subarray[2]).contains(pl)) {
					return (String) subarray[0];
				}
			}
		}
		return "NONE";
	}
	
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
    	
    	Player sender = e.getPlayer();
    	
    	for (Player p : Bukkit.getOnlinePlayers()) {
    		String playersroom = getRoom(sender);
    		if (!getRoom(p).equals(playersroom)) {
    			e.getRecipients().remove(p);
    		}
    		
    		if (p.isOp()) {
    			if (e.getMessage().contains(p.getName())) {
    				if (!getRoom(p).equals(getRoom(e.getPlayer()))) {
    					e.setMessage(ChatColor.BLUE + "[" + getRoom(e.getPlayer()) + "]" + ChatColor.WHITE + e.getMessage());
    				}
    			}
    		}
    	}
    	
    	if (!getRoom(e.getPlayer()).equals("NONE")) {
    		e.setMessage(ChatColor.RED + "[" + getRoom(sender) + "] " + ChatColor.WHITE + e.getMessage());
    	}
    }
	
}