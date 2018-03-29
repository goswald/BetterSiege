package bettersiege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterSiege extends JavaPlugin{
    Functions f = new Functions();
    ArrayList<Player> moveList = new ArrayList();
    HashMap<Location, Catapult> CatapultMap = new HashMap<>();
    HashMap<Player, Catapult> pCatapultMap = new HashMap<>();
    
    Logger BetterSiegeLogger= Bukkit.getLogger();
    @Override
    public void onEnable() {
        BetterSiegeLogger.info("Here's some message spam. BukkitPractice works.");
    }
    @Override
    public void onDisable() {
        BetterSiegeLogger.info("BukkitPractice has been disabled. WHAT HAVE YOU DONE?");
    }
    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        if(e.getClickedBlock() instanceof Sign)
        {
            Sign s = (Sign)e.getClickedBlock();
            if(s.getLine(0).equalsIgnoreCase("Catapult")) {
                if(f.isCatapult(e.getClickedBlock().getLocation())) {
                    if(e.getPlayer().hasPermission("BetterSiege*") || e.getPlayer().hasPermission("Catapult")) {
                        moveList.add(e.getPlayer());
                        pCatapultMap.put(e.getPlayer(), CatapultMap.get(e.getClickedBlock().getLocation()));
                    }
                    else e.getPlayer().sendMessage("You cannot operate a catapult.");
                }
            }
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(moveList.contains(e.getPlayer())) e.setCancelled(true);
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
}
