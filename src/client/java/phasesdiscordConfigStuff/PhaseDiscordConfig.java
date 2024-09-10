package phasesdiscordConfigStuff;

import eu.midnightdust.lib.config.MidnightConfig;

public class PhaseDiscordConfig extends MidnightConfig
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
    @Comment public static Comment text5;
    @Comment public static Comment text6;
    @Entry public static String mainAdvancedModeDetail = "Playing Minecraft";
    @Entry public static String mainAdvancedModeDetailWhenHoldingItem = "Holding {item_name}";

    @Entry public static String mainAdvancedModeStateMultiplayer = "Playing multiplayer on {server_ip}";
    @Entry public static String mainAdvancedModeStateMultiplayerPause = "Playing multiplayer on {server_ip} - Paused";

    @Entry public static String mainAdvancedModeStateSingleplayer = "Playing Singleplayer";
    @Entry public static String mainAdvancedModeStateSingleplayerPause = "Playing Singleplayer - Paused";

    @Entry public static String advancedModeDimensionOverworld = "In The Overworld";
    @Entry public static String advancedModeDimensionNether = "In The Nether";
    @Entry public static String advancedModeDimensionEnd = "In The End";
    @Entry public static String advancedModeDimensionCustom = "In {dimension_name} Dimension";

    @Entry public static boolean advancedModeChangeMainMenuText = false;

    @Entry public static String advancedModeMainMenuText = "Main Menu";

    @Comment public static Comment advancedModePicDetail1;

    @Comment public static Comment advancedModePicDetail2;

    @Comment public static Comment advancedModeOverworldPicDetail;

    @Comment public static Comment advancedModeNetherPicDetail;

    @Comment public static Comment advancedModeEndPicDetail;

    @Comment public static Comment advancedModeOtherPicDetail;

    @Entry public static String advancedModeOverworldPic = "overworld";

    @Entry public static String advancedModeNetherPic = "nether";

    @Entry public static String advancedModeEndPic = "the_end";

    @Entry public static String advancedModeCustomPic = "void";

    @Entry public static String advancedModeLargePic = "base";

    @Comment(centered = true) public static Comment otherOptions;

    @Entry public static boolean enableDebug = false;
    
    @Entry public static String discordAppID = "1147361100929708053";
}
