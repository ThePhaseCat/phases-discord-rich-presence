package com.phasesdiscord;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.ActivityType;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.Activity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import java.time.Instant;
import java.time.OffsetDateTime;
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
            "sulfur_cube1", "sulfur_cube2", "sulfur_cave1", "sulfur_cave2", "sulfur_cave3",
            "dappled1", "dappled2", "dappled3", "dappled4", "dappled5", "cushion1", "cushion2",
            "nether", "nether2", "nether3", "nethercool",
            "the_end", "end2", "end3", "actualendbg",
            "void", "base", "base_old", "creeper_icon", "fallback", "pack"
    };

    static long defaultAppID = 1147361100929708053L; //in order to check if the id matches
    static boolean usingDefaultAppID = true; //if the app id is the default one, then we can use the default image keys

    //new discord rpc stuff
    private static IPCClient ipcClient;
    private static volatile boolean ready = false;
    private static volatile boolean disconnected = false;
    private static long startTime;

    //to do, fix this
    public static void start() {
        if(!PhaseDiscordConfig.discordEnable)
        {
            return; //they don't want to use my mod :(
        }

        Thread discordThread = new Thread(() -> {
            long finalAppID = Long.parseLong(PhaseDiscordConfig.discordAppID);
            if(finalAppID == defaultAppID)
            {
                usingDefaultAppID = true;
            }
            else
            {
                usingDefaultAppID = false;
            }
            ipcClient = new IPCClient(finalAppID);

            ipcClient.setListener(new IPCListener() {
                @Override
                public void onPacketSent(IPCClient ipcClient, Packet packet) {

                }

                @Override
                public void onPacketReceived(IPCClient ipcClient, Packet packet) {

                }

                @Override
                public void onActivityJoin(IPCClient ipcClient, String s) {

                }

                @Override
                public void onActivitySpectate(IPCClient ipcClient, String s) {

                }

                @Override
                public void onActivityJoinRequest(IPCClient ipcClient, String s, User user) {

                }

                @Override
                public void onReady(IPCClient ipcClient) {
                    ready = true;
                }

                @Override
                public void onClose(IPCClient ipcClient, JsonObject jsonObject) {

                }

                @Override
                public void onDisconnect(IPCClient ipcClient, Throwable throwable) {

                }
            });

            try
            {
                ipcClient.connect();
                startTime = System.currentTimeMillis()/1000L;
            } catch (NoDiscordClientException e) {
                LOGGER.error("failed to connect to discord", e);
            }

            while(!Thread.currentThread().isInterrupted())
            {
                try
                {
                    if(ready)
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

                        RichPresence.Builder builder = new RichPresence.Builder();
                        builder.setActivityType(ActivityType.Playing).setStartTimestamp(startTime);

                        if(client.isLocalServer()) //singleplayer presence
                        {
                            singleplayerMainPresenceLogic(builder);
                        }
                        else if(client.getCurrentServer() != null) //multiplayer presence
                        {
                            multiplayerMainPresenceLogic(builder);
                        }
                        else //main menu presence
                        {
                            mainMenuMainPresenceLogic(builder);
                        }

                        ipcClient.sendRichPresence(builder.build());
                    }
                    Thread.sleep(PhaseDiscordConfig.discordRichPresenceUpdateRate);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    LOGGER.error("failed to update discord presence", e);
                }
            }
            ipcClient.close();
        });
        discordThread.setDaemon(true);
        discordThread.start();
    }

    private static IPCListener createListener()
    {
        return new IPCListener() {
            @Override
            public void onPacketSent(IPCClient ipcClient, Packet packet) {

            }

            @Override
            public void onPacketReceived(IPCClient ipcClient, Packet packet) {

            }

            @Override
            public void onActivityJoin(IPCClient ipcClient, String s) {

            }

            @Override
            public void onActivitySpectate(IPCClient ipcClient, String s) {

            }

            @Override
            public void onActivityJoinRequest(IPCClient ipcClient, String s, User user) {

            }

            @Override
            public void onReady(IPCClient ipcClient) {
                ready = true;
            }

            @Override
            public void onClose(IPCClient ipcClient, JsonObject jsonObject) {
                ready = false;
                disconnected = true;
            }

            @Override
            public void onDisconnect(IPCClient ipcClient, Throwable throwable) {
                ready = false;
                disconnected = true;
            }
        };
    }

    //these following methods are adopted from the Fabric-DiscordRPC repo (https://github.com/larroxtv/Fabric-DiscordRPC)
    //updates player head
    private static void updatePlayerHead(RichPresence.Builder builder)
    {
        String uuid = client.getGameProfile().id().toString();
        String playerHeadImage = getPlayerHeadURL(uuid, "head", 3);
        builder.setSmallImage(playerHeadImage, client.getUser().getName());
    }

    //fetches the image URL for the player head
    private static @NotNull String getPlayerHeadURL(String uuid, String type, int size)
    {
        return PhaseDiscordConfig.playerHeadServiceURL.replace("%uuid", uuid);
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
                    finalResult = finalResult.replace("%item", item_name);
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
                        finalResult = Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField").getString().replace("%item", item_name);
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

    //helper function to get the advanced mode image key based on passed in values
    private static String getAdvancedModeImageKey(boolean inMultiplayer, String multiplayerKey, String singleplayerKey, String serverIP)
    {
        if(inMultiplayer)
        {
            if(!checkIfImageKeyIsValid(multiplayerKey, true))
            {
                return "fallback";
            }
            else if(multiplayerKey.equals("server"))
            {
                return "https://api.mcsrvstat.us/icon/" + serverIP;
            }
            else
            {
                return multiplayerKey;
            }
        }
        else //so singleplayer
        {
            if(!checkIfImageKeyIsValid(singleplayerKey, false))
            {
                return "fallback";
            }
            else
            {
                return singleplayerKey;
            }
        }
    }

    //sets the assets in the presence to the correct dimension
    public static void setDimensionKey(boolean inAdvancedMode, String dimensionName, RichPresence.Builder builder, boolean inMultiplayer, String serverIP)
    {
        String largeImageKey = "";
        String largeImageText = "";

        if(inAdvancedMode)
        {
            switch(dimensionName)
            {
                case "minecraft:overworld":
                    largeImageKey = getAdvancedModeImageKey(inMultiplayer, imageNameOverworldMutliplayer, imageNameOverworldSingleplayer, serverIP);
                    largeImageText = PhaseDiscordConfig.advancedModeDimensionOverworld;
                    break;
                case "minecraft:the_nether":
                    largeImageKey = getAdvancedModeImageKey(inMultiplayer, imageNameNetherMutliplayer, imageNameNetherSingleplayer, serverIP);
                    largeImageText = PhaseDiscordConfig.advancedModeDimensionNether;
                    break;
                case "minecraft:the_end":
                    largeImageKey = getAdvancedModeImageKey(inMultiplayer, imageNameEndMutliplayer, imageNameEndSingleplayer, serverIP);
                    largeImageText = PhaseDiscordConfig.advancedModeDimensionEnd;
                    break;
                default: //custom dimension
                    customDimensionName = dimensionName.replace("minecraft:", "");
                    largeImageKey = getAdvancedModeImageKey(inMultiplayer, imageNameCustomMutliplayer, imageNameCustomSingleplayer, serverIP);
                    largeImageText = PhaseDiscordConfig.advancedModeDimensionCustom.replace("{dimension_name}", customDimensionName);
                    break;
            }
        }
        else
        {
            if(!PhaseDiscordConfig.enableDimension)
            {
                largeImageKey = "void";
                largeImageText = "Minecraft";
            }
            else
            {
                switch(dimensionName)
                {
                    case "minecraft:overworld":
                        largeImageKey = "overworld";
                        largeImageText = Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField").getString();
                        break;
                    case "minecraft:the_nether":
                        largeImageKey = "nether";
                        largeImageText = Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField").getString();
                        break;
                    case "minecraft:the_end":
                        largeImageKey = "end";
                        largeImageText = Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField").getString();
                        break;
                    default: //custom dimension
                        largeImageKey = "void";
                        if(!PhaseDiscordConfig.enableCustomDimensionSupport)
                        {
                            largeImageText = "In ???";
                        }
                        else
                        {
                            customDimensionName = dimensionName.replace("minecraft:", "");
                            largeImageText = "In " + customDimensionName + " Dimension";
                        }
                        break;
                }
            }
        }

        builder.setLargeImage(largeImageKey, largeImageText);
    }

    //updates the small image of advanced mode
    public static void updateSmallImageAdvancedMode(String name, RichPresence.Builder builder, boolean inMultiplayer, String serverIP)
    {
        if(checkIfImageKeyIsValid(name, inMultiplayer) == false)
        {
            builder.setSmallImage("fallback");
        }
        else
        {
            if(name.equals("server") && inMultiplayer)
            {
                String imageLink = "https://api.mcsrvstat.us/icon/"+serverIP;
                builder.setSmallImage(imageLink);
            }
            else
            {
                builder.setSmallImage(name);
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
            String baseString = Component.translatable("phases-discord-rich-presence.multiplayer.players.other").getString();
            return baseString.replaceFirst("%playercount", String.valueOf(playerCount));
        }
    }

    //prints out debug stuff, mainly meant for advanced mode debugging
    public static void LoggerStuff(String dimName, String item_name, String serverip)
    {
        if(PhaseDiscordConfig.enableDebug == true)
        {
            LOGGER.info("Advanced Mode Variables");
            LOGGER.info("Dimension Name - " + dimName);
            LOGGER.info("Item Name - " + item_name);
            LOGGER.info("Server IP - " + serverip);
            //LOGGER.info("Current Presence Details - " + builder.);
            //LOGGER.info("Current Presence State - " + activity.getState());
        }
    }

    //handles singleplayer presence logic
    public static void singleplayerMainPresenceLogic(RichPresence.Builder builder)
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
                singleplayerAdvanced(builder, dimensionName);
            }
            else //simple mode
            {
                singleplayerBasic(builder, dimensionName);
            }
        }
    }

    public static void singleplayerBasic(RichPresence.Builder builder, String dimensionName)
    {
        String itemToDisplay = getHeldItem(false);
        builder.setDetails(itemToDisplay);

        setDimensionKey(false, dimensionName, builder, false, "not used");
        if(PhaseDiscordConfig.showPlayerHeadAndUsername)
        {
            updatePlayerHead(builder);
        }
        else
        {
            builder.setSmallImage("base", "Phase's Minecraft Discord Rich Presnece");
        }

        if(!PhaseDiscordConfig.showPaused)
        {
            builder.setState(Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField").getString());
        }
        else
        {
            if(client.isPaused())
            {
                builder.setState(Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField").getString());
            }
            else
            {
                builder.setState(Component.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField").getString());
            }
        }

        //debug logging
        LoggerStuff(dimensionName, itemToDisplay, "singleplayer, not needed");
    }

    public static void singleplayerAdvanced(RichPresence.Builder builder, String dimensionName)
    {
        String itemToDisplay = getHeldItem(true);
        builder.setDetails(itemToDisplay);

        if(PhaseDiscordConfig.advancedModeShowPlayerHeadUser)
        {
            updatePlayerHead(builder);
        }
        else
        {
            updateSmallImageAdvancedMode(PhaseDiscordConfig.advancedModeLargePic, builder, false, "not used");
        }

        if(client.isPaused())
        {
            builder.setState(PhaseDiscordConfig.mainAdvancedModeStateSingleplayerPause);
        }
        else
        {
            builder.setState(PhaseDiscordConfig.mainAdvancedModeStateSingleplayer);
        }
        //set dimension stuff
        setDimensionKey(true, dimensionName, builder, false, "not used");

        //debug logging
        LoggerStuff(dimensionName, itemToDisplay, "singleplayer, not needed");
    }

    //handles multiplayer presence logic
    public static void multiplayerMainPresenceLogic(RichPresence.Builder builder)
    {
        ServerData server = client.getCurrentServer();
        String dimensionName = client.level.dimension().identifier().toString();
        //DimensionType dimensionType = client.world.getDimension();
        //String dimensionName = dimensionType.toString();
        //String dimensionName = dimensionType.effects().toString();
        //LOGGER.info(dimensionName
        String serverName = server.name;
        String serverIP = server.ip.toUpperCase();

        if(PhaseDiscordConfig.enableAdvancedMode)
        {
            multiplayerAdvanced(builder, dimensionName, serverName, serverIP);
        }
        else //simple mode
        {
            multiplayerBasic(builder, server, dimensionName, serverName, serverIP);
        }
    }

    public static void multiplayerBasic(RichPresence.Builder builder, ServerData server, String dimensionName, String serverName, String serverIP)
    {
        String itemToDisplay = getHeldItem(false);
        builder.setDetails(itemToDisplay);
        if(PhaseDiscordConfig.enableServerIcon)
        {
            String imageLink = "https://api.mcsrvstat.us/icon/"+serverIP;

            String imageText = getSimpleMultiplayerKey(client.isPaused() && PhaseDiscordConfig.showPaused);
            Object[] imageTextArgs = getSimpleMultiplayerArgs(server, client.level.players().size());

            String imageParsed = Component.translatable(imageText).getString();
            if(PhaseDiscordConfig.enableServerName && PhaseDiscordConfig.enableServerPlayerCount)
            {
                imageParsed = imageParsed.replace("%serverip", String.valueOf(imageTextArgs[0])).replace("%playercount", String.valueOf(imageTextArgs[1]));
            }
            else if(PhaseDiscordConfig.enableServerName)
            {
                imageParsed = imageParsed.replace("%serverip", String.valueOf(imageTextArgs[0]));
            }
            else if(PhaseDiscordConfig.enableServerPlayerCount)
            {
                imageParsed = imageParsed.replace("%playercount", String.valueOf(imageTextArgs[0]));
            }

            builder.setLargeImage(imageLink, Component.translatable(imageParsed, imageTextArgs).getString());
        }
        else
        {
            setDimensionKey(false, dimensionName, builder, true, "not used");
        }

        if(PhaseDiscordConfig.showPlayerHeadAndUsername)
        {
            updatePlayerHead(builder);
        }
        else
        {
            builder.setSmallImage("base", "Phase's Minecraft Discord Rich Presence");
        }

        String stateKey = getSimpleMultiplayerKey(client.isPaused() && PhaseDiscordConfig.showPaused);
        Object[] args = getSimpleMultiplayerArgs(server, client.level.players().size());

        String stateParsed = Component.translatable(stateKey).getString();
        if(PhaseDiscordConfig.enableServerName && PhaseDiscordConfig.enableServerPlayerCount)
        {
            stateParsed = stateParsed.replace("%serverip", String.valueOf(args[0])).replace("%playercount", String.valueOf(args[1]));
        }
        else if(PhaseDiscordConfig.enableServerName)
        {
            stateParsed = stateParsed.replace("%serverip", String.valueOf(args[0]));
        }
        else if(PhaseDiscordConfig.enableServerPlayerCount)
        {
            stateParsed = stateParsed.replace("%playercount", String.valueOf(args[0]));
        }

        builder.setState(Component.translatable(stateParsed, args).getString());

        //debug logging
        LoggerStuff(dimensionName, itemToDisplay, serverIP);
    }

    public static void multiplayerAdvanced(RichPresence.Builder builder, String dimensionName, String serverName, String serverIP)
    {
        String itemToDisplay = getHeldItem(true);
        builder.setDetails(itemToDisplay);

        if(PhaseDiscordConfig.advancedModeShowPlayerHeadUser)
        {
            updatePlayerHead(builder);
        }
        else
        {
            updateSmallImageAdvancedMode(PhaseDiscordConfig.advancedModeLargePic, builder, true, serverIP);
        }

        String stateParsed;
        if(client.isPaused())
        {
            stateParsed = PhaseDiscordConfig.mainAdvancedModeStateMultiplayerPause.replaceFirst("%serverip", serverIP);
            stateParsed = stateParsed.replaceFirst("%playercount", String.valueOf(client.level.players().size()));
            stateParsed = stateParsed.replaceFirst("%servername", serverName);
            stateParsed = stateParsed.replaceFirst("%rpc", serverIP);
            stateParsed = stateParsed.replaceFirst("%rpc", String.valueOf(client.level.players().size()));
            builder.setState(stateParsed);
        }
        else
        {
            stateParsed = PhaseDiscordConfig.mainAdvancedModeStateMultiplayer.replaceFirst("%serverip", serverIP);
            stateParsed = stateParsed.replaceFirst("%playercount", String.valueOf(client.level.players().size()));
            stateParsed = stateParsed.replaceFirst("%servername", serverName);
            stateParsed = stateParsed.replaceFirst("%rpc", serverIP);
            stateParsed = stateParsed.replaceFirst("%rpc", String.valueOf(client.level.players().size()));
            builder.setState(stateParsed);
        }

        //set dimension stuff
        setDimensionKey(true, dimensionName, builder, true, serverIP);

        //debug logging
        LoggerStuff(dimensionName, itemToDisplay, serverIP);
    }

    public static void mainMenuMainPresenceLogic(RichPresence.Builder builder)
    {
        if(PhaseDiscordConfig.enableAdvancedMode) //advanced mode
        {
            mainMenuAdvanced(builder);
        }
        else //simple mode
        {
            mainMenuBasic(builder);
        }
    }

    public static void mainMenuBasic(RichPresence.Builder builder)
    {
        builder.setLargeImage("base", "Phase's Minecraft Discord Rich Presence");
        builder.setDetails(Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField").getString());
        if(PhaseDiscordConfig.showPlayerHeadAndUsername)
        {
            updatePlayerHead(builder);
        }
        builder.setState("Phase's Minecraft Discord Rich Presence");
    }

    public static void mainMenuAdvanced(RichPresence.Builder builder)
    {
        //main menu large image error handle
        if(checkIfImageKeyIsValid(largeImageKey, false))
        {
            //activity.assets().setLargeImage("https://api.mcsrvstat.us/icon/mc.hypixel.net");
            builder.setLargeImage(largeImageKey, PhaseDiscordConfig.advancedModeLargeText);
        }
        else
        {
            builder.setLargeImage("fallback", PhaseDiscordConfig.advancedModeLargeText);
        }

        if(PhaseDiscordConfig.advancedModeChangeMainMenuText)
        {
            builder.setDetails(PhaseDiscordConfig.advancedModeMainMenuText);
        }
        else
        {
            builder.setDetails(Component.translatable("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField").getString());
        }

        builder.setState(PhaseDiscordConfig.advancedModeMainMenuState);
    }
}
