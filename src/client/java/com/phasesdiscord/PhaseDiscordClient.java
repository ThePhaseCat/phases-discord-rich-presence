package com.phasesdiscord;

import com.phasesdiscord.discordstuff.DiscordMain;
import net.fabricmc.api.ClientModInitializer;

public class PhaseDiscordClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		DiscordMain.DiscordBootup();
	}
}