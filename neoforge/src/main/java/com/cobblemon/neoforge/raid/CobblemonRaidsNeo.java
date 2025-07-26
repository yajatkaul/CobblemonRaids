package com.cobblemon.neoforge.raid;

import com.cobblemon.common.raid.CobblemonRaids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(CobblemonRaids.MOD_ID)
public class CobblemonRaidsNeo {

    public CobblemonRaidsNeo() {
        NeoForge.EVENT_BUS.register(this);
        CobblemonRaids.register();
    }

    @SubscribeEvent
    public void onCommandRegistration(RegisterCommandsEvent event) {
        CobblemonRaids.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
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
}
