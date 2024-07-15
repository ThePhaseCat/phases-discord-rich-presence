package com.phasesdiscord;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.lib.config.MidnightConfig;

/**
 * ModMenu integration class
 */
public class MenuIntegration implements ModMenuApi {

    /**
     * Get the config screen factory
     * @return the config screen factory
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MidnightConfig.getScreen(parent, "phases-discord-rich-presence");
    }
}
