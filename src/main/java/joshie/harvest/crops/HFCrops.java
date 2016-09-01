package joshie.harvest.crops;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.animals.AnimalFoodType;
import joshie.harvest.api.calendar.Season;
import joshie.harvest.api.cooking.Ingredient;
import joshie.harvest.api.crops.ICrop;
import joshie.harvest.api.crops.IGrowthHandler;
import joshie.harvest.core.base.FMLDefinition;
import joshie.harvest.core.base.MeshIdentical;
import joshie.harvest.core.helpers.generic.RegistryHelper;
import joshie.harvest.core.util.HFLoader;
import joshie.harvest.crops.block.BlockHFCrops;
import joshie.harvest.crops.block.BlockSprinkler;
import joshie.harvest.crops.handlers.drop.DropHandlerMelon;
import joshie.harvest.crops.handlers.drop.DropHandlerNetherWart;
import joshie.harvest.crops.handlers.drop.DropHandlerPotato;
import joshie.harvest.crops.handlers.growth.GrowthHandlerNether;
import joshie.harvest.crops.handlers.growth.GrowthHandlerSeasonal;
import joshie.harvest.crops.handlers.state.*;
import joshie.harvest.crops.item.ItemCrop;
import joshie.harvest.crops.item.ItemHFSeeds;
import joshie.harvest.crops.tile.TileCrop;
import joshie.harvest.crops.tile.TileCrop.TileWithered;
import joshie.harvest.crops.tile.TileSprinkler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;

import static joshie.harvest.api.animals.AnimalFoodType.FRUIT;
import static joshie.harvest.api.calendar.Season.*;
import static joshie.harvest.core.HFTab.FARMING;
import static joshie.harvest.core.helpers.generic.ConfigHelper.getBoolean;
import static joshie.harvest.core.helpers.generic.ConfigHelper.getInteger;
import static joshie.harvest.core.lib.HFModInfo.MODID;
import static joshie.harvest.core.lib.LoadOrder.HFCROPS;

@HFLoader(priority = HFCROPS)
public class HFCrops {
    //Crops and Custom Farmland
    public static final BlockHFCrops CROPS = new BlockHFCrops().register("crops_block");
    public static final BlockSprinkler SPRINKLER = new BlockSprinkler().register("sprinkler");
    public static final IGrowthHandler FARMLAND = new GrowthHandlerSeasonal(EnumPlantType.Crop, Blocks.FARMLAND);
    public static final IGrowthHandler SOUL_SAND = new GrowthHandlerNether(EnumPlantType.Nether, Blocks.SOUL_SAND);

    //Seed Bag Item
    public static final ItemHFSeeds SEEDS = new ItemHFSeeds().register("crops_seeds");
    public static final ItemCrop CROP = new ItemCrop().register("crops");

    //Null Crop
    public static final Crop NULL_CROP = new Crop();

    //Spring Crops
    public static final ICrop TURNIP = registerCrop("turnip", 120, 60, 5, 0, 0, 0xFFFFFF, SPRING).setFoodStats(1, 0.4F).setStateHandler(new StateHandlerTurnip());
    public static final ICrop POTATO = registerCrop("potato", 150, 80, 8, 0, 0, 0xBE8D2B, SPRING).setItem(new ItemStack(Items.POTATO)).setDropHandler(new DropHandlerPotato()).setStateHandler(new StateHandlerSeedFood(Blocks.POTATOES));
    public static final ICrop CUCUMBER = registerCrop("cucumber", 200, 60, 10, 5, 0, 0x36B313, SPRING).setFoodStats(2, 0.25F).setAnimalFoodType(FRUIT).setStateHandler(new StateHandlerCucumber());
    public static final ICrop STRAWBERRY = registerCrop("strawberry", 150, 30, 9, 7, 3, 0xFF7BEA, SPRING).setFoodStats(3, 0.8F).setAnimalFoodType(FRUIT).setStateHandler(new StateHandlerStrawberry());
    public static final ICrop CABBAGE = registerCrop("cabbage", 500, 250, 15, 0, 8, 0x8FFF40, SPRING).setFoodStats(1, 0.5F).setStateHandler(new StateHandlerCabbage());

    //Summer Crops
    public static final ICrop ONION = registerCrop("onion", 150, 80, 8, 0, 0, 0XDCC307, SUMMER).setFoodStats(1, 0.4F).setStateHandler(new StateHandlerOnion());
    public static final ICrop TOMATO = registerCrop("tomato", 200, 60, 10, 7, 0, 0XE60820, SUMMER).setFoodStats(3, 0.5F).setAnimalFoodType(FRUIT).setStateHandler(new StateHandlerTomato());
    public static final ICrop CORN = registerCrop("corn", 300, 100, 15, 12, 0, 0XD4BD45, SUMMER).setFoodStats(2, 0.3F).setStateHandler(new StateHandlerCorn());
    public static final ICrop PUMPKIN = registerCrop("pumpkin", 500, 125, 15, 0, 3, 0XE09A39, SUMMER).setIngredient(new Ingredient("pumpkin", 2, 0.3F)).setItem(new ItemStack(Blocks.PUMPKIN)).setGrowsToSide(Blocks.PUMPKIN).setStateHandler(new StateHandlerStem(Blocks.PUMPKIN));
    public static final ICrop PINEAPPLE = registerCrop("pineapple", 1000, 500, 21, 5, 8, 0XD7CF00, SUMMER).setFoodStats(2, 1.34F).setAnimalFoodType(FRUIT).setStateHandler(new StateHandlerPineapple());
    public static final ICrop WATERMELON = registerCrop("watermelon", 250, 25, 11, 0, 3, 0xc92b3e, SUMMER).setItem(new ItemStack(Items.MELON)).setAnimalFoodType(FRUIT).setDropHandler(new DropHandlerMelon()).setGrowsToSide(Blocks.MELON_BLOCK).setStateHandler(new StateHandlerStem(Blocks.MELON_BLOCK));

    //Autumn Crops
    public static final ICrop EGGPLANT = registerCrop("eggplant", 120, 80, 10, 7, 0, 0XA25CC4, AUTUMN).setFoodStats(3, 1.1F).setStateHandler(new StateHandlerEggplant());
    public static final ICrop SPINACH = registerCrop("spinach", 200, 80, 6, 0, 3, 0X90AE15, AUTUMN).setFoodStats(2, 1.0F).setStateHandler(new StateHandlerSpinach());
    public static final ICrop CARROT = registerCrop("carrot", 300, 120, 8, 0, 0, 0XF8AC33, AUTUMN).setItem(new ItemStack(Items.CARROT)).setStateHandler(new StateHandlerSeedFood(Blocks.CARROTS));
    public static final ICrop SWEET_POTATO = registerCrop("sweet_potato", 300, 120, 6, 4, 0, 0XD82AAC, AUTUMN).setFoodStats(2, 0.35F).setStateHandler(new StateHandlerSweetPotato());
    public static final ICrop GREEN_PEPPER = registerCrop("green_pepper", 150, 40, 8, 2, 8, 0x56D213, AUTUMN).setFoodStats(2, 0.5F).setStateHandler(new StateHandlerGreenPepper());
    public static final ICrop BEETROOT = registerCrop("beetroot", 250, 75, 8, 0, 0, 0x690000, AUTUMN).setItem(new ItemStack(Items.BEETROOT)).setStateHandler(new StateHandlerSeedFood(Blocks.BEETROOTS));

    //Year Long Crops
    public static final ICrop GRASS = registerCrop("grass", 500, 0, 11, 0, 0, 0x7AC958, SPRING, SUMMER, AUTUMN).setAnimalFoodType(AnimalFoodType.GRASS).setBecomesDouble(6).setHasAlternativeName().setRequiresSickle().setNoWaterRequirements().setStateHandler(new StateHandlerGrass());
    public static final ICrop WHEAT = registerCrop("wheat", 150, 100, 28, 0, 0, 0XEAC715, SPRING, SUMMER, AUTUMN).setItem(new ItemStack(Items.WHEAT)).setAnimalFoodType(AnimalFoodType.GRASS).setRequiresSickle().setStateHandler(new StateHandlerWheat());

    //Nether Crop
    public static final ICrop NETHER_WART = registerCrop("nether_wart", 25000, 10, 4, 1, 5, 0x8B0000).setItem(new ItemStack(Items.NETHER_WART)).setStateHandler(new StateHandlerNetherWart()).setPlantType(EnumPlantType.Nether).setNoWaterRequirements().setSoilRequirements(SOUL_SAND).setDropHandler(new DropHandlerNetherWart());


    public static void preInit() {
        //Register the crop serializer
        LootFunctionManager.registerFunction(new SetCropType.Serializer());

        registerVanillaCrop(Items.WHEAT, WHEAT);
        registerVanillaCrop(Items.CARROT, CARROT);
        registerVanillaCrop(Items.POTATO, POTATO);
        registerVanillaCrop(Items.BEETROOT, BEETROOT);
        registerVanillaCrop(Items.MELON, WATERMELON);
        registerVanillaCrop(Blocks.PUMPKIN, PUMPKIN);
        registerVanillaCrop(Items.NETHER_WART, NETHER_WART);

        //Add a new crop item for things that do not have an item yet :D
        for (Crop crop : CropRegistry.REGISTRY.getValues()) {
            if (crop != NULL_CROP) {
                if (!crop.hasItemAssigned()) {
                    crop.setItem(CROP.getStackFromObject(crop));
                }

                //Register always in the ore dictionary
                ItemStack clone = crop.getCropStack().copy();
                clone.setItemDamage(OreDictionary.WILDCARD_VALUE);

                String name = "crop" + WordUtils.capitalizeFully(crop.getRegistryName().getResourcePath(), '_').replace("_", "");
                if (!isInDictionary(name, clone)) {
                    OreDictionary.registerOre(name, clone);
                }
            }
        }

        RegistryHelper.registerTiles(TileCrop.class, TileWithered.class, TileSprinkler.class);
        if (DISABLE_VANILLA_MOISTURE) {
            Blocks.FARMLAND.setTickRandomly(false);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void preInitClient() {
        ModelLoader.setCustomMeshDefinition(SEEDS, new MeshIdentical(SEEDS));
        ModelLoader.setCustomStateMapper(CROPS, new CropStateMapper());
        ModelLoader.setCustomMeshDefinition(CROP, new FMLDefinition<Crop>(CROP, "crops", CropRegistry.REGISTRY) {
            @Override
            public boolean shouldSkip(Crop crop) {
                return super.shouldSkip(crop) || crop.getCropStack().getItem() != CROP;
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                Crop crop = HFCrops.SEEDS.getCropFromStack(stack);
                return crop != null ? crop.getColor() : -1;
            }
        }, SEEDS);

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
                if (world != null && pos != null) {
                    if (BlockHFCrops.isWithered(world.getBlockState(pos))) {
                        return 0xA64DFF;
                    }
                }

                return -1;
            }
        }, CROPS);

        //Register the models
        FMLDefinition.getDefinition("crops").registerEverything();
    }

    private static void registerVanillaCrop(Item item, ICrop crop) {
        HFApi.crops.registerCropProvider(new ItemStack(item), ((Crop)crop).getRegistryName());
        item.setCreativeTab(FARMING);
    }

    private static void registerVanillaCrop(Block block, ICrop crop) {
        HFApi.crops.registerCropProvider(new ItemStack(block), ((Crop)crop).getRegistryName());
        block.setCreativeTab(FARMING);
    }

    private static ICrop registerCrop(String name, int cost, int sell, int stages, int regrow, int year, int color, Season... seasons) {
        return HFApi.crops.registerCrop(new ResourceLocation(MODID, name), cost, sell, stages, regrow, year, color, seasons);
    }

    private static boolean isInDictionary(String name, ItemStack stack) {
        for (ItemStack check: OreDictionary.getOres(name)) {
            if (check.getItem() == stack.getItem() && (check.getItemDamage() == OreDictionary.WILDCARD_VALUE || check.getItemDamage() == stack.getItemDamage())) {
                return true;
            }
        }

        return false;
    }

    //Configure
    public static boolean SEASONAL_BONEMEAL;
    public static boolean ENABLE_BONEMEAL;
    public static boolean ALWAYS_GROW;
    public static boolean DISABLE_VANILLA_HOE;
    public static boolean DISABLE_VANILLA_SEEDS;
    public static boolean DISABLE_VANILLA_MOISTURE;
    public static int SPRINKLER_DRAIN_RATE;

    public static void configure() {
        ALWAYS_GROW = getBoolean("Crops always grow", false, "This setting when set to true, will make crops grow based on random tick instead of day by day, Take note that this also affects the number of seeds a crop bag will plant. It will only plant 3 seeds instead of a 3x3");
        ENABLE_BONEMEAL = getBoolean("Enable bonemeal", false, "Enabling this will allow you to use bonemeal on plants to grow them.");
        SEASONAL_BONEMEAL = getBoolean("Seasonal bonemeal", true, "If you have bonemeal enabled, with this setting active, bonemeal will only work when the crop is in season");
        DISABLE_VANILLA_SEEDS = getBoolean("Disable vanilla seeds", false, "If this is true, vanilla seeds will not plant their crops");
        DISABLE_VANILLA_HOE = getBoolean("Disable vanilla hoes", false, "If this is true, vanilla hoes will not till dirt");
        DISABLE_VANILLA_MOISTURE = getBoolean("Disable vanilla moisture", true, "If this is set to true then farmland will not automatically become wet, and must be watered, it will also not automatically revert to dirt. (Basically disables random ticks for farmland)");
        SPRINKLER_DRAIN_RATE = getInteger("Sprinkler's daily consumption", 250, "This number NEEDs to be a factor of 1000, Otherwise you'll have trouble refilling the sprinkler manually. Acceptable values are: 1, 2, 4, 5, 8, 10, 20, 25, 40, 50, 100, 125, 200, 250, 500, 1000");
    }
}