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

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class NoEscapeListener implements Listener {
	
	private JavaPlugin plugin;
	
	public NoEscapeListener(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void dmg(EntityDamageEvent event) {
		if(event.isCancelled())
			return;
		
		boolean causedByPlayer = false;
		boolean hitPlayer = false;
		
		Entity damagee = event.getEntity();
		
		List<String> list = plugin.getConfig().getStringList("disabled-in-worlds");
		if(list.contains(damagee.getWorld().getName())) 
			return; // we are in a disabled world
		
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if(e.getDamager() instanceof Player) // the one that hit the thing is a player
				causedByPlayer = true;
		}
		
		if(damagee instanceof Player) 
			hitPlayer = true;		
		
		if(!hitPlayer && !causedByPlayer) 
			return; // no players involved, no need to continue
		
		if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
				event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
				event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
		
			if( hitPlayer || causedByPlayer ) { 
				if(this.plugin.getConfig().getBoolean("PvM")) {
					if( hitPlayer && !causedByPlayer ) {
						if(!((Player)damagee).hasPermission("noescape.bypass"))
							createTask((Player)damagee); // player is hit by entity
					}
					else if ( !hitPlayer && causedByPlayer ) {
						if(this.plugin.getConfig().getBoolean("PvM")) {
							if(!((Player)(((EntityDamageByEntityEvent) event).getDamager())).hasPermission("noescape.bypass"))
								createTask((Player)(((EntityDamageByEntityEvent) event).getDamager()));
							// player is hitting something not human
						}
					}
				}
				
				if(this.plugin.getConfig().getBoolean("PvP")) {
					if(!((Player)damagee).hasPermission("noescape.bypass"))
						createTask((Player)damagee);
				
					if(!((Player)(((EntityDamageByEntityEvent) event).getDamager())).hasPermission("noescape.bypass"))
						createTask((Player)(((EntityDamageByEntityEvent) event).getDamager()));
				}
			}
				
			return; // end events that were an entity_attack
		}
		
		if(event.getCause() == EntityDamageEvent.DamageCause.FALL ) {
			if(this.plugin.getConfig().getBoolean("ignore-falling"))
				return;
			else {
				if(!((Player)damagee).hasPermission("noescape.bypass"))
					createTask((Player)damagee);
			}
			
			return;
		}
		
		if(this.plugin.getConfig().getBoolean("PvE")) {
			// I believe all the other damage types are from the environment
			if(!((Player)damagee).hasPermission("noescape.bypass"))
				createTask((Player)damagee);
		}
			
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(NoEscape.NoEscapePlayers.contains(player)) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("message")));
		}
	}
	
	private void createTask(Player player) {
		NoEscape.NoEscapePlayers.add(player);
		BukkitTask task = new NoEscapeTask(player)
                          .runTaskLater(this.plugin, 20 * this.plugin.getConfig().getInt("timer"));
	}

}
