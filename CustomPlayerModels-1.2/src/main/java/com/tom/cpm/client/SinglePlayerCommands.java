package com.tom.cpm.client;

import java.util.List;
import java.util.StringTokenizer;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.PlayerControllerCreative;
import net.minecraft.src.PlayerControllerSP;

import com.tom.cpm.common.Command;

import cpw.mods.fml.client.FMLClientHandler;

//Port of:
//https://gist.github.com/mojontwins/de1fb522bb3ed724987f12768a3cd2cb
public class SinglePlayerCommands {

	public static final Command.CommandHandlerBase<Void> cpm = new Command.CommandHandlerBase<Void>() {
		{
			registerClient();
		}

		@Override
		protected void sendMessage(Void sender, String string) {
			FMLClientHandler.instance().getClient().ingameGUI.addChatMessage(string);
		}
	};

	public static void executeCommand(Minecraft mc, String command) {
		StringTokenizer tokenizer = new StringTokenizer(command);

		int numTokens = tokenizer.countTokens();
		if (numTokens == 0) return;

		String[] tokens = new String [numTokens];
		int idx = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens [idx++] = tokenizer.nextToken();
		}

		if (idx > 0) {
			String cmd = tokens [0];
			if ("/gamemode".equals(cmd)) {
				if (idx > 1) {
					String gameMode = tokens [1];
					if ("0".equals(gameMode) || "survival".equals(gameMode)) {
						if (mc.thePlayer.capabilities.isCreativeMode) mc.ingameGUI.addChatMessage("Game mode changed to survival");
						PlayerControllerCreative.disableAbilities(mc.thePlayer);
						mc.playerController = new PlayerControllerSP(mc);
					} else if ("1".equals(gameMode) || "creative".equals(gameMode)) {
						if (!mc.thePlayer.capabilities.isCreativeMode) mc.ingameGUI.addChatMessage("Game mode changed to creative");
						PlayerControllerCreative.enableAbilities(mc.thePlayer);
						mc.playerController = new PlayerControllerCreative(mc);
					}
				}
			} else if ("/time".equals(cmd)) {
				if (idx > 2 && "set".equals(tokens [1])) {
					int timeSet = -1;
					if ("night".equals(tokens [2])) {
						timeSet = 14000;
					} else if ("day".equals(tokens [2])) {
						timeSet = 1000;
					} else {
						try {
							timeSet = Integer.parseInt(tokens [2]);
						} catch (Exception e) { }
					}
					long timeBaseDay = mc.theWorld.getWorldTime() / 24000L * 24000L;
					long elapsedDay = mc.theWorld.getWorldTime() % 24000L;
					if (timeSet > elapsedDay) timeBaseDay += 24000L;
					mc.theWorld.getWorldInfo().setWorldTime(timeBaseDay + timeSet);
					mc.ingameGUI.addChatMessage("Time set to " + timeSet);
				}
			} else if ("/tp".equals(cmd)) {
				if (idx > 3) {
					double x = mc.thePlayer.posX;
					double y = mc.thePlayer.posY;
					double z = mc.thePlayer.posZ;

					try {
						x = Double.parseDouble(tokens [1]);
					} catch (Exception e) { }

					try {
						y = Double.parseDouble(tokens [2]);
					} catch (Exception e) { }

					try {
						z = Double.parseDouble(tokens [3]);
					} catch (Exception e) { }

					mc.thePlayer.setPositionAndUpdate(x, y, z);
					mc.ingameGUI.addChatMessage("Teleporting to " + x + " " + y + " " + z);
				}
			} else if ("/give".equals(cmd)) {
				try {
					int var21 = Integer.parseInt(tokens[1]);
					if (Item.itemsList[var21] != null) {
						mc.ingameGUI.addChatMessage("Giving some " + var21);
						int amount = 1;
						int meta = 0;
						if (tokens.length > 2) {
							amount = Integer.parseInt(tokens[2]);
						}

						if (tokens.length > 3) {
							meta = Integer.parseInt(tokens[3]);
						}

						if (amount < 1) {
							amount = 1;
						}

						if (amount > 64) {
							amount = 64;
						}

						mc.thePlayer.dropPlayerItem(new ItemStack(var21, amount, meta)).delayBeforeCanPickup = 0;
					} else {
						mc.ingameGUI.addChatMessage("There's no item with id " + var21);
					}
				} catch (NumberFormatException var22) {
					mc.ingameGUI.addChatMessage("There's no item with id " + tokens[1]);
				}
			} else if ("/toggledownfall".equals(cmd)) {
				mc.theWorld.setRainStrength(mc.theWorld.isRaining() ? 0 : 1);
				mc.ingameGUI.addChatMessage("Toggling rain and snow, hold on...");
			} else {
				cpm.onCommand(null, command.substring(1));
			}
		}
	}

	public static List<String> tabComplete(String text) {
		return cpm.onTabComplete(text.substring(1));
	}
}
