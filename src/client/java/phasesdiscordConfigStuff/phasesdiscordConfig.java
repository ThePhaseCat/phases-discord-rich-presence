package phasesdiscordConfigStuff;

import eu.midnightdust.lib.config.MidnightConfig;

public class phasesdiscordConfig extends MidnightConfig {

    //@Entry(category = "basic") public static String discordState = "Playing Minecraft";
    @Entry(category = "basic") public static boolean discordEnable = true;

    @Entry(category = "basic") public static boolean enableServerIP = true;

    @Entry(category = "basic") public static boolean enableItem = true;

    @Entry(category = "basic") public static boolean enableDimension = true;

    @Entry(category = "test") public static String test = "test";
}
