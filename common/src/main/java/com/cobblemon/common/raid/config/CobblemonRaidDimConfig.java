package com.cobblemon.common.raid.config;

import com.cobblemon.common.raid.CobblemonRaids;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CobblemonRaidDimConfig {
    private static final String FILE_PATH = "./config/cobblemon_raids/cobblemon_raids_dim.json";

    public static class RaidDimensionConfig {
        public static class DimensionEntry {
            public String dimension;     // e.g., "minecraft:overworld"
            public float weight = 1.0f;  // Default weight
        }

        public static class RarityEntry {
            public float chance;
            public List<DimensionEntry> dimensions = new ArrayList<>();
        }

        public Map<String, RarityEntry> rarities = new HashMap<>();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static RaidDimensionConfig CONFIG;

    public static void register() {
        load();
    }

    public static void load() {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                CobblemonRaids.LOGGER.info("Raid dim config not found, creating default.");
                save();
                return;
            }

            String json = Files.readString(Path.of(FILE_PATH));
            CONFIG = GSON.fromJson(json, RaidDimensionConfig.class);
        } catch (Exception e) {
            CobblemonRaids.LOGGER.error("Failed to load raid dim config");
            CONFIG = getDefaultConfig(); // fallback
        }
    }

    public static RaidDimensionConfig getConfig() {
        if (CONFIG == null) CONFIG = getDefaultConfig();
        return CONFIG;
    }

    private static void save() {
        RaidDimensionConfig config = getDefaultConfig();
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            Files.createDirectories(Path.of("./config/cobblemon_raids"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(config));
        } catch (IOException e) {
            CobblemonRaids.LOGGER.error("Failed to save Cobblemon Raids config:", e);
        }
    }

    private static RaidDimensionConfig getDefaultConfig() {
        RaidDimensionConfig config = new RaidDimensionConfig();

        RaidDimensionConfig.RarityEntry common = new RaidDimensionConfig.RarityEntry();
        common.chance = 0.938f;
        RaidDimensionConfig.DimensionEntry overworld = new RaidDimensionConfig.DimensionEntry();
        overworld.dimension = "minecraft:overworld";
        overworld.weight = 1.0f;
        common.dimensions.add(overworld);

        RaidDimensionConfig.RarityEntry uncommon = new RaidDimensionConfig.RarityEntry();
        uncommon.chance = 0.05f;

        RaidDimensionConfig.RarityEntry rare = new RaidDimensionConfig.RarityEntry();
        rare.chance = 0.01f;

        RaidDimensionConfig.RarityEntry ultra_rare = new RaidDimensionConfig.RarityEntry();
        ultra_rare.chance = 0.002f;
        RaidDimensionConfig.DimensionEntry end = new RaidDimensionConfig.DimensionEntry();
        end.dimension = "minecraft:the_end";
        end.weight = 0.3f;
        RaidDimensionConfig.DimensionEntry nether = new RaidDimensionConfig.DimensionEntry();
        nether.dimension = "minecraft:the_nether";
        nether.weight = 0.7f;
        ultra_rare.dimensions.add(nether);
        ultra_rare.dimensions.add(end);

        config.rarities.put("common", common);
        config.rarities.put("uncommon", uncommon);
        config.rarities.put("rare", rare);
        config.rarities.put("ultra-rare", ultra_rare);

        return config;
    }
}
