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

    @Entry(category = BASIC) public static boolean showPlayerHeadAndUsername = true;

    @Comment(category = ADVANCED, centered = true) public static Comment text2;
    @Entry(category = ADVANCED) public static boolean enableAdvancedMode = false;

    @Comment(category = ADVANCED) public static Comment advanceExplain;
    @Comment(category = ADVANCED) public static Comment advanceExplain2;

    @Entry(category = ADVANCED) public static String mainAdvancedModeDetail = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField").getString();
    @Entry(category = ADVANCED) public static String mainAdvancedModeDetailWhenHoldingItem = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField").getString();

    @Entry(category = ADVANCED) public static String mainAdvancedModeStateMultiplayer = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField").getString();
    @Entry(category = ADVANCED) public static String mainAdvancedModeStateMultiplayerPause = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseTextField").getString();

    @Entry(category = ADVANCED) public static String mainAdvancedModeStateSingleplayer = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField").getString();;
    @Entry(category = ADVANCED) public static String mainAdvancedModeStateSingleplayerPause = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField").getString();;

    @Entry(category = ADVANCED) public static String advancedModeDimensionOverworld = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField").getString();;
    @Entry(category = ADVANCED) public static String advancedModeDimensionNether = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField").getString();;
    @Entry(category = ADVANCED) public static String advancedModeDimensionEnd = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField").getString();;
    @Entry(category = ADVANCED) public static String advancedModeDimensionCustom = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionCustomTextField").getString();;

    @Entry(category = ADVANCED) public static boolean advancedModeChangeMainMenuText = false;

    @Entry(category = ADVANCED) public static String advancedModeMainMenuText = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField").getString();

    @Entry(category = ADVANCED) public static String advancedModeLargeText = "Phase's Discord Rich Presence";

    @Entry(category = ADVANCED) public static String advancedModeMainMenuState = "Phase's Discord Rich Presence";

    @Comment(category = ADVANCED) public static Comment advancedModePicDetail1;

    @Comment(category = ADVANCED) public static Comment advancedModePicDetail2;

    @Comment(category = ADVANCED) public static Comment advancedModePicDetail3;

    @Comment(category = ADVANCED) public static Comment advancedModeOverworldPicDetail;

    @Comment(category = ADVANCED) public static Comment advancedModeOverworldPicDetail2;

    @Comment(category = ADVANCED) public static Comment advancedModeOverworldPicDetail3;

    @Comment(category = ADVANCED) public static Comment advancedModeNetherPicDetail;

    @Comment(category = ADVANCED) public static Comment advancedModeEndPicDetail;

    @Comment(category = ADVANCED) public static Comment advancedModeOtherPicDetail;

    @Entry(category = ADVANCED) public static String advancedModeLargePic = "base";

    @Entry(category = ADVANCED) public static String advancedModeOverworldPic = "overworld";

    @Entry(category = ADVANCED) public static String advancedModeNetherPic = "nether";

    @Entry(category = ADVANCED) public static String advancedModeEndPic = "the_end";

    @Entry(category = ADVANCED) public static String advancedModeCustomPic = "void";

    @Entry(category = ADVANCED) public static boolean advancedModeShowPlayerHeadUser = true;

    @Comment(category = OTHER, centered = true) public static Comment otherOptions;

    @Entry(category = OTHER) public static boolean enableDebug = false;

    @Entry(category = OTHER) public static int discordRichPresenceUpdateRate = 5000;

    @Entry(category = OTHER) public static String discordAppID = "1147361100929708053";
}