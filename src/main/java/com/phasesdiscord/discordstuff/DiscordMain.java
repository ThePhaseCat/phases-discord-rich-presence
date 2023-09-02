package com.phasesdiscord.discordstuff;

import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.MinecraftClient;

import static com.phasesdiscord.PhaseDiscordClient.playername;

public class DiscordMain {

    static DiscordRichPresence rich = null;



    public static void DiscordBootup()
    {
        playername = MinecraftClient.getInstance().getSession().getUsername();
        DiscordStartup.discordStartup();

    }

}
