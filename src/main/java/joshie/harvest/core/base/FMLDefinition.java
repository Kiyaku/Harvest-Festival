package joshie.harvest.core.base;

import joshie.harvest.core.base.item.ItemHFFML;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class FMLDefinition<E extends Impl<E>> implements ItemMeshDefinition {
    private static final HashMap<String, FMLDefinition> DEFINITIONS = new HashMap<>();
    protected IForgeRegistry<E> registry;
    protected HashMap<E, ModelResourceLocation> models = new HashMap<>();
    protected String name;
    protected ItemHFFML item;

    public FMLDefinition(ItemHFFML item, String name, IForgeRegistry<E> registry) {
        this.registry = registry;
        this.name = name;
        this.item = item;
        ModelBakery.registerItemVariants(item);
        DEFINITIONS.put(name, this);
    }

    @SuppressWarnings("unchecked")
    public static <B extends Impl<B>> FMLDefinition<B> getDefinition(String name) {
        return DEFINITIONS.get(name);
    }

    public boolean shouldSkip(E e) {
        return e == item.getNullValue();
    }

    public void registerEverything() {
        for (E e : registry.getValues()) {
            if (shouldSkip(e)) continue; //Don't register the null
            ModelResourceLocation model = new ModelResourceLocation(new ResourceLocation(e.getRegistryName().getResourceDomain(), name + "/" + e.getRegistryName().getResourcePath()), "inventory");
            ModelBakery.registerItemVariants(item, model);
            models.put(e, model);
        }
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        return models.get(registry.getValues().get(stack.getItemDamage()));
    }
}
