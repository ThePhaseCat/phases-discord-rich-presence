package com.phasesdiscord.discordstuff;

import net.arikia.dev.drpc.DiscordRichPresence;

public class DiscordMain {

    static DiscordRichPresence rich = null;

    public static void DiscordBootup()
    {
        DiscordStartup.discordStartup();
    }
}
