package com.phasesdiscord;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.lib.config.MidnightConfig;

import static com.phasesdiscord.PhaseDiscordClient.loadSuccess;


/**
 * ModMenu integration class
 */
public class MenuIntegration implements ModMenuApi {
    public static ConfigScreenFactory<?> configScreenFactory;

    /**
     * Get the config screen factory
     * @return the config screen factory
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        Thread.startVirtualThread(new Thread(() -> {
            // Prevent the Modmenu from getting the configuration screen until all resource files are loaded, ensuring that the multilingual translation is working properly
            while (loadSuccess == false) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
        return configScreenFactory;
    }
}
