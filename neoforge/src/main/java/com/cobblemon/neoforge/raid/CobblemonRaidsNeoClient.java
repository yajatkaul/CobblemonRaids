package com.cobblemon.neoforge.raid;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.CobblemonRaidsClient;
import com.cobblemon.common.raid.blocks.RaidEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = CobblemonRaids.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CobblemonRaidsNeoClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }
}
