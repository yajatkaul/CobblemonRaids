package com.cobblemon.neoforge.raid;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.codecs.RaidData;
import com.cobblemon.common.raid.commands.CobblemonRaidCommands;
import com.cobblemon.common.raid.blocks.RaidEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.NotNull;

@Mod(CobblemonRaids.MOD_ID)
public class CobblemonRaidsNeo {

    public CobblemonRaidsNeo(IEventBus modEventBus, @NotNull ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        CobblemonRaids.register();

        modEventBus.addListener(this::registerDatapackRegistries);
    }

    @SubscribeEvent
    public void onCommandRegistration(RegisterCommandsEvent event) {
        CobblemonRaidCommands.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        CobblemonRaids.onServerTickEnd(event.getServer());
    }

    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        CobblemonRaids.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public void onServerStart(ServerStoppedEvent event) {
        CobblemonRaids.onServerClosed(event.getServer());
    }

    public static final ResourceKey<Registry<RaidData>> RAID_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_data"));

    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(RAID_REGISTRY_KEY, RaidData.CODEC);
    }
}
