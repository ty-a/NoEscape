/*Copyright (C) 2014 TyA <tyler@faceyspacies.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files 
(the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
 subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */
package com.faceyspacies.NoEscape;

import java.util.LinkedList;
import java.util.List;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NoEscape extends JavaPlugin {
	
	protected static List<Player> NoEscapePlayers = new LinkedList<Player>();
	protected static List<EntityType> safeMobs = new LinkedList<EntityType>();
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig(); // shouldn't override actual config if it exist
		
		if(this.getConfig().getBoolean("auto-update")) {
			Updater updater = new Updater(this, 78377, this.getFile(), Updater.UpdateType.DEFAULT, false);
			Updater.UpdateResult result = updater.getResult();
			switch(result) {
			case SUCCESS:
				getLogger().info("Found new version; Updating. Reload or Restart Server to finalize.");
				break;
			case DISABLED:
				break;
			case FAIL_APIKEY:
				getLogger().info("An invalid API key was provided. Please ensure you have the correct key in the Updater/config.yml file.");
				break;
			case FAIL_BADID:
				break;
			case FAIL_DBO:
				getLogger().info("Unable to contact dev.bukkit.org; unable to check for update");
				break;
			case FAIL_DOWNLOAD:
				getLogger().info("A new version is available, but failed to download it.");
				break;
			case FAIL_NOVERSION:
				break;
			case NO_UPDATE:
				break;
			case UPDATE_AVAILABLE:
				break;
			}
		}
		else if(this.getConfig().getBoolean("check-for-updates")) {
			Updater updater = new Updater(this, 78377, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
			
			if(updater.getResult().equals(Updater.UpdateResult.UPDATE_AVAILABLE)) {
				getLogger().info("A new version of NoEscape is available! Please update soon!");

			}
		}
		
		getCommand("noescape").setExecutor(this);
		getServer().getPluginManager().registerEvents(new NoEscapeListener(this), this);
		
		List<String> safeMobsFromConfig = this.getConfig().getStringList("safe-mobs");
		for(String element: safeMobsFromConfig) {
			element = element.toUpperCase().replace(" ", "_");
			if(EntityType.valueOf(element) != null )
				safeMobs.add(EntityType.valueOf(element));
		}		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("noescape")) { 
			if(args.length < 1)
				return false; // not correct num of params
			
			if(!args[0].equalsIgnoreCase("reload"))
				return false; // invalid command
			
			if (!sender.hasPermission("noescape.reload")) {
				sender.sendMessage("You do not have permission to reload the configuration");
				return true;
			}
			
			this.reloadConfig();
			sender.sendMessage("Reloaded the config");
			return true;
		} 
		return false; 
	}
}
