package joshie.harvest.core.handlers;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.core.IShippable;
import joshie.harvest.api.core.IShippingRegistry;
import joshie.harvest.api.crops.ICrop;
import joshie.harvest.core.util.HFApiImplementation;
import joshie.harvest.core.util.holder.HolderRegistry;
import net.minecraft.item.ItemStack;

@HFApiImplementation
public class ShippingRegistry implements IShippingRegistry {
    public static final ShippingRegistry INSTANCE = new ShippingRegistry();
    private final HolderRegistry<Long> registry = new HolderRegistry<>();

    private ShippingRegistry() {}

    @Override
    public void registerSellable(ItemStack stack, long value) {
        registry.registerItem(stack, value);
    }

    @Override
    public long getSellValue(ItemStack stack) {
        if (stack.getItem() instanceof IShippable) {
            return ((IShippable)stack.getItem()).getSellValue(stack);
        }

        //Special case Crops
        ICrop crop = HFApi.crops.getCropFromStack(stack);
        if (crop != null) {
            return crop.getSellValue(stack);
        }

        Long value = registry.getValueOf(stack);
        return value == null ? 0 : value;
    }
}
