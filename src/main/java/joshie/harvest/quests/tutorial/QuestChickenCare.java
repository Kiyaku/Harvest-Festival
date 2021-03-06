package joshie.harvest.quests.tutorial;

import joshie.harvest.animals.HFAnimals;
import joshie.harvest.animals.entity.EntityHarvestChicken;
import joshie.harvest.animals.item.ItemAnimalTool.Tool;
import joshie.harvest.api.quests.HFQuest;
import joshie.harvest.api.npc.INPC;
import joshie.harvest.api.quests.Quest;
import joshie.harvest.core.helpers.InventoryHelper;
import joshie.harvest.core.lib.HFQuests;
import joshie.harvest.crops.HFCrops;
import joshie.harvest.quests.QuestQuestion;
import joshie.harvest.quests.TutorialSelection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

import static joshie.harvest.animals.block.BlockTray.Tray.NEST_EMPTY;
import static joshie.harvest.animals.item.ItemAnimalTool.Tool.CHICKEN_FEED;
import static joshie.harvest.npc.HFNPCs.GODDESS;

@HFQuest("tutorial.chicken")
public class QuestChickenCare extends QuestQuestion {
    private boolean attempted;
    private boolean hasThrown;
    private boolean hasFed;

    public QuestChickenCare() {
        super(new TutorialSelection("chicken"));
        setNPCs(GODDESS);
    }

    @Override
    public boolean canStartQuest(EntityPlayer player, Set<Quest> active, Set<Quest> finished) {
        return player.getHeldItemMainhand() != null && InventoryHelper.ITEM_STACK.matches(player.getHeldItemMainhand(), HFCrops.TURNIP.getCropStack()) && finished.contains(HFQuests.TUTORIAL_CROPS);
    }

    @Override
    public void onEntityInteract(EntityPlayer player, Entity target) {
        if (!hasFed && (quest_stage == 1 || quest_stage == 2)) {
            if (target instanceof EntityChicken) {
                ItemStack held = player.getActiveItemStack();
                if (held != null && !hasFed) {
                    if (!hasFed && InventoryHelper.ITEM_STACK.matches(held, HFAnimals.TOOLS.getStackFromEnum(Tool.CHICKEN_FEED))) {
                        hasFed = true;
                    }

                    if (!player.worldObj.isRemote) increaseStage(player);
                }
            }
        }
    }

    @Override
    public void onRightClickBlock(EntityPlayer player, BlockPos pos, EnumFacing face) {
        if (!hasThrown && (quest_stage == 1 || quest_stage == 2)) {
            for (Entity entity: player.getPassengers()) {
                if (entity instanceof EntityHarvestChicken) {
                    hasThrown = true; //You have now thrown!
                    if (!player.worldObj.isRemote) increaseStage(player);
                }
            }
        }
    }

    @Override
    public String getScript(EntityPlayer player, EntityLiving entity, INPC npc) {
        if (quest_stage == 0) {
            increaseStage(player);
            //The goddess welcomes you and sees that you have a turnip
            //She thanks you for growing them for her, she explains that has a wonderful gift
            //One you have never seen before, She explains she has a chicken she would like to give you
            //She then proceeds to ask if you know how to care for chickens
            return "start";
        } else if (quest_stage == 1) {
            //Now that the goddess knows that you do not how to take care of chicken she starts off on a rant
            //She explains that in order to care four chickens, you must feed them
            //She tells you that you can feed them by hand, or place chicken feed in a feeding tray
            //And they will feed themselves, She also tells you that they need to be loved,
            //She explains the best way for a chicken to feel loved is when you pick it up
            //You can do this by right clicking it, and to put it down, right click the ground
            //She explains you can also make it love you when feed it by hand
            //She explains that doing this will make the chicken like you more, and in doing so
            //She asks the player to go feed by hand, and throw the chicken (giving the player feed)
            increaseStage(player);
            return "throw";
        } else if (quest_stage == 2 || quest_stage == 3) {
            ItemStack held = player.getHeldItemMainhand();
            if (!attempted && held != null) {
                if (held.getItem() instanceof ItemSeeds) {
                    takeHeldStack(player, 1);
                    rewardItem(player, HFAnimals.TOOLS.getStackFromEnum(CHICKEN_FEED));
                    //Goddess thanks the player for the seeds and then gives them 1 chicken feed
                    return "reminder.seeds";
                } else if (held.getItem() instanceof ItemEgg) {
                    takeHeldStack(player, 1);
                    rewardEntity(player, "harvestfestival.chicken");
                    //Goddess thanks the player for the egg, she then informs the player
                    //That she will give them another chicken
                    return "reminder.chicken";
                }
            }

            //The goddess Reminds you to go pick up and throw a chicken, as well as feed one chicken feed
            //She allow informs the player that if they ran out of feed, she will happily trade for more
            //She also explains that she will trade a vanilla egg for a chicken if yours happens to die
            //If the player gives them seeds
            attempted = true;
            return "reminder.throw";
        } else if (quest_stage == 4) {
            //The goddess congratulates you on performing the task, she then goes on to say that
            //Over time the chicken will eventually produce bigger and better eggs that you can sell for more money
            //She also explains that for chickens to lay eggs they need a nesting box
            //Chickens will lay their eggs in here and you can then collect them and ship them off
            //The goddess now asks the player to return when they have one egg from the special chickens
            attempted = false;
            increaseStage(player);
            return "egg";
        } else if (quest_stage == 5) {
            ItemStack held = player.getHeldItemMainhand();
            if (held != null) {
                if (HFAnimals.EGG.matches(held)) {
                    complete(player);
                    //The goddess thanks the player for their time and gives them a reward of a large egg
                    //She explains this is a valuable egg from the best of chickens, you'll have to take care
                    //Of yours properly if you wish to look after it. She also heard that yulif had a spare cow
                    //And that you should go talk to him if you want it
                    return "complete";
                } else if (attempted && held.getItem() == Item.getItemFromBlock(Blocks.HAY_BLOCK)) {
                    rewardItem(player, HFAnimals.TRAY.getStackFromEnum(NEST_EMPTY));
                    takeHeldStack(player, 1);
                    //Thanks the player for the hay, and reminds them to get her an egg
                    return "reminder.nest";
                }
            }

            //The goddess reminds you that she wants an egg from one of the special chickens
            //She also tells that if you lost the nest, bring her a hay bale
            attempted = true;
            return "reminder.egg";
        }

        return null;
    }

    @Override
    public void onStageChanged(EntityPlayer player, int previous, int stage) {
        if (previous == 1) { //Gives the player the basic tools to help them
            rewardEntity(player, "harvestfestival.chicken");
            rewardItem(player, new ItemStack(HFAnimals.TOOLS, 16, CHICKEN_FEED.ordinal()));
        } else if (previous == 2) {
            rewardItem(player, HFAnimals.TRAY.getStackFromEnum(NEST_EMPTY));
        }
    }

    @Override
    public boolean canReward(ItemStack stack) {
        return stack.isItemEqual(HFAnimals.TOOLS.getStackFromEnum(CHICKEN_FEED)) || stack.isItemEqual(HFAnimals.TRAY.getStackFromEnum(NEST_EMPTY));
    }

    @Override
    public boolean canSpawnEntity(String entity) {
        return entity.equals("harvestfestival.chicken");
    }

    @Override
    public void claim(EntityPlayer player) {
        if (quest_stage == 0) {
            rewardItem(player, new ItemStack(HFAnimals.TOOLS, 16, CHICKEN_FEED.ordinal()));
            rewardItem(player, HFAnimals.TRAY.getStackFromEnum(NEST_EMPTY));

            //Spawn the chicken on the players head
            Entity theEntity = new EntityHarvestChicken(player.worldObj);
            theEntity.setPosition(player.posX, player.posY, player.posZ);
            theEntity.startRiding(player, true);
            player.worldObj.spawnEntityInWorld(theEntity);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        hasThrown = nbt.getBoolean("HasThrown");
        hasFed = nbt.getBoolean("HasFed");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("HasThrown", hasThrown);
        nbt.setBoolean("HasFed", hasFed);
        return nbt;
    }
}