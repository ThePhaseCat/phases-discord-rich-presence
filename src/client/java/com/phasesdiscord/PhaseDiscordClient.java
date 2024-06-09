package com.phasesdiscord;

import club.minnced.discord.rpc.*;
import club.minnced.discord.rpc.DiscordEventHandlers;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.dimension.DimensionType;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import java.util.Timer;
import java.util.TimerTask;

public class PhaseDiscordClient implements ClientModInitializer {
    DiscordRPC discord = DiscordRPC.INSTANCE; //discord rich presence instance
    String appID = "1147361100929708053"; //app id for discord, you should probably NOT change this
    String steamId = ""; //this is useless because minecraft isn't a steam game, this is just for the sake of
    // passing it in methods

    MinecraftClient client = MinecraftClient.getInstance(); //client instance

    DiscordEventHandlers handlers = new DiscordEventHandlers(); //discord event handler

    Timer t = new Timer();
    Long start_time = System.currentTimeMillis() / 1000;

    String customDimensionName;

    String imageNameOverworld;

    String imageNameNether;

    String imageNameEnd;

    String imageNameCustom;

    String largeImageKey;


    @Override
    public void onInitializeClient() {
        //config
        MidnightConfig.init("phases-discord-rich-presence", PhaseDiscordConfig.class);

        handlers.ready = (user) -> System.out.println("Phase's Discord Rich Presence is ready!");
        discord.Discord_Initialize(appID, handlers, true, steamId);

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
        }, 5000, 5000);
    }


    private void basicDiscordPresence() {
        DiscordRichPresence presence = new DiscordRichPresence();

        presence.details = "Main Menu"; //presence details
        presence.largeImageKey = "testicon1"; //large image key for an icon, the thing inside must be uploaded
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

        if(PhaseDiscordConfig.discordEnable == false) {
            discord.Discord_ClearPresence();
            return;
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
                    presence.details = "Playing Minecraft";
                } else {
                    ItemStack held_item = client.player.getStackInHand(Hand.MAIN_HAND);
                    String item_name = held_item.getName().getString();
                    if (!item_name.equals("Air")) {
                        presence.details = "Holding " + item_name;
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
                        presence.state = "Playing Multiplayer";
                    } else {
                        if (client.isPaused()) {
                            presence.state = "Playing Multiplayer - Paused";
                        } else {
                            presence.state = "Playing Multiplayer";
                        }
                    }
                    presence.state = "Playing Multiplayer";
                } else {
                    if (PhaseDiscordConfig.showPaused == false) {
                        presence.state = "Playing Multiplayer on " + serverip;
                    } else {
                        if (client.isPaused()) {
                            presence.state = "Playing Multiplayer on " + serverip + " - Paused";
                        } else {
                            presence.state = "Playing Multiplayer on " + serverip;
                        }
                    }
                    //presence.state = "Playing Multiplayer on " + serverip;
                }
                presence.partySize = 1;
                presence.partyMax = 1;
                discord.Discord_UpdatePresence(presence);
            } else //means in singeplayer
            {
                if (PhaseDiscordConfig.showPaused == false) {
                    presence.state = "Playing Singleplayer";
                } else {
                    if (client.isPaused()) {
                        presence.state = "Playing Singleplayer - Paused";
                    } else {
                        presence.state = "Playing Singleplayer";
                    }
                }
                //presence.state = "Playing Singleplayer";
                presence.partySize = 1;
                presence.partyMax = 1;
            }


            //dimension checks
            if (dimensionName.equals("minecraft:overworld")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    //empty
                } else {
                    presence.smallImageKey = "overworld";
                    presence.smallImageText = "In The Overworld";
                }
            } else if (dimensionName.equals("minecraft:the_nether")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    //empty
                } else {
                    presence.smallImageKey = "nether";
                    presence.smallImageText = "In The Nether";
                }
            } else if (dimensionName.equals("minecraft:the_end")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    //empty
                } else {
                    presence.smallImageKey = "the_end";
                    presence.smallImageText = "In The End";
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
                System.out.println("Basic Presence Updated");
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
                if(!item_name.equals("Air"))
                {
                    presence.details = PhaseDiscordConfig.mainAdvancedModeDetailWhenHoldingItem.replace("{item_name}", item_name);
                }
                else
                {
                    presence.details = PhaseDiscordConfig.mainAdvancedModeDetail;
                }
            }

            //presence start stuff
            presence.startTimestamp = start_time;
            presence.largeImageKey = largeImageKey; //change icon for when in a world
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
                if(client.isPaused())
                {
                    presence.state = PhaseDiscordConfig.mainAdvancedModeStateMultiplayerPause.replace("{server_ip}", serverip);
                }
                else
                {
                    presence.state = PhaseDiscordConfig.mainAdvancedModeStateMultiplayer.replace("{server_ip}", serverip);
                }
            }
            else //in singleplayer
            {
                if(client.isPaused())
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
                presence.smallImageKey = imageNameOverworld;
                presence.smallImageText = PhaseDiscordConfig.advancedModeDimensionOverworld;
            }
            else if(dimensionName.equals("minecraft:the_nether"))
            {
                presence.smallImageKey = imageNameNether;
                presence.smallImageText = PhaseDiscordConfig.advancedModeDimensionNether;
            }
            else if(dimensionName.equals("minecraft:the_end"))
            {
                presence.smallImageKey = imageNameEnd;
                presence.smallImageText = PhaseDiscordConfig.advancedModeDimensionEnd;
            }
            else
            {
                customDimensionName = dimensionName.replace("minecraft:", "");
                presence.smallImageKey = imageNameCustom;
                presence.smallImageText = PhaseDiscordConfig.advancedModeDimensionCustom.replace("{dimension_name}", customDimensionName);
            }

            if(PhaseDiscordConfig.enableDebug == true)
            {
                System.out.println("Advanced Mode Variables");
                System.out.println("Dimension Name - " + dimensionName);
                System.out.println("Item Name - " + item_name);
                System.out.println("Server IP - " + serverip);
            }

            discord.Discord_UpdatePresence(presence);
        }
        else
        {
            System.out.println("World is null, cannot update presence.");
            DiscordRichPresence presence = new DiscordRichPresence();

            presence.details = PhaseDiscordConfig.advancedModeMainMenuText;
            presence.largeImageKey = "testicon1"; //large image key for an icon, the thing inside must be uploaded
            // to discord application's rich presence assets
            presence.largeImageText = "Phase's Minecraft Discord Rich Presence"; //large image text when hovered

            presence.instance = 1; //i'm going to be honest, idk what this does

            discord.Discord_UpdatePresence(presence); //update discord presence
        }
    }




}