package richcole;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BasicPlugin extends JavaPlugin {

	private Logger log;
	private BasicListener listener;

	@Override
	public void onEnable() {
		log = getLogger();
		log.info("enabled");
		listener = new BasicListener(this, log, getServer());
		getServer().getPluginManager().registerEvents(listener, this);
	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return listener.handleCommand(sender, cmd, label, args);
	}	
	
}
