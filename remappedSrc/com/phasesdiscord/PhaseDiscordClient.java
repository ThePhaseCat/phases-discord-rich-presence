package com.phasesdiscord;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import static com.phasesdiscord.MenuIntegration.configScreenFactory;
import static phasesdiscordConfigStuff.PhaseDiscordConfig.*;
import static net.minecraft.server.packs.PackType.CLIENT_RESOURCES;

public class PhaseDiscordClient implements ClientModInitializer {

    public static boolean loadSuccess = false;
    public static final String ModID = "phases-discord-rich-presence";
    //logger
    public static final Logger LOGGER = LoggerFactory.getLogger(ModID);

    @Override
    public void onInitializeClient()
    {
        //config stuff
        ResourceLoader.get(CLIENT_RESOURCES).registerReloader(Identifier.fromNamespaceAndPath(ModID, "discord-rich-presence"), new SimpleResourceReloader<Void>() {
            // After all the resource files have been loaded, the Modmenu configuration interface is provided so that the text translation works properly
            @Override
            protected Void prepare(SharedState store) {
                // It's useless, just leave it blank
                return null; }

            @Override
            protected void apply(Void unused, SharedState store) {
                mainAdvancedModeDetail = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField", "Playing Minecraft").getString();
                mainAdvancedModeDetailWhenHoldingItem = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField", "Holding %s").getString();
                mainAdvancedModeStateMultiplayer = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField", "Playing Multiplayer on %s with %s players").getString();
                mainAdvancedModeStateMultiplayerPause = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseTextField", "Playing multiplayer on %s with %s players - Paused").getString();
                mainAdvancedModeStateSingleplayer = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField", "Playing Singleplayer").getString();
                mainAdvancedModeStateSingleplayerPause = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField", "Playing Singleplayer - Paused").getString();

                advancedModeDimensionOverworld = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField", "In The Overworld").getString();
                advancedModeDimensionNether = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField", "In The Nether").getString();
                advancedModeDimensionEnd = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField", "In The End").getString();
                advancedModeDimensionCustom = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionCustomTextField", "In %s Dimension").getString();

                advancedModeMainMenuText = Component.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField", "Main Menu").getString();

                MidnightConfig.init(ModID, PhaseDiscordConfig.class);
                try {
                    Class.forName("com.terraformersmc.modmenu.api.ModMenuApi");
                    configScreenFactory = parent -> MidnightConfig.getScreen(parent, ModID);
                    loadSuccess = true;
                } catch (ClassNotFoundException ignored) {
                }

                //starts the new discord client system
                try {
                    RPC.start();
                } catch (Exception e) {
                    LOGGER.error("Failed to start Discord RPC?");
                    throw new RuntimeException(e);
                }
            }
        });
    }
}