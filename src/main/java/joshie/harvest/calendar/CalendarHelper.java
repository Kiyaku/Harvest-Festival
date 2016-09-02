package joshie.harvest.calendar;

import joshie.harvest.api.calendar.CalendarDate;
import joshie.harvest.api.calendar.Season;
import joshie.harvest.api.calendar.Weekday;
import joshie.harvest.core.handlers.HFTrackers;
import net.minecraft.world.World;

import static joshie.harvest.calendar.HFCalendar.DAYS_PER_SEASON;
import static joshie.harvest.calendar.HFCalendar.TICKS_PER_DAY;

public class CalendarHelper {
    private static final Season[] SEASONS;
    private static final Weekday[] DAYS;
    static {
        SEASONS = Season.class.getEnumConstants();
        DAYS = Weekday.class.getEnumConstants();
    }

    private static Weekday getWeekday(int days) {
        int modulus = days % 7;
        if (modulus < 0) modulus = 0;
        return DAYS[modulus];
    }

    private static Weekday getWeekday(long time) {
        return getWeekday(getElapsedDays(time));
    }

    public static void setDate(World world, CalendarDate date) {
        long time = world.getWorldTime();
        Season previous = date.getSeason();
        date.setWeekday(getWeekday(time)).setDay(getDay(time)).setSeason(getSeason(time)).setYear(getYear(time));
        if (previous != date.getSeason()) {
            HFTrackers.getCalendar(world).onSeasonChanged();
        }
    }

    private static int getYear(long totalTime) {
        return (int) Math.floor(getElapsedDays(totalTime) / 4 / DAYS_PER_SEASON);
    }

    public static Season getSeason(long totalTime) {
        return SEASONS[Math.max(0, (int)Math.floor((getElapsedDays(totalTime) / DAYS_PER_SEASON) % 4))];
    }

    private static int getDay(long totalTime) {
        return getElapsedDays(totalTime) % DAYS_PER_SEASON;
    }

    public static int getElapsedDays(long totalTime) {
        return (int) (totalTime / TICKS_PER_DAY);
    }

    private static int getTotalDays(int day, Season season, int year) {
        int season_days = DAYS_PER_SEASON * season.ordinal();
        int year_days = (year - 1) * (DAYS_PER_SEASON * 4);
        return day + season_days + year_days;
    }

    public static int getTotalDays(CalendarDate date) {
        int current_days = date.getDay();
        int season_days = HFCalendar.DAYS_PER_SEASON * date.getSeason().ordinal();
        int year_days = (date.getYear() - 1) * (DAYS_PER_SEASON * 4);
        return current_days + season_days + year_days;
    }

    public static int getYearsPassed(CalendarDate birthday, CalendarDate date) {
        int current_total_days = getTotalDays(date);
        int birthday_total_days = getTotalDays(birthday);
        int one_year = DAYS_PER_SEASON * 4;
        
        int years_passed = current_total_days / one_year; 
        int birthday_years = birthday_total_days / one_year;
                
        return years_passed - birthday_years;
    }

    public static long getTime(int day, Season season, int year) {
        return (getTotalDays(day, season, year)) * TICKS_PER_DAY;
    }

    public static long getTime(World world) {
        return (world.getWorldTime() + 6000) % TICKS_PER_DAY;
    }
    
    public static int getScaledTime(int time) {
        return (int) ((time / 24000D) * TICKS_PER_DAY);
    }
}
