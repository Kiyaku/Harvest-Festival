package joshie.harvest.shops.gui;

import joshie.harvest.api.shops.IShopGuiOverlay;
import joshie.harvest.core.lib.HFModInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShopOverlay implements IShopGuiOverlay {
    private ResourceLocation shop_overlay;

    public ShopOverlay(String name) {
        this.shop_overlay = new ResourceLocation(HFModInfo.MODID + ":textures/gui/shops/" + name + ".png");
    }

    public ShopOverlay() {}

    @SideOnly(Side.CLIENT)
    @Override
    public void renderOverlay(GuiScreen gui, int x, int y, int xSize, int ySize) {
        if (shop_overlay != null) {
            gui.mc.renderEngine.bindTexture(shop_overlay);
            gui.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        }
    }
}