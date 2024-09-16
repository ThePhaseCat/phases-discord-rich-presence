package phasesdiscordConfigStuff;

import eu.midnightdust.lib.config.MidnightConfig;

public class PhaseDiscordConfig extends MidnightConfig
{
    public static final String BASIC = "basic";
    public static final String ADVANCED = "advanced";
    public static final String OTHER = "other";

    @Comment(category = BASIC, centered = true) public static Comment text1;
    @Entry(category = BASIC) public static boolean discordEnable = true;

    @Entry(category = BASIC) public static boolean enableItem = true;

    @Entry(category = BASIC) public static boolean enableDimension = true;

    @Entry(category = BASIC) public static boolean enableServerIP = true;

    @Entry(category = BASIC) public static boolean enableCustomDimensionSupport = true;

    @Entry(category = BASIC) public static boolean showPaused = true;

    @Comment(category = ADVANCED, centered = true) public static Comment text2;
    @Entry(category = ADVANCED) public static boolean enableAdvancedMode = false;

    @Comment(category = ADVANCED) public static Comment text3;
    @Comment(category = ADVANCED) public static Comment text4;
    @Comment(category = ADVANCED) public static Comment text5;
    @Comment(category = ADVANCED) public static Comment text6;
    @Entry(category = ADVANCED) public static String mainAdvancedModeDetail = "Playing Minecraft";
    @Entry(category = ADVANCED) public static String mainAdvancedModeDetailWhenHoldingItem = "Holding {item_name}";

    @Entry(category = ADVANCED) public static String mainAdvancedModeStateMultiplayer = "Playing multiplayer on {server_ip}";
    @Entry(category = ADVANCED) public static String mainAdvancedModeStateMultiplayerPause = "Playing multiplayer on {server_ip} - Paused";

    @Entry(category = ADVANCED) public static String mainAdvancedModeStateSingleplayer = "Playing Singleplayer";
    @Entry(category = ADVANCED) public static String mainAdvancedModeStateSingleplayerPause = "Playing Singleplayer - Paused";

    @Entry(category = ADVANCED) public static String advancedModeDimensionOverworld = "In The Overworld";
    @Entry(category = ADVANCED) public static String advancedModeDimensionNether = "In The Nether";
    @Entry(category = ADVANCED) public static String advancedModeDimensionEnd = "In The End";
    @Entry(category = ADVANCED) public static String advancedModeDimensionCustom = "In {dimension_name} Dimension";

    @Entry(category = ADVANCED) public static boolean advancedModeChangeMainMenuText = false;

    @Entry(category = ADVANCED) public static String advancedModeMainMenuText = "Main Menu";

    @Comment(category = ADVANCED) public static Comment advancedModePicDetail1;

    @Comment(category = ADVANCED) public static Comment advancedModePicDetail2;

    @Comment(category = ADVANCED) public static Comment advancedModeOverworldPicDetail;

    @Comment(category = ADVANCED) public static Comment advancedModeNetherPicDetail;

    @Comment(category = ADVANCED) public static Comment advancedModeEndPicDetail;

    @Comment(category = ADVANCED) public static Comment advancedModeOtherPicDetail;

    @Entry(category = ADVANCED) public static String advancedModeOverworldPic = "overworld";

    @Entry(category = ADVANCED) public static String advancedModeNetherPic = "nether";

    @Entry(category = ADVANCED) public static String advancedModeEndPic = "the_end";

    @Entry(category = ADVANCED) public static String advancedModeCustomPic = "void";

    @Entry(category = ADVANCED) public static String advancedModeLargePic = "base";

    @Comment(category = OTHER, centered = true) public static Comment otherOptions;

    @Entry(category = OTHER) public static boolean enableDebug = false;
    
    @Entry(category = OTHER) public static String discordAppID = "1147361100929708053";

    @Entry(category = OTHER) public static int discordRichPresenceUpdateRate = 5000;
}
