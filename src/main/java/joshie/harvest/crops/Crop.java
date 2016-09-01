package joshie.harvest.crops;

import joshie.harvest.api.animals.AnimalFoodType;
import joshie.harvest.api.calendar.Season;
import joshie.harvest.api.cooking.Ingredient;
import joshie.harvest.api.crops.ICrop;
import joshie.harvest.api.crops.IDropHandler;
import joshie.harvest.api.crops.IGrowthHandler;
import joshie.harvest.api.crops.IStateHandler;
import joshie.harvest.core.util.Text;
import joshie.harvest.crops.handlers.state.StateHandlerDefault;
import net.minecraft.block.Block;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

import static joshie.harvest.core.lib.HFModInfo.MODID;

public class Crop extends IForgeRegistryEntry.Impl<Crop> implements ICrop {
    private static final Random rand = new Random();

    //CropData
    private IStateHandler stateHandler;
    private IGrowthHandler growthHandler;
    private IDropHandler dropHandler;
    private Block growsToSide;
    private boolean needsWatering;
    private boolean alternativeName;
    private boolean requiresSickle;
    private ItemStack item;
    private Season[] seasons;
    private int cost;
    private int sell;
    private int stages;
    private int regrow;
    protected int year;
    private int bag_color;
    private int doubleStage;
    private AnimalFoodType foodType;
    private EnumPlantType type;
    private Ingredient ingredient;
    private int hunger;
    private float saturation;

    public Crop() {
        this(new ResourceLocation(MODID, "null_crop"), Season.SPRING, 0, 0, 3, 0, 0, 0);
    }

    /**
     * Constructor for crop
     *
     * @param season      the season this crop grows in
     * @param cost        how much the seed costs
     * @param sell        how much this sells for
     * @param stages      how many stages this crop has
     * @param regrow      the stage this returns to once harvested
     * @param year        the year in which this crop can be purchased
     */
    public Crop(ResourceLocation key, Season season, int cost, int sell, int stages, int regrow, int year, int color) {
        this(key, new Season[]{ season }, cost, sell, stages, regrow, year, color);
    }

    public Crop(ResourceLocation key, Season[] seasons, int cost, int sell, int stages, int regrow, int year, int color) {
        this.seasons = seasons;
        this.cost = cost;
        this.sell = sell;
        this.stages = stages;
        this.regrow = regrow;
        this.year = year;
        this.alternativeName = false;
        this.foodType = AnimalFoodType.VEGETABLE;
        this.bag_color = color;
        this.stateHandler = new StateHandlerDefault(this);
        this.growthHandler = HFCrops.FARMLAND;
        this.needsWatering = true;
        this.doubleStage = Integer.MAX_VALUE;
        this.dropHandler = null;
        this.growsToSide = null;
        this.type = EnumPlantType.Crop;
        this.setRegistryName(key);
        CropRegistry.REGISTRY.register(this);
    }

    @Override
    public Crop setHasAlternativeName() {
        this.alternativeName = true;
        return this;
    }

    @Override
    public Crop setItem(ItemStack item) {
        this.item = item;
        if (this.item.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood)this.item.getItem();
            setFoodStats(food.getHealAmount(item), food.getSaturationModifier(item));
        }

        return this;
    }

    @Override
    public Crop setStateHandler(IStateHandler handler) {
        this.stateHandler = handler;
        return this;
    }

    @Override
    public Crop setRequiresSickle() {
        this.requiresSickle = true;
        return this;
    }

    @Override
    public Crop setAnimalFoodType(AnimalFoodType foodType) {
        this.foodType = foodType;
        return this;
    }

    @Override
    public Crop setPlantType(EnumPlantType plantType) {
        this.type = plantType;
        return this;
    }

    @Override
    public Crop setFoodStats(int hunger, float saturation) {
        this.hunger = hunger;
        this.saturation = saturation;
        String name = getRegistryName().getResourcePath();
        if (Ingredient.INGREDIENTS.containsKey(name)) {
            this.ingredient = Ingredient.INGREDIENTS.get(name);
        } else this.ingredient = new Ingredient(name, hunger, saturation);
        return this;
    }

    @Override
    public Crop setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    @Override
    public Crop setNoWaterRequirements() {
        this.needsWatering = false;
        return this;
    }

    @Override
    public Crop setSoilRequirements(IGrowthHandler handler) {
        this.growthHandler = handler;
        return this;
    }

    @Override
    public Crop setBecomesDouble(int doubleStage) {
        this.doubleStage = doubleStage;
        return this;
    }

    @Override
    public Crop setDropHandler(IDropHandler handler) {
        this.dropHandler = handler;
        return this;
    }

    @Override
    public Crop setGrowsToSide(Block block) {
        this.growsToSide = block;
        return this;
    }


    /**
     * Return true if this crop uses an item other than HFCrop item
     **/
    public boolean hasItemAssigned() {
        return item != null;
    }

    /**
     * This is the season this crop survives in
     *
     * @return the season that is valid for this crop
     */
    public Season[] getSeasons() {
        return seasons;
    }

    /**
     * This is how much the seed costs in the store.
     * If the seed isn't purchasable return 0
     *
     * @return the cost in gold
     */
    public long getSeedCost() {
        return cost;
    }

    /**
     * This is how much this crop well sell for at level 1.
     * If this crop cannot be sold return 0
     *
     * @return the sell value in gold
     */
    public long getSellValue(ItemStack stack) {
        return sell;
    }

    /**
     * Return how many stages this crop has.
     * A return value of 0, means the crop is instantly grown.
     *
     * @return the stage
     */
    public int getStages() {
        return stages;
    }

    /**
     * The year in which this crop can be purchased
     **/
    public int getPurchaseYear() {
        return year;
    }

    /**
     * Return the stage that the plant returns to when it's harvested.
     * A return value of 0, means the crop is destroyed.
     *
     * @return the stage
     */
    @Override
    public int getRegrowStage() {
        return regrow;
    }

    @Override
    public boolean requiresSickle() {
        return requiresSickle;
    }

    @Override
    public boolean requiresWater() {
        return needsWatering;
    }

    @Override
    public boolean isDouble(int stage) {
        return stage == doubleStage;
    }

    @Override
    public Block growsToSide() {
        return growsToSide;
    }

    @Override
    public IGrowthHandler getGrowthHandler() {
        return growthHandler;
    }

    /**
     * This is called when bringing up the list of crops for sale
     *
     * @return whether this item can be purchased in this shop or not
     */
    public boolean canPurchase() {
        return getSeedCost() > 0;
    }

    /**
     * Return the colour of the seed bag
     **/
    public int getColor() {
        return bag_color;
    }

    /**
     * The type of animal food this is
     **/
    public AnimalFoodType getFoodType() {
        return foodType;
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getHunger() {
        return hunger;
    }

    public float getSaturation() {
        return saturation;
    }

    @Override
    public ItemStack getSeedStack() {
        return HFCrops.SEEDS.getStackFromCrop(this);
    }

    @Override
    public ItemStack getCropStack() {
        return item == null ? null : item.copy();
    }

    @Override
    public ItemStack getHarvested() {
        return dropHandler == null ? getCropStack() : dropHandler.getDrop(rand, item.getItem());
    }

    @Override
    public IStateHandler getStateHandler() {
        return stateHandler;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return (stack.getItem() == getCropStack().getItem());
    }

    @Override
    public EnumPlantType getPlantType() {
        return type;
    }

    /**
     * Gets the localized crop name for this crop
     *
     * @param isItem the item
     * @return crop name
     */
    public String getLocalizedName(boolean isItem) {
        String suffix = alternativeName ? ((isItem) ? ".item" : ".block") : "";
        return Text.translate(getRegistryName().getResourceDomain(), "crop." + StringUtils.replace(getRegistryName().getResourcePath(), "_", ".") + suffix);
    }

    /**
     * Gets the localized seed name for this crop
     *
     * @return seed name
     */
    public String getSeedsName() {
        String suffix = alternativeName ? ".block" : "";
        String name = Text.translate(getRegistryName().getResourceDomain(), "crop." + StringUtils.replace(getRegistryName().getResourcePath(), "_", ".") + suffix);
        String seeds = Text.translate("crop.seeds");
        String text = Text.translate("crop.seeds.format.standard");
        text = StringUtils.replace(text, "%C", name);
        text = StringUtils.replace(text, "%S", seeds);
        return text;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Crop && getRegistryName().equals(((Crop) o).getRegistryName());
    }

    @Override
    public int hashCode() {
        return getRegistryName().hashCode();
    }
}