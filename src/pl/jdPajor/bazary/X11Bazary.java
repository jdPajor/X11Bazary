package pl.jdPajor.bazary;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import pl.jdPajor.bazary.cmd.BazarCommand;
import pl.jdPajor.bazary.listener.*;
import pl.jdPajor.bazary.util.CmdMan;

public class X11Bazary extends JavaPlugin {
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new BazarListener(), this);
		CmdMan.register(new BazarCommand());
	}

}
