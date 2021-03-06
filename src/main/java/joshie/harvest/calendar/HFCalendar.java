package joshie.harvest.calendar;

import joshie.harvest.api.HFApi;
import joshie.harvest.api.calendar.SeasonProvider;
import joshie.harvest.core.helpers.generic.ConfigHelper;
import joshie.harvest.core.util.HFLoader;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

import static joshie.harvest.core.helpers.generic.ConfigHelper.*;
import static joshie.harvest.core.lib.LoadOrder.HFCALENDAR;

@HFLoader(priority = HFCALENDAR)
public class HFCalendar {
    private static final SeasonProvider HIDDEN = new SeasonProviderHidden();
    public static Configuration CONFIG;
    private static DimensionType SEASONS;
    public static int DAYS_PER_SEASON;
    public static long TICKS_PER_DAY;
    public static boolean ENABLE_SUNNY;
    public static boolean ENABLE_RAIN;
    public static boolean ENABLE_TYPHOON;
    public static boolean ENABLE_SNOW;
    public static boolean ENABLE_BLIZZARD;
    public static boolean ENABLE_DATE_HUD;
    public static boolean ENABLE_GOLD_HUD;
    public static int OVERWORLD_ID;
    public static boolean HIDE_CALENDAR_TEXTURE;
    public static boolean HIDE_GOLD_TEXTURE;
    public static int X_CALENDAR;
    public static int Y_CALENDAR;
    public static int X_GOLD;
    public static int Y_GOLD;

    public static void preInit() {
        SEASONS = DimensionType.register("seasons", "seasons", OVERWORLD_ID, HFWorldProvider.class, true);
        DimensionManager.unregisterDimension(0);
        DimensionManager.registerDimension(0, SEASONS);
        HFApi.calendar.registerSeasonProvider(1, HIDDEN);
        HFApi.calendar.registerSeasonProvider(-1, HIDDEN);
    }

    public static void save() {
        ConfigHelper.setConfig(CONFIG);
        ConfigHelper.setCategory("calendar");
        setInteger("HUD > Calendar X", X_CALENDAR);
        setInteger("HUD > Calendar Y", Y_CALENDAR);
        setBoolean("HUD > Calendar Hide Texture", HIDE_CALENDAR_TEXTURE);
        setInteger("HUD > Gold X", X_GOLD);
        setInteger("HUD > Gold Y", Y_GOLD);
        setBoolean("HUD > Gold Hide Texture", HIDE_GOLD_TEXTURE);
        CONFIG.save();
    }

    //Configuration
    public static void configure() {
        CONFIG = ConfigHelper.getConfig();
        OVERWORLD_ID = getInteger("Overworld ID", 3);
        DAYS_PER_SEASON = getInteger("Days per season", 30);
        TICKS_PER_DAY = getInteger("Ticks per day", 24000);
        ENABLE_SUNNY = getBoolean("Weather > Enable sunny", true);
        ENABLE_RAIN = getBoolean("Weather > Enable rain", true);
        ENABLE_TYPHOON = getBoolean("Weather > Enable typhoon", true);
        ENABLE_SNOW = getBoolean("Weather > Enable snow", true);
        ENABLE_BLIZZARD = getBoolean("Weather > Enable blizzard", true);
        HIDE_CALENDAR_TEXTURE = getBoolean("HUD > Calendar Hide Texture", false);
        X_CALENDAR = getInteger("HUD > Calendar X", 0);
        Y_CALENDAR = getInteger("HUD > Calendar Y", 0);
        HIDE_GOLD_TEXTURE = getBoolean("HUD > Gold Hide Texture", false);
        X_GOLD = getInteger("HUD > Gold X", 0);
        Y_GOLD = getInteger("HUD > Gold Y", 0);
        ENABLE_DATE_HUD = getBoolean("HUD > Enable data", true);
        ENABLE_GOLD_HUD = getBoolean("HUD > Enable gold", true);
    }
}
