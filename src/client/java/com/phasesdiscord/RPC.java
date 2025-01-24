package com.phasesdiscord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import phasesdiscordConfigStuff.PhaseDiscordConfig;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;

import static com.phasesdiscord.PhaseDiscordClient.LOGGER;

public class RPC
{
    private static final Activity activity = new Activity();

    static MinecraftClient client = MinecraftClient.getInstance();

    static String imageNameOverworld;

    static String imageNameNether;

    static String imageNameEnd;

    static String imageNameCustom;

    static String largeImageKey;

    static String customDimensionName = ""; //default value

    static String[] imageKeyArray = {
            "overworld", "mountain", "swamp", "blankplains", "caveoverlookview", "mountainview",
            "pale_garden1", "pale_garden2", "pale_garden3", "shaders1", "shaders2", "trial_chamber",
            "nether", "nether2", "nether3", "nethercool",
            "the_end", "end2", "end3", "actualendbg",
            "void", "base", "creeper_icon", "fallback"
    };

    public static void start() {
            new Thread(() -> {
                if (!PhaseDiscordConfig.discordEnable) { // rich presence is disabled
                    return;
                }

                final CreateParams params = new CreateParams();
                params.setClientID(1147361100929708053L);
                params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
                activity.timestamps().setStart(Instant.now());

                try (final Core core = new Core(params)) {
                    while (true) {
                        try {
                            if (client.isInSingleplayer()) {
                                if (client.world != null) {
                                    DimensionType dimensionType = client.world.getDimension();
                                    String dimensionName = dimensionType.effects().toString();
                                    String itemToDisplay = "";
                                    imageNameOverworld = PhaseDiscordConfig.advancedModeOverworldPic;
                                    imageNameNether = PhaseDiscordConfig.advancedModeNetherPic;
                                    imageNameEnd = PhaseDiscordConfig.advancedModeEndPic;
                                    imageNameCustom = PhaseDiscordConfig.advancedModeCustomPic;
                                    largeImageKey = PhaseDiscordConfig.advancedModeLargePic;

                                    if (PhaseDiscordConfig.enableAdvancedMode) { // advanced mode is enabled
                                        itemToDisplay = getHeldItem(true);
                                        activity.setDetails(itemToDisplay);
                                        activity.assets().setLargeText("Phase's Minecraft Discord Rich Presence");
                                        if (!checkIfImageKeyIsValid(largeImageKey)) { // large image stuff
                                            activity.assets().setLargeImage("fallback"); // change icon for when in a world
                                        } else {
                                            activity.assets().setLargeImage(largeImageKey); // change icon for when in a world
                                        }

                                        if (client.currentScreen != null) {
                                            activity.setState(PhaseDiscordConfig.mainAdvancedModeStateSingleplayerPause);
                                        } else {
                                            activity.setState(PhaseDiscordConfig.mainAdvancedModeStateSingleplayer);
                                        }
                                        // set dimension stuff
                                        setDimensionKey(true, dimensionName, activity);
                                    } else { // do simple mode stuff
                                        itemToDisplay = getHeldItem(false);
                                        activity.setDetails(itemToDisplay);
                                        activity.assets().setLargeText("Phase's Minecraft Discord Rich Presence");
                                        activity.assets().setLargeImage("base");

                                        if (PhaseDiscordConfig.showPlayerHeadAndUsername) {
                                            updatePlayerHead();
                                        } else {
                                            setDimensionKey(false, dimensionName, activity);
                                        }

                                        if (!PhaseDiscordConfig.showPaused) {
                                            activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField", "Playing Singleplayer").getString());
                                        } else {
                                            if (client.currentScreen != null) {
                                                activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerPauseTextField", "Playing Singleplayer - Paused").getString());
                                            } else {
                                                activity.setState(Text.translatableWithFallback("phases-discord-rich-presence.midnightconfig.mainAdvancedModeStateSingleplayerTextField", "Playing Singleplayer").getString());
                                            }
                                        }
                                    }
                                }
                            } else if (client.getCurrentServerEntry() != null) {
                                activity.assets().setLargeText("test");
                                activity.assets().setLargeImage("large");
                                updatePlayerHead();
                                activity.assets().setSmallText("insert player name here when i get around to it");
                                activity.setDetails("In Multiplayer");
                                activity.setState("HI!");
                            } else {
                                activity.assets().setLargeText("replace");
                                activity.assets().setLargeImage("large");
                                updatePlayerHead();
                                activity.assets().setSmallText("insert player name here when i get around to it");
                                activity.setDetails("In Main Menu");
                                activity.setState("HELLO!");
                            }

                            core.activityManager().updateActivity(activity);

                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            LOGGER.error("Thread was interrupted", e);
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e) {
                            LOGGER.error("An error occurred while updating Discord activity", e);
                        }
                    }
                } catch (RuntimeException e) {
                    LOGGER.error("Failed to initialize Discord Core", e);
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
            if(dimensionName.equals("minecraft:overworld"))
            {
                if(checkIfImageKeyIsValid(imageNameOverworld) == false)
                {
                    activity.assets().setSmallImage("fallback");
                }
                else
                {
                    activity.assets().setSmallImage(imageNameOverworld);
                }

                activity.assets().setSmallText(PhaseDiscordConfig.advancedModeDimensionOverworld);
            }
            else if(dimensionName.equals("minecraft:the_nether"))
            {
                if(checkIfImageKeyIsValid(imageNameNether) == false)
                {
                    activity.assets().setSmallImage("fallback");
                }
                else
                {
                    activity.assets().setSmallImage(imageNameNether);
                }

                activity.assets().setSmallText(PhaseDiscordConfig.advancedModeDimensionNether);
            }
            else if(dimensionName.equals("minecraft:the_end"))
            {
                if(checkIfImageKeyIsValid(imageNameEnd) == false)
                {
                    activity.assets().setSmallImage("fallback");
                }
                else
                {
                    activity.assets().setSmallImage(imageNameEnd);
                }

                activity.assets().setSmallText(PhaseDiscordConfig.advancedModeDimensionEnd);
            }
            else
            {
                customDimensionName = dimensionName.replace("minecraft:", "");
                if(checkIfImageKeyIsValid(imageNameCustom) == false)
                {
                    activity.assets().setSmallImage("fallback");
                }
                else
                {
                    activity.assets().setSmallImage(imageNameCustom);
                }

                activity.assets().setSmallText(PhaseDiscordConfig.advancedModeDimensionCustom.replace("{dimension_name}", customDimensionName));
            }
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

    public static boolean checkIfImageKeyIsValid(String imageKey)
    {
        if(Arrays.stream(imageKeyArray).anyMatch(imageKey::equals))
        {
            return true;
        }
        else
        {
            LOGGER.info("An image key for advanced mode is invalid, setting to fallback.");
            LOGGER.info("Invalid Image Key - " + imageKey);
            return false;
        }
    }
}
