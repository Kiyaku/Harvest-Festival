package joshie.harvest.tools.item;

import joshie.harvest.api.crops.IBreakCrops;
import joshie.harvest.tools.ToolHelper;
import joshie.harvest.crops.block.BlockHFCrops;
import joshie.harvest.core.helpers.generic.DirectionHelper;
import joshie.harvest.core.base.item.ItemTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;

public class ItemSickle extends ItemTool<ItemHoe> implements IBreakCrops {
    public ItemSickle() {
        super("sickle", new HashSet<>());
    }

    @Override
    public int getFront(ToolTier tier) {
        switch (tier) {
            case BASIC:
            case COPPER:
                return 0;
            case SILVER:
                return 1;
            case GOLD:
                return 2;
            case MYSTRIL:
                return 4;
            case CURSED:
            case BLESSED:
                return 8;
            case MYTHIC:
                return 14;
            default:
                return 0;
        }
    }

    @Override
    public int getSides(ToolTier tier) {
        switch (tier) {
            case BASIC:
                return 0;
            case COPPER:
            case SILVER:
            case GOLD:
                return 1;
            case MYSTRIL:
                return 2;
            case CURSED:
            case BLESSED:
                return 4;
            case MYTHIC:
                return 7;
            default:
                return 0;
        }
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        return (state.getBlock() != Blocks.GRASS && material == Material.GRASS) || material == Material.LEAVES || material == Material.VINE ? 10F : super.getStrVsBlock(stack, state);
    }

    @Override
    public float getStrengthVSCrops(EntityPlayer player, World world, BlockPos pos, IBlockState state, ItemStack stack) {
        if (!player.canPlayerEdit(pos, EnumFacing.DOWN, stack)) return 0F;
        else {
            EnumFacing front = DirectionHelper.getFacingFromEntity(player);
            Block initial = world.getBlockState(pos).getBlock();
            if (!(initial instanceof BlockHFCrops)) {
                return 0F;
            }

            ToolTier tier = getTier(stack);
            //Facing North, We Want East and West to be 1, left * this.left
            for (int x2 = getXMinus(tier, front, pos.getX()); x2 <= getXPlus(tier, front, pos.getX()); x2++) {
                for (int z2 = getZMinus(tier, front, pos.getZ()); z2 <= getZPlus(tier, front, pos.getZ()); z2++) {
                    BlockPos newPos = new BlockPos(x2, pos.getY(), z2);
                    Block block = world.getBlockState(newPos).getBlock();
                    if (block instanceof BlockHFCrops) {
                        if (!world.isRemote) {
                            block.removedByPlayer(state, world, newPos, player, true);
                        }

                        boolean isWithered = BlockHFCrops.isWithered(world.getBlockState(newPos));
                        IBlockState particleState = isWithered ? Blocks.TALLGRASS.getDefaultState() : Blocks.CARROTS.getDefaultState();
                        displayParticle(world, newPos, EnumParticleTypes.BLOCK_CRACK, particleState);
                        playSound(world, newPos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS);
                        ToolHelper.performTask(player, stack, getExhaustionRate(stack));
                    }
                }
            }
        }

        return 1F;
    }
}