package com.cobblemon.common.raid.managers;

import com.cobblemon.common.raid.config.CobblemonRaidDimConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Random;

public class DimensionUtils {

    private static final Random RANDOM = new Random();

    public static ResourceKey<Level> rollDimension() {
        CobblemonRaidDimConfig.RaidDimensionConfig config = CobblemonRaidDimConfig.getConfig();

        float roll = RANDOM.nextFloat();
        String selectedRarity = null;

        float cumulative = 0f;
        for (var entry : config.rarities.entrySet()) {
            cumulative += entry.getValue().chance;
            if (roll < cumulative) {
                selectedRarity = entry.getKey();
                break;
            }
        }

        if (selectedRarity == null || !config.rarities.containsKey(selectedRarity)) {
            selectedRarity = "common";
        }

        CobblemonRaidDimConfig.RaidDimensionConfig.RarityEntry rarityData = config.rarities.get(selectedRarity);
        if (rarityData == null || rarityData.dimensions.isEmpty()) {
            return Level.OVERWORLD;
        }

        float totalWeight = 0f;
        for (var entry : rarityData.dimensions) {
            totalWeight += entry.weight;
        }

        float dimRoll = RANDOM.nextFloat() * totalWeight;
        float dimCumulative = 0f;

        for (var entry : rarityData.dimensions) {
            dimCumulative += entry.weight;
            if (dimRoll < dimCumulative) {
                return ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(entry.dimension));
            }
        }

        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(rarityData.dimensions.getFirst().dimension));
    }
}
