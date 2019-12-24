package com.manelnavola.mcinteractive.adventure.customitems;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import com.manelnavola.mcinteractive.adventure.CustomItemInfo;
import com.manelnavola.mcinteractive.adventure.CustomItemStackBuilder;
import com.manelnavola.mcinteractive.adventure.CustomTrail;

@SuppressWarnings("deprecation")
public class ThrowableStone extends CustomItemStackable {
	
	private static CustomTrail trail = new CustomTrail(Particle.SMOKE_NORMAL, 1, 0.1);
	
	public ThrowableStone() {
		super(new CustomItemFlag[] {CustomItemFlag.DISPENSES, CustomItemFlag.RIGHT_CLICK});
		ItemStack common = new CustomItemStackBuilder<>(Material.STONE)
				.name("Throwable Block")
				.amount(10)
				.lore("Right click to throw!")
				.lore(ChatColor.GRAY + "Stackable")
				.addEnchantEffect()
				.build();
		setRarities(common, null, null, null);
	}
	
	@Override
	public void onPlayerInteract(Player p, CustomItemInfo cii) {
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SNOW_GOLEM_SHOOT, 1F, 1F);
		FallingBlock fb = p.getWorld().spawnFallingBlock(p.getLocation(), new MaterialData(Material.STONE));
		fb.setVelocity(p.getLocation().getDirection().multiply(0.7F).add(new Vector(0, 0.2F, 0)));
		registerTrail(fb, trail);
	}
	
	@Override
	public void onBlockDispense(Location l, Vector dir, CustomItemInfo cii) {
		FallingBlock fb = l.getWorld().spawnFallingBlock(l, new MaterialData(Material.STONE));
		fb.setVelocity(dir.multiply(0.7F).add(new Vector(0, 0.2F, 0)));
		registerTrail(fb, trail);
	}
	
}
