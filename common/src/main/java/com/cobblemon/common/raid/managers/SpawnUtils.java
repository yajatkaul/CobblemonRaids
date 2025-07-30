package com.cobblemon.common.raid.managers;

import com.cobblemon.common.raid.codecs.RaidData;
import com.cobblemon.common.raid.datapack.DatapackRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class SpawnUtils {
    public static RaidData spawnRaid(ServerLevel level, BlockPos pos) {
        Holder<Biome> biomeHolder = level.getBiome(pos);
        String selectedRarity = rollRarity();

        List<RaidData> filteredByRarity = DatapackRegister.raidsRegistry.stream()
                .filter(raid -> raidMatches(raid, selectedRarity, biomeHolder, level))
                .collect(Collectors.toList());

        if (filteredByRarity.isEmpty()) {
            return null;
        }

        return weightedChoice(filteredByRarity);
    }

    private static boolean raidMatches(RaidData raid, String selectedRarity, Holder<Biome> biomeHolder, ServerLevel level) {
        if (!raid.rarity().equalsIgnoreCase(selectedRarity)) return false;

        Optional<String> biomeIdOpt = raid.biome();
        if (biomeIdOpt.isEmpty()) return true;

        String biomeId = biomeIdOpt.get();

        // Handle tag match
        if (biomeId.startsWith("#")) {
            TagKey<Biome> tagKey = TagKey.create(Registries.BIOME, ResourceLocation.tryParse(biomeId.substring(1)));
            return biomeHolder.is(tagKey);
        }

        // Handle direct biome key match
        Optional<ResourceKey<Biome>> biomeKeyOpt = level.registryAccess()
                .registryOrThrow(Registries.BIOME)
                .getResourceKey(biomeHolder.value());

        return biomeKeyOpt.map(key -> key.location().toString().equals(biomeId)).orElse(false);
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
        List<ServerPlayer> players = level.players().stream().filter(p -> p.level() == level).toList();
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

                int y = getSafeY(level, x, z);

                candidatePos = new BlockPos(x, y, z);
            } else {
                // No players: spawn somewhere in the overworld
                int x = level.random.nextInt(30000) - 15000;
                int z = level.random.nextInt(30000) - 15000;
                int y = getSafeY(level, x, z);

                candidatePos = new BlockPos(x, y, z);
            }

            if (candidatePos.getY() > 0 && level.isLoaded(candidatePos)) {
                pos = candidatePos;
                break;
            }
        }

        return pos;
    }

    private static int getSafeY(ServerLevel level, int x, int z) {
        ResourceKey<Level> dim = level.dimension();

        if (dim == Level.NETHER) {
            // Nether: find the top-most solid block below the bedrock ceiling (usually around Y=120)
            for (int y = 120; y > 31; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = level.getBlockState(pos);
                if (state.isCollisionShapeFullBlock(level, pos)) {
                    return y + 1;
                }
            }
            return 32; // fallback low position
        } else if (dim == Level.END) {
            // End: usually spawns on End Stone, avoid void
            for (int y = 80; y > 30; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = level.getBlockState(pos);
                if (state.isCollisionShapeFullBlock(level, pos)) {
                    return y + 1;
                }
            }
            return 60;
        } else {
            // Overworld or others: use heightmap
            return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        }
    }
}
