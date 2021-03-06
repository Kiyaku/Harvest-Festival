package joshie.harvest.core.handlers;

import joshie.harvest.animals.AnimalTrackerServer;
import joshie.harvest.calendar.CalendarServer;
import joshie.harvest.core.helpers.generic.MCServerHelper;
import joshie.harvest.core.util.HFEvents;
import joshie.harvest.player.PlayerTrackerServer;
import joshie.harvest.town.TownTrackerServer;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static joshie.harvest.calendar.HFCalendar.TICKS_PER_DAY;

@HFEvents
public class EventsHandler {
    @HFEvents
    public static class ClientReset {
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void onOpenGui(GuiOpenEvent event) {
            if (event.getGui() instanceof GuiWorldSelection || event.getGui() instanceof GuiMultiplayer) {
                HFTrackers.resetClient();
            }
        }
    }

    //Server tick for new day
    @SubscribeEvent
    public void onTick(ServerTickEvent event) {
        if (event.phase != Phase.END) return;
        for (World world: FMLCommonHandler.instance().getMinecraftServerInstance().worldServers) {
            if (world != null) {
                if (world.getWorldTime() % TICKS_PER_DAY == 1) {
                    newDay(world); //Perform everything
                    if (world.provider.getDimension() == 0) { //If it's the overworld, tick the player trackers and the calendar
                        HFTrackers.<CalendarServer>getCalendar(world).newDay(world);
                        for (PlayerTrackerServer player : HFTrackers.getPlayerTrackers()) {
                            player.newDay();
                        }
                    }
                }
            }
        }
    }

    //New day
    public static void newDay(final World world) {
        HFTrackers.getTickables(world).newDay();
        HFTrackers.<AnimalTrackerServer>getAnimalTracker(world).newDay();
        HFTrackers.<TownTrackerServer>getTownTracker(world).newDay();
        HFTrackers.markDirty(world);
    }

    //Sync data on login
    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            HFTrackers.<PlayerTrackerServer>getPlayerTracker(player).getStats().setBirthday(FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0]); //Set birthday to overworld date
            HFTrackers.<CalendarServer>getCalendar(player.worldObj).recalculateAndUpdate(player.worldObj);
            HFTrackers.<TownTrackerServer>getTownTracker(event.player.worldObj).syncToPlayer(player);
            PlayerTrackerServer data = HFTrackers.getPlayerTracker(player);
            data.syncPlayerStats(player);
        }
    }

    @SubscribeEvent
    public void onChangeDimension(PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            World world = MCServerHelper.getWorld(event.toDim);
            HFTrackers.<CalendarServer>getCalendar(world).recalculateAndUpdate(world);
            HFTrackers.<TownTrackerServer>getTownTracker(world).syncToPlayer(event.player); //Resync the town data
        }
    }
}