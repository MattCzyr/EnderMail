package com.chaosthedude.endermail.config;

import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.endermail.util.OverlaySide;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {

	private static final ForgeConfigSpec.Builder GENERAL_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static final General GENERAL = new General(GENERAL_BUILDER);
	public static final Client CLIENT = new Client(CLIENT_BUILDER);

	public static final ForgeConfigSpec GENERAL_SPEC = GENERAL_BUILDER.build();
	public static final ForgeConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

	public static class General {
		public final ForgeConfigSpec.BooleanValue hideLockerLocation;
		public final ForgeConfigSpec.IntValue maxDeliveryDistance;
		public final ForgeConfigSpec.IntValue lockerDeliveryRadius;
		public final ForgeConfigSpec.BooleanValue lockerDeliveryRadiusIgnoresY;
		public final ForgeConfigSpec.BooleanValue logDeliveries;
		public final ForgeConfigSpec.ConfigValue<List<String>> packageContentsBlacklist;

		General(ForgeConfigSpec.Builder builder) {
			String desc;
			builder.push("General");
			
			desc = "Determines whether a locker\'s location will be hidden to the package sender.";
			hideLockerLocation = builder.comment(desc).define("hideLockerLocation", false);

			desc = "The maximum distance that packages can be delivered over. Set to -1 for no distance limit.";
			maxDeliveryDistance = builder.comment(desc).defineInRange("maxDeliveryDistance", -1, -1, 1000000);
			
			desc = "Packages with delivery locations within this radius of a locker will be delivered to the locker.";
			lockerDeliveryRadius = builder.comment(desc).defineInRange("lockerDeliveryRadius", 50, 0, 500);
			
			desc = "Determines whether a locker\'s delivery radius will ignore a package\'s delivery location\'s Y-coordinate.";
			lockerDeliveryRadiusIgnoresY = builder.comment(desc).define("lockerDeliveryRadiusIgnoresY", true);
			
			desc = "Determines whether package deliveries will be logged in the console.";
			logDeliveries = builder.comment(desc).define("logDeliveries", false);
			
			desc = "The list of items that are not allowed to be placed in packages. Ex: [\"endermail:package\"]";
			packageContentsBlacklist = builder.comment(desc).define("packageContentsBlacklist", new ArrayList<String>());

			builder.pop();
		}
	}

	public static class Client {
		public final ForgeConfigSpec.BooleanValue displayWithChatOpen;
		public final ForgeConfigSpec.IntValue lineOffset;
		public final ForgeConfigSpec.EnumValue<OverlaySide> overlaySide;

		Client(ForgeConfigSpec.Builder builder) {
			String desc;
			builder.push("Client");

			desc = "Displays Package Controller information even while chat is open.";
			displayWithChatOpen = builder.comment(desc).define("displayWithChatOpen", true);

			desc = "The line offset for information rendered on the HUD.";
			lineOffset = builder.comment(desc).defineInRange("lineOffset", 1, 0, 50);

			desc = "The side for information rendered on the HUD. Ex: LEFT, RIGHT";
			overlaySide = builder.comment(desc).defineEnum("overlaySide", OverlaySide.LEFT);

			builder.pop();
		}
	}

}
