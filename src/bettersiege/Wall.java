package bettersiege;

import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

public class Wall {
    int health;
    int maxHealth;
    int maxX;
    int minX;
    int maxY;
    int minY;
    int maxZ;
    int minZ;
    Block sign;
    
    void fetchData(FileConfiguration config, String section, Server server) {
        
    }
    void saveData(FileConfiguration config, String section, BetterSiege betterSiege) {
        
    }
    boolean createWall(Block[] blockArray, int Health, Block ward) {
        health = Health;
        maxHealth = Health;
        //Ensuring none of the blocks are the same block.
        for(int i = 0; i < blockArray.length; i++) {
            for(int i2 = 0; i2 < blockArray.length; i++) {
                if(blockArray[i].equals(blockArray[i2])) {
                    if(i != i2) return false;
                }
            }
        }
        //Ensuring all of the blocks line up as they should to form 8 even corners.
        if(blockArray[0].getX() != blockArray[6].getX()) return false;
        if(blockArray[4].getX() != blockArray[2].getX()) return false;
        if(blockArray[5].getX() != blockArray[3].getX()) return false;
        if(blockArray[1].getX() != blockArray[7].getX()) return false;
        if(blockArray[2].getX() != blockArray[6].getX()) return false;
        if(blockArray[4].getX() != blockArray[0].getX()) return false;
        if(blockArray[5].getX() != blockArray[1].getX()) return false;
        if(blockArray[3].getX() != blockArray[7].getX()) return false;
        if(blockArray[0].getY() != blockArray[1].getY()) return false;
        if(blockArray[6].getY() != blockArray[7].getY()) return false;
        if(blockArray[2].getY() != blockArray[3].getY()) return false;
        if(blockArray[5].getY() != blockArray[4].getY()) return false;
        if(blockArray[4].getY() != blockArray[0].getY()) return false;
        if(blockArray[5].getY() != blockArray[1].getY()) return false;
        if(blockArray[7].getY() != blockArray[3].getY()) return false;
        if(blockArray[2].getY() != blockArray[6].getY()) return false;
        if(blockArray[2].getZ() != blockArray[4].getZ()) return false;
        if(blockArray[4].getZ() != blockArray[5].getZ()) return false;
        if(blockArray[5].getZ() != blockArray[3].getZ()) return false;
        if(blockArray[3].getZ() != blockArray[2].getZ()) return false;
        if(blockArray[7].getZ() != blockArray[6].getZ()) return false;
        if(blockArray[6].getZ() != blockArray[0].getZ()) return false;
        if(blockArray[0].getZ() != blockArray[1].getZ()) return false;
        if(blockArray[1].getZ() != blockArray[7].getZ()) return false;
        //Setting all the values.
        maxX = blockArray[1].getX();
        minX = blockArray[0].getX();
        maxY = blockArray[0].getY();
        minY = blockArray[6].getY();
        maxZ = blockArray[4].getZ();
        minZ = blockArray[0].getZ();
        return true;
    }
    boolean insideWall(Block b) {
        if(b.getX() <= maxX) {
            if(b.getX() >= minX) {
                if(b.getY() <= maxY) {
                    if(b.getY() >= minY) {
                        if(b.getZ() <= maxZ) {
                            if(b.getZ() >= minZ) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    int damageWall(int amount) {
        health = health - amount;
        return health;
    }
    int repairWall(int amount) {
        health = health + amount;
        if(health > maxHealth) health = maxHealth;
        return health;
    }
}
