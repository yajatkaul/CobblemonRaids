package com.cobblemon.neoforge.raid;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.mod.common.platform.events.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

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
}
