package com.cobblemon.common.raid.events;

import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.common.raid.managers.RaidManager;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEvents {

    public static void onPlayerLeave(ServerPlayer player) {
        RaidBoss raid = RaidManager.getRaidFromPlayer(player);

        if(raid != null) {
            raid.removePlayer(player);
        }
    }
}
