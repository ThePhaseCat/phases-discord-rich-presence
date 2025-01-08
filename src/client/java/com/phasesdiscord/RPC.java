package com.phasesdiscord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class RPC
{
    private static final Activity activity = new Activity();

    static MinecraftClient client = MinecraftClient.getInstance();

    //returns if player is playing multiplayer or not
    public static boolean isOnMultiplayerServer() {
        return !client.isInSingleplayer();
    }

    public static void start() {
        new Thread(() -> {
            final CreateParams params = new CreateParams();
            params.setClientID(1147361100929708053L);
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
            activity.timestamps().setStart(Instant.now());
            try (final Core core = new Core(params)) {
                while (true) {
                    if (client.isInSingleplayer()) {
                        activity.assets().setLargeText("test");
                        activity.assets().setLargeImage("large");
                        updatePlayerHead();
                        activity.assets().setSmallText("insert player name here when i get around to it");
                        activity.setDetails("In Singleplayer");
                        activity.setState("HI!");
                    } else if (isOnMultiplayerServer()) {
                        activity.assets().setLargeText("test");
                        activity.assets().setLargeImage("large");
                        updatePlayerHead();
                        activity.assets().setSmallText("insert player name here when i get around to it");
                        activity.setDetails("In Multiplayer");
                        activity.setState("HI!");
                    } else {
                        activity.assets().setLargeText("BlueMoon-Client | Fabric 1.21");
                        activity.assets().setLargeImage("large");
                        updatePlayerHead();
                        activity.assets().setSmallText("insert player name here when i get around to it");
                        activity.setDetails("In Main Menu");
                        activity.setState("HELLO!");
                    }
                    core.activityManager().updateActivity(activity);

                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        }).start();
    }

    //these following methods are adopted from the Fabric-DiscordRPC repo (https://github.com/copyandbuild/Fabric-DiscordRPC)
    
    private static void updatePlayerHead() {
        String uuid = client.getGameProfile().getId().toString();
        String playerHeadImage = getPlayerHeadURL(uuid, "head", 3);
        activity.assets().setSmallImage(playerHeadImage);
    }

    @Contract(pure = true)
    private static @NotNull String getPlayerHeadURL(String uuid, String type, int size) {
        return "https://api.mineatar.io/" + type + "/" + uuid + "?scale=" + size;
    }
}
