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
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
		
		Player damager = null;
		Player damagee = null;
		
		Entity damageeEntity = event.getEntity();
		
		List<String> list = plugin.getConfig().getStringList("disabled-in-worlds");
		if(list.contains(damageeEntity.getWorld().getName())) 
			return; // we are in a disabled world
		
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if(e.getDamager() instanceof Player) {// the one that hit the thing is a player
				causedByPlayer = true;
				damager = (Player)e.getDamager();
			}
			else if(e.getDamager() instanceof Arrow) {
				if(((Arrow) (e.getDamager())).getShooter() instanceof Player) {
					causedByPlayer = true;
					damager = (Player)(((Arrow)e.getDamager()).getShooter());
				}
			}
		}
		
		if(damageeEntity instanceof Player) {
			hitPlayer = true;
			damagee = (Player)damageeEntity;
		}
					
		
		if(!hitPlayer && !causedByPlayer) 
			return; // no players involved, no need to continue
		
		if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
				event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
				event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE ||
				event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
			
			if(!hitPlayer) {
				if(NoEscape.safeMobs.contains(event.getEntity().getType())) {
					return; // player hit a safe to hit mob
				}
			}
			
			if( !hitPlayer && causedByPlayer ) { 
				if(this.plugin.getConfig().getBoolean("PvM")) {
					if(!(damager.hasPermission("noescape.bypass")))
						createTask(damager); // player hit entity
				}
			}
			
			else if( hitPlayer && !causedByPlayer ) {
				if(this.plugin.getConfig().getBoolean("PvM")) {
					if(!(damagee.hasPermission("noescape.bypass")));
						createTask(damagee);
				}
			}
				
			if(this.plugin.getConfig().getBoolean("PvP")) {
				if( hitPlayer && causedByPlayer ) {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
			
					if(!(damager.hasPermission("noescape.bypass")))
						createTask(damager);
				}
			}
				
			return; // end events that were an entity_attack
		}
		
		if(hitPlayer) {
			if( event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
				if(this.plugin.getConfig().getBoolean("ignore-drowning"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}	
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
				if(this.plugin.getConfig().getBoolean("ignore-starvation"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
				if(this.plugin.getConfig().getBoolean("ignore-lava"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.POISON){
				if(this.plugin.getConfig().getBoolean("ignore-poison"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
				if(this.plugin.getConfig().getBoolean("ignore-suffocation"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.CONTACT) {
				if(this.plugin.getConfig().getBoolean("ignore-contact"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
				if(this.plugin.getConfig().getBoolean("ignore-block-explosion"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
				if(this.plugin.getConfig().getBoolean("ignore-falling-block"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.FIRE) {
				if(this.plugin.getConfig().getBoolean("ignore-fire"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
				if(this.plugin.getConfig().getBoolean("ignore-fire-tick"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
				if(this.plugin.getConfig().getBoolean("ignore-lightning"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
				if(this.plugin.getConfig().getBoolean("ignore-magic"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.MELTING) {
				if(this.plugin.getConfig().getBoolean("ignore-snowman-melting"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.WITHER) {
				if(this.plugin.getConfig().getBoolean("ignore-wither"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.FALL ) {
				if(this.plugin.getConfig().getBoolean("ignore-falling"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
				if(this.plugin.getConfig().getBoolean("ignore-void"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.SUICIDE) {
				if(this.plugin.getConfig().getBoolean("ignore-suicide"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}
			else if(event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
				if(this.plugin.getConfig().getBoolean("ignore-custom"))
					return;
				else {
					if(!(damagee).hasPermission("noescape.bypass"))
						createTask(damagee);
				}
			}	
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		List<String> list = plugin.getConfig().getStringList("whitelisted-commands");
		for(String command: list) {
			if(event.getMessage().toLowerCase().startsWith("/" + command.toLowerCase()))
				return;
		}
		
		Player player = event.getPlayer();
		if(NoEscape.NoEscapePlayers.contains(player)) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("message")));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if(event.isCancelled())
			return;
		
		if(plugin.getConfig().getBoolean("while-falling-allow-commands"))
			return; // if we'll allow, nothing to do
		
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return; // if the player is in creative, no reason to continue
		
		Block belowBlock = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
		Block currBlock;
		int numberOfAirBlocks = 0;

		if(belowBlock.getType().equals(Material.AIR)) {
			numberOfAirBlocks++;
			
			for(int i = 1; i <= plugin.getConfig().getInt("air-amount-to-count"); i++) {
				currBlock = belowBlock.getRelative(BlockFace.DOWN);
				if(currBlock.getType().equals(Material.AIR)) {
					belowBlock = currBlock;
					numberOfAirBlocks++;
				} else {
					break;
				}
			}
			// we are above air
			
			if(numberOfAirBlocks >= plugin.getConfig().getInt("air-amount-to-count")) {
				createTask(event.getPlayer());
			}
		}
	}
	
	private void createTask(Player player) {
		NoEscape.NoEscapePlayers.add(player);
		@SuppressWarnings("unused")
		BukkitTask task = new NoEscapeTask(player)
                          .runTaskLater(this.plugin, 20 * this.plugin.getConfig().getInt("timer"));
	}

}
