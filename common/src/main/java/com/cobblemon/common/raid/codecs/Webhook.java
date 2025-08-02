package com.cobblemon.common.raid.codecs;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.managers.RaidUtils;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public record Webhook(
        String webhookUrl
) {
    public WebhookClient client() {
        return WebhookClient.withUrl(this.webhookUrl);
    }

    public static final Codec<Webhook> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("webhookUrl").forGetter(Webhook::webhookUrl)
    ).apply(instance, Webhook::new));

    public void sendWebhook(String imgUrl, String raidName, long raidDuration, String cords, String dimension, Pokemon pokemon, String level) {
        WebhookClient client = client();
        if (client == null) return;

        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();

        // Set title (no formatting here, Discord doesn't parse markdown in title)
        builder.setTitle(new WebhookEmbed.EmbedTitle(raidName, null));

        // Set image
        if(imgUrl != null){
            builder.setImageUrl(imgUrl);
        }

        // Set timestamp to now
        Instant now = Instant.now();
        builder.setTimestamp(now);

        // Format start time as 12-hour clock with AM/PM (e.g., 1:24 PM)
        String formattedTime = DateTimeFormatter.ofPattern("h:mm a")
                .withZone(ZoneId.systemDefault())
                .format(now);

        // Add informational fields
        builder.addField(new WebhookEmbed.EmbedField(false, "**Start Time:**", formattedTime));
        builder.addField(new WebhookEmbed.EmbedField(false, "**Raid Duration:**", RaidUtils.formatTime((int) raidDuration)));
        builder.addField(new WebhookEmbed.EmbedField(false, "**Dimension:**", dimension));
        builder.addField(new WebhookEmbed.EmbedField(false, "**Coords:**", cords));
        builder.addField(new WebhookEmbed.EmbedField(false, "**Level:**", level));

        // Add IV stats
        for (Map.Entry<? extends Stat, ? extends Integer> iv : pokemon.getIvs()) {
            String statName = iv.getKey().getDisplayName().getString();
            String statValue = String.valueOf(iv.getValue());
            builder.addField(new WebhookEmbed.EmbedField(true, "**" + statName + ":**", statValue));
        }

        // Send the embed
        client.send(builder.build());
    }

    //Save-Load
    private static final LevelResource WEBHOOK_DATA_SAVE_PATH = new LevelResource("raid/webhook");

    public void saveToJson(MinecraftServer server) {
        Path savePath = server.getWorldPath(WEBHOOK_DATA_SAVE_PATH).resolve("webhook.json");
        DataResult<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, this);

        try {
            Files.createDirectories(savePath.getParent());
        } catch (IOException e) {
            CobblemonRaids.LOGGER.warn("Failed to create directories for webhook data", e);
            return;
        }

        result.ifSuccess(json -> {
            String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            try (FileWriter writer = new FileWriter(savePath.toFile())) {
                writer.write(jsonString);
                writer.flush();
            } catch (IOException e) {
                CobblemonRaids.LOGGER.info("Failed to save webhook data", e);
            }
        });
    }

    public static Webhook loadFromJson(MinecraftServer server) {
        Path folderPath = server.getWorldPath(WEBHOOK_DATA_SAVE_PATH);
        AtomicReference<Webhook> thisHook = new AtomicReference<Webhook>(null);

        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            CobblemonRaids.LOGGER.warn("Failed to create directories for webhook data", e);
            return null;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, "*.json")) {
            for (Path path : stream) {
                try (FileReader reader = new FileReader(path.toFile())) {
                    JsonElement json = JsonParser.parseReader(reader);
                    DataResult<Pair<Webhook, JsonElement>> result = Webhook.CODEC.decode(JsonOps.INSTANCE, json);

                    result.resultOrPartial(error -> {
                        CobblemonRaids.LOGGER.warn("Failed to decode file {}: {}", path.getFileName(), error);
                    }).ifPresent(pair -> {
                        thisHook.set(pair.getFirst());
                    });

                } catch (Exception e) {
                    CobblemonRaids.LOGGER.error("Error reading file {}: {}", path, e);
                }
            }
        } catch (IOException e) {
            CobblemonRaids.LOGGER.error("Failed to list webhook data files", e);
        }

        return thisHook.get();
    }
}
