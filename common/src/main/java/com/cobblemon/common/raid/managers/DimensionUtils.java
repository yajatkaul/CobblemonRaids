package com.cobblemon.common.raid.managers;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DimensionUtils {

    private static final Random RANDOM = new Random();

    public static ResourceKey<Level> rollDimension() {
        // Step 1: Roll for rarity
        float rarityRoll = RANDOM.nextFloat();
        String selectedRarity = getSelectedRarity(rarityRoll);

//        System.out.println("[RAID] Rolled rarity: " + selectedRarity + " (roll=" + rarityRoll + ")");

        // Step 2: Get dimensions for that rarity
        // If the selected rarity has no dimensions defined, default to Overworld
        List<ResourceKey<Level>> possibleDimensions = getDimensionsByRarity()
                .getOrDefault(selectedRarity, List.of(Level.OVERWORLD));

        // Ensure there's always at least one dimension to pick from
        if (possibleDimensions.isEmpty()) {
            possibleDimensions = List.of(Level.OVERWORLD);
//            System.out.println("[RAID] No dimensions found for rarity '" + selectedRarity + "', defaulting to Overworld.");
        }

        // Step 3: Weight-based selection within that rarity
        Map<ResourceKey<Level>, Float> weights = getDimensionWeights();
        float totalWeight = 0f;
        for (ResourceKey<Level> dim : possibleDimensions) {
            // Use getOrDefault to handle cases where a dimension in possibleDimensions might not have an explicit weight
            totalWeight += weights.getOrDefault(dim, 1.0f);
        }

        float dimRoll = RANDOM.nextFloat() * totalWeight;
        float dimCumulative = 0f;

        for (ResourceKey<Level> dim : possibleDimensions) {
            dimCumulative += weights.getOrDefault(dim, 1.0f);
            if (dimRoll < dimCumulative) {
//                System.out.println("[RAID] Selected dimension: " + dim.location() + " (roll=" + dimRoll + ", rarity=" + selectedRarity + ")");
                return dim;
            }
        }

        // This fallback should ideally not be reached if totalWeight is calculated correctly and possibleDimensions is not empty.
        // It's a safeguard.

        // System.out.println("[RAID] Fallback to first dimension in list: " + fallbackDim.location() + " (rarity=" + selectedRarity + ")");
        return possibleDimensions.getFirst();
    }

    private static @NotNull String getSelectedRarity(float rarityRoll) {
        float rarityCumulative = 0f;
        String selectedRarity = null; // Initialize to null to ensure it's set by the loop

        for (Map.Entry<String, Float> entry : getDimensionRarityChances().entrySet()) {
            rarityCumulative += entry.getValue();
            if (rarityRoll < rarityCumulative) {
                selectedRarity = entry.getKey();
                break;
            }
        }

        // Fallback in case no rarity is selected (shouldn't happen with valid chances summing to 1)
        if (selectedRarity == null) {
            selectedRarity = "common";
        }
        return selectedRarity;
    }

    private static Map<String, Float> getDimensionRarityChances() {
        return Map.of(
                "common", 0.6f,
                "rare", 0.3f,
                "legendary", 0.1f
        );
    }

    // This method now allows multiple dimensions per rarity, enabling the weight-based selection in Step 3
    private static Map<String, List<ResourceKey<Level>>> getDimensionsByRarity() {
        return Map.of(
                "common", List.of(Level.OVERWORLD),
                "rare", List.of(Level.NETHER, Level.END),
                "legendary", List.of(Level.END)
        );
    }

    private static Map<ResourceKey<Level>, Float> getDimensionWeights() {
        return Map.of(
                Level.OVERWORLD, 1.0f,
                Level.NETHER, 0.7f,
                Level.END, 0.3f
        );
    }
}