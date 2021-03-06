package joshie.harvest.quests.trade;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.quests.HFQuest;
import joshie.harvest.api.npc.INPC;
import joshie.harvest.api.calendar.CalendarDate;
import joshie.harvest.core.handlers.HFTrackers;
import joshie.harvest.calendar.CalendarHelper;
import joshie.harvest.core.helpers.generic.MCClientHelper;
import joshie.harvest.core.lib.HFSounds;
import joshie.harvest.tools.HFTools;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static joshie.harvest.api.core.ITiered.ToolTier.CURSED;
import static joshie.harvest.core.helpers.generic.ItemHelper.spawnXP;
import static joshie.harvest.npc.HFNPCs.PRIEST;


@HFQuest("trade.cursed")
public class QuestBless extends QuestTrade {
    private static final ItemStack hoe = HFTools.HOE.getStack(CURSED);
    private static final ItemStack sickle = HFTools.SICKLE.getStack(CURSED);
    private static final ItemStack watering = HFTools.WATERING_CAN.getStack(CURSED);
    private static final ItemStack axe = HFTools.AXE.getStack(CURSED);
    private static final ItemStack hammer = HFTools.HAMMER.getStack(CURSED);
    private CalendarDate date;
    private ItemStack tool;

    public QuestBless() {
        setNPCs(PRIEST);
    }

    private int getDifference(CalendarDate then, CalendarDate now) {
        int thenDays = CalendarHelper.getTotalDays(then);
        int nowDays = CalendarHelper.getTotalDays(now);
        return (nowDays - thenDays);
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    @Override
    public String getLocalized(String quest) {
        if (quest.equals("wait")) {
            CalendarDate today = HFApi.calendar.getDate(MCClientHelper.getWorld());
            return I18n.translateToLocalFormatted("harvestfestival.quest.trade.cursed.wait", 3 - (getDifference(date, today)));
        } else if (quest.equals("done")) {
            return I18n.translateToLocalFormatted("harvestfestival.quest.trade.cursed.done", tool.getDisplayName());
        } else return super.getLocalized(quest);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getScript(EntityPlayer player, EntityLiving entity, INPC npc) {
        if (quest_stage == 0) {
            boolean hasGold = HFTrackers.getPlayerTracker(player).getStats().getGold() >= 10000L;
            boolean hasTool = isHolding(player, hoe) || isHolding(player, sickle) || isHolding(player, watering) || isHolding(player, axe) || isHolding(player, hammer);
            if (hasGold && hasTool) {
                increaseStage(player);
                return "accept";
            } else if (hasTool) {
                return "gold";
            } else return null;
        } else {
            CalendarDate today = HFApi.calendar.getDate(MCClientHelper.getWorld());
            if (getDifference(date, today) >= 3) {
                complete(player);
                player.worldObj.playSound(player, player.posX, player.posY, player.posZ, HFSounds.BLESS_TOOL, SoundCategory.NEUTRAL, 0.25F, 1F);
                for (int i = 0; i < 32; i++) {
                    player.worldObj.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, entity.posX + player.worldObj.rand.nextFloat() + player.worldObj.rand.nextFloat() - 1F, entity.posY + 0.25D + entity.worldObj.rand.nextFloat() + entity.worldObj.rand.nextFloat(), entity.posZ + player.worldObj.rand.nextFloat() + player.worldObj.rand.nextFloat() - 1F, 0, 0, 0);
                }

                return "done";
            }

            return "wait";
        }
    }

    @Override
    public void onStageChanged(EntityPlayer player, int previous, int stage) {
        if (previous == 0) {
            date = HFApi.calendar.getDate(player.worldObj).copy();
            ItemStack stack = player.getHeldItemMainhand().copy();
            tool = new ItemStack(stack.getItem(), 1, stack.getItemDamage() + 1);
            rewardGold(player, -10000L);
            takeHeldStack(player, 1);
        }
    }

    @Override
    public void claim(EntityPlayer player) {
        rewardItem(player, tool);
        spawnXP(player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ, 5);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("Date")) {
            date = CalendarDate.fromNBT(nbt.getCompoundTag("Date"));
            tool = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Item"));
        }
    }

    /** Called to write data about this quest
     * @param nbt the nbt tag to write to
     * @return the nbt tag**/
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (date != null) {
            nbt.setTag("Date", date.toNBT());
            nbt.setTag("Item", tool.writeToNBT(new NBTTagCompound()));
        }

        return nbt;
    }

    private boolean isHolding(EntityPlayer player, ItemStack stack) {
        return player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == stack.getItem() && player.getHeldItemMainhand().getItemDamage() == stack.getItemDamage();
    }
}
