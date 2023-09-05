package com.phasesdiscord;

import club.minnced.discord.rpc.*;
import club.minnced.discord.rpc.DiscordEventHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class PhaseDiscordClient implements ClientModInitializer {
	DiscordRPC discord = DiscordRPC.INSTANCE; //discord rich presence instance
	String appID = "1147361100929708053"; //app id for discord, you should probably NOT change this
	String steamId = ""; //this is useless because minecraft isn't a steam game, this is just for the sake of
	// passing it in methods

	MinecraftClient client = MinecraftClient.getInstance(); //client instance

	DiscordEventHandlers handlers = new DiscordEventHandlers(); //discord event handler
	@Override
	public void onInitializeClient() {
		handlers.ready = (user) -> System.out.println("Ready!");
		discord.Discord_Initialize(appID, handlers, true, steamId);

		basicDiscordPresence();

	}


	private void basicDiscordPresence()
	{
		DiscordRichPresence presence = new DiscordRichPresence();

		presence.details = "Main Menu"; //presence details
		presence.largeImageKey = "testicon1"; //large image key for an icon, the thing inside must be uploaded
		// to discord application's rich presence assets
		presence.largeImageText = "Phase's Minecraft Discord Rich Presence"; //large image text when hovered

		presence.instance = 1; //i'm going to be honest, idk what this does
		discord.Discord_UpdatePresence(presence); //update discord presence
	}
}