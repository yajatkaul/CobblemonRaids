package com.cobblemon.common.raid.config;

import com.cobblemon.common.raid.CobblemonRaids;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CobblemonRaidsConfig {
    private static final String FILE_PATH = "./config/cobblemon_raids/cobblemon_raids.json";

    public static int raidCoolDown = 3600;
    public static float raidOccurrencePercentage = 50f;

    public static void register() {
        load();
    }

    public static void save() {
        JsonObject json = new JsonObject();
        json.addProperty("raidCoolDown", raidCoolDown);
        json.addProperty("raidOccurrencePercentage", raidOccurrencePercentage);

        try {
            Files.createDirectories(Path.of("./config/cobblemon_raids"));
            try (FileWriter writer = new FileWriter(FILE_PATH)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(json));
            }
        } catch (IOException e) {
            CobblemonRaids.LOGGER.error("Failed to save Cobblemon Raids config:", e);
        }
    }

    public static void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            CobblemonRaids.LOGGER.info("Raids config not found, creating default.");
            save();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            if (json.has("raidCoolDown")) {
                raidCoolDown = json.get("raidCoolDown").getAsInt();
            }
            if (json.has("raidOccurrencePercentage")) {
                raidOccurrencePercentage = json.get("raidOccurrencePercentage").getAsFloat();
            }
        } catch (Exception e) {
            CobblemonRaids.LOGGER.error("Failed to load Cobblemon Raids config:", e);
        }
    }
}