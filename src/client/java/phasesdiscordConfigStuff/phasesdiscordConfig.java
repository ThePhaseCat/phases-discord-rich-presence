package phasesdiscordConfigStuff;

import eu.midnightdust.lib.config.MidnightConfig;

public class phasesdiscordConfig extends MidnightConfig
{
    @Comment(centered = true) public static Comment text1;
    @Entry public static boolean discordEnable = true;

    @Entry public static boolean enableItem = true;

    @Entry public static boolean enableDimension = true;

    @Entry public static boolean enableServerIP = true;

    @Entry public static boolean enableCustomDimensionSupport = true;

    @Entry public static boolean showPaused = true;

    @Comment(centered = true) public static Comment text2;
}
