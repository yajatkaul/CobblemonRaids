package com.cobblemon.fabric.raid;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.codecs.RaidData;
import com.cobblemon.common.raid.commands.CobblemonRaidCommands;
import com.cobblemon.common.raid.events.PlayerEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class CobblemonRaidsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CobblemonRaids.register();

        CommandRegistrationCallback.EVENT.register(CobblemonRaidCommands::registerCommands);

        ServerTickEvents.END_SERVER_TICK.register(CobblemonRaids::onServerTickEnd);

        ServerLifecycleEvents.SERVER_STARTED.register(CobblemonRaids::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPED.register(CobblemonRaids::onServerClosed);
        registerDatapacks();

        ServerPlayerEvents.LEAVE.register(PlayerEvents::onPlayerLeave);

        CobblemonRaids.registerCommon();
    }

    public static final ResourceKey<Registry<RaidData>> RAID_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_data"));

    public void registerDatapacks() {
        DynamicRegistries.registerSynced(RAID_REGISTRY_KEY, RaidData.CODEC);
    }

}
