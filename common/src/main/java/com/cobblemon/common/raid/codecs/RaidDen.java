package com.cobblemon.common.raid.codecs;

import com.cobblemon.common.raid.CobblemonRaids;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record RaidDen(String name,
                      ResourceKey<Level> denLevel,
                      String denType,
                      Vec3 bossSpawn,
                      Vec3 denSpawn,
                      List<CatchSpawn> catchSpawns) {
    public static final Codec<RaidDen> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(RaidDen::name),
            Level.RESOURCE_KEY_CODEC.fieldOf("denLevel").forGetter(RaidDen::denLevel),
            Codec.STRING.fieldOf("denType").forGetter(RaidDen::denType),
            Vec3.CODEC.fieldOf("bossSpawn").forGetter(RaidDen::bossSpawn),
            Vec3.CODEC.fieldOf("denSpawn").forGetter(RaidDen::denSpawn),
            CatchSpawn.CODEC.listOf().fieldOf("catchSpawns").forGetter(RaidDen::catchSpawns)
    ).apply(instance, RaidDen::new));

    //Save-Load
    private static final LevelResource DEN_DATA_SAVE_PATH = new LevelResource("raid/raid_dens");

    public void saveToJson(MinecraftServer server) {
        Path savePath = server.getWorldPath(DEN_DATA_SAVE_PATH).resolve(name + ".json");
        DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, this);

        try {
            Files.createDirectories(savePath.getParent());
        } catch (IOException e) {
            CobblemonRaids.LOGGER.warn("Failed to create directories for den data", e);
            return;
        }

        result.ifSuccess(json -> {
            String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            try (FileWriter writer = new FileWriter(savePath.toFile())) {
                writer.write(jsonString);
                writer.flush();
                //TODO
                //ArenaManager.loadArenas(server);
            } catch (IOException e) {
                CobblemonRaids.LOGGER.info("Failed to save den data", e);
            }
        });
    }

    public static Set<RaidDen> loadFromJson(MinecraftServer server) {
        Set<RaidDen> denDataList = new HashSet<>();
        Path folderPath = server.getWorldPath(DEN_DATA_SAVE_PATH);

        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            CobblemonRaids.LOGGER.warn("Failed to create directories for den data", e);
            return Set.of();
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, "*.json")) {
            for (Path path : stream) {
                try (FileReader reader = new FileReader(path.toFile())) {
                    JsonElement json = JsonParser.parseReader(reader);
                    DataResult<Pair<RaidDen, JsonElement>> result = RaidDen.CODEC.decode(JsonOps.INSTANCE, json);

                    result.resultOrPartial(error -> {
                        CobblemonRaids.LOGGER.warn("Failed to decode file {}: {}", path.getFileName(), error);
                    }).ifPresent(pair -> {
                        denDataList.add(pair.getFirst());
                    });

                } catch (Exception e) {
                    CobblemonRaids.LOGGER.error("Error reading file {}: {}", path, e);
                }
            }
        } catch (IOException e) {
            CobblemonRaids.LOGGER.error("Failed to list den data files", e);
        }

        return denDataList;
    }
}
