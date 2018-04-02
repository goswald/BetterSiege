package bettersiege;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

public class Wall {
    int health;
    int maxHealth;
    Location bottomBackLeft;
    Location bottomBackRight;
    Location topFrontLeft;
    Location topFrontRight;
    void fetchData(FileConfiguration config, String section, Server server) {
        
    }
    void saveData(FileConfiguration config, String section, BetterSiege betterSiege) {
        
    }
    boolean createWall(Block[] blockArray, int Health) {
        health = Health;
        maxHealth = Health;
        for(int i = 0; i < blockArray.length; i++) {
            for(int i2 = 0; i2 < blockArray.length; i++) {
                if(blockArray[i].equals(blockArray[i2])) {
                    if(i != i2) return false;
                }
            }
        }
        if(blockArray[0].getY() != blockArray[1].getY()) return false;
        if(blockArray[0].getZ() != blockArray[1].getZ()) return false;
        if(blockArray[0].getX() != blockArray[2].getX()) return false;
        if(blockArray[1].getX() != blockArray[3].getX()) return false;
        if(blockArray[2].getY() != blockArray[3].getY()) return false;
        if(blockArray[2].getZ() != blockArray[3].getZ()) return false;
        bottomBackLeft = blockArray[0].getLocation();
        bottomBackRight = blockArray[1].getLocation();
        topFrontLeft = blockArray[2].getLocation();
        topFrontRight = blockArray[3].getLocation();
        return true;
    }
}
