package joshie.harvest.calendar.render;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.calendar.Season;
import joshie.harvest.api.calendar.Weather;
import joshie.harvest.core.helpers.generic.MCClientHelper;
import joshie.harvest.core.util.HFEvents;
import joshie.harvest.npc.gui.ContainerNPCBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.terraingen.BiomeEvent.GetFoliageColor;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@HFEvents(Side.CLIENT)
public class CalendarRender {
    private static final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
    private static double fogStart = 1D;
    private static double fogTarget = 1D;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == ElementType.CROSSHAIRS) {
            if (MCClientHelper.getPlayer().openContainer instanceof ContainerNPCBase) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onFogRender(RenderFogEvent event) {
        if (!event.getState().getMaterial().isLiquid()) {
            //Update the fog smoothly
            if (fogTarget != fogStart) {
                if (fogTarget > fogStart) {
                    fogStart += 1D;
                } else if (fogTarget < fogStart) {
                    fogStart -= 1D;
                }
            }

            Minecraft mc = MCClientHelper.getMinecraft();
            blockpos$mutableblockpos.setPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            int i1 = mc.gameSettings.fancyGraphics ? 10 : 5;
            int j = MathHelper.floor_double(mc.thePlayer.posY);
            int j2 = mc.theWorld.getPrecipitationHeight(blockpos$mutableblockpos).getY();
            int k2 = j - i1;
            int l2 = j + i1;

            if (k2 < j2) {
                k2 = j2;
            }

            if (l2 < j2) {
                l2 = j2;
            }

            Weather weather = HFApi.calendar.getWeather(mc.theWorld);
            if (k2 != l2) {
                if (weather == Weather.BLIZZARD) {
                    fogTarget = -200D;
                } else if (weather == Weather.SNOW) {
                    fogTarget = 0D;
                } else fogTarget = 1D;
            } else fogTarget = 1D;

            //If we're snow or resetting the target
            if (weather.isSnow() || fogTarget != fogStart) {
                GlStateManager.setFogEnd(Math.min(event.getFarPlaneDistance(), 192.0F) * 0.5F);
                GlStateManager.setFogStart((float) fogStart);
            }
        } else {
            fogStart = 1D;
            fogTarget = 1D;
        }
    }

    @SubscribeEvent
    public void onFogColor(FogColors event) {
        if (!event.getState().getMaterial().isLiquid()) {
            Weather weather = HFApi.calendar.getWeather(MCClientHelper.getWorld());
            if (weather == Weather.SNOW || weather == Weather.BLIZZARD) {
                event.setRed(1F);
                event.setBlue(1F);
                event.setGreen(1F);
            }
        }
    }

    //Orange Leaves in Autumn
    @SubscribeEvent
    public void getFoliageColor(GetFoliageColor event) {
        if (HFApi.calendar.getDate(MCClientHelper.getWorld()).getSeason() == Season.AUTUMN) {
            event.setNewColor(0xFF9900);
        }
    }
}