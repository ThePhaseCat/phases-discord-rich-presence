package com.phasesdiscord;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import static com.phasesdiscord.MenuIntegration.configScreenFactory;
import static phasesdiscordConfigStuff.PhaseDiscordConfig.*;
import static net.minecraft.resource.ResourceType.CLIENT_RESOURCES;

public class PhaseDiscordClient implements ClientModInitializer {

    public static boolean loadSuccess = false;

    //logger
    public static final Logger LOGGER = LoggerFactory.getLogger("phases-discord-rich-presence");

    @Override
    public void onInitializeClient()
    {
        //config stuff
        ResourceManagerHelper.get(CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener()
        {
            // After all the resource files have been loaded, the Modmenu configuration interface is provided so that the text translation works properly
            @Override
            public Identifier getFabricId()
            {
                return Identifier.of("phases-discord-rich-presence", "discord-rich-presence");
            }

            @Override
            public void reload(ResourceManager manager)
            {
                mainAdvancedModeDetail = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField", "Playing Minecraft").getString();
                mainAdvancedModeDetailWhenHoldingItem = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField", "Holding %s").getString();
                mainAdvancedModeStateMultiplayer = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField", "Playing Multiplayer on %s with %s players").getString();
                mainAdvancedModeStateMultiplayerPause = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseTextField", "Playing multiplayer on %s with %s players - Paused").getString();
                mainAdvancedModeStateSingleplayer = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField", "Playing Singleplayer").getString();
                mainAdvancedModeStateSingleplayerPause = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField", "Playing Singleplayer - Paused").getString();
                advancedModeDimensionOverworld = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField", "In The Overworld").getString();
                advancedModeDimensionNether = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField", "In The Nether").getString();
                advancedModeDimensionEnd = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField", "In The End").getString();
                advancedModeDimensionCustom = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionCustomTextField", "In %s Dimension").getString();
                advancedModeMainMenuText = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField", "Main Menu").getString();

                MidnightConfig.init("phases-discord-rich-presence", PhaseDiscordConfig.class);
                try {
                    Class.forName("com.terraformersmc.modmenu.api.ModMenuApi");
                    configScreenFactory = parent -> MidnightConfig.getScreen(parent, "phases-discord-rich-presence");
                    loadSuccess = true;
                } catch (ClassNotFoundException ignored) {
                }
            }
        });

        //starts the new discord client system
        RPC.start();
    }
}