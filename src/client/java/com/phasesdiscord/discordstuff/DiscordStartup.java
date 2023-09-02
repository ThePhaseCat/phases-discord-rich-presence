package com.phasesdiscord.discordstuff;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

import static com.phasesdiscord.discordstuff.DiscordMain.rich;

public class DiscordStartup {

    public static void discordStartup()
    {
        System.out.println("Discord stuff");
        rich = new  DiscordRichPresence.Builder("Playing Minecraft - Main Menu").setDetails("Insert stuff here").build();
        DiscordRPC.discordUpdatePresence(rich);

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
            @Override
            public void apply(DiscordUser user) {
                System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
            }
        }).build();

        DiscordRPC.discordInitialize("1147361100929708053", handlers, true);
        System.out.println("things should be showing on discord now");
    }
}
