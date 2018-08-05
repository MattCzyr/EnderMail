package com.chaosthedude.endermail.config;

import java.io.File;

import com.chaosthedude.endermail.EnderMail;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler {
	
	public static Configuration config;

	public static boolean displayWithChatOpen = true;
	public static int lineOffset = 1;

	public static void loadConfig(File configFile) {
		config = new Configuration(configFile);

		config.load();
		init();

		MinecraftForge.EVENT_BUS.register(new ChangeListener());
	}

	public static void init() {
		String comment;
		
		comment = "Displays Package Controller information even while chat is open.";
		displayWithChatOpen = loadBool(Configuration.CATEGORY_CLIENT, "endermail.displayWithChatOpen", comment, displayWithChatOpen);

		comment = "The line offset for information rendered on the HUD.";
		lineOffset = loadInt(Configuration.CATEGORY_CLIENT, "endermail.lineOffset", comment, lineOffset);

		if (config.hasChanged()) {
			config.save();
		}
	}

	public static int loadInt(String category, String name, String comment, int def) {
		final Property prop = config.get(category, name, def);
		prop.setComment(comment);
		int val = prop.getInt(def);
		if (val < 0) {
			val = def;
			prop.set(def);
		}

		return val;
	}

	public static boolean loadBool(String category, String name, String comment, boolean def) {
		final Property prop = config.get(category, name, def);
		prop.setComment(comment);
		return prop.getBoolean(def);
	}

	public static String[] loadStringArray(String category, String comment, String name, String[] def) {
		Property prop = config.get(category, name, def);
		prop.setComment(comment);
		return prop.getStringList();
	}

	public static class ChangeListener {
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(EnderMail.MODID)) {
				init();
			}
		}
	}

}
