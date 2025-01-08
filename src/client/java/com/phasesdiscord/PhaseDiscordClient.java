package com.phasesdiscord;

import club.minnced.discord.rpc.*;
import club.minnced.discord.rpc.DiscordEventHandlers;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static com.phasesdiscord.MenuIntegration.configScreenFactory;
import static phasesdiscordConfigStuff.PhaseDiscordConfig.*;
import static net.minecraft.resource.ResourceType.CLIENT_RESOURCES;

public class PhaseDiscordClient implements ClientModInitializer {
    DiscordRPC discord = DiscordRPC.INSTANCE; //discord rich presence instance
    String appID = "1147361100929708053"; //app id for discord, you should probably NOT change this
    String steamId = ""; //this is useless because minecraft isn't a steam game, this is just for the sake of
    // passing it in methods
    MinecraftClient client = MinecraftClient.getInstance(); //client instance
    public static boolean loadSuccess = false;

    DiscordEventHandlers handlers = new DiscordEventHandlers(); //discord event handler

    Timer t = new Timer();
    Long start_time = System.currentTimeMillis() / 1000;

    String customDimensionName;

    String imageNameOverworld;

    String imageNameNether;

    String imageNameEnd;

    String imageNameCustom;

    String largeImageKey;

    int amountOfPlayers;

    //this gets changed at runtime via config
    int discordPresenceUpdateRate = 5000;

    String imageKeyArray[] = {
            "overworld", "mountain", "swamp", "blankplains", "caveoverlookview", "mountainview",
            "pale_garden1", "pale_garden2", "pale_garden3", "shaders1", "shaders2", "trial_chamber",
            "nether", "nether2", "nether3", "nethercool",
            "the_end", "end2", "end3", "actualendbg",
            "void", "base", "creeper_icon", "fallback"
    };

    //logger
    public static final Logger LOGGER = LoggerFactory.getLogger("phases-discord-rich-presence");

    @Override
    public void onInitializeClient() {
        //config
        ResourceManagerHelper.get(CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            // After all the resource files have been loaded, the Modmenu configuration interface is provided so that the text translation works properly
            @Override
            public Identifier getFabricId() {
                return Identifier.of("phases-discord-rich-presence", "discord-rich-presence");}
            @Override
            public void reload(ResourceManager manager) {
                mainAdvancedModeDetail = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField","Playing Minecraft").getString();
                mainAdvancedModeDetailWhenHoldingItem = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField","Holding %s").getString();
                mainAdvancedModeStateMultiplayer = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField","Playing Multiplayer on %s with %s players").getString();
                mainAdvancedModeStateMultiplayerPause = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseTextField","Playing multiplayer on %s with %s players - Paused").getString();
                mainAdvancedModeStateSingleplayer = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField","Playing Singleplayer").getString();
                mainAdvancedModeStateSingleplayerPause = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField","Playing Singleplayer - Paused").getString();
                advancedModeDimensionOverworld = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField","In The Overworld").getString();
                advancedModeDimensionNether = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField","In The Nether").getString();
                advancedModeDimensionEnd = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField","In The End").getString();
                advancedModeDimensionCustom = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionCustomTextField","In %s Dimension").getString();
                advancedModeMainMenuText = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField","Main Menu").getString();

                MidnightConfig.init("phases-discord-rich-presence", PhaseDiscordConfig.class);
                configScreenFactory = parent -> MidnightConfig.getScreen(parent, "phases-discord-rich-presence");
                loadSuccess = true;}});

        handlers.ready = (user) -> LOGGER.info(
                "Phase's Discord Rich Presence Client is ready!"
                        );
        /*
        discord.Discord_Initialize(appID, handlers, true, steamId);

        discordPresenceUpdateRate = PhaseDiscordConfig.discordRichPresenceUpdateRate;

        basicDiscordPresence();
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                discord.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }, "RPC-Callback-Handler").start();

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                updateDiscordPresence();
            }
        }, discordPresenceUpdateRate, discordPresenceUpdateRate);
        */
    }


    private void basicDiscordPresence() {
        DiscordRichPresence presence = new DiscordRichPresence();

        if(PhaseDiscordConfig.discordEnable == true)
        {
            presence.largeImageKey = largeImageKey;
        }
        else
        {
            presence.largeImageKey = "testicon1";
        }

        presence.details = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField","Main Menu").getString(); //presence details
         //large image key for an icon, the thing inside must be uploaded
        // to discord application's rich presence assets
        presence.largeImageText = "Phase's Minecraft Discord Rich Presence"; //large image text when hovered

        presence.instance = 1; //i'm going to be honest, idk what this does
        discord.Discord_UpdatePresence(presence); //update discord presence
    }

    private void updateDiscordPresence() {

        imageNameOverworld = PhaseDiscordConfig.advancedModeOverworldPic;
        imageNameNether = PhaseDiscordConfig.advancedModeNetherPic;
        imageNameEnd = PhaseDiscordConfig.advancedModeEndPic;
        imageNameCustom = PhaseDiscordConfig.advancedModeCustomPic;
        largeImageKey = PhaseDiscordConfig.advancedModeLargePic;

        //get the amount of players
        if(client.world != null)
        {
            amountOfPlayers = client.world.getPlayers().size() - 1;
        }
        else
        {
            amountOfPlayers = 0;
        }

        if(PhaseDiscordConfig.discordEnable == false) {
            discord.Discord_ClearPresence();
        }
        else if(PhaseDiscordConfig.enableAdvancedMode == true)
        {
            advancedDiscordPresence();
        }
        else if (PhaseDiscordConfig.enableAdvancedMode == false) {
            simpleDiscordPresence();
        }
        else {
            basicDiscordPresence();
        }
    }

    private void simpleDiscordPresence()
    {
        if (client.world != null) {
            boolean inSingleplayer = client.isInSingleplayer();
            DimensionType dimensionType = client.world.getDimension();
            String dimensionName = dimensionType.effects().toString();

            DiscordRichPresence presence = new DiscordRichPresence();
            
            //check if player holds something, update presence from there
            if (client.player != null) {
                if (PhaseDiscordConfig.enableItem == false) {
                    presence.details = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField","Playing Minecraft").getString();
                } else {
                    ItemStack held_item = client.player.getStackInHand(Hand.MAIN_HAND);
                    String item_name = held_item.getName().getString();
                    if (!item_name.equals(Items.AIR.getName().getString())) {
                        presence.details = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField","Holding" + item_name, item_name).getString();
                    }
                }

            }

            //presence start stuff
            presence.startTimestamp = start_time;
            presence.largeImageKey = "base"; //change icon for when in a world
            presence.largeImageText = "Phase's Minecraft Discord Rich Presence";
            presence.instance = 1; //still no clue what this means

            //all of this stuff here is useless
            presence.partyId = "priv_party";
            presence.matchSecret = "abXyyz";
            presence.joinSecret = "moonSqikCklaw";
            presence.spectateSecret = "moonSqikCklawkLopalwdNq";

            //presence state checks
            if (!inSingleplayer) {
                String serverip = "";
                if (client.getCurrentServerEntry() != null) {
                    serverip = client.getCurrentServerEntry().address.toUpperCase();
                }
                if (PhaseDiscordConfig.enableServerIP == false) {
                    if (PhaseDiscordConfig.showPaused == false) {
                        if(PhaseDiscordConfig.enableServerPlayerCount == true)
                        {
                            /*
                            ip - no
                            paused - no
                            player count - yes
                             */
                            presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerNoIP","Playing Multiplayer with " + " players", String.valueOf(amountOfPlayers)).getString();
                        }
                        else
                        {
                            /*
                            ip - no
                            paused - no
                            player count - no
                             */
                            presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerBasic","Playing Multiplayer").getString();
                        }
                    } else {
                        if (client.currentScreen != null) { // client.currentScreen is not null when paused
                            /*
                            ip - no
                            paused - yes
                             */
                            presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseBasic","Playing multiplayer - Paused").getString();
                        } else {
                            if(PhaseDiscordConfig.enableServerPlayerCount == true)
                            {
                                /*
                                ip - no
                                paused - no
                                player count - yes
                                 */
                                presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerNoIP","Playing Multiplayer with " + " players", String.valueOf(amountOfPlayers)).getString();
                            }
                            else {
                                /*
                                ip - no
                                paused - no
                                player count - no
                                 */
                                presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerBasic","Playing Multiplayer").getString();
                            }
                        }
                    }
                    presence.details = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerBasic","Playing Multiplayer").getString();
                } else {
                    if (PhaseDiscordConfig.showPaused == false) {
                        if(PhaseDiscordConfig.enableServerPlayerCount == true)
                        {
                            /*
                            ip - yes
                            paused - no
                            player count - yes
                             */
                            presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField","Playing Multiplayer on " + serverip + " with " + amountOfPlayers + " players", serverip, String.valueOf(amountOfPlayers)).getString();
                        }
                        else
                        {
                            /*
                            ip - yes
                            paused - no
                            player count - no
                             */
                            presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerNoAmount","Playing multiplayer on " + serverip, serverip).getString();
                        }
                    } else {
                        if (client.currentScreen != null) {
                            /*
                            ip - yes
                            paused - yes
                             */
                            presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseNoAmount","Playing Multiplayer on " + serverip +  " - Paused", serverip).getString();
                        } else {
                            if(PhaseDiscordConfig.enableServerPlayerCount == true)
                            {
                                /*
                                ip - yes
                                paused - no
                                player count - yes
                                 */
                                presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField","Playing Multiplayer on " + serverip + " with " + amountOfPlayers + " players", serverip, String.valueOf(amountOfPlayers)).getString();
                            }
                            else {
                                /*
                                ip - yes
                                paused - no
                                player count - no
                                 */
                                presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerNoAmount","Playing multiplayer on " + serverip, serverip).getString();
                            }
                        }
                    }
                    //presence.state = "Playing Multiplayer on " + serverip;
                }

                discord.Discord_UpdatePresence(presence);
            } else //means in singeplayer
            {
                if (PhaseDiscordConfig.showPaused == false) {
                    presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField","Playing Singleplayer").getString();
                } else {
                    if (client.currentScreen != null) {
                        presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField","Playing Singleplayer - Paused").getString();
                    } else {
                        presence.state = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField","Playing Singleplayer").getString();
                    }
                }
                //presence.state = "Playing Singleplayer";
            }


            //dimension checks
            if (dimensionName.equals("minecraft:overworld")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    //empty
                } else {
                    presence.smallImageKey = "overworld";
                    presence.smallImageText = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField","In The Overworld").getString();
                }
            } else if (dimensionName.equals("minecraft:the_nether")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    //empty
                } else {
                    presence.smallImageKey = "nether";
                    presence.smallImageText = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField","In The Nether").getString();
                }
            } else if (dimensionName.equals("minecraft:the_end")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    //empty
                } else {
                    presence.smallImageKey = "the_end";
                    presence.smallImageText = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField","In The End").getString();
                }
            } else {
                if (PhaseDiscordConfig.enableDimension == false) {
                    //empty
                } else {
                    if (PhaseDiscordConfig.enableCustomDimensionSupport == false) {
                        //empty
                    } else {
                        //System.out.println(dimensionName + " why is this not working");
                        customDimensionName = dimensionName.replace("minecraft:", "");
                        presence.smallImageKey = "void";
                        presence.smallImageText = "In " + customDimensionName + " Dimension";
                    }
                }
            }

            discord.Discord_UpdatePresence(presence);

            if(PhaseDiscordConfig.enableDebug == true)
            {
                LOGGER.info("Basic Presence Updated");
            }
        }
    }

    private void advancedDiscordPresence()
    {
        if(client.world != null)
        {
            boolean inSingleplayer = client.isInSingleplayer();
            DimensionType dimensionType = client.world.getDimension();
            String dimensionName = dimensionType.effects().toString();
            String item_name = "";
            String serverip = "";

            DiscordRichPresence presence = new DiscordRichPresence();

            if(client.player != null)
            {
                ItemStack held_item = client.player.getStackInHand(Hand.MAIN_HAND);
                item_name = held_item.getName().getString();
                if(!item_name.equals(Items.AIR.getName().getString()))
                {
                    presence.details = PhaseDiscordConfig.mainAdvancedModeDetailWhenHoldingItem.replace("%s", item_name);
                }
                else
                {
                    presence.details = PhaseDiscordConfig.mainAdvancedModeDetail;
                }
            }


            //presence start stuff
            presence.startTimestamp = start_time;
            if(checkIfImageKeyIsValid(largeImageKey) == false)
            {
                presence.largeImageKey = "fallback"; //change icon for when in a world
            }
            else
            {
                presence.largeImageKey = largeImageKey; //change icon for when in a world
            }
            presence.largeImageText = "Phase's Minecraft Discord Rich Presence";
            presence.instance = 1; //still no clue what this means

            //all of this stuff here is useless
            presence.partyId = "priv_party";
            presence.matchSecret = "abXyyz";
            presence.joinSecret = "moonSqikCklaw";
            presence.spectateSecret = "moonSqikCklawkLopalwdNq";

            if(!inSingleplayer) //in multiplayer
            {
                //serverip = client.getCurrentServerEntry().address.toUpperCase();
                if(client.getCurrentServerEntry() != null)
                {
                    serverip = client.getCurrentServerEntry().address.toUpperCase();
                }
                if(client.currentScreen != null)
                {
                    presence.state = PhaseDiscordConfig.mainAdvancedModeStateMultiplayerPause.replaceFirst("%s", serverip);
                    presence.state = presence.state.replace("%s", String.valueOf(amountOfPlayers));
                }
                else
                {
                    presence.state = PhaseDiscordConfig.mainAdvancedModeStateMultiplayer.replaceFirst("%s", serverip);
                    presence.state = presence.state.replace("%s", String.valueOf(amountOfPlayers));
                }
            }
            else //in singleplayer
            {
                if(client.currentScreen != null)
                {
                    presence.state = PhaseDiscordConfig.mainAdvancedModeStateSingleplayerPause;
                }
                else
                {
                    presence.state = PhaseDiscordConfig.mainAdvancedModeStateSingleplayer;
                }
            }



            if(dimensionName.equals("minecraft:overworld"))
            {
                if(checkIfImageKeyIsValid(imageNameOverworld) == false)
                {
                    presence.smallImageKey = "fallback";
                }
                else
                {
                    presence.smallImageKey = imageNameOverworld;
                }

                presence.smallImageText = PhaseDiscordConfig.advancedModeDimensionOverworld;
            }
            else if(dimensionName.equals("minecraft:the_nether"))
            {
                if(checkIfImageKeyIsValid(imageNameNether) == false)
                {
                    presence.smallImageKey = "fallback";
                }
                else
                {
                    presence.smallImageKey = imageNameNether;
                }

                presence.smallImageText = PhaseDiscordConfig.advancedModeDimensionNether;
            }
            else if(dimensionName.equals("minecraft:the_end"))
            {
                if(checkIfImageKeyIsValid(imageNameEnd) == false)
                {
                    presence.smallImageKey = "fallback";
                }
                else
                {
                    presence.smallImageKey = imageNameEnd;
                }

                presence.smallImageText = PhaseDiscordConfig.advancedModeDimensionEnd;
            }
            else
            {
                customDimensionName = dimensionName.replace("minecraft:", "");
                if(checkIfImageKeyIsValid(imageNameCustom) == false)
                {
                    presence.smallImageKey = "fallback";
                }
                else
                {
                    presence.smallImageKey = imageNameCustom;
                }
                presence.smallImageText = PhaseDiscordConfig.advancedModeDimensionCustom.replace("{dimension_name}", customDimensionName);
            }

            if(PhaseDiscordConfig.enableDebug == true)
            {
                LOGGER.info("Advanced Mode Variables");
                LOGGER.info("Dimension Name - " + dimensionName);
                LOGGER.info("Item Name - " + item_name);
                LOGGER.info("Server IP - " + serverip);
                LOGGER.info("Current Presence Details - " + presence.details);
                LOGGER.info("Current Presence State - " + presence.state);
            }

            discord.Discord_UpdatePresence(presence);
        }
        else
        {
            LOGGER.info("World is null, cannot update presence.");
            DiscordRichPresence presence = new DiscordRichPresence();

            presence.details = PhaseDiscordConfig.advancedModeMainMenuText;
            if(checkIfImageKeyIsValid(largeImageKey) == false) {
                presence.largeImageKey = "fallback";
            }
            else
            {
                presence.largeImageKey = largeImageKey; //large image key for an icon, the thing inside must be uploaded
            }

            // to discord application's rich presence assets
            presence.largeImageText = "Phase's Minecraft Discord Rich Presence"; //large image text when hovered

            presence.instance = 1; //i'm going to be honest, idk what this does

            discord.Discord_UpdatePresence(presence); //update discord presence
        }
    }

    public boolean checkIfImageKeyIsValid(String imageKey)
    {
        if(Arrays.stream(imageKeyArray).anyMatch(imageKey::equals))
        {
            return true;
        }
        else
        {
            LOGGER.info("An image key for advanced mode is invalid, setting to fallback.");
            LOGGER.info("Invalid Image Key - " + imageKey);
            return false;
        }
    }
}