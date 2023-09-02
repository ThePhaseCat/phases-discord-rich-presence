package com.phasesdiscord;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhaseDiscord implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("phases-discord-rich-presence");



	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");


	}
}