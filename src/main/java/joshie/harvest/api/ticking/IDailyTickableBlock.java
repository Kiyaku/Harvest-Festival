package joshie.harvest.api.ticking;


import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** This is a capability for ticking tile entities, and entities once daily **/
public interface IDailyTickableBlock {
    /** Called when the day ticks over
     *  @param world, always use this, rather than other types
     *  @param pos the position of the block
     *  @param state the block state
     *  @return return true if the tile block still exists, it will get automatically removed if it's false**/
    boolean newDay(World world, BlockPos pos, IBlockState state);
}
