package joshie.harvest.tools.item;

import com.google.common.collect.Sets;
import joshie.harvest.api.gathering.ISmashable.ToolType;
import joshie.harvest.core.HFTab;
import joshie.harvest.core.base.item.ItemToolSmashing;
import joshie.harvest.core.lib.HFSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class ItemHammer extends ItemToolSmashing {
    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE);

    public ItemHammer() {
        super("pickaxe", EFFECTIVE_ON);
        setCreativeTab(HFTab.MINING);
    }

    @Override
    public ToolType getToolType() {
        return ToolType.HAMMER;
    }

    @Override
    public void playSound(World world, BlockPos pos) {
        world.playSound(null, pos, HFSounds.SMASH_ROCK, SoundCategory.BLOCKS, world.rand.nextFloat() * 0.45F, world.rand.nextFloat() * 1.0F + 0.5F);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        return material != Material.IRON && material != Material.ANVIL && material != Material.ROCK ? super.getStrVsBlock(stack, state) : this.getEffiency(stack);
    }

    @Override
    public boolean onSmashed(EntityPlayer player, ItemStack stack, ToolTier tier, int harvestLevel, World world, BlockPos pos, IBlockState state) {
        if (state.getBlock() == Blocks.FARMLAND) {
            return world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        }

        return super.onSmashed(player, stack, tier, harvestLevel, world, pos, state);
    }

    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[]{ getCreativeTab(), HFTab.GATHERING };
    }
}