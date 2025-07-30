package com.cobblemon.common.raid.managers;

import com.cobblemon.common.raid.codecs.RaidData;
import com.cobblemon.common.raid.datapack.DatapackRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SpawnUtils {
    public static RaidData spawnRaid() {
        String selectedRarity = rollRarity();
        List<RaidData> filteredByRarity = DatapackRegister.raidsRegistry.stream()
                .filter(r -> r.rarity().equalsIgnoreCase(selectedRarity))
                .collect(Collectors.toList());

        return weightedChoice(filteredByRarity);
    }

    private static String rollRarity() {
        float roll = new Random().nextFloat();
        float cumulative = 0f;

        for (Map.Entry<String, Float> entry : getRarityChances().entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                return entry.getKey();
            }
        }
        return "common"; // fallback
    }

    private static Map<String, Float> getRarityChances() {
        return Map.of(
                "common", 0.6f,
                "rare", 0.3f,
                "legendary", 0.1f
        );
    }

    private static RaidData weightedChoice(List<RaidData> candidates) {
        float totalWeight = 0;
        for (RaidData r : candidates) totalWeight += r.weight();

        float roll = new Random().nextFloat() * totalWeight;
        float cumulative = 0;

        for (RaidData r : candidates) {
            cumulative += r.weight();
            if (roll < cumulative) {
                return r;
            }
        }
        return candidates.getFirst(); // fallback
    }

    public static BlockPos getRaidSpawnPos(ServerLevel level) {
        List<ServerPlayer> players = level.players();
        BlockPos pos = null;
        int attempts = 10;

        for (int i = 0; i < attempts; i++) {
            BlockPos candidatePos;

            if (!players.isEmpty()) {
                // Choose a random player
                ServerPlayer target = players.get(level.random.nextInt(players.size()));
                BlockPos playerPos = target.blockPosition();

                // Radius between 200 and 800
                int radius = 200 + level.random.nextInt(601);
                double angle = level.random.nextDouble() * 2 * Math.PI;

                int x = playerPos.getX() + (int) (radius * Math.cos(angle));
                int z = playerPos.getZ() + (int) (radius * Math.sin(angle));
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

                candidatePos = new BlockPos(x, y, z);
            } else {
                // No players: spawn somewhere in the overworld
                int x = level.random.nextInt(30000) - 15000;
                int z = level.random.nextInt(30000) - 15000;
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

                candidatePos = new BlockPos(x, y, z);
            }

            if (candidatePos.getY() > 0 && level.isLoaded(candidatePos)) {
                pos = candidatePos;
                break;
            }
        }

        // Fallback: center of world at surface level
        if (pos == null) {
            int x = 0;
            int z = 0;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            pos = new BlockPos(x, y, z);
        }

        return pos;
    }

}
