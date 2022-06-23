package pl.jdPajor.bazary.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.jdPajor.bazary.data.Bazar;
import pl.jdPajor.bazary.data.BazarItem;
import pl.jdPajor.bazary.util.Cmd;
import pl.jdPajor.bazary.util.Colors;

public class BazarCommand extends Cmd {

	public BazarCommand() {
		super("bazar");
	}

	@Override
	public boolean onExecute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("otworz")) {
				Bazar b = Bazar.getBazar(sender.getName());
				if (b == null) {
					b = new Bazar(sender.getName());
					sender.sendMessage(Colors.fix("&cOtwarto bazar! Aby go zamknac, wpisz: /bazar zamknij"));
				} else {
					sender.sendMessage(Colors.fix("&cMasz juz otwarty bazar!"));
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("zamknij")) {
				Bazar b = Bazar.getBazar(sender.getName());
				if (b == null) {
					sender.sendMessage(Colors.fix("&c7Nie masz otwartego bazaru!"));
				} else {
					for (BazarItem i : b.items) {
						((Player) sender).getInventory().addItem(i.is);
					}
					Bazar.bazars.remove(b);
					sender.sendMessage(Colors.fix("&cZamknieto bazar!"));
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("list")) {
				Bazar b = Bazar.getBazar(sender.getName());
				if (b == null) {
					sender.sendMessage(Colors.fix("&cNie masz otwartego bazaru!"));
				} else {
					for (BazarItem i : b.items) {
						sender.sendMessage(Colors.fix("&c" + i.is.getType().toString() + " &7 za &e" + i.price));
					}
				}
				return true;
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("dodaj")) {
				Bazar b = Bazar.getBazar(sender.getName());
				if (b == null) {
					sender.sendMessage(Colors.fix("&cNie masz otwartego bazaru!"));
				} else {
					if (b.items.size() > 27) {
						sender.sendMessage(Colors.fix("&c7Osagnieto limit przedmiotow na bazarze!"));
						return true;
					}
					int i = 0;
					try {
						i = Integer.parseInt(args[1]);
					} catch (Exception e) {
						sender.sendMessage(Colors.fix("&cWpisz liczbe!"));
						return true; 
					}
					if (i < 1) {
						sender.sendMessage(Colors.fix("&cWpisz liczbe wieksza niz 0!"));
						return true;
					}
					Player p = (Player) sender;
					b.items.add(new BazarItem(i, p.getInventory().getItemInHand()));
					p.getInventory().removeItem(p.getInventory().getItemInHand());
				}
				return true;
			}
			
		}
		sender.sendMessage(Colors.fix("&c/bazar otworz &7- otwiera bazar"));
		sender.sendMessage(Colors.fix("&c/bazar zamknij &7- zamyka bazar"));
		sender.sendMessage(Colors.fix("&c/bazar list &7- lista przedmiotow do sprzedarzy"));
		sender.sendMessage(Colors.fix("&c/bazar dodaj [cena] &7- dodaje item trzymany w lapce do bazaru"));
		return true;
	}

}
