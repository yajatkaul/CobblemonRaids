package com.cobblemon.common.raid.managers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class RaidUtils {
    public static void giveItems(ServerPlayer player, ItemStack itemStack) {
        if (player.getInventory().getFreeSlot() == -1) {
            ItemEntity itemEntity = new ItemEntity(
                    player.level(),
                    player.getX(),
                    player.getY() + 1,
                    player.getZ(),
                    itemStack
            );
            itemEntity.setPickUpDelay(20);
            player.level().addFreshEntity(itemEntity);
        } else {
            player.addItem(itemStack);
        }
    }

    public static void giveItems(Set<ServerPlayer> players, ItemStack itemStack) {
        for (ServerPlayer player : players) {
            giveItems(player, itemStack);
        }
    }

    public static String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds); // e.g., 3:10:12
        } else {
            return String.format("%d:%02d", minutes, seconds); // e.g., 3:12
        }
    }
}
