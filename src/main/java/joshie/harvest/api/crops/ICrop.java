package joshie.harvest.api.crops;

import joshie.harvest.api.animals.AnimalFoodType;
import joshie.harvest.api.calendar.Season;
import joshie.harvest.api.cooking.Ingredient;
import joshie.harvest.api.core.IShippable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nullable;

/** This is returned when you create a new crop **/
public interface ICrop extends IShippable {
    /** Returns this crop in seed form, with default stats
     *  @return     the itemstack for this crop as seeds **/
    ItemStack getSeedStack();

    /** Return this crop in item form, with default stats
     *  @return     the itemstack for this crop as an item **/
    ItemStack getCropStack();

    /** Harvested stack **/
    ItemStack getHarvested();

    /** Returns the localized name for this crop
     * @param       isItem true if we are asking for the crops item name,
     *              false if we are asking for it's block name
     *
     * @return      the localized name*/
    String getLocalizedName(boolean isItem);

    /** Returns the localized name for this seed
     * @return      the localized name for this crop as seeds*/
    String getSeedsName();

    /** Returns how many stages this crop has
     * @return      the total number of stages*/
    int getStages();

    /** The stage this crop regrows **/
    int getRegrowStage();

    /** The colour of the seed bag */
    int getColor();

    /** The year this seed becomes available for purchase **/
    int getPurchaseYear();

    /** How much this seed costs **/
    long getSeedCost();

    /** Whether this seed is for sale at all **/
    boolean canPurchase();

    /** Returns the type of animal food this crop is **/
    AnimalFoodType getFoodType();

    /** The seasons this crop can grow in **/
    Season[] getSeasons();

    /** Returns the render handler this crop uses **/
    IStateHandler getStateHandler();

    /** Return the soil handler for this crop **/
    IGrowthHandler getGrowthHandler();

    /** Whether this crop requires a sickle to be harvested **/
    boolean requiresSickle();

    /** Whether this crop requires water to grow **/
    boolean requiresWater();

    /** Whether or not an item was assigned to this crop yet **/
    boolean hasItemAssigned();

    /** Whether the crop is double tall at this stage **/
    boolean isDouble(int stage);

    /** Whether this crop grows to the side when it is fully grown
     *  Pumpkins and Melons. Returns the block that grows, otherwise returns null */
    Block growsToSide();

    /** Returns true when the itemstack matches this crop
     *
     * @param       stack the itemstack
     * @return      whether the passed in stack is this crop */
    boolean matches(ItemStack stack);

    /** Returns the plant type of this crop, by default returns Crop **/
    EnumPlantType getPlantType();

    /** Associates this crop with the item
     * @param       item of this crop
     * @return      the instance*/
    ICrop setItem(ItemStack item);

    /** Associates this crop with this VisualHandler
     * @param       handler item of this crop
     * @return      the instance*/
    ICrop setStateHandler(IStateHandler handler);

    /** If if you call this when creating a crop,
     *  it will use a different name for it's block and item form.
     * @return      the ICrop */
    ICrop setHasAlternativeName();

    /** If you call this when creating a crop
     *  It will require a sickle to be harvested  */
    ICrop setRequiresSickle();

    /** If you call this when creating a crop,
     *  It will not need to be watered **/
    ICrop setNoWaterRequirements();

    /** If you call this when creating a crop,
     *  The handler will called when trying to plant the crop,
     *  So you can specify whether this crop is allowed to be placed
     *  on this type of soil or whatever. */
    ICrop setSoilRequirements(IGrowthHandler handler);

    /** Sets the stage at which this crop becomes double tall **/
    ICrop setBecomesDouble(int doubleStage);

    /** Associates this crop with this drop handler **/
    ICrop setDropHandler(IDropHandler handler);

    /** Sets that this crop grows to the side (pumpkins and melons) **/
    ICrop setGrowsToSide(Block block);

    /** Set the animal food type of this crop, Crops default to vegetable **/
    ICrop setAnimalFoodType(AnimalFoodType type);

    /** Set the plant type of this crop **/
    ICrop setPlantType(EnumPlantType plantType);

    /** Set the eat values
     *  You don't need to worry about this if you are using your own custom item
     *  As these values are only called for crops that are created by harvest festival **/
    ICrop setFoodStats(int hunger, float saturation);

    /** Returns the ingredient this crop is represented by
     * Crops will only have an ingredient if setFoodStats was called
     * or setItem was called with an ItemFood**/
    @Nullable
    Ingredient getIngredient();

    /** Set an ingredient **/
    ICrop setIngredient(Ingredient ingredient);
}