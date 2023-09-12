package com.phasesdiscord;

import club.minnced.discord.rpc.*;
import club.minnced.discord.rpc.DiscordEventHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

import java.util.Timer;
import java.util.TimerTask;

public class PhaseDiscordClient implements ClientModInitializer {
	DiscordRPC discord = DiscordRPC.INSTANCE; //discord rich presence instance
	String appID = "1147361100929708053"; //app id for discord, you should probably NOT change this
	String steamId = ""; //this is useless because minecraft isn't a steam game, this is just for the sake of
	// passing it in methods

	MinecraftClient client = MinecraftClient.getInstance(); //client instance

	DiscordEventHandlers handlers = new DiscordEventHandlers(); //discord event handler

	Timer t = new Timer();
	Long start_time = System.currentTimeMillis() / 1000;
	@Override
	public void onInitializeClient() {
		handlers.ready = (user) -> System.out.println("Ready!");
		discord.Discord_Initialize(appID, handlers, true, steamId);

		basicDiscordPresence();
		new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				discord.Discord_RunCallbacks();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ignored) {
				}
			}
		}, "RPC-Callback-Handler").start();

		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				updateDiscordPresence();
			}
		}, 5000, 5000);
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

	private void updateDiscordPresence()
	{
		if(client.world != null)
		{
			boolean inSingleplayer = client.isInSingleplayer();
			DimensionType dimensionType = client.world.getDimension();
			String dimensionName = dimensionType.effects().toString();

			DiscordRichPresence presence = new DiscordRichPresence();

			//check if player holds something, update presence from there
			if(client.player!=null){
				ItemStack held_item = client.player.getStackInHand(Hand.MAIN_HAND);
				String item_name = held_item.getName().getString();
				if (!item_name.equals("Air")) {
					presence.details = "Holding " + item_name;
				}
			}

			//presence start stuff
			presence.startTimestamp = start_time;
			presence.largeImageKey = "testicon1"; //change icon for when in a world
			presence.largeImageText = "Phase's Minecraft Discord Rich Presence";
			presence.instance = 1; //still no clue what this means

			//all of this stuff here is useless
			presence.partyId = "priv_party";
			presence.matchSecret = "abXyyz";
			presence.joinSecret = "moonSqikCklaw";
			presence.spectateSecret = "moonSqikCklawkLopalwdNq";

			//presence state checks
			if(!inSingleplayer)
			{
				String serverIP = "";
				if(client.getCurrentServerEntry() != null)
				{
					serverIP = client.getCurrentServerEntry().address;
				}
				presence.state = "Playing Multiplayer on " + serverIP;
				presence.partyId = serverIP;
				presence.matchSecret = serverIP.toLowerCase();
			}
			else //means in singeplayer
			{
				presence.state = "Playing Singleplayer";
				presence.partySize = 1;
				presence.partyMax = 1;
			}


			//dimension checks
			if(dimensionName.equals("minecraft:overworld"))
			{
				presence.smallImageKey = "testicon2";
				presence.smallImageText = "In The Overworld";
			}
			if(dimensionName.equals("minecraft:the_nether"))
			{
				presence.smallImageKey = "testicon3";
				presence.smallImageText = "In The Nether";
			}
			if(dimensionName.equals("minecraft:the_end"))
			{
				presence.smallImageKey = "testicon4";
				presence.smallImageText = "In The End";
			}

			discord.Discord_UpdatePresence(presence);
		}
		else
		{
			basicDiscordPresence();
		}
	}
}