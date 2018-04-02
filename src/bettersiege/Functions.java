package bettersiege;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Functions {
    //ensures that the structure matches the schematic
    //not yet detailed
    boolean isCatapult(Location l)
    {
        return true;
    }
    //borrowed from sk89q's code. It's under GNU, so everything's fine
    public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
         if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        } else {
            return null;
        }
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
        Block[] blockArray = new Block[4];
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
        return blockArray;
    }
}
