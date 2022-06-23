package pl.jdPajor.bazary.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.jdPajor.bazary.data.Bazar;
import pl.jdPajor.bazary.data.BazarItem;
import pl.jdPajor.bazary.util.Colors;

public class BazarListener implements Listener {
	
	@EventHandler
	public void bz1(PlayerMoveEvent e) {
		if (Bazar.getBazar(e.getPlayer().getName()) != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void bz2(PlayerTeleportEvent e) {
		if (Bazar.getBazar(e.getPlayer().getName()) != null) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void bz3(PlayerPickupItemEvent e) {
		if (e.getPlayer() instanceof Player)
		if (Bazar.getBazar(e.getPlayer().getName()) != null) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void bz4(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().startsWith("/bazar")) return;
		if (Bazar.getBazar(e.getPlayer().getName()) != null) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(Colors.fix("&cNie mozesz uzywac komend gdy masz otwarty bazar!"));
		}
	}

	@EventHandler
	public void bq1(PlayerQuitEvent e) {
		Bazar b = Bazar.getBazar(e.getPlayer().getName());
		if (b != null) {
			for (BazarItem i : b.items) {
				e.getPlayer().getInventory().addItem(i.is);
			}
			Bazar.bazars.remove(b);
		}
	}
	

	@EventHandler
	public void bazarBuy(InventoryClickEvent e) {
		if (e.getView().getTitle().contains(Colors.fix("&cBazar gracza: &f"))) {
			e.setCancelled(true);
			Player o = Bukkit.getPlayer(e.getView().getTitle().replace(Colors.fix("&cBazar gracza: &f"), ""));
			Bazar b = Bazar.getBazar(o.getName());
			if (b.items.size() < e.getSlot()) return;
			int price = b.items.get(e.getSlot()).price;
			if (e.getWhoClicked().getInventory().containsAtLeast(new ItemStack(Material.DIAMOND), price)) {
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().getInventory().removeItem(new ItemStack(Material.DIAMOND, price));
				o.getInventory().addItem(new ItemStack(Material.DIAMOND, price));
				e.getWhoClicked().getInventory().addItem(b.items.get(e.getSlot()).is);
				b.items.remove(e.getSlot());
				for (HumanEntity ent : e.getViewers()) {
					ent.closeInventory();
				}
			} else {
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().sendMessage(Colors.fix("&cNie posiadasz wystarczajacej ilosci przedmiotow aby to kupic!"));
			}
		}
	}
	
	@EventHandler
	public void bazarOpen(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player)) {
			return;
		}
		Bazar b = Bazar.getBazar(e.getRightClicked().getName());
		if (b == null) {
			return;
		}
		Inventory inv = Bukkit.createInventory(e.getPlayer(), 27, Colors.fix("&cBazar gracza: &f" + e.getRightClicked().getName()));
		if (b.items.size() == 0) {
			e.getPlayer().sendMessage(Colors.fix("&cTen bazar jest pusty!"));
			return;
		}
		for (int i = 0; i < b.items.size(); i++) {
			BazarItem it = b.items.get(i);
			ItemStack is = it.is.clone();
			ItemMeta im = is.getItemMeta();
			List<String> lr = new ArrayList<String>();
			if (im.hasLore()) {
				lr = im.getLore();
			}
			if (lr == null) lr = new ArrayList<String>();
			lr.add(Colors.fix("&7============="));
			lr.add(Colors.fix("&6Cena: &5" + it.price));
			lr.add(Colors.fix("&7============="));
			im.setLore(lr);
			is.setItemMeta(im);
			inv.setItem(i, is);
		}
		e.getPlayer().openInventory(inv);
	}
	
}
