package phasesdiscordConfigStuff;

import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.text.Text;

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

    @Entry(category = BASIC) public static boolean enableServerPlayerCount = true;

    @Entry(category = BASIC) public static boolean showPaused = true;

    @Comment(category = ADVANCED, centered = true) public static Comment text2;
    @Entry(category = ADVANCED) public static boolean enableAdvancedMode = false;

    @Comment(category = ADVANCED) public static Comment advanceExplain;
    @Comment(category = ADVANCED) public static Comment advanceExplain2;

    @Entry(category = ADVANCED) public static String mainAdvancedModeDetail = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField","Playing Minecraft").getString();
    @Entry(category = ADVANCED) public static String mainAdvancedModeDetailWhenHoldingItem = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField","Holding %s").getString();

    @Entry(category = ADVANCED) public static String mainAdvancedModeStateMultiplayer = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField","Playing Multiplayer on %s with %s players").getString();
    @Entry(category = ADVANCED) public static String mainAdvancedModeStateMultiplayerPause = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseTextField","Playing Multiplayer on %s with %s players - Paused").getString();

    @Entry(category = ADVANCED) public static String mainAdvancedModeStateSingleplayer = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField","Playing Singleplayer").getString();;
    @Entry(category = ADVANCED) public static String mainAdvancedModeStateSingleplayerPause = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField","Playing Singleplayer - Paused").getString();;

    @Entry(category = ADVANCED) public static String advancedModeDimensionOverworld = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField","In The Overworld").getString();;
    @Entry(category = ADVANCED) public static String advancedModeDimensionNether = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField","In The Nether").getString();;
    @Entry(category = ADVANCED) public static String advancedModeDimensionEnd = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField","In The End").getString();;
    @Entry(category = ADVANCED) public static String advancedModeDimensionCustom = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionCustomTextField","In %s Dimension").getString();;

    @Entry(category = ADVANCED) public static boolean advancedModeChangeMainMenuText = false;

    @Entry(category = ADVANCED) public static String advancedModeMainMenuText = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField","Main Menu").getString();

    @Comment(category = ADVANCED) public static Comment advancedModePicDetail1;

    @Comment(category = ADVANCED) public static Comment advancedModePicDetail2;

    @Comment(category = ADVANCED) public static Comment advancedModePicDetail3;

    @Comment(category = ADVANCED) public static Comment advancedModeOverworldPicDetail;

    @Comment(category = ADVANCED) public static Comment advancedModeOverworldPicDetail2;

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
