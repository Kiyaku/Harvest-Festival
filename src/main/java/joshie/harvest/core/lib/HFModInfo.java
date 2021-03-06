package joshie.harvest.core.lib;

import net.minecraft.util.ResourceLocation;

public class HFModInfo {
    public static final String MODID = "harvestfestival";
    public static final String MODNAME = "Harvest Festival";
    public static final String JAVAPATH = "joshie.harvest.";
    public static final String CAPNAME = "HF";
    public static final String COMMANDNAME = "hf";
    public static final String FAKENPC = "joshie.harvest.npc.render.FakeNPCRenderer.FakeNPCTile";
    public static final String FAKEANIMAL = "joshie.harvest.core.render.FakeEntityRenderer.FakeEntityTile";
    public static final String GIFTPATH = "joshie.harvest.npc.gift.Gifts";
    public static final String SCHEDULEPATH = "joshie.harvest.npc.schedule.Schedule";
    public static final String VERSION = "@VERSION@";

    
    public static final ResourceLocation elements = new ResourceLocation(MODID, "textures/gui/gui_elements.png");
    public static ResourceLocation stars = new ResourceLocation(MODID, "textures/gui/gui_stars.png");
}