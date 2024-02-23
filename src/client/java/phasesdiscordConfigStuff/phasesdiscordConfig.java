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
    @Entry public static boolean enableAdvancedMode = false;

    @Comment public static Comment text3;
    @Comment public static Comment text4;
    @Entry public static String mainAdvancedModeDetail = "Playing Minecraft";
    @Entry public static String mainAdvancedModeDetailWhenHoldingItem = "Holding {item_name}";

    @Entry public static String mainAdvancedModeStateMultiplayer = "Playing multiplayer on {server_ip}";
    @Entry public static String mainAdvancedModeStateMultiplayerPause = "Playing multiplayer on {server_ip} - Paused";

    @Entry public static String mainAdvancedModeStateSingleplayer = "Playing Singleplayer";
    @Entry public static String mainAdvancedModeStateSingleplayerPause = "Playing Singleplayer - Paused";
}
