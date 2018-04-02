package bettersiege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BetterSiege extends JavaPlugin implements Listener {
    //Useful things for the entire class
    Functions f = new Functions();
    HashMap<Location, Catapult> CatapultMap = new HashMap<>();
    HashMap<Player, Catapult> pCatapultMap = new HashMap<>();
    ArrayList<Wall> wallList = new ArrayList<>();
    HashMap<Player, ArrayList<Block>> constructWallMap = new HashMap<>();
    ArrayList<Player> constructWallList = new ArrayList<>();
    static final Logger BetterSiegeLogger = Bukkit.getLogger();
    ScheduledExecutorService sES = Executors.newScheduledThreadPool(2);
    //For catapults.
    ReentrantLock l = new ReentrantLock();
    //For walls.
    ReentrantLock l2 = new ReentrantLock();
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        sES.scheduleWithFixedDelay(new catapultRunnable(), 1, 1, TimeUnit.SECONDS);
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
            if(s.getLine(1).equalsIgnoreCase("[Catapult]")) {
                l.lock();
                try {
                    //Not all things that say catapult meet the requirements to be a catapult -- the sign placed with proper perms and a correct structure
                    if(CatapultMap.containsKey(e.getClickedBlock().getLocation()) && f.isCatapult(e.getClickedBlock().getLocation())) {
                        if(e.getPlayer().hasPermission("BetterSiege.*") || e.getPlayer().hasPermission("BetterSiege.Catapult")) {
                            pCatapultMap.put(e.getPlayer(), CatapultMap.get(e.getClickedBlock().getLocation()));
                            e.getPlayer().sendMessage("You are now operating the catapult.");
                        }
                        else e.getPlayer().sendMessage("You cannot operate a catapult.");
                    }
                }
                finally {
                    l.unlock();
                }
            }
        }
        l2.lock();
        try {
            if(constructWallList.contains(e.getPlayer())) {
                constructWallList.remove(e.getPlayer());
                if(constructWallMap.containsKey(e.getPlayer())) {
                    ArrayList<Block> wallListLocal = constructWallMap.get(e.getPlayer());
                    if(wallListLocal.size() != 8) {
                        wallListLocal.add(e.getClickedBlock());
                        if(wallListLocal.size() == 8) e.getPlayer().sendMessage("Hit a sign for the final block to construct a ward.");
                    }
                    else {
                        if(e.getClickedBlock() instanceof Sign) {
                            wallListLocal.add(e.getClickedBlock());
                            constructWallMap.put(e.getPlayer(), wallListLocal);
                            sES.schedule(new wallRunnable(e.getPlayer()), 1, TimeUnit.MILLISECONDS);
                            e.getPlayer().sendMessage("Please wait while the ward is constructed.");
                        }
                        else {
                            e.getPlayer().sendMessage("You must click a sign to construct the ward.");
                        }
                    }
                }
                else {
                    ArrayList<Block> wallList = new ArrayList<>();
                    wallList.add(e.getClickedBlock());
                    constructWallMap.put(e.getPlayer(), wallList);
                }
            }
        }
        finally {
            l2.unlock();
        }
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        //Handles events where siege projectiles hit a block
        if(e.getEntity() instanceof FallingBlock) {
            FallingBlock block = (FallingBlock)e.getEntity();
            if(block.getCustomName() != null) {
                if(block.getCustomName().equalsIgnoreCase("Catapult Stone")) {
                    if(e.getHitBlock() != null) {
                        Block hitBlock = e.getHitBlock();
                        l2.lock();
                        try {
                            
                        }
                        finally {
                            l2.unlock();
                        }
                    }
                }
            }
        }
        Location loc = e.getEntity().getLocation();
        Vector vec = e.getEntity().getVelocity();
        Location loc2 = new Location(loc.getWorld(), loc.getX()+vec.getX(), loc.getY()+vec.getY(), loc.getZ()+vec.getZ());
    }
    @EventHandler 
    public void onEntityHitEntity(EntityDamageByEntityEvent e) {
        //Handles events where siege projectiles hit an entity
        if(e.getDamager() instanceof FallingBlock) {
            FallingBlock block = (FallingBlock)e.getEntity();
            if(block.getCustomName() != null) {
                if(block.getCustomName().equalsIgnoreCase("Catapult Stone")) {
                    e.setDamage(20);
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
            l.lock();
            try {
                if(theSender instanceof Player) {
                    Player p = (Player)theSender;
                    if(pCatapultMap.containsKey(p)) {
                        pCatapultMap.remove(p);
                        theSender.sendMessage("You have exited the catapult.");
                    }
                }
            }
            finally {
                l.unlock();
            }
        }
        if(commandLabel.equalsIgnoreCase("cFire")) {
            
        }
        return true;
    }
    //Runs every second to update catapults.
    public class catapultRunnable implements Runnable {
        @Override
        public void run() {
            l.lock();
            try {
                if(!pCatapultMap.isEmpty()) {
                    Set operators = pCatapultMap.keySet();
                    Iterator opIterator = operators.iterator();
                    while(opIterator.hasNext()) {
                        Player p = (Player)opIterator.next();
                        Catapult c = pCatapultMap.get(p);
                        if(!c.isEnded()) {
                            p.setHealth(c.getHealth());
                            p.setFoodLevel(c.addCharge());
                        }
                        else pCatapultMap.remove(p);
                    }
                }
            }
            finally {
                l.unlock();
            }
        }
    }
    //Constructs a ward.
    public class wallRunnable implements Runnable {
        Player p;
        ArrayList<Block> blockList;
        Sign ward;
        
        wallRunnable(Player player) {
            p = player;
        };
        @Override
        public void run() {
            l2.lock();
            try {
                blockList = constructWallMap.get(p);
                ward = (Sign)blockList.get(8);
                blockList.remove(8);
                constructWallMap.remove(p);
            }
            finally {
                l2.unlock();
            }
            try {
                this.wait(1000);
            } 
            catch (InterruptedException ex) {
                Logger.getLogger(BetterSiege.class.getName()).log(Level.SEVERE, null, ex);
            }
            Block[] blockArray = f.sortBlocks(blockList);
            
        }
    }
}
