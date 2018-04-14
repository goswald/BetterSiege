package bettersiege;

import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Functions {
    //ensures that the structure matches the schematic
    //not yet detailed
    boolean isCatapult(Location l)
    {
        try {
            for(int i = 0; i < 4; i++) {
                InputStream fis = getClass().getResourceAsStream("plugins" + '\\' + "BetterSiege" + '\\' + "Catapult" + Integer.toString(i) + ".schematic");
                NBTTagCompound nbtdata = NBTCompressedStreamTools.a(fis);;
                short width = nbtdata.getShort("Width");
                short height = nbtdata.getShort("Height");
                short length = nbtdata.getShort("Length");
                byte[] blocks = nbtdata.getByteArray("Blocks");
                fis.close();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //calculates the vector for the catapult bolt
    Vector catapultProjectile(Player p, double d, double d2) {
        Vector cP = p.getVelocity();
        cP.setX(cP.getX());
        cP.setY(cP.getBlockY());
        cP.setZ(cP.getBlockZ());
        return cP;
    }
    //Gives the bottom back left, bottom back right, top forward left, top forward right, top back left, top back right, bottom forward left, and bottom forward right blocks in order
    Block[] sortBlocks(ArrayList<Block> blockList) {
        Block[] blockArray = new Block[8];
        Block bottomBackLeft = blockList.get(0);
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getX() < bottomBackLeft.getX()) {
                if(blockList.get(i).getY() < bottomBackLeft.getY()) {
                    if(blockList.get(i).getZ() < bottomBackLeft.getZ()) bottomBackLeft = blockList.get(i);
                }
            }
        }
        blockArray[0] = bottomBackLeft;
        Block bottomBackRight = blockList.get(0);
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getX() > bottomBackRight.getX()) {
                if(blockList.get(i).getY() < bottomBackRight.getY()) {
                    if(blockList.get(i).getZ() < bottomBackRight.getZ()) bottomBackRight = blockList.get(i);
                }
            }
        }
        blockArray[1] = bottomBackRight;
        Block topForwardLeft = blockList.get(0);
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getX() < topForwardLeft.getX()) {
                if(blockList.get(i).getY() > topForwardLeft.getY()) {
                    if(blockList.get(i).getZ() > topForwardLeft.getZ()) topForwardLeft = blockList.get(i);
                }
            }
        }
        blockArray[2] = topForwardLeft;
        Block topForwardRight = blockList.get(0);
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getX() < topForwardRight.getX()) {
                if(blockList.get(i).getY() > topForwardRight.getY()) {
                    if(blockList.get(i).getZ() > topForwardRight.getZ()) topForwardRight = blockList.get(i);
                }
            }
        }
        blockArray[3] = topForwardRight;
        Block topBackLeft = blockList.get(0);
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getX() < topBackLeft.getX()) {
                if(blockList.get(i).getY() < topBackLeft.getY()) {
                    if(blockList.get(i).getZ() > topBackLeft.getZ()) topBackLeft = blockList.get(i);
                }
            }
        }
        blockArray[4] = topBackLeft;
        Block topBackRight = blockList.get(0);
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getX() > topBackRight.getX()) {
                if(blockList.get(i).getY() < topBackRight.getY()) {
                    if(blockList.get(i).getZ() > topBackRight.getZ()) topBackRight = blockList.get(i);
                }
            }
        }
        blockArray[5] = topBackRight;
        Block bottomForwardLeft = blockList.get(0);
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getX() > bottomForwardLeft.getX()) {
                if(blockList.get(i).getY() < bottomForwardLeft.getY()) {
                    if(blockList.get(i).getZ() > bottomForwardLeft.getZ()) bottomForwardLeft = blockList.get(i);
                }
            }
        }
        blockArray[6] = bottomForwardLeft;
        Block bottomForwardRight = blockList.get(0);
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getX() > bottomForwardRight.getX()) {
                if(blockList.get(i).getY() < bottomForwardRight.getY()) {
                    if(blockList.get(i).getZ() > bottomForwardRight.getZ()) bottomForwardRight = blockList.get(i);
                }
            }
        }
        blockArray[7] = bottomForwardRight;
        return blockArray;
    }
    ArrayList<Block> getWallMaterials(World world, Block[] blockArray) {
        int maxX = blockArray[1].getX();
        int maxY = blockArray[0].getY();
        int maxZ = blockArray[4].getZ();
        ArrayList<Block> blockList = new ArrayList<>();
        for(int minX = blockArray[0].getX(); minX <= maxX; minX++) {
            for(int minY = blockArray[6].getY(); minY <= maxY; minY++) {
                for(int minZ = blockArray[0].getZ(); minZ <= maxZ; minZ++) blockList.add(new Location(world, minX, minY, minZ).getBlock());
            }
        }
        return blockList;
    }
    int calculateMaxHealth(ArrayList<Block> blockList) {
        int health = 0;
        for(int i = 0; i < blockList.size(); i++) {
            if(blockList.get(i).getType() == Material.AIR) health = health - 5;
            else {
                
            }
        }
        return health;
    }
}
