package com.phasesdiscord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.Activity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import java.time.Instant;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.phasesdiscord.PhaseDiscordClient.LOGGER;

public class RPC
{
    private static final Activity activity = new Activity();

    static Minecraft client = Minecraft.getInstance();

    static String imageNameOverworldSingleplayer;

    static String imageNameNetherSingleplayer;

    static String imageNameEndSingleplayer;

    static String imageNameCustomSingleplayer;

    static String imageNameOverworldMutliplayer;

    static String imageNameNetherMutliplayer;

    static String imageNameEndMutliplayer;

    static String imageNameCustomMutliplayer;

    static String largeImageKey;

    static String customDimensionName = ""; //default value

    static String[] imageKeyArray = {
            "overworld", "mountain", "swamp", "blankplains", "caveoverlookview", "mountainview",
            "polar_bear", "savanna", "savanna_plateau",
            "pale_garden1", "pale_garden2", "pale_garden3", "shaders1", "shaders2", "trial_chamber",
            "spring1", "spring2", "vibrant_visuals1", "vibrant_visuals2",
            "lush_cave", "dripstone_cave", "deep_dark1", "deep_dark2", "happy_ghast1",
            "coppergolem1", "coppergolem2", "coppergolem3", "coppergolem4",
            "nautilus1", "nautilus2", "nautilus3", "spear1",
            "baby_overworld1", "baby_overworld2", "baby_overworld3", "baby_nether1", "baby_nether2", "baby_fish",
            "nether", "nether2", "nether3", "nethercool",
            "the_end", "end2", "end3", "actualendbg",
            "void", "base", "base_old", "creeper_icon", "fallback", "pack"
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
                        imageNameOverworldSingleplayer = PhaseDiscordConfig.advancedModeOverworldPicSingle;
                        imageNameNetherSingleplayer = PhaseDiscordConfig.advancedModeNetherPicSingle;
                        imageNameEndSingleplayer = PhaseDiscordConfig.advancedModeEndPicSingle;
                        imageNameCustomSingleplayer = PhaseDiscordConfig.advancedModeCustomPicSingle;

                        imageNameOverworldMutliplayer = PhaseDiscordConfig.advancedModeOverworldPicMult;
                        imageNameNetherMutliplayer = PhaseDiscordConfig.advancedModeNetherPicMult;
                        imageNameEndMutliplayer = PhaseDiscordConfig.advancedModeEndPicMult;
                        imageNameCustomMutliplayer = PhaseDiscordConfig.advancedModeCustomPicMult;

                        largeImageKey = PhaseDiscordConfig.advancedModeLargePic;
                        if(client.isLocalServer()) //singleplayer presence
                        {
                            singleplayerPresenceLogic(activity);
                        }
                        else if(client.getCurrentServer() != null) //multiplayer presence
                        {
                            multiplayerPresenceLogic(activity);
                        }
                        else //main menu presence
                        {
                            mainMenuPresenceLogic(activity);
                        }

                        try {
                            core.activityManager().updateActivity(activity);
                        } catch (Exception e) {
                            LOGGER.error("Failed to update activity, closing connection");
                            core.activityManager().clearActivity();
                            throw new RuntimeException(e);
                        }

                        try {
                            Thread.sleep(PhaseDiscordConfig.discordRichPresenceUpdateRate);
                        } catch (InterruptedException e) {
                            LOGGER.error("Thread was interrupted", e);
                            Thread.currentThread().interrupt();
                            core.activityManager().clearActivity();
                            throw new RuntimeException(e);
                        }
                    } catch (RuntimeException e) {
                        LOGGER.error("Unexpected unexpected error while updating Discord Rich Presence, closing connection.", e);
                        e.printStackTrace();
                        core.activityManager().clearActivity();
                        break; //exit while loop, stop running
                    }
                }
            } catch (RuntimeException e) {
                LOGGER.error("Unexpected error while starting Discord Rich Presence", e);
                e.printStackTrace();
                return;
            }
        }).start();
    }


    //these following methods are adopted from the Fabric-DiscordRPC repo (https://github.com/larroxtv/Fabric-DiscordRPC)
    //updates player head
    private static void updatePlayerHead()
    {
        String uuid = client.getGameProfile().id().toString();
        String playerHeadImage = getPlayerHeadURL(uuid, "head", 3);
        activity.assets().setSmallImage(playerHeadImage);
        activity.assets().setSmallText(client.getUser().getName());
    }

    //fetches the image URL for the player head
    @Contract(pure = true)
    private static @NotNull String getPlayerHeadURL(String uuid, String type, int size)
    {
        return "https://mc-heads.net/avatar/" + uuid;
    }

    //returns the string/name of the item the player is currently holding
    private static String getHeldItem(boolean isAdvancedMode)
    {
        String finalResult = ""; //default value
        String item_name = ""; //default value
        if(client.player != null)
        {
            ItemStack held_item = client.player.getItemInHand(InteractionHand.MAIN_HAND);
            item_name = held_item.getHoverName().getString();
            if(isAdvancedMode)
            {
                if(!held_item.isEmpty())
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
                    finalResult = Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField").getString();
                }
                else
                {
                    if(!held_item.isEmpty())
                    {
                        finalResult = Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField", item_name).getString();
                    }
                    else
                    {
                        finalResult = Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField").getString();
                    }
                }

                return finalResult;
            }
        }

        return "(If this is showing, then a bug has occured)"; //just in case, but this should never be shown
    }

    //sets the assets in the presence to the correct dimension
    public static void setDimensionKey(boolean inAdvancedMode, String dimensionName, Activity presence, boolean inMultiplayer, String serverIP)
    {
        if(inAdvancedMode)
        {
            if(dimensionName.equals("minecraft:overworld"))
            {
                if(inMultiplayer) //multiplayer checks
                {
                    if(checkIfImageKeyIsValid(imageNameOverworldMutliplayer, true) == false)
                    {
                        activity.assets().setLargeImage("fallback");
                    }
                    else
                    {
                        if(imageNameOverworldMutliplayer.equals("server"))
                        {
                            String imageLink = "https://api.mcsrvstat.us/icon/"+serverIP;
                            activity.assets().setLargeImage(imageLink);
                        }
                        else
                        {
                            activity.assets().setLargeImage(imageNameOverworldMutliplayer);
                        }
                    }
                }
                else //singleplayer checks
                {
                    if(checkIfImageKeyIsValid(imageNameOverworldSingleplayer, false) == false)
                    {
                        activity.assets().setLargeImage("fallback");
                    }
                    else
                    {
                        activity.assets().setLargeImage(imageNameOverworldSingleplayer);
                    }
                }

                activity.assets().setLargeText(PhaseDiscordConfig.advancedModeDimensionOverworld);
            }
            else if(dimensionName.equals("minecraft:the_nether"))
            {
                if(inMultiplayer) //multiplayer checks
                {
                    if(checkIfImageKeyIsValid(imageNameNetherMutliplayer, true) == false)
                    {
                        activity.assets().setLargeImage("fallback");
                    }
                    else
                    {
                        if(imageNameNetherMutliplayer.equals("server"))
                        {
                            String imageLink = "https://api.mcsrvstat.us/icon/"+serverIP;
                            activity.assets().setLargeImage(imageLink);
                        }
                        else
                        {
                            activity.assets().setLargeImage(imageNameNetherMutliplayer);
                        }
                    }
                }
                else //singleplayer checks
                {
                    if(checkIfImageKeyIsValid(imageNameNetherSingleplayer, false) == false)
                    {
                        activity.assets().setLargeImage("fallback");
                    }
                    else
                    {
                        activity.assets().setLargeImage(imageNameNetherSingleplayer);
                    }
                }

                activity.assets().setLargeText(PhaseDiscordConfig.advancedModeDimensionNether);
            }
            else if(dimensionName.equals("minecraft:the_end"))
            {
                if(inMultiplayer) //multiplayer checks
                {
                    if(checkIfImageKeyIsValid(imageNameEndMutliplayer, true) == false)
                    {
                        activity.assets().setLargeImage("fallback");
                    }
                    else
                    {
                        if(imageNameEndMutliplayer.equals("server"))
                        {
                            String imageLink = "https://api.mcsrvstat.us/icon/"+serverIP;
                            activity.assets().setLargeImage(imageLink);
                        }
                        else
                        {
                            activity.assets().setLargeImage(imageNameEndMutliplayer);
                        }
                    }
                }
                else //singleplayer checks
                {
                    if(checkIfImageKeyIsValid(imageNameEndSingleplayer, false) == false)
                    {
                        activity.assets().setLargeImage("fallback");
                    }
                    else
                    {
                        activity.assets().setLargeImage(imageNameEndSingleplayer);
                    }
                }

                activity.assets().setLargeText(PhaseDiscordConfig.advancedModeDimensionEnd);
            }
            else //custom dimension stuff
            {
                customDimensionName = dimensionName.replace("minecraft:", "");
                if(inMultiplayer) //multiplayer checks
                {
                    if(checkIfImageKeyIsValid(imageNameCustomMutliplayer, true) == false)
                    {
                        activity.assets().setLargeImage("fallback");
                    }
                    else
                    {
                        if(imageNameCustomMutliplayer.equals("server"))
                        {
                            String imageLink = "https://api.mcsrvstat.us/icon/"+serverIP;
                            activity.assets().setLargeImage(imageLink);
                        }
                        else
                        {
                            activity.assets().setLargeImage(imageNameCustomMutliplayer);
                        }
                    }
                }
                else //singleplayer checks
                {
                    if(checkIfImageKeyIsValid(imageNameCustomSingleplayer, false) == false)
                    {
                        activity.assets().setLargeImage("fallback");
                    }
                    else
                    {
                        activity.assets().setLargeImage(imageNameCustomSingleplayer);
                    }
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
                    presence.assets().setLargeText(Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField").getString());
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
                    presence.assets().setLargeText(Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField").getString());
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
                    presence.assets().setLargeText(Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField").getString());
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
    public static void updateSmallImageAdvancedMode(String name, Activity presence, boolean inMultiplayer, String serverIP)
    {
        if(checkIfImageKeyIsValid(name, inMultiplayer) == false)
        {
            presence.assets().setSmallImage("fallback");
        }
        else
        {
            if(name.equals("server") && inMultiplayer)
            {
                String imageLink = "https://api.mcsrvstat.us/icon/"+serverIP;
                presence.assets().setSmallImage(imageLink);
            }
            else
            {
                presence.assets().setSmallImage(name);
            }
        }
    }

    //checks if the image string passed in through advanced mode is a valid string
    //given the array holding all possible images
    public static boolean checkIfImageKeyIsValid(String imageKey, boolean inMultiplayer)
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
            else if(imageKey.equals("server") && inMultiplayer) //show the server icon, so force to be true
            {
                return true;
            }
            else
            {
                //LOGGER.info(imageKey + ", " + inMultiplayer);
                LOGGER.info("An image key for advanced mode is invalid, setting to fallback.");
                LOGGER.info("Invalid Image Key was..." + imageKey);
                return false;
            }
        }
    }

    //helper method to get the right translation key for multiplayer simple state
    public static String getSimpleMultiplayerKey(boolean gamePaused)
    {
        String base = "phases-discord-rich-presence.multiplayer.";

        if(PhaseDiscordConfig.enableServerName && PhaseDiscordConfig.enableServerPlayerCount)
        {
            if(gamePaused)
            {
                return base + "full.paused";
            }
            else
            {
                return base + "full";
            }
        }

        if(PhaseDiscordConfig.enableServerName)
        {
            if(gamePaused)
            {
                return base + "serverOnly.paused";
            }
            else
            {
                return base + "serverOnly";
            }
        }

        if(PhaseDiscordConfig.enableServerPlayerCount)
        {
            if(gamePaused)
            {
                return base + "playerCountOnly.paused";
            }
            else
            {
                return base + "playerCountOnly";
            }
        }

        if(gamePaused)
        {
            return base + "base.paused";
        }
        else
        {
            return base + "base";
        }
    }

    //helper method to get right arguments to pass in for multiplayer simple state
    public static Object[] getSimpleMultiplayerArgs(ServerData server, int playerCount)
    {
        String playerCountString = getPlayerCountStringPart(playerCount);

        if(PhaseDiscordConfig.enableServerName && PhaseDiscordConfig.enableServerPlayerCount)
        {
            return new Object[]{server.name, playerCountString};
        }

        if(PhaseDiscordConfig.enableServerName)
        {
            return new Object[]{server.name};
        }

        if(PhaseDiscordConfig.enableServerPlayerCount)
        {
            return new Object[]{playerCountString};
        }

        return new Object[]{}; //just in case
    }

    public static String getPlayerCountStringPart(int playerCount)
    {
        if(playerCount == 1) //playing by yourself
        {
            return Component.translatable("phases-discord-rich-presence.multiplayer.players.one").getString();
        }
        else //more players
        {
            return Component.translatable("phases-discord-rich-presence.multiplayer.players.other", playerCount).getString();
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

    //handles singleplayer presence logic
    public static void singleplayerPresenceLogic(Activity activity)
    {
        if(client.level != null)
        {
            String dimensionName = client.level.dimension().identifier().toString();
            //DimensionType dimensionType = client.world.getDimension();
            //String dimensionName = dimensionType.toString();
            //String dimensionName = dimensionType.effects().toString();
            //LOGGER.info(dimensionName);
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
                    updateSmallImageAdvancedMode(PhaseDiscordConfig.advancedModeLargePic, activity, false, "not used");
                }

                if(client.screen != null)
                {
                    activity.setState(PhaseDiscordConfig.mainAdvancedModeStateSingleplayerPause);
                }
                else
                {
                    activity.setState(PhaseDiscordConfig.mainAdvancedModeStateSingleplayer);
                }
                // set dimension stuff
                setDimensionKey(true, dimensionName, activity, false, "not used");
                //activity.assets().setSmallText(PhaseDiscordConfig.advancedModeLargeText);

            }
            else //simple mode
            {
                itemToDisplay = getHeldItem(false);
                activity.setDetails(itemToDisplay);

                setDimensionKey(false, dimensionName, activity, false, "not used");
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
                    activity.setState(Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField").getString());
                }
                else
                {
                    if(client.screen != null)
                    {
                        activity.setState(Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField").getString());
                    }
                    else
                    {
                        activity.setState(Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField").getString());
                    }
                }
            }

            //debug logging
            LoggerStuff(dimensionName, itemToDisplay, "singleplayer, not needed", activity);
        }
    }

    //handles multiplayer presence logic
    public static void multiplayerPresenceLogic(Activity activity)
    {
        ServerData server = client.getCurrentServer();
        String dimensionName = client.level.dimension().identifier().toString();
        //DimensionType dimensionType = client.world.getDimension();
        //String dimensionName = dimensionType.toString();
        //String dimensionName = dimensionType.effects().toString();
        //LOGGER.info(dimensionName);
        String itemToDisplay = "";
        String serverName = server.name;
        String serverIP = server.ip.toUpperCase();

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
                updateSmallImageAdvancedMode(PhaseDiscordConfig.advancedModeLargePic, activity, true, serverIP);
            }

            String stateParsed;
            if(client.screen != null)
            {
                stateParsed = PhaseDiscordConfig.mainAdvancedModeStateMultiplayerPause.replaceFirst("%s", serverIP);
                stateParsed = stateParsed.replaceFirst("%s", String.valueOf(client.level.players().size()));
                activity.setState(stateParsed);
            }
            else
            {
                stateParsed = PhaseDiscordConfig.mainAdvancedModeStateMultiplayer.replaceFirst("%s", serverIP);
                stateParsed = stateParsed.replaceFirst("%s", String.valueOf(client.level.players().size()));
                activity.setState(stateParsed);
            }

            // set dimension stuff
            setDimensionKey(true, dimensionName, activity, true, serverIP);
            //activity.assets().setSmallText(PhaseDiscordConfig.advancedModeLargeText);
        }
        else //simple mode
        {
            itemToDisplay = getHeldItem(false);
            activity.setDetails(itemToDisplay);
            if(PhaseDiscordConfig.enableServerIcon)
            {
                String imageLink = "https://api.mcsrvstat.us/icon/"+serverIP;
                activity.assets().setLargeImage(imageLink);

                String imageText = getSimpleMultiplayerKey(client.screen != null && PhaseDiscordConfig.showPaused);
                Object[] imageTextArgs = getSimpleMultiplayerArgs(server, client.level.players().size());

                activity.assets().setLargeText(Component.translatable(
                        imageText,
                        imageTextArgs
                ).getString());
            }
            else
            {
                setDimensionKey(false, dimensionName, activity, true, "not used");
            }

            if(PhaseDiscordConfig.showPlayerHeadAndUsername)
            {
                updatePlayerHead();
            }
            else
            {
                activity.assets().setSmallText("Phase's Minecraft Discord Rich Presence");
                activity.assets().setSmallImage("base");
            }

            String stateKey = getSimpleMultiplayerKey(client.screen != null && PhaseDiscordConfig.showPaused);
            Object[] args = getSimpleMultiplayerArgs(server, client.level.players().size());

            activity.setState(Component.translatable(
                    stateKey,
                    args
            ).getString());
        }

        //debug logging
        LoggerStuff(dimensionName, itemToDisplay, serverIP, activity);
    }

    public static void mainMenuPresenceLogic(Activity activity)
    {
        if(PhaseDiscordConfig.enableAdvancedMode) //advanced mode
        {

            //main menu large image error handle
            if(checkIfImageKeyIsValid(largeImageKey, false))
            {
                //activity.assets().setLargeImage("https://api.mcsrvstat.us/icon/mc.hypixel.net");
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
                activity.setDetails(Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField").getString());
            }

            activity.assets().setLargeText(PhaseDiscordConfig.advancedModeLargeText); //to be changed via options eventually
            activity.setState(PhaseDiscordConfig.advancedModeMainMenuState);
        }
        else //simple mode
        {
            activity.assets().setLargeImage("base");
            activity.setDetails(Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField").getString());
            activity.assets().setLargeText("Phase's Minecraft Discord Rich Presence");
            if(PhaseDiscordConfig.showPlayerHeadAndUsername)
            {
                updatePlayerHead();
                activity.assets().setSmallText(client.getUser().getName());
            }

            activity.setState("Phase's Minecraft Discord Rich Presence"); //maybe add an option to change this?
        }
    }
}
