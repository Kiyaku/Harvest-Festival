package joshie.harvest.api.cooking;

import net.minecraft.item.ItemStack;

import java.util.List;

/** Register your recipe handler, to handle special recipe types **/
public interface RecipeHandler {
    /** Returns the result of this recipe for this item
     *  @param utensil  the utensil being used
     *  @param stacks   the items input to the recipe
     *  @param ingredients the ingredient value of the stacks**/
    ItemStack getResult(Utensil utensil, List<ItemStack> stacks, List<Ingredient> ingredients);
}