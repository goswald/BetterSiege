package bettersiege;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterSiege extends JavaPlugin{
    //Useful things for the entire class
    Functions f = new Functions();
    HashMap<Location, Catapult> CatapultMap = new HashMap<>();
    HashMap<Player, Catapult> pCatapultMap = new HashMap<>();
    ScheduledExecutorService sES = Executors.newScheduledThreadPool(1);
        public void inCatapult() {
    }
    
    Logger BetterSiegeLogger= Bukkit.getLogger();
    @Override
    public void onEnable() {
        BetterSiegeLogger.info("BetterSiege has been enabled.");
    }
    @Override
    public void onDisable() {
        sES.shutdown();
        BetterSiegeLogger.info("BetterSiege has been disabled.");
    }
    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if(e.getClickedBlock() instanceof Sign)
        {
            Sign s = (Sign)e.getClickedBlock();
            if(s.getLine(0).equalsIgnoreCase("Catapult")) {
                //Not all things that say catapult meet the requirements to be a catapult -- the sign placed with proper perms and a correct structure
                if(CatapultMap.containsKey(e.getClickedBlock().getLocation()) && f.isCatapult(e.getClickedBlock().getLocation())) {
                    if(e.getPlayer().hasPermission("BetterSiege*") || e.getPlayer().hasPermission("Catapult")) {
                        pCatapultMap.put(e.getPlayer(), CatapultMap.get(e.getClickedBlock().getLocation()));
                        sES.scheduleWithFixedDelay(new catapultRunnable(e.getPlayer(), CatapultMap.get(e.getClickedBlock())), 0, 500, TimeUnit.MILLISECONDS);
                    }
                    else e.getPlayer().sendMessage("You cannot operate a catapult.");
                }
            }
        }
    }
    @Override
    public boolean onCommand(CommandSender theSender, Command cmd, String commandLabel, String[] args) {
        if(commandLabel.equalsIgnoreCase("sHelp")) {
            theSender.sendMessage("Use the first letter of a siege weapon followed by \"Help\" to get help for that specific weapon. For example, use /cHelp to get help for the catapult.");
        }
        if(commandLabel.equalsIgnoreCase("cHelp")) {
            theSender.sendMessage("Use /cExit to leave the catapult.");
            theSender.sendMessage("Use /cFire [Release angle, with 0 being the back and 180 being the end of the catapult] [Horizontal direction, within a range of rotation of -45 at leftmost and 45 at rightmost] to fire a catapult.");
            theSender.sendMessage("You can only fire once loaded (the hunger bar is full). Your health is the health of the catapult.");
        }
        if(commandLabel.equalsIgnoreCase("cExit")) {
            
        }
        return true;
    }
    public class catapultRunnable implements Runnable {
        public catapultRunnable(Player p, Catapult c) {};
        
        @Override
        public void run() {
            
        }
    }
}
