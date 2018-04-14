package bettersiege;

//All of the data for a catapult and the things to be done to it

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Catapult {
    int health = 100;
    int charge = 0;
    int ammunition = 0;
    Location l;
    List<Location> blocks;
    boolean isEnded = false;
    
    void fetchData(FileConfiguration config, String section, Server server) {
        ConfigurationSection sec = config.getConfigurationSection(section);
        List<String> data = sec.getStringList("List");
        health = Integer.parseInt(data.get(0));
        charge = Integer.parseInt(data.get(1));
        ammunition = Integer.parseInt(data.get(2));
        l = new Location(server.getWorld(sec.getString("location.World")), sec.getDouble("location.X"), sec.getDouble("location.Y"), sec.getDouble("location.Z"));
    }
    void saveData(FileConfiguration config, String section, BetterSiege betterSiege) {
        ConfigurationSection sec = config.getConfigurationSection(section);
        List<String> data = new ArrayList<>();
        data.add(0, Integer.toString(health));
        data.add(1, Integer.toString(charge));
        data.add(2, Integer.toString(ammunition));
        sec.set("location.World" , l.getWorld().getName());
        sec.set("location.X" , l.getX());
        sec.set("location.Y" , l.getY());
        sec.set("location.Z" , l.getZ());
        sec.getConfigurationSection(section);
        betterSiege.saveConfig();
    }
    void setLocation(Location loc, List<Location> bloc) {
        l = loc;
        blocks = bloc;
    }
    List<Location> getBlocks() {
        return blocks;
    }
    boolean addDamage(int amount) {
        health = health - amount;
        if(health <= 0) return true;
        return false;
    }
    int addHealth(int amount) {
        health = health + amount;
        if(health > 100) health = 100;
        return health;
    }
    int getHealth() {
        return health;
    }
    void resetCharge() {
        charge = 0;
    }
    int addCharge() {
        charge = charge + 4;
        if(charge > 20) charge = 20;
        return charge;
    }
    boolean isCharged() {
        if(charge == 20) return true;
        return false;
    }
    void setAmmunition(int amount) {
        ammunition = amount;
    }
    int getAmmunition() {
        return Math.floorDiv(ammunition, 16);
    }
    void end() {
        isEnded = true;
    }
    boolean isEnded() {
        return isEnded;
    }
}
