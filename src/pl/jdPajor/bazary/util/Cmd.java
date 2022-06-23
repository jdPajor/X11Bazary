package pl.jdPajor.bazary.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class Cmd extends Command {
	private  String name;
    
    public Cmd( String name) {
        super(name);
        this.name = name;
    }
    
    public boolean execute( CommandSender sender,  String label,  String[] args) {
        return this.onExecute(sender, args);
    }
    
    public abstract boolean onExecute( CommandSender sender,  String[] args);
    
    public String getName() {
        return this.name;
    }
}
