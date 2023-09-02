package com.phasesdiscord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class PhaseDiscordClient implements ClientModInitializer {

	MinecraftClient client = MinecraftClient.getInstance();


	String appID = "1147361100929708053"; //app id for discord

	DiscordEventHandlers handlers = new DiscordEventHandlers();
	@Override
	public void onInitializeClient() {
		handlers.ready = (user) -> System.out.println("Ready!");

	}

}