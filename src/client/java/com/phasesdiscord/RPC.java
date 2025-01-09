package com.phasesdiscord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import java.time.Instant;

public class RPC
{
    private static final Activity activity = new Activity();

    static MinecraftClient client = MinecraftClient.getInstance();

    static String customDimensionName = ""; //default value

    public static void start() {
        new Thread(() -> {
            if(PhaseDiscordConfig.discordEnable == false) //rich presense is disabled
            {
                return;
            }

            final CreateParams params = new CreateParams();
            params.setClientID(1147361100929708053L);
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
            activity.timestamps().setStart(Instant.now());

            try (final Core core = new Core(params)) {
                while (true) {
                    if (client.isInSingleplayer()) {
                        if(client.world != null)
                        {
                            DimensionType dimensionType = client.world.getDimension();
                            String dimensionName = dimensionType.effects().toString();
                            String serverip = "";
                            String itemToDisplay = "";

                            //everything above is anything that needs to be set/checked first (if that makes sense)
                            if(PhaseDiscordConfig.enableAdvancedMode == true) //advanced mode is enabled
                            {
                               itemToDisplay = getHeldItem(true);
                               activity.setDetails(itemToDisplay);
                            }
                            else //do simple mode stuff
                            {
                                itemToDisplay = getHeldItem(false);
                                activity.setDetails(itemToDisplay);
                                activity.assets().setLargeText("Phase's Minecraft Discord Rich Presence");
                                activity.assets().setLargeImage("base");

                                if(PhaseDiscordConfig.showPlayerHeadAndUsername == true)
                                {
                                    updatePlayerHead();
                                }
                                else
                                {
                                    setDimensionKey(false, dimensionName, activity);
                                }

                                if (PhaseDiscordConfig.showPaused == false) {
                                    activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField","Playing Singleplayer").getString());
                                } else {
                                    if (client.currentScreen != null) {
                                        activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField","Playing Singleplayer - Paused").getString());
                                    } else {
                                        activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField","Playing Singleplayer").getString());
                                    }
                                }
                            }
                        }
                    }
                    else if (client.getCurrentServerEntry() != null) {
                        activity.assets().setLargeText("test");
                        activity.assets().setLargeImage("large");
                        updatePlayerHead();
                        activity.assets().setSmallText("insert player name here when i get around to it");
                        activity.setDetails("In Multiplayer");
                        activity.setState("HI!");
                    }
                    else {
                        activity.assets().setLargeText("replace");
                        activity.assets().setLargeImage("large");
                        updatePlayerHead();
                        activity.assets().setSmallText("insert player name here when i get around to it");
                        activity.setDetails("In Main Menu");
                        activity.setState("HELLO!");
                    }

                    core.activityManager().updateActivity(activity);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            } catch (RuntimeException e) {
                //e.printStackTrace();
            }

        }).start();
    }

    //these following methods are adopted from the Fabric-DiscordRPC repo (https://github.com/copyandbuild/Fabric-DiscordRPC)
    //updates player head
    private static void updatePlayerHead() {
        String uuid = client.getGameProfile().getId().toString();
        String playerHeadImage = getPlayerHeadURL(uuid, "head", 3);
        activity.assets().setSmallImage(playerHeadImage);
        activity.assets().setSmallText(client.getSession().getUsername());
    }

    //fetches the image URL for the player head
    @Contract(pure = true)
    private static @NotNull String getPlayerHeadURL(String uuid, String type, int size) {
        return "https://api.mineatar.io/" + type + "/" + uuid + "?scale=" + size;
    }

    private static String getHeldItem(boolean isAdvancedMode)
    {
        String finalResult = ""; //default value
        String item_name = ""; //default value
        if(client.player != null)
        {
            ItemStack held_item = client.player.getStackInHand(Hand.MAIN_HAND);
            item_name = held_item.getName().getString();
            if(isAdvancedMode)
            {
                if(!item_name.equals(Items.AIR.getName().getString()))
                {
                    finalResult = PhaseDiscordConfig.mainAdvancedModeDetailWhenHoldingItem.replace("%s", item_name);
                }
                else
                {
                    finalResult = PhaseDiscordConfig.mainAdvancedModeDetail;
                }

                return finalResult;
            }
            else //simple mode
            {
                if (PhaseDiscordConfig.enableItem == false) {
                    finalResult = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailTextField","Playing Minecraft").getString();
                } else {
                    if (!item_name.equals(Items.AIR.getName().getString())) {
                        finalResult = Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeDetailWhenHoldingItemTextField","Holding" + item_name, item_name).getString();
                    }
                    else
                    {
                        finalResult = "Playing Minecraft";
                    }
                }

                return finalResult;
            }
        }

        return ""; //just in case
    }

    public static void setDimensionKey(boolean inAdvancedMode, String dimensionName, Activity presence)
    {
        if(inAdvancedMode)
        {

        }
        else
        {
            if (dimensionName.equals("minecraft:overworld")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    presence.assets().setSmallImage("void");
                    presence.assets().setSmallText("Minecraft");
                } else {
                    presence.assets().setSmallImage("overworld");
                    presence.assets().setSmallText(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionOverworldTextField","In The Overworld").getString());
                }
            } else if (dimensionName.equals("minecraft:the_nether")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    presence.assets().setSmallImage("void");
                    presence.assets().setSmallText("Minecraft");
                } else {
                    presence.assets().setSmallImage("nether");
                    presence.assets().setSmallText(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionNetherTextField","In The Nether").getString());
                }
            } else if (dimensionName.equals("minecraft:the_end")) {
                if (PhaseDiscordConfig.enableDimension == false) {
                    presence.assets().setSmallImage("void");
                    presence.assets().setSmallText("Minecraft");
                } else {
                    presence.assets().setSmallImage("the_end");
                    presence.assets().setSmallText(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.advancedModeDimensionEndTextField","In The End").getString());
                }
            } else {
                if (PhaseDiscordConfig.enableDimension == false) {
                    presence.assets().setSmallImage("void");
                    presence.assets().setSmallText("Minecraft");
                } else {
                    if (PhaseDiscordConfig.enableCustomDimensionSupport == false) {
                        presence.assets().setSmallImage("void");
                        presence.assets().setSmallText("");
                    } else {
                        //System.out.println(dimensionName + " why is this not working");
                        customDimensionName = dimensionName.replace("minecraft:", "");
                        presence.assets().setSmallImage("void");
                        presence.assets().setSmallText("In " + customDimensionName + " Dimension");
                    }
                }
            }
        }
    }
}
