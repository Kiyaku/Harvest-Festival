package joshie.harvest.core.base.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Set;

public class ItemToolChargeable extends ItemTool<ItemToolChargeable> {
    public ItemToolChargeable(String toolClass, Set<Block> effective) {
        super(toolClass, effective);
    }

    protected int getMaxCharge(ItemStack stack) {
        return getTier(stack).ordinal();
    }

    protected boolean canCharge(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound("Data", true);
        int amount = tag.getInteger("Charge");
        return amount < getMaxCharge(stack);
    }

    protected int getCharge(ItemStack stack) {
        return stack.getSubCompound("Data", true).getInteger("Charge");
    }

    protected void setCharge(ItemStack stack, int amount) {
        stack.getSubCompound("Data", true).setInteger("Charge", amount);
    }

    protected void increaseCharge(ItemStack stack, int amount) {
        setCharge(stack, getCharge(stack) + amount);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer playerIn, EnumHand hand) {
        if (canUseAndDamage(stack)) {
            playerIn.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, stack);
        } else return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (count <= 31995 && count % 32 == 0) {
            if (canCharge(stack)) {
                increaseCharge(stack, 1);
            }
        }
    }

    protected ToolTier getChargeTier(ItemStack stack, int charge) {
        return ToolTier.values()[charge];
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (timeLeft <= 31973) {
            int charge = (Math.min(7, Math.max(0, getCharge(stack))));
            setCharge(stack, 0); //Reset the charge
            onFinishedCharging(world, entity, getMovingObjectPositionFromPlayer(world, entity), stack, getChargeTier(stack, charge));
        }
    }

    protected void onFinishedCharging(World world, EntityLivingBase entity, @Nullable RayTraceResult result, ItemStack stack, ToolTier toolTier) {}
}
