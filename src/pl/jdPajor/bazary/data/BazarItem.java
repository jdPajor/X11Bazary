package pl.jdPajor.bazary.data;

import org.bukkit.inventory.ItemStack;

public class BazarItem {
	public int price;
	public ItemStack is;
	
	public BazarItem(int i, ItemStack s) {
		this.price = i;
		this.is = s;
	}
}
