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
                mainAdvancedModeDetail = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField").getString();
                mainAdvancedModeDetailWhenHoldingItem = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField").getString();
                mainAdvancedModeStateMultiplayer = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerTextField").getString();
                mainAdvancedModeStateMultiplayerPause = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateMultiplayerPauseTextField").getString();
                mainAdvancedModeStateSingleplayer = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField").getString();
                mainAdvancedModeStateSingleplayerPause = Text.translatable("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField").getString();
                advancedModeDimensionOverworld = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField").getString();
                advancedModeDimensionNether = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField").getString();
                advancedModeDimensionEnd = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField").getString();
                advancedModeDimensionCustom = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeDimensionCustomTextField").getString();
                advancedModeMainMenuText = Text.translatable("phases-discord-rich-presence.midnightconfig.advancedModeMainMenuTextTextField").getString();

                MidnightConfig.init("phases-discord-rich-presence", PhaseDiscordConfig.class);
                try {
                    Class.forName("com.terraformersmc.modmenu.api.ModMenuApi");
                    configScreenFactory = parent -> MidnightConfig.getScreen(parent, "phases-discord-rich-presence");
                    loadSuccess = true;
                } catch (ClassNotFoundException ignored) {
                }

                //starts the new discord client system
                RPC.start();
            }
        });
    }
}