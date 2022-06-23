package pl.jdPajor.bazary.util;

import net.md_5.bungee.api.ChatColor;

public class Colors {
	
	public static String fix(String s) {
		return addColors(s.replace("{.}", "\u2022"));
	}

	private static String addColors(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}
