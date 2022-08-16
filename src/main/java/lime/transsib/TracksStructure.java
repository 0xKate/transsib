package lime.transsib;

import mods.railcraft.api.tracks.TrackSpec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.EnumTrackMeta;
import mods.railcraft.common.blocks.tracks.TileTrack;
import mods.railcraft.common.blocks.tracks.TrackTools;

import java.util.Random;

public class TracksStructure extends WorldGenerator
{
    private EnumTrack junctionTrack = EnumTrack.JUNCTION;
    private TrackSpec junctionSpec = junctionTrack.getTrackSpec();
    public TracksStructure() {

    }
    public boolean generate(World w, Random r, int cx, int y, int cz) {
        if (isValidXChunk(cx)) {
            generateXChunk(w, cx, y, cz);
        }

        if (isValidZChunk(cz)) {
            generateZChunk(w, cx, y, cz);
        }

        if (isValidXChunk(cx) && isValidZChunk(cz)) {
            generateCrossroad(w, cx, y, cz);
        }

        return true;
    }

    private boolean isValidXChunk(int cx){
        return (cx == 0 || cx % Config.tunnel_frequency == 0);
    }

    private boolean isValidZChunk(int cz){
        return (cz == 0 || cz % Config.tunnel_frequency == 0);
    }

    private void generateXChunk(World w, int cx, int y, int cz){
        int x = cx * 16;
        int z = cz * 16;
        for (int tz = z; tz <= z + 16; tz++) {
            generateTracks(w, x + 8, y, tz, tz % Config.pumpkin_frequency == 0, Config.booster_frequency > 0 && tz % Config.booster_frequency == 0, "X");
        }
    }

    private void generateZChunk(World w, int cx, int y, int cz){
        int x = cx * 16;
        int z = cz * 16;
        for (int tx = x; tx <= x + 16; tx++) {
            generateTracks(w, tx, y, z + 8, tx % Config.pumpkin_frequency == 0, Config.booster_frequency > 0 && tx % Config.booster_frequency == 0, "Z");
        }
    }

    private void buildCeiling(World w, int x, int y, int z){
        if (isFallingBlock(w, x, y, z)) {
            if (isFallingBlock(w, x, y+1, z)){
                w.setBlock(x, y, z, Blocks.fence);
            } else {
                w.setBlock(x, y, z, Blocks.dirt);
            }

        }


    }

    private void generateCrossroad(World w, int cx, int y, int cz){
        int x = (cx * 16) + 8;
        int z = (cz * 16) + 8;

        if (!noWater(w, x, y, z)) {
            handleWater(w, x,y,z, "XZ");
        }
        buildCeiling(w, x, y+3, z);
        w.setBlock(     x, y+2, z, Blocks.air);
        w.setBlock(     x, y+1, z, Blocks.air);
        TileTrack tile = TrackTools.placeTrack(junctionSpec, w, x, y+0, z, EnumTrackMeta.NORTH_SOUTH.ordinal());

        // and clean up some space around
        buildCeiling(w, x-1, y+3, z-1);
        w.setBlock(     x-1, y+2, z-1, Blocks.air);
        w.setBlock(     x-1, y+1, z-1, Blocks.air);
        w.setBlock(     x-1, y+0, z-1, Blocks.air);

        buildCeiling(w, x+1, y+3, z-1);
        w.setBlock(     x+1, y+2, z-1, Blocks.air);
        w.setBlock(     x+1, y+1, z-1, Blocks.air);
        w.setBlock(     x+1, y+0, z-1, Blocks.air);

        buildCeiling(w, x-1, y+3, z+1);
        w.setBlock(     x-1, y+2, z+1, Blocks.air);
        w.setBlock(     x-1, y+1, z+1, Blocks.air);
        w.setBlock(     x-1, y+0, z+1, Blocks.air);

        buildCeiling(w, x+1, y+3, z+1);
        w.setBlock(     x+1, y+2, z+1, Blocks.air);
        w.setBlock(     x+1, y+1, z+1, Blocks.air);
        w.setBlock(     x+1, y+0, z+1, Blocks.air);

        if (Config.build_beacons == true){
            int ty = w.getTopSolidOrLiquidBlock(x,z);
            for (int good_y = ty+1; good_y > Config.depth + 2; good_y--) {
                if (World.doesBlockHaveSolidTopSurface(w, x, good_y, z)){
                    w.setBlock(x, good_y+1, z, Blocks.lit_pumpkin);
                    break;
                }
            }
        }
    }

    private void handleWater(World w, int x, int y, int z, String axis){
        if (axis.equals("Z")) {
            w.setBlock(x,y-1,z, Blocks.cobblestone);
            w.setBlock(x,y+0,z, Blocks.air);
            w.setBlock(x,y+1,z, Blocks.air);
            w.setBlock(x,y+2,z, Blocks.air);
            w.setBlock(x, y+0, z - 1, Blocks.glass);
            w.setBlock(x, y+1, z - 1, Blocks.glass);
            w.setBlock(x, y+2, z - 1, Blocks.glass);
            w.setBlock(x, y+0, z + 1, Blocks.glass);
            w.setBlock(x, y+1, z + 1, Blocks.glass);
            w.setBlock(x, y+2, z + 1, Blocks.glass);
        } else if (axis.equals("X")) {
            w.setBlock(x,y-1,z, Blocks.cobblestone);
            w.setBlock(x,y+0,z, Blocks.air);
            w.setBlock(x,y+1,z, Blocks.air);
            w.setBlock(x,y+2,z, Blocks.air);
            w.setBlock(x -1, y+0, z, Blocks.glass);
            w.setBlock(x -1, y+1, z, Blocks.glass);
            w.setBlock(x -1, y+2, z, Blocks.glass);
            w.setBlock(x +1, y+0, z, Blocks.glass);
            w.setBlock(x +1, y+1, z, Blocks.glass);
            w.setBlock(x +1, y+2, z, Blocks.glass);
        } else if (axis.equals("XZ")) {
            w.setBlock(x,y-1,z, Blocks.cobblestone);
            w.setBlock(x,y+0,z, Blocks.air);
            w.setBlock(x,y+1,z, Blocks.air);
            w.setBlock(x,y+2,z, Blocks.air);
            w.setBlock(x +1, y+0, z+1, Blocks.glass);
            w.setBlock(x +1, y+1, z+1, Blocks.glass);
            w.setBlock(x +1, y+2, z+1, Blocks.glass);
            w.setBlock(x +1, y+0, z-1, Blocks.glass);
            w.setBlock(x +1, y+1, z-1, Blocks.glass);
            w.setBlock(x +1, y+2, z-1, Blocks.glass);
            w.setBlock(x -1, y+0, z-1, Blocks.glass);
            w.setBlock(x -1, y+1, z-1, Blocks.glass);
            w.setBlock(x -1, y+2, z-1, Blocks.glass);
            w.setBlock(x -1, y+0, z+1, Blocks.glass);
            w.setBlock(x -1, y+1, z+1, Blocks.glass);
            w.setBlock(x -1, y+2, z+1, Blocks.glass);
            w.setBlock(x +2, y+0, z+2, Blocks.glass);
            w.setBlock(x +2, y+1, z+2, Blocks.glass);
            w.setBlock(x +2, y+2, z+2, Blocks.glass);
            w.setBlock(x +2, y+0, z-2, Blocks.glass);
            w.setBlock(x +2, y+1, z-2, Blocks.glass);
            w.setBlock(x +2, y+2, z-2, Blocks.glass);
            w.setBlock(x -2, y+0, z-2, Blocks.glass);
            w.setBlock(x -2, y+1, z-2, Blocks.glass);
            w.setBlock(x -2, y+2, z-2, Blocks.glass);
            w.setBlock(x -2, y+0, z+2, Blocks.glass);
            w.setBlock(x -2, y+1, z+2, Blocks.glass);
            w.setBlock(x -2, y+2, z+2, Blocks.glass);

        }
    }

    private void generateTracks(World w, int x, int y, int z, boolean generate_pumpkin, boolean generate_booster, String axis){
        if (!noWater(w, x, y, z)) {
            handleWater(w, x,y,z, axis);
        }
        if (generate_pumpkin) {
            w.setBlock(x, y - 1, z, Blocks.lit_pumpkin);
        }
        if (!(World.doesBlockHaveSolidTopSurface(w, x, y - 1, z))) {
            w.setBlock(x, y - 1, z, Blocks.cobblestone);
        }
        buildCeiling(w, x, y + 3, z);
        w.setBlock(x, y + 2, z, Blocks.air);
        w.setBlock(x, y + 1, z, Blocks.air);

        if (Config.booster_frequency > 0 && generate_booster) {
            w.setBlock(x, y - 1, z, Blocks.redstone_block);
            w.setBlock(x, y, z, Blocks.golden_rail);
        } else {
            w.setBlock(x, y, z, Blocks.rail);
        }
    }

    private boolean noWater(World w, int x, int y, int z){
        for (int l = y - 1; l <= y + 2; l++) {
            // don't generate in the water or lava
            if (w.getBlock(x, l, z) instanceof BlockLiquid) {
                return false;
            }
        }
        return true;
    }


    private boolean isFallingBlock(World w, int x, int y, int z){
        Block b = w.getBlock(x,y,z);
        return (b instanceof BlockFalling || b instanceof BlockLiquid);
    }
}
