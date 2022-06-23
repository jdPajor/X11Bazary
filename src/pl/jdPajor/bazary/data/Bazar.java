package pl.jdPajor.bazary.data;

import java.util.ArrayList;
import java.util.List;

public class Bazar {
	public static List<Bazar> bazars = new ArrayList<Bazar>();
	
	public static Bazar getBazar(String name) {
		for (Bazar b : bazars) {
			if (b.name.equals(name)) {
				return b;
			}
		}
		return null;
	}
	
	public String name;
	public List<BazarItem> items;
	
	public Bazar(String name) {
		this.name = name;
		this.items = new ArrayList<BazarItem>();
		bazars.add(this); 
	}

}
