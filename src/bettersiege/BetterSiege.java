package bettersiege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterSiege extends JavaPlugin implements Listener {
    //Useful things for the entire class
    Functions f = new Functions();
    HashMap<Location, Catapult> CatapultMap = new HashMap<>();
    HashMap<Player, Catapult> pCatapultMap = new HashMap<>();
    ArrayList<Wall> wallList = new ArrayList<>();
    HashMap<Player, ArrayList<Block>> constructWallMap = new HashMap<>();
    ArrayList<Player> constructWallList = new ArrayList<>();
    HashMap<Player, Integer> repairWallMap = new HashMap<>();
    ArrayList<Player> repairCatapultList = new ArrayList<>();
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
                        if(!repairCatapultList.contains(e.getPlayer())) {
                            if(e.getPlayer().hasPermission("BetterSiege.*") || e.getPlayer().hasPermission("BetterSiege.Catapult")) {
                                Inventory catapultInventory = Bukkit.createInventory(e.getPlayer(), 9, "Catapult");
                                int ammunition = CatapultMap.get(e.getClickedBlock().getLocation()).ammunition;
                                int i = 0;
                                while(ammunition > 0) {
                                    catapultInventory.setItem(i, new ItemStack(Material.STONE, 16));
                                }
                                e.getPlayer().openInventory(catapultInventory);
                                pCatapultMap.put(e.getPlayer(), CatapultMap.get(e.getClickedBlock().getLocation()));
                                e.getPlayer().sendMessage("You are now operating the catapult.");
                            }
                            else e.getPlayer().sendMessage("You cannot operate a catapult.");
                        }
                        else {
                            if(e.getPlayer().hasPermission("BetterSiege.*") || e.getPlayer().hasPermission("BetterSiege.buildCatapult")) {
                                CatapultMap.get(e.getClickedBlock().getLocation()).addHealth(10);
                                repairCatapultList.remove(e.getPlayer());
                                e.getPlayer().sendMessage("You repaired the catapult.");
                            }
                        }
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
                            sES.schedule(new wallRunnable(e.getPlayer(), this.getServer()), 1, TimeUnit.MILLISECONDS);
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
            if(repairWallMap.containsKey(e.getPlayer())) {
                for(int i = 0; i < wallList.size(); i++) {
                    if(wallList.get(i).insideWall(e.getClickedBlock())) {
                        wallList.get(i).repairWall(repairWallMap.get(e.getPlayer()));
                        repairWallMap.remove(e.getPlayer());
                        e.getPlayer().sendMessage("You have healed the ward.");
                    }
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
                        boolean isWall = false;
                        Block hitBlock = e.getHitBlock();
                        l2.lock();
                        try {
                            for(int i = 0; i < wallList.size(); i++) {
                                if(wallList.get(i).insideWall(hitBlock)) {
                                    if(wallList.get(i).damageWall(20) <= 0) {
                                        wallList.remove(i);
                                    }
                                    isWall = true;
                                    i = wallList.size();
                                }
                            }
                        }
                        finally {
                            l2.unlock();
                        }
                        if(!isWall) {
                            e.getHitBlock().setType(Material.AIR);
                            new Location(e.getHitBlock().getWorld(), e.getHitBlock().getX() + 1, e.getHitBlock().getY(), e.getHitBlock().getZ()).getBlock().setType(Material.AIR);
                            new Location(e.getHitBlock().getWorld(), e.getHitBlock().getX() - 1, e.getHitBlock().getY(), e.getHitBlock().getZ()).getBlock().setType(Material.AIR);
                            new Location(e.getHitBlock().getWorld(), e.getHitBlock().getX(), e.getHitBlock().getY() + 1, e.getHitBlock().getZ()).getBlock().setType(Material.AIR);
                            new Location(e.getHitBlock().getWorld(), e.getHitBlock().getX(), e.getHitBlock().getY() - 1, e.getHitBlock().getZ()).getBlock().setType(Material.AIR);
                            new Location(e.getHitBlock().getWorld(), e.getHitBlock().getX(), e.getHitBlock().getY(), e.getHitBlock().getZ() + 1).getBlock().setType(Material.AIR);
                            new Location(e.getHitBlock().getWorld(), e.getHitBlock().getX(), e.getHitBlock().getY(), e.getHitBlock().getZ() - 1).getBlock().setType(Material.AIR);
                            e.getHitBlock().getWorld().playSound(e.getHitBlock().getLocation(), Sound.BLOCK_ANVIL_BREAK, 2, 0.5F);
                        }
                    }
                }
            }
        }
        //Handles events where projectiles hit a siege weapon
        if(e.getHitBlock() != null && e.getEntity() instanceof Arrow) {
            l.lock();
            try {
                Iterator<Catapult> catapults = CatapultMap.values().iterator();
                while(catapults.hasNext()) {
                    Catapult c = catapults.next();
                    List<Location> blocks = c.getBlocks();
                    for(int i = 0; i < blocks.size(); i++) {
                        if(blocks.get(i).equals(e.getHitBlock().getLocation())) {
                            Arrow a = (Arrow)e.getEntity();
                            if(a.isCritical()) {
                                if(c.addDamage(15)) {
                                    c.end();
                                }
                            }
                            else {
                                if(c.addDamage(10)) {
                                    c.end();
                                }
                            }
                        }
                    }
                }
            }
            finally {
                l.unlock();
            }
        }
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
    //Prevents putting anything but a 16 stack of stone into a catapult slot.
    @EventHandler
    public void onInventoryClick(InventoryInteractEvent e) {
        if(e.getInventory().getName() != null) {
            if(e.getInventory().getName().equalsIgnoreCase("Catapult")) {
                for(int i = 0; i < e.getInventory().getSize(); i++) {
                    if(e.getInventory().getItem(i).getType() != Material.STONE) {
                        if(e.getInventory().getItem(i).getType() != Material.AIR) e.setCancelled(true);
                    }
                    else {
                        if(e.getInventory().getItem(i).getAmount() != 16) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    //Calculates ammunition.
    @EventHandler
    public void onExitInventory(InventoryCloseEvent e) {
        if(e.getInventory().getName() != null) {
            if(e.getInventory().getName().equalsIgnoreCase("Catapult")) {
                l.lock();
                try {
                    int amount = 0;
                    for(int i = 0; i < e.getInventory().getSize(); i++) {
                        if(e.getInventory().getItem(i).getType() == Material.STONE) {
                            amount = amount + 16;
                        }
                    }
                    pCatapultMap.get((Player)e.getPlayer()).setAmmunition(amount);
                }
                finally {
                    l.unlock();
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
        if(commandLabel.equalsIgnoreCase("cRepair")) {
            if(theSender instanceof Player) {
                if(theSender.hasPermission("BetterSiege.*") || theSender.hasPermission("BetterSiege.buildCatapult")) {
                    Player p = (Player)theSender;
                    l.lock();
                    try {
                        repairCatapultList.add(p);
                        p.sendMessage("Interact with the catapult sign to repair it.");
                    }
                    finally {
                        l.unlock();
                    }
                }
            }
        }
        if(commandLabel.equalsIgnoreCase("wBuild")) {
            if(theSender instanceof Player) {
                if(theSender.hasPermission("BetterSiege.*") || theSender.hasPermission("BetterSiege.Wall")) {
                    l2.lock();
                    try {
                        Player p = (Player)theSender;
                        p.sendMessage("Hit a corner of the wall, or the sign if this is the final step.");
                        constructWallList.add(p);
                    }
                    finally {
                        l2.unlock();
                    }
                }
            }
        }
        if(commandLabel.equalsIgnoreCase("wRepair")) {
            if(theSender instanceof Player) {
                if(theSender.hasPermission("BetterSiege.*") || theSender.hasPermission("BetterSiege.Wall")) {
                    if(args.length == 1) {
                        Player p = (Player)theSender;
                        if(p.getHealth() - Integer.parseInt(args[0]) > 0) {
                            p.setHealth(p.getHealth() - Integer.parseInt(args[0]));
                            repairWallMap.put((Player)theSender, Integer.parseInt(args[0]));
                            p.sendMessage("Interact with a part of ward to repair it.");
                        }
                    }
                }
            }
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
                        else {
                            pCatapultMap.remove(p);
                        }
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
        Block wardBlock;
        Server s;
        
        wallRunnable(Player player, Server server) {
            p = player;
            s = server;
        };
        @Override
        public void run() {
            l2.lock();
            try {
                blockList = constructWallMap.get(p);
                ward = (Sign)blockList.get(8);
                wardBlock = blockList.get(8);
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
            ArrayList<Block> newBlockList = f.getWallMaterials(blockArray[0].getWorld(), blockArray);
            try {
                this.wait(1000);
            } 
            catch (InterruptedException ex) {
                Logger.getLogger(BetterSiege.class.getName()).log(Level.SEVERE, null, ex);
            }
            int health = f.calculateMaxHealth(newBlockList);
            try {
                this.wait(5000);
            } 
            catch (InterruptedException ex) {
                Logger.getLogger(BetterSiege.class.getName()).log(Level.SEVERE, null, ex);
            }
            Wall w = new Wall();
            w.createWall(blockArray, health, wardBlock);
            ward.setLine(0, ChatColor.GOLD + "-WARD-");
            ward.setLine(1, ChatColor.BLACK + "HEALTH:");
            ward.setLine(2, ChatColor.GOLD + Integer.toString(w.damageWall(0)));
            ward.setLine(3, ChatColor.BLACK + Integer.toString(w.damageWall(0)));
            p.sendMessage("The wall has been constructed.");
        }
    }
}
