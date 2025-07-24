package com.phasesdiscord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.Activity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import java.time.Instant;
import java.util.Arrays;

import static com.phasesdiscord.PhaseDiscordClient.LOGGER;

public class RPC
{
    private static final Activity activity = new Activity();

    static MinecraftClient client = MinecraftClient.getInstance();

    static String imageNameOverworld;

    static String imageNameNether;

    static String imageNameEnd;

    static String imageNameCustom;

    static String largeImageKey;

    static String customDimensionName = ""; //default value

    static String[] imageKeyArray = {
            "overworld", "mountain", "swamp", "blankplains", "caveoverlookview", "mountainview",
            "polar_bear", "savanna", "savanna_plateau",
            "pale_garden1", "pale_garden2", "pale_garden3", "shaders1", "shaders2", "trial_chamber",
            "spring1", "spring2", "vibrant_visuals1", "vibrant_visuals2",
            "lush_cave", "deep_dark1", "deep_dark2", "happy_ghast1",
            "nether", "nether2", "nether3", "nethercool",
            "the_end", "end2", "end3", "actualendbg",
            "void", "base", "base_old", "creeper_icon", "fallback"
    };

    static long defaultAppID = 1147361100929708053L; //in order to check if the id matches
    static boolean usingDefaultAppID = true; //if the app id is the default one, then we can use the default image keys

    public static void start() {
        new Thread(() -> {
            if (!PhaseDiscordConfig.discordEnable) { // rich presence is disabled
                return;
            }

            long finalAppID = Long.parseLong(PhaseDiscordConfig.discordAppID);
            if(finalAppID == defaultAppID)
            {
                usingDefaultAppID = true;
            }
            else
            {
                usingDefaultAppID = false;
            }

            final CreateParams params = new CreateParams();
            params.setClientID(finalAppID);
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
            activity.timestamps().setStart(Instant.now());

            try (final Core core = new Core(params)) {
                //comment to enable logging
                //THIS SPAMS THE CONSOLE A LOT YOU HAVE BEEN WARNED
                core.setLogHook(LogLevel.DEBUG, (level, message) -> LOGGER.info("[Discord] " + message));

                while(true)
                {
                    try
                    {
                        imageNameOverworld = PhaseDiscordConfig.advancedModeOverworldPic;
                        imageNameNether = PhaseDiscordConfig.advancedModeNetherPic;
                        imageNameEnd = PhaseDiscordConfig.advancedModeEndPic;
                        imageNameCustom = PhaseDiscordConfig.advancedModeCustomPic;
                        largeImageKey = PhaseDiscordConfig.advancedModeLargePic;
                        if(client.isInSingleplayer()) //singleplayer presence
                        {
                            if(client.world != null)
                            {
                                DimensionType dimensionType = client.world.getDimension();
                                String dimensionName = dimensionType.effects().toString();
                                String itemToDisplay = "";

                                if(PhaseDiscordConfig.enableAdvancedMode) //advanced mode on
                                {
                                    itemToDisplay = getHeldItem(true);
                                    activity.setDetails(itemToDisplay);

                                    if(PhaseDiscordConfig.advancedModeShowPlayerHeadUser)
                                    {
                                        updatePlayerHead();
                                    }
                                    else
                                    {
                                        updateSmallImageAdvancedMode(PhaseDiscordConfig.advancedModeLargePic, activity);
                                    }

                                    if(client.currentScreen != null)
                                    {
                                        activity.setState(PhaseDiscordConfig.mainAdvancedModeStateSingleplayerPause);
                                    }
                                    else
                                    {
                                        activity.setState(PhaseDiscordConfig.mainAdvancedModeStateSingleplayer);
                                    }
                                    // set dimension stuff
                                    setDimensionKey(true, dimensionName, activity);
                                    //activity.assets().setSmallText(PhaseDiscordConfig.advancedModeLargeText);

                                }
                                else //simple mode
                                {
                                    itemToDisplay = getHeldItem(false);
                                    activity.setDetails(itemToDisplay);

                                    setDimensionKey(false, dimensionName, activity);
                                    if(PhaseDiscordConfig.showPlayerHeadAndUsername)
                                    {
                                        updatePlayerHead();
                                    }
                                    else
                                    {
                                        activity.assets().setSmallText("Phase's Minecraft Discord Rich Presence");
                                        activity.assets().setSmallImage("base");
                                    }

                                    if(!PhaseDiscordConfig.showPaused)
                                    {
                                        activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField", "Playing Singleplayer").getString());
                                    }
                                    else
                                    {
                                        if(client.currentScreen != null)
                                        {
                                            activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField", "Playing Singleplayer - Paused").getString());
                                        }
                                        else
                                        {
                                            activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField", "Playing Singleplayer").getString());
                                        }
                                    }
                                }

                                //debug logging
                                LoggerStuff(dimensionName, itemToDisplay, "singleplayer, not needed", activity);
                            }
                        }
                        else if(client.getCurrentServerEntry() != null) //multiplayer presence
                        {
                            DimensionType dimensionType = client.world.getDimension();
                            String dimensionName = dimensionType.effects().toString();
                            String itemToDisplay = "";
                            String serverIP = client.getCurrentServerEntry().address.toUpperCase();

                            if(PhaseDiscordConfig.enableAdvancedMode)
                            {
                                itemToDisplay = getHeldItem(true);
                                activity.setDetails(itemToDisplay);

                                if(PhaseDiscordConfig.advancedModeShowPlayerHeadUser)
                                {
                                    updatePlayerHead();
                                }
                                else
                                {
                                    updateSmallImageAdvancedMode(PhaseDiscordConfig.advancedModeLargePic, activity);
                                }

                                String stateParsed;
                                if(client.currentScreen != null)
                                {
                                    stateParsed = PhaseDiscordConfig.mainAdvancedModeStateMultiplayerPause.replaceFirst("%s", serverIP);
                                    stateParsed = stateParsed.replaceFirst("%s", String.valueOf(client.world.getPlayers().size()));
                                    activity.setState(stateParsed);
                                }
                                else
                                {
                                    stateParsed = PhaseDiscordConfig.mainAdvancedModeStateMultiplayer.replaceFirst("%s", serverIP);
                                    stateParsed = stateParsed.replaceFirst("%s", String.valueOf(client.world.getPlayers().size()));
                                    activity.setState(stateParsed);
                                }

                                // set dimension stuff
                                setDimensionKey(true, dimensionName, activity);
                                //activity.assets().setSmallText(PhaseDiscordConfig.advancedModeLargeText);
                            }
                            else //simple mode
                            {
                                itemToDisplay = getHeldItem(false);
                                activity.setDetails(itemToDisplay);

                                setDimensionKey(false, dimensionName, activity);
                                if(PhaseDiscordConfig.showPlayerHeadAndUsername)
                                {
                                    updatePlayerHead();
                                }
                                else
                                {
                                    activity.assets().setSmallText("Phase's Minecraft Discord Rich Presence");
                                    activity.assets().setSmallImage("base");
                                }

                                if(!PhaseDiscordConfig.showPaused)
                                {
                                    activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField", "Playing Multiplayer on %s with %s players", serverIP, client.world.getPlayers().size()).getString());
                                }
                                else
                                {
                                    if(client.currentScreen != null)
                                    {
                                        activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseTextField", "Playing Multiplayer on %s with %s players - Paused", serverIP, client.world.getPlayers().size()).getString());
                                    }
                                    else
                                    {
                                        activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField", "Playing Multiplayer on %s with %s players", serverIP, client.world.getPlayers().size()).getString());
                                    }
                                }
                            }

                            //debug logging
                            LoggerStuff(dimensionName, itemToDisplay, serverIP, activity);

                        }
                        else //main menu presence
                        {
                            if(PhaseDiscordConfig.enableAdvancedMode) //advanced mode
                            {

                                //main menu large image error handle
                                if(Arrays.stream(imageKeyArray).anyMatch(largeImageKey::equals))
                                {
                                    activity.assets().setLargeImage(largeImageKey);
                                }
                                else
                                {
                                    activity.assets().setLargeImage("fallback");
                                }

                                if(PhaseDiscordConfig.advancedModeChangeMainMenuText)
                                {
                                    activity.setDetails(PhaseDiscordConfig.advancedModeMainMenuText);
                                }
                                else
                                {
                                    activity.setDetails(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField","Main Menu").getString());
                                }

                                activity.assets().setLargeText(PhaseDiscordConfig.advancedModeLargeText); //to be changed via options eventually
                                activity.setState(PhaseDiscordConfig.advancedModeMainMenuState);
                            }
                            else //simple mode
                            {
                                activity.assets().setLargeImage("base");
                                activity.setDetails(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField","Main Menu").getString());
                                activity.assets().setLargeText("Phase's Minecraft Discord Rich Presence");
                                if(PhaseDiscordConfig.showPlayerHeadAndUsername)
                                {
                                    updatePlayerHead();
                                    activity.assets().setSmallText(client.getSession().getUsername());
                                }

                                activity.setState("Phase's Minecraft Discord Rich Presence"); //maybe add an option to change this?
                            }
                        }

                        core.activityManager().updateActivity(activity);

                        try {
                            Thread.sleep(PhaseDiscordConfig.discordRichPresenceUpdateRate);
                        } catch (InterruptedException e) {
                            LOGGER.error("Thread was interrupted", e);
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            } catch (RuntimeException e) {
                LOGGER.error("Failed to initialize Discord Core", e);
            }
        }).start();
    }


    //these following methods are adopted from the Fabric-DiscordRPC repo (https://github.com/copyandbuild/Fabric-DiscordRPC)
    //updates player head
    private static void updatePlayerHead()
    {
        String uuid = client.getGameProfile().getId().toString();
        String playerHeadImage = getPlayerHeadURL(uuid, "head", 3);
        activity.assets().setSmallImage(playerHeadImage);
        activity.assets().setSmallText(client.getSession().getUsername());
    }

    //fetches the image URL for the player head
    @Contract(pure = true)
    private static @NotNull String getPlayerHeadURL(String uuid, String type, int size)
    {
        return "https://api.mineatar.io/" + type + "/" + uuid + "?scale=" + size;
    }

    //returns the string/name of the item the player is currently holding
    private static String getHeldItem(boolean isAdvancedMode)
    {
        String finalResult = ""; //default value
        String item_name = ""; //default value
        if(client.player != null)
        {
            ItemStack held_item = client.player.getStackInHand(Hand.MAIN_HAND);
            item_name = held_item.getName().getString();
            if(isAdvancedMode)
            {
                if(!item_name.equals(Items.AIR.getName().getString()))
                {
                    finalResult = PhaseDiscordConfig.mainAdvancedModeDetailWhenHoldingItem.replace("%s", item_name);
                }
                else
                {
                    finalResult = PhaseDiscordConfig.mainAdvancedModeDetail;
                }

                return finalResult;
            }
            else //simple mode
            {
                if(PhaseDiscordConfig.enableItem == false)
                {
                    finalResult = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField","Playing Minecraft").getString();
                }
                else
                {
                    if(!item_name.equals(Items.AIR.getName().getString()))
                    {
                        finalResult = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField","Holding" + item_name, item_name).getString();
                    }
                    else
                    {
                        finalResult = "Playing Minecraft";
                    }
                }

                return finalResult;
            }
        }

        return "(If this is showing, then a bug has occured)"; //just in case, but this should never be shown
    }

    //sets the assets in the presence to the correct dimension
    public static void setDimensionKey(boolean inAdvancedMode, String dimensionName, Activity presence)
    {
        if(inAdvancedMode)
        {
            if(dimensionName.equals("minecraft:overworld"))
            {
                if(checkIfImageKeyIsValid(imageNameOverworld) == false)
                {
                    activity.assets().setLargeImage("fallback");
                }
                else
                {
                    activity.assets().setLargeImage(imageNameOverworld);
                }

                activity.assets().setLargeText(PhaseDiscordConfig.advancedModeDimensionOverworld);
            }
            else if(dimensionName.equals("minecraft:the_nether"))
            {
                if(checkIfImageKeyIsValid(imageNameNether) == false)
                {
                    activity.assets().setLargeImage("fallback");
                }
                else
                {
                    activity.assets().setLargeImage(imageNameNether);
                }

                activity.assets().setLargeText(PhaseDiscordConfig.advancedModeDimensionNether);
            }
            else if(dimensionName.equals("minecraft:the_end"))
            {
                if(checkIfImageKeyIsValid(imageNameEnd) == false)
                {
                    activity.assets().setLargeImage("fallback");
                }
                else
                {
                    activity.assets().setLargeImage(imageNameEnd);
                }

                activity.assets().setLargeText(PhaseDiscordConfig.advancedModeDimensionEnd);
            }
            else
            {
                customDimensionName = dimensionName.replace("minecraft:", "");
                if(checkIfImageKeyIsValid(imageNameCustom) == false)
                {
                    activity.assets().setLargeImage("fallback");
                }
                else
                {
                    activity.assets().setLargeImage(imageNameCustom);
                }

                activity.assets().setLargeText(PhaseDiscordConfig.advancedModeDimensionCustom.replace("{dimension_name}", customDimensionName));
            }
        }
        else
        {
            if(dimensionName.equals("minecraft:overworld"))
            {
                if(PhaseDiscordConfig.enableDimension == false)
                {
                    presence.assets().setLargeImage("void");
                    presence.assets().setLargeText("Minecraft");
                }
                else
                {
                    presence.assets().setLargeImage("overworld");
                    presence.assets().setLargeText(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField","In The Overworld").getString());
                }
            }
            else if(dimensionName.equals("minecraft:the_nether"))
            {
                if(PhaseDiscordConfig.enableDimension == false)
                {
                    presence.assets().setLargeImage("void");
                    presence.assets().setLargeText("Minecraft");
                }
                else
                {
                    presence.assets().setLargeImage("nether");
                    presence.assets().setLargeText(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField","In The Nether").getString());
                }
            }
            else if(dimensionName.equals("minecraft:the_end"))
            {
                if(PhaseDiscordConfig.enableDimension == false)
                {
                    presence.assets().setLargeImage("void");
                    presence.assets().setLargeText("Minecraft");
                }
                else
                {
                    presence.assets().setLargeImage("the_end");
                    presence.assets().setLargeText(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField","In The End").getString());
                }
            }
            else
            {
                if(PhaseDiscordConfig.enableDimension == false)
                {
                    presence.assets().setLargeImage("void");
                    presence.assets().setLargeText("Minecraft");
                }
                else
                {
                    if(PhaseDiscordConfig.enableCustomDimensionSupport == false)
                    {
                        presence.assets().setLargeImage("void");
                        presence.assets().setLargeText("");
                    }
                    else
                    {
                        //System.out.println(dimensionName + " why is this not working");
                        customDimensionName = dimensionName.replace("minecraft:", "");
                        presence.assets().setLargeImage("void");
                        presence.assets().setLargeText("In " + customDimensionName + " Dimension");
                    }
                }
            }
        }
    }

    //updates the small image of advanced mode
    public static void updateSmallImageAdvancedMode(String name, Activity presence)
    {
        if(checkIfImageKeyIsValid(name) == false)
        {
            presence.assets().setSmallImage("fallback");
        }
        else
        {
            presence.assets().setSmallImage(name);
        }
    }

    //checks if the image string passed in through advanced mode is a valid string
    //given the array holding all possible images
    public static boolean checkIfImageKeyIsValid(String imageKey)
    {
        if(Arrays.stream(imageKeyArray).anyMatch(imageKey::equals))
        {
            return true;
        }
        else
        {
            if(usingDefaultAppID == false)
            {
                return true; //always return true, expect the user to have handled valid keys themselves
            }

            LOGGER.info("An image key for advanced mode is invalid, setting to fallback.");
            LOGGER.info("Invalid Image Key was..." + imageKey);
            return false;
        }
    }

    //prints out debug stuff, mainly meant for advanced mode debugging
    public static void LoggerStuff(String dimName, String item_name, String serverip, Activity presence)
    {
        if(PhaseDiscordConfig.enableDebug == true)
        {
            LOGGER.info("Advanced Mode Variables");
            LOGGER.info("Dimension Name - " + dimName);
            LOGGER.info("Item Name - " + item_name);
            LOGGER.info("Server IP - " + serverip);
            LOGGER.info("Current Presence Details - " + activity.getDetails());
            LOGGER.info("Current Presence State - " + activity.getState());
        }
    }
}